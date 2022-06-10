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
#include <dev_roanh_cpqkeys_algo_Bliss.h>
#include <graph.hh>
#include <chrono>

using namespace bliss;
using namespace std::chrono;

/**
 * Computes the canonical labeling of the given colored graph. Returns
 * the time in nanoseconds required for computations.
 * @param env JNI environment.
 * @param obj Calling class.
 * @param edges The input graph as a list of edges with two consecutive
 *        indices representing the ID of the source and target vertex
 *        of an edge respectively.
 * @param colors An array with as length the number of vertices in the
 *        graph and at each index for the vertex with the same ID as the
 *        index its color (as an integer).
 * @return An array with two elements, first the time in nanoseconds it
 *         took to construct the graph and second the time in nanoseconds
 *         it took to compute the canonical form of the graph.
 */
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Bliss_computeCanon(JNIEnv* env, jclass obj, jintArray edges, jintArray colors){
	steady_clock::time_point start_time = steady_clock::now();

	//construct the graph
	int size = env->GetArrayLength(colors);
	Graph* graph = new Graph(size);

	int len = env->GetArrayLength(edges);
	jint* elem = env->GetIntArrayElements(edges, 0);
	for(int i = 0; i < len; i += 2){
		graph->add_edge(elem[i], elem[i + 1]);
	}
	env->ReleaseIntArrayElements(edges, elem, 0);

	elem = env->GetIntArrayElements(colors, 0);
	for(int i = 0; i < size; i++){
		graph->change_color(i, elem[i]);
	}
	env->ReleaseIntArrayElements(colors, elem, 0);

	steady_clock::time_point mid_time = steady_clock::now();

	//compute canonical form
	Stats stats;
	const unsigned int* canon = graph->canonical_form(stats, 0, (void*)0);

	steady_clock::time_point end_time = steady_clock::now();

	//return times
	jlongArray result = env->NewLongArray(2);

	jlong data[2];
	data[0] = duration_cast<nanoseconds>(mid_time - start_time).count();
	data[1] = duration_cast<nanoseconds>(end_time - mid_time).count();
	env->SetLongArrayRegion(result, 0, 2, data);

	return result;
}
