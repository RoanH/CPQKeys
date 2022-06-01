#include <dev_roanh_cpqkeys_algo_Nishe.h>
#include <nishe/PartitionNest.h>
#include <nishe/Refiner-inl.h>
#include <nishe/DirectedGraph.h>
#include <sstream>

using namespace nishe;

JNIEXPORT jint JNICALL Java_dev_roanh_cpqkeys_algo_Nishe_test(JNIEnv* env, jclass obj, jint num){
	return 2 * num;
}

JNIEXPORT void JNICALL Java_dev_roanh_cpqkeys_algo_Nishe_computeCanon(JNIEnv* env, jclass obj, jobjectArray adj, jintArray colors){
	//construct pi
	PartitionNest pi;

	jsize len = env->GetArrayLength(adj);
	pi.unit(len);//TODO maybe not required

	jint* colorData = env->GetIntArrayElements(colors, 0);
	for(int i = 0; i < len; i++){
		if(colorData[i] < 0){
			pi.enqueue_new_index(-colorData[i] - 1);
			pi.advance_level();
			pi.commit_pending_indices();
		}else{
			pi.enqueue_new_index(colorData[i] - 1);
		}
	}
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

	//verify
	std::cout << "pi: " << pi << std::endl;

}

int main(int argc, char** argv){


	//construct pi
	PartitionNest pi;


	//construct graph
	DirectedGraph graph;

	//TODO various add_arc(u, v) calls (ints), 0-indexed


	//refine graph
	RefineTraceValue<DirectedGraph> trace;
	Refiner<DirectedGraph> refiner;

	refiner.refine(graph, &pi, &trace);

	//in theory do something with the reordered pi and refinement trace


}
