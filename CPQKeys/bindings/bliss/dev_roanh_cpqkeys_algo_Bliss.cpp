#include <dev_roanh_cpqkeys_algo_Bliss.h>
#include <graph.hh>
#include <chrono>

using namespace std::chrono;

JNIEXPORT jlongArray JNICALL Java_dev_roanh_cpqkeys_algo_Bliss_computeCanon(JNIEnv* env, jclass obj, jintArray edges, jintArray colors){
	steady_clock::time_point start_time = steady_clock::now();

	//TODO prep

	steady_clock::time_point mid_time = steady_clock::now();

	//TODO canon

	steady_clock::time_point end_time = steady_clock::now();

	//return times
	jlongArray result = env->NewLongArray(2);

	jlong data[2];
	data[0] = duration_cast<nanoseconds>(mid_time - start_time).count();
	data[1] = duration_cast<nanoseconds>(end_time - mid_time).count();
	env->SetLongArrayRegion(result, 0, 2, data);

	return result;
}
