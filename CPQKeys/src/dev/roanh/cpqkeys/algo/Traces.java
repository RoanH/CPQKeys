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

import dev.roanh.cpqkeys.GraphUtil;
import dev.roanh.cpqkeys.GraphUtil.ColoredGraph;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Util;

public class Traces{

	
	
	
	
	
	public static void test(){
		int[][] graph = new int[][]{
			{1, 2, 3, 4, 5},
			{0, 1, 2, 3, 4, 5},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1}
		};
		
		//+1 it all so negation can be used to denote range ends
		int[] colors = new int[]{1, -3, 2, 4, -5, -6};
		
		long[] times = computeCanon(graph, colors);
		System.out.println("construct time: " + times[0]);
		System.out.println("runtime: " + times[1]);
	}
	
	//undirected
	//edge labels to nodes (minimises chain length)
	//number vertices
	//vertex colored graph
	private static long[] computeCanon(Graph<Vertex, Predicate> input){
		long start = System.nanoTime();
		ColoredGraph graph = GraphUtil.numberVertices(Util.edgeLabelsToNodes(GraphUtil.toUndirectedGraph(input))).toColoredGraph();
		int[][] adj = graph.getAdjacencyMatrix();
		
		//in degree
		int[] deg = new int[graph.getNodeCount()];
		for(int[] row : adj){
			for(int node : row){
				deg[node]++;
			}
		}
		
		//copy data
		int[] idx = new int[graph.getNodeCount()];
		for(int i = 0; i < adj.length; i++){
			int[] data = new int[adj[i].length + deg[i]];
			System.arraycopy(adj[i], 0, data, 0, adj[i].length);
			idx[i] = adj[i].length;
			deg[i] = adj[i].length;
			adj[i] = data;
		}
		
		//add all edges reversed (undirected edges are represented as bidirectional edges)
		for(int src = 0; src < adj.length; src++){
			for(int i = 0; i < deg[src]; i++){
				int node = adj[src][i];
				adj[node][idx[node]++] = src;
			}
		}
		
		int[] colors = Nauty.prepareColors(graph);
		long end = System.nanoTime();
		
		
		
		return null;//TODO
	}
	
	//NOTE: Traces REQUIRES an undirected graph so make sure to add all edges in both directions
	/**
	 * Computes the canonical form of the given coloured graph using traces. Returns
	 * the time in nanoseconds required for computations.
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
	private static native long[] computeCanon(int[][] adj, int[] colors);
}
