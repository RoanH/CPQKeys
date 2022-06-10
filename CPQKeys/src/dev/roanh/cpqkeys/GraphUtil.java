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
import java.util.Map.Entry;

import dev.roanh.gmark.util.DataProxy;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;

public class GraphUtil{

	//TODO consider moving this to gmark
	public static final <T> NumberedGraph<T> numberVertices(Graph<T, Void> in){
		NumberedGraph<T> out = new NumberedGraph<T>();
		Map<GraphNode<T, Void>, GraphNode<VertexData<GraphNode<T, Void>>, Void>> nodeMap = new HashMap<GraphNode<T, Void>, GraphNode<VertexData<GraphNode<T, Void>>, Void>>();
		
		List<GraphNode<T, Void>> nodes = in.getNodes();
		for(int i = 0; i < nodes.size(); i++){
			VertexData<GraphNode<T, Void>> vertex = new VertexData<GraphNode<T, Void>>(i, nodes.get(i));
			nodeMap.put(vertex.getData(), out.addUniqueNode(vertex));
		}
		
		for(Entry<GraphNode<T, Void>, GraphNode<VertexData<GraphNode<T, Void>>, Void>> entry : nodeMap.entrySet()){
			for(GraphEdge<T, Void> edge : entry.getKey().getOutEdges()){
				entry.getValue().addUniqueEdgeTo(nodeMap.get(edge.getTargetNode()));
			}
		}
		
		return out;
	}
	
	//public static final

	public static class NumberedGraph<T> extends Graph<VertexData<GraphNode<T, Void>>, Void>{
		private static final Object DEFAULT = new Object();
		
		public ColoredGraph toColoredGraph(){
			Map<Object, List<Integer>> colorMap = new HashMap<Object, List<Integer>>();
			
			int[][] adj = new int[getNodeCount()][];
			for(GraphNode<VertexData<GraphNode<T, Void>>, Void> node : getNodes()){
				adj[node.getData().getID()] = node.getOutEdges().stream().map(GraphEdge::getTargetNode).map(GraphNode::getData).mapToInt(VertexData::getID).toArray();
				T data = node.getData().getData().getData();
				if(data instanceof DataProxy){
					colorMap.computeIfAbsent(((DataProxy<?>)data).getData(), k->new ArrayList<Integer>()).add(node.getData().getID());
				}else{
					colorMap.computeIfAbsent(DEFAULT, k->new ArrayList<Integer>()).add(node.getData().getID());
				}
			}
			
			return new ColoredGraph(adj, colorMap.values());
		}
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
