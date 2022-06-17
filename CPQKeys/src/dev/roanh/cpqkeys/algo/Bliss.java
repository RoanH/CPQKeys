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

import java.util.HashMap;
import java.util.Map;

import dev.roanh.cpqkeys.Algorithm;
import dev.roanh.cpqkeys.GraphUtil;
import dev.roanh.cpqkeys.GraphUtil.NumberedGraph;
import dev.roanh.cpqkeys.VertexData;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.DataProxy;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;
import dev.roanh.gmark.util.Util;

/**
 * Binding for Bliss.
 * @author Roan
 * @see <a href="http://www.tcs.hut.fi/Software/bliss/">Bliss website</a>
 */
public class Bliss{
	/**
	 * Algorithm binding for bliss.
	 */
	public static final Algorithm INSTANCE = new Algorithm("Bliss", Bliss::computeCanon);
	
	/**
	 * Runs Bliss on the given input graph. The input graph first
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
		NumberedGraph<Object, Void> graph = GraphUtil.numberVertices(Util.edgeLabelsToNodes(input));

		//map edges
		int[] edges = new int[graph.getEdgeCount() * 2];
		int idx = 0;
		for(GraphEdge<VertexData<GraphNode<Object, Void>>, Void> edge : graph.getEdges()){
			edges[idx * 2] = edge.getSource().getID();
			edges[idx * 2 + 1] = edge.getTarget().getID();
		}
		
		//map colours
		Map<Object, Integer> colorCache = new HashMap<Object, Integer>();
		int[] colors = new int[graph.getNodeCount()];
		for(GraphNode<VertexData<GraphNode<Object, Void>>, Void> node : graph.getNodes()){
			Object data = node.getData().getData().getData();
			if(data instanceof DataProxy){
				data = ((DataProxy<?>)data).getData();
				colors[node.getData().getID()] = colorCache.computeIfAbsent(data, obj->colorCache.size() + 1);
			}else{
				colors[node.getData().getID()] = 0;
			}
		}
		
		long end = System.nanoTime();
		
		long[] times = computeCanon(edges, colors);
		return new long[]{
			end - start,
			times[0],
			times[1]
		};
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
