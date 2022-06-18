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
package dev.roanh.cpqkeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.roanh.gmark.util.DataProxy;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;
import dev.roanh.gmark.util.Util;

/**
 * Various utilities for transforming graphs.
 * @author Roan
 * @see Util
 */
public class GraphUtil{
	
	/**
	 * Converts the given directed input graph to a graph where all directed edges
	 * are instead represented by labelled vertices. Essentially, for each edge <code>
	 * (a) --b--&gt; (c)</code> the edge will be turned into three edges with and two
	 * new nodes. If the edge is labelled then the middle of the three new edges will
	 * inherit the label. The final result would looks as follows  <code> (a) -- (tail)
	 * --b-- (head) -- (c)</code>. Thus this transform triples the number of edges in the
	 * graph and adds two new nodes for every edge that used to be in the old graph. The
	 * returned graph has {@link Object} as the vertex data type. There are two options
	 * for the actual class of these vertex data objects. Either they are a vertex data
	 * object from the old graph and thus of generic type V. Or they are a {@link DataProxy}
	 * instance wrapping either the string "head" or "tail". It is worth noting that the
	 * returned graph instance is still technically a directed graph as {@link Graph}
	 * instances are always directed, but it can be treated as undirected. For performance
	 * reasons only the middle of the three new edges inherits the former edge label.
	 * This also keeps the growth of the graph to a minimum when the
	 * {@link Util#edgeLabelsToNodes(Graph)} is also used.
	 * @param <V> The vertex data type.
	 * @param <E> The edge label data type.
	 * @param in The input graph to transform.
	 * @return The transformed undirected graph.
	 * @see <a href="https://cpqkeys.roanh.dev/notes/to_undirected">Notes on transforming directed graphs to undirected graphs</a>
	 */
	public static <V, E> Graph<Object, E> toUndirectedGraph(Graph<V, E> in){
		Graph<Object, E> out = new Graph<Object, E>();
		
		in.getNodes().forEach(node->out.addUniqueNode(node.getData()));
		for(GraphEdge<V, E> edge : in.getEdges()){
			GraphNode<Object, E> head = out.addUniqueNode(new DataProxy<String>("head"));
			GraphNode<Object, E> tail = out.addUniqueNode(new DataProxy<String>("tail"));
			
			tail.addUniqueEdgeFrom(edge.getSource());
			tail.addUniqueEdgeTo(head, edge.getData());
			head.addUniqueEdgeTo(edge.getTarget());
		}
		
		return out;
	}
	
	/**
	 * Converts the given input graph to a coloured graph instance
	 * by grouping vertices with their colour determined by the
	 * content of their {@link DataProxy} instance. All vertex data
	 * objects that are not DataProxy instances are given the same colour.
	 * @param <V> The vertex data type.
	 * @param <E> The edge label data type.
	 * @param graph The input graph to transform.
	 * @return The constructed coloured graph.
	 * @see ColoredGraph
	 */
	public static <V, E> ColoredGraph toColoredGraph(Graph<V, E> graph){
		Map<Object, List<Integer>> colorMap = new HashMap<Object, List<Integer>>();
		int[][] adj = graph.toAdjacencyList();
		final Object nolabel = new Object();
		
		for(GraphNode<V, E> node : graph.getNodes()){
			V data = node.getData();
			if(data instanceof DataProxy){
				colorMap.computeIfAbsent(((DataProxy<?>)data).getData(), k->new ArrayList<Integer>()).add(node.getID());
			}else{
				colorMap.computeIfAbsent(nolabel, k->new ArrayList<Integer>()).add(node.getID());
			}
		}
		
		return new ColoredGraph(adj, colorMap.values());
	}
	
	/**
	 * Represents a coloured graph.
	 * @author Roan
	 * @see GraphUtil#toColoredGraph(Graph)
	 */
	public static class ColoredGraph{
		/**
		 * The adjacency list representing the graph.
		 */
		private int[][] graph;
		/**
		 * A collection of lists where each list has the
		 * IDs of nodes with the same colour.
		 */
		private Collection<List<Integer>> colorMap;
		
		/**
		 * Constructs a new coloured graph with the given
		 * adjacency list and colour information.
		 * @param adj The adjacency list of the graph.
		 * @param colors The colour information.
		 */
		private ColoredGraph(int[][] adj, Collection<List<Integer>> colors){
			graph = adj;
			colorMap = colors;
		}
		
		/**
		 * Gets the total number of nodes in this graph.
		 * @return The total number of nodes in this graph.
		 */
		public int getNodeCount(){
			return graph.length;
		}
		
		/**
		 * Gets the colour map for this coloured graph, each
		 * list in the returned collection contains the IDs
		 * of nodes with the same colour.
		 * @return The colour map for this coloured graph.
		 */
		public Collection<List<Integer>> getColorMap(){
			return colorMap;
		}
		
		/**
		 * Gets the adjacency list representation of this graph.
		 * @return The adjacency list representation of this graph.
		 */
		public int[][] getAdjacencyList(){
			return graph;
		}
	}
}
