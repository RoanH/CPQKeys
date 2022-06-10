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
package dev.roanh.cpqkeys.algo;

public class Nauty{
	
	
	
	
	
	
	
	
	public static void test(){
//		int[][] graph = new int[][]{
//			{1},
//			{0, 1}
//		};
		
		int[][] graph = new int[][]{
			{1},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1}
		};
		
		//+1 it all so negation can be used to denote range ends
		int[] colors = new int[]{1, -3, 2, 4, -5, -6};
		
		long[] times = computeCanonDense(graph, colors);
		System.out.println("construct time: " + times[0]);
		System.out.println("runtime: " + times[1]);
	}
	
	
	/**
	 * Computes the canonical form of the given coloured graph using the sparse
	 * version of nauty. Returns the time in nanoseconds required for computations.
	 * @param adj The input graph in adjacency list format, <code>n</code>
	 *        arrays with each the indices of the neighbours of the <code>
	 *        n</code>-th vertex.
	 * @param colors The array containing raw color information data. Contains vertex
	 *        indices in blocks of the same color with the start of a block of the same
	 *        color being indicated by a negated value. All vertex indices are also always
	 *        one higher than their actual index in the graph.
	 * @return An array with two elements, first the time in nanoseconds it
	 *         took to construct the graph and second the time in nanoseconds
	 *         it took to compute the canonical form of the graph.
	 */
	private static native long[] computeCanonSparse(int[][] adj, int[] colors);
	
	/**
	 * Computes the canonical form of the given coloured graph using the dense
	 * version of nauty. Returns the time in nanoseconds required for computations.
	 * @param adj The input graph in adjacency list format, <code>n</code>
	 *        arrays with each the indices of the neighbours of the <code>
	 *        n</code>-th vertex.
	 * @param colors The array containing raw color information data. Contains vertex
	 *        indices in blocks of the same color with the start of a block of the same
	 *        color being indicated by a negated value. All vertex indices are also always
	 *        one higher than their actual index in the graph.
	 * @return An array with two elements, first the time in nanoseconds it
	 *         took to construct the graph and second the time in nanoseconds
	 *         it took to compute the canonical form of the graph.
	 */
	private static native long[] computeCanonDense(int[][] adj, int[] colors);
}
