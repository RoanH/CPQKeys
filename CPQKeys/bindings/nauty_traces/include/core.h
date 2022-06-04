#ifndef include_dev_roanh_cpqkeys_core
#define include_dev_roanh_cpqkeys_core

#include "nausparse.h"

void constructSparseGraph(JNIEnv*, jobjectArray*, sparsegraph*);

void parseColoring(JNIEnv*, int, jintArray*, int*, int*);

#endif