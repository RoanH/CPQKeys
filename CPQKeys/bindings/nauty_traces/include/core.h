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
