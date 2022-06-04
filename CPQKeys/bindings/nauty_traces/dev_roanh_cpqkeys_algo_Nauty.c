#include "dev_roanh_cpqkeys_algo_Nauty.h"
#include "core.h"

JNIEXPORT jint JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_test(JNIEnv* env, jclass obj, jint num){
	return 2 * num;
}

//sparse nauty
JNIEXPORT void JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanon(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	SG_DECL(graph);

	constructSparseGraph(env, &adj, &graph);

	DYNALLSTAT(int, labels, labels_sz);
	DYNALLSTAT(int, ptn, ptn_sz);
	DYNALLSTAT(int, orbits, orbits_sz);

	static DEFAULTOPTIONS_SPARSEGRAPH(options);
	statsblk stats;
	options.getcanon = TRUE;

	int n = graph.nv;
	DYNALLOC1(int, labels, labels_sz, n, "malloc");
	DYNALLOC1(int, ptn, ptn_sz, n, "malloc");
	DYNALLOC1(int, orbits, orbits_sz, n, "malloc");

	parseColoring(env, n, &colors, labels, ptn);

	//compute canonical form and labelling
	SG_DECL(canon);
	sparsenauty(&graph, labels, ptn, orbits, &options, &stats, &canon);

	//TODO return some representation of canon
	printf("canon:");
	for(int i = 0; i < n; i++){
		printf(" %d", labels[i]);
	}
	printf("\n");
}
