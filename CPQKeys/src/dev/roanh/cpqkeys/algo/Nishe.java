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

import dev.roanh.cpqkeys.Algorithm;
import dev.roanh.cpqkeys.GraphUtil;
import dev.roanh.cpqkeys.GraphUtil.ColoredGraph;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Util;

/**
 * Binding for Nishe.
 * @author Roan
 * @see <a href="https://code.google.com/archive/p/nishe/">Nishe website</a>
 * @see <a href="https://github.com/b0ri5/nishe-googlecode">Nishe GitHub mirror</a>
 * @see <a href="https://stars.library.ucf.edu/etd/4004/">Nishe Thesis</a>
 */
public class Nishe{
	/**
	 * Algorithm binding for Nishe.
	 */
	public static final Algorithm INSTANCE = new Algorithm("Nishe", Nishe::computeCanon);
	
	/**
	 * Runs Nishe on the given input graph. The input graph first
	 * has its edge labels converted to nodes, then has its nodes
	 * numbered and is finally converted to a coloured graph.
	 * @param input The input graph.
	 * @return An array of time measurements containing in the first
	 *         index the graph transform time, in the second index the
	 *         native setup time (graph construction) and in the third
	 *         index the canonization time. All times are in nanoseconds.
	 */
	private static long[] computeCanon(Graph<Vertex, Predicate> input){
		long start = System.nanoTime();
		
		ColoredGraph cg = GraphUtil.numberVertices(Util.edgeLabelsToNodes(input)).toColoredGraph();
		int[] colors = new int[cg.getNodeCount()];
		int idx = 0;
		for(List<Integer> group : cg.getColorMap()){
			group.sort(null);
			colors[idx++] = -group.get(0) - 1;
			for(int i = 1; i < group.size(); i++){
				colors[idx++] = group.get(i) + 1;
			}
		}
		
		long end = System.nanoTime();
		
		long[] times = computeCanon(cg.getAdjacencyMatrix(), colors);
		return new long[]{
			end - start,
			times[0],
			times[1]
		};
	}
	
	/**
	 * Computes the canonical trace value and new partition nest for the
	 * given graph with the given vertex colouring. Returns the time in
	 * milliseconds required for computations.
	 * @param adj The input graph in adjacency list format, <code>n</code>
	 *        arrays with each the indices of the neighbours of the <code>
	 *        n</code>-th vertex.
	 * @param colors An array defining the color of each vertex, the array
	 *        is expected to contain the indices of the vertices with the
	 *        same color in the following format. The index used to refer
	 *        to a vertex is always one higher than the actual index in the
	 *        adjacency list. A negative index indicates the start of a new
	 *        color group with all following vertices being of the same color.
	 *        Vertices within each color group are expected to be in sorted
	 *        ascending order.
	 * @return An array with two elements, first the time in nanoseconds it
	 *         took to construct the partition nest and graph and second the
	 *         time in nanoseconds it took to compute the refinement trace.
	 */
	private static native long[] computeCanon(int[][] adj, int[] colors);
}
