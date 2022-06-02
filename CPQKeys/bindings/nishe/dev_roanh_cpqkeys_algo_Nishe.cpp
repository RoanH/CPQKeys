#include <dev_roanh_cpqkeys_algo_Nishe.h>
#include <nishe/PartitionNest.h>
#include <nishe/Refiner-inl.h>
#include <nishe/DirectedGraph.h>
#include <sstream>
#include <chrono>

using namespace nishe;
using namespace std::chrono;

/**
 * Simple test function that double the input integer, only
 * used to validate that the native library linked and compiled successfully.
 * @param env JNI environment.
 * @param obj Calling class.
 * @param num The input number to double.
 * @return The input number times two.
 */
JNIEXPORT jint JNICALL Java_dev_roanh_cpqkeys_algo_Nishe_test(JNIEnv* env, jclass obj, jint num){
	return 2 * num;
}

/**
 * Computes the canonical trace value for the given graph with the given vertex coloring
 */
JNIEXPORT jintArray JNICALL Java_dev_roanh_cpqkeys_algo_Nishe_computeCanon(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
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

	std::cout << "in pi: " << pi << std::endl;
	steady_clock::time_point mid_time = steady_clock::now();

	//refine graph
	RefineTraceValue<DirectedGraph> trace;
	Refiner<DirectedGraph> refiner;

	refiner.refine(graph, &pi, &trace);

	steady_clock::time_point end_time = steady_clock::now();
	std::cout << "out pi: " << pi << std::endl;

	//return times
	jintArray result = env->NewIntArray(2);

	jint data[2];
	data[0] = duration<milliseconds>(mid_time - start_time).count();
	data[1] = duration<milliseconds>(end_time - mid_time).count();
	env->SetIntArrayRegion(result, 0, 2, data);

	return result;
}
