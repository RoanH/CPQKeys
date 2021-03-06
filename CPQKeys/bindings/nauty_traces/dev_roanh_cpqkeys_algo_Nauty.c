/*
 * CPQKeys: An evaluation of various graph canonization algorithms.
 * Copyright (C) 2022  Roan Hofland (roan@roanh.dev).  All rights reserved.
 * GitHub Repository: https://github.com/RoanH/CPQKeys
 *
 * CPQKeys is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CPQKeys is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#include <dev_roanh_cpqkeys_algo_Nauty.h>
#include <nautinv.h>
#include <core.h>

/**
 * Computes the canonical form of the given colored graph using the dense
 * version of nauty. Returns the time in nanoseconds required for computations.
 * @param The JNI environment.
 * @param Calling class.
 * @param adj The input graph in adjacency list format, n arrays with
 *        each the indices of the neighbors of the n-th vertex.
 * @param colors The array containing raw color information data. Contains vertex
 *        indices in blocks of the same color with the start of a block of the same
 *        color being indicated by a negated value. All vertex indices are also always
 *        one higher than their actual index in the graph.
 * @return An array with two elements, first the time in nanoseconds it
 *         took to construct the graph and second the time in nanoseconds
 *         it took to compute the canonical form of the graph.
 */
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanonDense(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	struct timespec start;
	clock_gettime(CLOCK_MONOTONIC_RAW, &start);

	DYNALLSTAT(int, labels, labels_sz);
	DYNALLSTAT(int, ptn, ptn_sz);
	DYNALLSTAT(int, orbits, orbits_sz);
	DYNALLSTAT(graph, input, input_sz);

	static DEFAULTOPTIONS_DIGRAPH(options);
	statsblk stats;
	options.getcanon = TRUE;
	options.defaultptn = FALSE;

	int n = (*env)->GetArrayLength(env, adj);
	int m = SETWORDSNEEDED(n);
	nauty_check(WORDSIZE, m, n, NAUTYVERSIONID);

	DYNALLOC1(int, labels, labels_sz, n, "malloc");
	DYNALLOC1(int, ptn, ptn_sz, n, "malloc");
	DYNALLOC1(int, orbits, orbits_sz, n, "malloc");
	DYNALLOC2(graph, input, input_sz, n, m, "malloc");
	EMPTYGRAPH(input, m, n);

	parseColoring(env, n, &colors, labels, ptn);

	for(int i = 0; i < n; i++){
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
	DYNALLSTAT(graph, canon, canon_sz);
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

/**
 * Computes the canonical form of the given colored graph using the sparse
 * version of nauty. Returns the time in nanoseconds required for computations.
 * @param The JNI environment.
 * @param Calling class.
 * @param adj The input graph in adjacency list format, n arrays with
 *        each the indices of the neighbors of the n-th vertex.
 * @param colors The array containing raw color information data. Contains vertex
 *        indices in blocks of the same color with the start of a block of the same
 *        color being indicated by a negated value. All vertex indices are also always
 *        one higher than their actual index in the graph.
 * @return An array with two elements, first the time in nanoseconds it
 *         took to construct the graph and second the time in nanoseconds
 *         it took to compute the canonical form of the graph.
 */
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Nauty_computeCanonSparse(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	struct timespec start;
	clock_gettime(CLOCK_MONOTONIC_RAW, &start);
	SG_DECL(graph);

	constructSparseGraph(env, &adj, &graph);

	DYNALLSTAT(int, labels, labels_sz);
	DYNALLSTAT(int, ptn, ptn_sz);
	DYNALLSTAT(int, orbits, orbits_sz);

	static DEFAULTOPTIONS_SPARSEDIGRAPH(options);
	statsblk stats;
	options.getcanon = TRUE;
	options.defaultptn = FALSE;

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
