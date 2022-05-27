#include "dev_roanh_cpqkeys_algo_Nauty.h"
#include "nausparse.h"

JNIEXPORT jint JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_test(JNIEnv* env, jclass obj, jint num){
	return 2 * num;
}

void canon(sparsegraph* graph){
	DYNALLSTAT(int, labels, labels_sz);
	DYNALLSTAT(int, ptn, ptn_sz);
	DYNALLSTAT(int, orbits, orbits_sz);

	static DEFAULTOPTIONS_SPARSEGRAPH(options);
	statsblk stats;
	options.getcanon = TRUE;

	int n = graph->nv;
	DYNALLOC1(int, labels, labels_sz, n, "malloc");
	DYNALLOC1(int, ptn, ptn_sz, n, "malloc");
	DYNALLOC1(int, orbits, orbits_sz, n, "malloc");

	//compute canonical form and labelling
	SG_DECL(canon);
	sparsenauty(&graph, labels, ptn, orbits, &options, &stats, &canon);

	//TODO return some representation of canon
}

JNIEXPORT void JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanon(JNIEnv* env, jclass obj, jobjectArray adj){
	SG_DECL(graph);

	jsize len = (*env)->GetArrayLength(env, adj);
	nauty_check(WORDSIZE, SETWORDSNEEDED(len), len, NAUTYVERSIONID);
	graph.nv = len;

	graph.nde = 0;
	for(int i = 0; i < len){
		graph.nde += (*env)->GetArrayLength(env, (jintArray)env->GetObjectArrayElement(adj, i));
	}

	SG_ALLOC(graph, len, graph.nde, "malloc");
	int offset = 0;
	for(int i = 0; i < len; i++){
		jintArray row = (jintArray)env->GetObjectArrayElement(adj, i);
		jsize rlen = (*env)->GetArrayLength(env, row);
		jint* elem = (*env)->GetIntArrayElements(env, row, 0);

		graph.v[i] = offset;
		graph.d[i] = rlen;

		for(int j = 0; j < rlen; j++){
			graph.e[offset] = elem[j];
			offset++;
		}

		(*env)->ReleaseIntArrayElements(env, row, elem, 0);
	}

	canon(&graph);
}
