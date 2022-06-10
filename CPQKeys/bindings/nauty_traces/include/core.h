#ifndef include_dev_roanh_cpqkeys_core
#define include_dev_roanh_cpqkeys_core

#include <jni.h>
#include <nausparse.h>
#include <time.h>

#ifdef __CYGWIN__
#define CLOCK_MONOTONIC_RAW CLOCK_MONOTONIC
#endif

/**
 * Constructs a sparse graph from the given adjacency list
 * representation of a graph.
 */
void constructSparseGraph(JNIEnv*, jobjectArray*, sparsegraph*);

/**
 * Constructs the graph coloring information arrays 'labels' and 'ptn'
 * from the given color data array.
 */
void parseColoring(JNIEnv*, int, jintArray*, int*, int*);

/**
 * Computes the time in nanoseconds between the given start and end time.
 */
jlong totalTime(struct timespec*, struct timespec*);

#endif
