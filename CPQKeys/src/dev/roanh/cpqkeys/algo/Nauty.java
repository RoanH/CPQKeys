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
	
	

	private static native long[] computeCanonSparse(int[][] adj, int[] colors);
	
	private static native long[] computeCanonDense(int[][] adj, int[] colors);
}
