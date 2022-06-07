#ifndef include_dev_roanh_cpqkeys_core
#define include_dev_roanh_cpqkeys_core

#include <jni.h>
#include <nausparse.h>
#include <time.h>

#ifdef __CYGWIN__
#define CLOCK_MONOTONIC_RAW CLOCK_MONOTONIC
#endif

void constructSparseGraph(JNIEnv*, jobjectArray*, sparsegraph*);

void parseColoring(JNIEnv*, int, jintArray*, int*, int*);

jlong totalTime(struct timespec*, struct timespec*);

#endif
