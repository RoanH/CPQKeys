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

public class Bliss{

	
	
	
	
	public static void test(){
		int[] edges = new int[]{
			0, 5,
			5, 2,
			2, 4,
			4, 1,
			1, 3,
			1, 6,
			6, 0,
			3, 1
		};
		
		int[] colors = new int[7];
		colors[4] = 0;
		colors[3] = 0;
		colors[2] = 1;
		colors[0] = 1;
		colors[1] = 1;
		colors[5] = 2;
		colors[6] = 3;
		
		long[] times = computeCanon(edges, colors);
		System.out.println("construct time: " + times[0]);
		System.out.println("runtime: " + times[1]);
	}
	
	/**
	 * Computes the canonical labelling of the given coloured graph. Returns
	 * the time in milliseconds required for computations.
	 * @param edges The input graph as a list of edges with two consecutive
	 *        indices representing the ID of the source and target vertex
	 *        of an edge respectively.
	 * @param colors An array with as length the number of vertices in the
	 *        graph and at each index for the vertex with the same ID as the
	 *        index its color (as an integer).
	 * @return An array with two elements, first the time in nanoseconds it
	 *         took to construct the graph and second the time in nanoseconds
	 *         it took to compute the canonical form of the graph.
	 */
	private static native long[] computeCanon(int[] edges, int[] colors);
}
