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

import java.util.List;
import java.util.function.BiFunction;

import dev.roanh.cpqkeys.Algorithm;
import dev.roanh.cpqkeys.GraphUtil;
import dev.roanh.cpqkeys.GraphUtil.ColoredGraph;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Util;

public class Nauty{
	public static final Algorithm DENSE = new Algorithm("Nauty (dense)", g->runNauty(g, Nauty::computeCanonDense));
	public static final Algorithm SPARSE = new Algorithm("Nauty (sparse)", g->runNauty(g, Nauty::computeCanonSparse));
	
	//edge labels -> nodes
	//number vertices
	//vertex coloured graph
	private static long[] runNauty(Graph<Vertex, Predicate> input, BiFunction<int[][], int[], long[]> version){
		long start = System.nanoTime();
		ColoredGraph graph = GraphUtil.numberVertices(Util.edgeLabelsToNodes(input)).toColoredGraph();
		int[] colors = prepareColors(graph);
		long end = System.nanoTime();
		
		long[] times = version.apply(graph.getAdjacencyMatrix(), colors);
		return new long[]{end - start, times[0], times[1]};
	}
	
	protected static int[] prepareColors(ColoredGraph graph){
		int[] colors = new int[graph.getNodeCount()];
		int idx = 0;
		for(List<Integer> group : graph.getColorMap()){
			for(int i = 0; i < group.size() - 1; i++){
				colors[idx++] = group.get(i) + 1;
			}
			colors[idx++] = -group.get(group.size() - 1) - 1;
		}
		return colors;
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
