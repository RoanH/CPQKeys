#include <dev_roanh_cpqkeys_algo_Nauty.h>
#include <core.h>

//dense nauty
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanonSparse(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	struct timespec start;
	clock_gettime(CLOCK_MONOTONIC_RAW, &start);

	//TODO setup

	struct timespec mid;
	clock_gettime(CLOCK_MONOTONIC_RAW, &mid);

	//compute canonical form and labeling
	//TODO

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
