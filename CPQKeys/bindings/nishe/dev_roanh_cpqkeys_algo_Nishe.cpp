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
#include <dev_roanh_cpqkeys_algo_Nishe.h>
#include <nishe/PartitionNest.h>
#include <nishe/Refiner-inl.h>
#include <nishe/DirectedGraph.h>
#include <chrono>

using namespace nishe;
using namespace std::chrono;

/**
 * Computes the canonical trace value and new partition nest for the
 * given graph with the given vertex coloring. Returns the time in
 * nanoseconds required for computations.
 * @param env JNI environment.
 * @param obj Calling class.
 * @param adj The input graph in adjacency list format, n arrays with
 *        each the indices of the neighbors of the n-th vertex.
 * @param colors An array defining the color of each vertex, the array
 *        is expected to contain the indices of the vertices with the
 *        same color in the following format. The index used to refer
 *        to a vertex is always one higher than the actual index in the
 *        adjacency list. A negative index indicates the start of a new
 *        color group with all following vertices being of the same color.
 *        Vertices within each color group are expected to be in sorted
 *        ascending order.
 * @return An array with two elements, first the time in nanoseconds it
 *         took to construct the partition nest and graph and second the
 *         time in nanoseconds it took to compute the refinement trace.
 */
JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Nishe_computeCanon(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	steady_clock::time_point start_time = steady_clock::now();

	//construct pi
	PartitionNest pi;

	jsize len = env->GetArrayLength(adj);
	pi.unit(len);

	jint* colorData = env->GetIntArrayElements(colors, 0);
	for(int i = 0; i < len; i++){
		if(colorData[i] < 0){
			pi.elements()[i] = -colorData[i] - 1;
			if(i != 0){
				pi.enqueue_new_index(i);
			}
		}else{
			pi.elements()[i] = colorData[i] - 1;
		}
	}
	pi.commit_pending_indices();
	env->ReleaseIntArrayElements(colors, colorData, 0);

	//construct graph
	DirectedGraph graph;
	for(int u = 0; u < len; u++){
		jintArray row = (jintArray)(env->GetObjectArrayElement(adj, u));
		jsize rlen = env->GetArrayLength(row);
		jint* elem = env->GetIntArrayElements(row, 0);

		for(int i = 0; i < rlen; i++){
			graph.add_arc(u, elem[i]);
		}

		env->ReleaseIntArrayElements(row, elem, 0);
	}

	steady_clock::time_point mid_time = steady_clock::now();

	//refine graph
	RefineTraceValue<DirectedGraph> trace;
	Refiner<DirectedGraph> refiner;

	refiner.refine(graph, &pi, &trace);

	steady_clock::time_point end_time = steady_clock::now();

	//return times
	jlongArray result = env->NewLongArray(2);

	jlong data[2];
	data[0] = duration_cast<nanoseconds>(mid_time - start_time).count();
	data[1] = duration_cast<nanoseconds>(end_time - mid_time).count();
	env->SetLongArrayRegion(result, 0, 2, data);

	return result;
}
