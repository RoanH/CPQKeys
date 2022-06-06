#ifndef include_dev_roanh_cpqkeys_core
#define include_dev_roanh_cpqkeys_core

#include <jni.h>
#include <nausparse.h>
#include <time.h>

void constructSparseGraph(JNIEnv*, jobjectArray*, sparsegraph*);

void parseColoring(JNIEnv*, int, jintArray*, int*, int*);

jlong totalTime(struct timespec*, struct timespec*);

#endif
