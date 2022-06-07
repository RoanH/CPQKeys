#include <dev_roanh_cpqkeys_algo_Nauty.h>
#include <core.h>

//dense nauty
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanonDense(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	struct timespec start;
	clock_gettime(CLOCK_MONOTONIC_RAW, &start);

	DYNALLSTAT(int, labels, labels_sz);
	DYNALLSTAT(int, ptn, ptn_sz);
	DYNALLSTAT(int, orbits, orbits_sz);
	DYNALLSTAT(graph, input, input_sz);

	static DEFAULTOPTIONS_GRAPH(options);
	statsblk stats;
	options.getcanon = TRUE;

	int n = (*env)->GetArrayLength(env, adj);
	int m = SETWORDSNEEDED(n);
	nauty_check(WORDSIZE, m, n, NAUTYVERSIONID);

	DYNALLOC1(int, labels, labels_sz,n, "malloc");
	DYNALLOC1(int, ptn, ptn_sz, n, "malloc");
	DYNALLOC1(int, orbits, orbits_sz, n, "malloc");
	DYNALLOC2(graph, input, input_sz, n, m, "malloc");
	EMPTYGRAPH(input, m, n);

	for(int i = 0; i < len; i++){
		jintArray row = (jintArray)((*env)->GetObjectArrayElement(env, adj, i));
		jsize rlen = (*env)->GetArrayLength(env, row);
		jint* elem = (*env)->GetIntArrayElements(env, row, 0);

		for(int j = 0; j < rlen; j++){
			ADDONEEDGE(input, i, elem[j], m);
		}
	}

	struct timespec mid;
	clock_gettime(CLOCK_MONOTONIC_RAW, &mid);

	//compute canonical form and labeling
	DYNALLSTAT(graph, canon ,canon_sz);
	DYNALLOC2(graph, canon, canon_sz, n, m, "malloc");
	densenauty(input, labels, ptn, orbits, &options, &stats, m, n, canon);

	struct timespec end;
	clock_gettime(CLOCK_MONOTONIC_RAW, &end);

	//return times
	jlongArray result = (*env)->NewLongArray(env, 2);

	jlong data[2];
	data[0] = totalTime(&start, &mid);
	data[1] = totalTime(&mid, &end);
	(*env)->SetLongArrayRegion(env, result, 0, 2, data);

	return result;
}

//sparse nauty
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanonSparse(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	struct timespec start;
	clock_gettime(CLOCK_MONOTONIC_RAW, &start);
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

	struct timespec mid;
	clock_gettime(CLOCK_MONOTONIC_RAW, &mid);

	//compute canonical form and labeling
	SG_DECL(canon);
	sparsenauty(&graph, labels, ptn, orbits, &options, &stats, &canon);

	struct timespec end;
	clock_gettime(CLOCK_MONOTONIC_RAW, &end);

	//return times
	jlongArray result = (*env)->NewLongArray(env, 2);

	jlong data[2];
	data[0] = totalTime(&start, &mid);
	data[1] = totalTime(&mid, &end);
	(*env)->SetLongArrayRegion(env, result, 0, 2, data);

	return result;
}
