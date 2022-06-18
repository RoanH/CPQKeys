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

public class GraphUtil{
	
	//technically still directed but edges need to be treated as undirected
	//note, only leaving the label on one edge to reduce graph size increase
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
	
	public static <V, E> ColoredGraph  toColoredGraph(Graph<V, E> graph){
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
	
	public static class ColoredGraph{
		private int[][] graph;
		//each list has the ids of same color nodes
		private Collection<List<Integer>> colorMap;
		
		private ColoredGraph(int[][] adj, Collection<List<Integer>> colors){
			graph = adj;
			colorMap = colors;
		}
		
		public int getNodeCount(){
			return graph.length;
		}
		
		public Collection<List<Integer>> getColorMap(){
			return colorMap;
		}
		
		public int[][] getAdjacencyMatrix(){
			return graph;
		}
	}
}
