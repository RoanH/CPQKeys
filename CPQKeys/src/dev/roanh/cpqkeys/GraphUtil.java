package dev.roanh.cpqkeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;

public class GraphUtil{

	public static final Graph<VertexData<GraphNode<Object, Void>>, Void> numberVertices(Graph<Object, Void> in){
		Graph<VertexData<GraphNode<Object, Void>>, Void> out = new Graph<VertexData<GraphNode<Object, Void>>, Void>();
		Map<GraphNode<Object, Void>, GraphNode<VertexData<GraphNode<Object, Void>>, Void>> nodeMap = new HashMap<GraphNode<Object, Void>, GraphNode<VertexData<GraphNode<Object, Void>>, Void>>();
		
		List<GraphNode<Object, Void>> nodes = in.getNodes();
		for(int i = 0; i < nodes.size(); i++){
			VertexData<GraphNode<Object, Void>> vertex = new VertexData<GraphNode<Object, Void>>(i, nodes.get(i));
			nodeMap.put(vertex.getData(), out.addUniqueNode(vertex));
		}
		
		for(Entry<GraphNode<Object, Void>, GraphNode<VertexData<GraphNode<Object, Void>>, Void>> entry : nodeMap.entrySet()){
			for(GraphEdge<Object, Void> edge : entry.getKey().getOutEdges()){
				entry.getValue().addUniqueEdgeTo(nodeMap.get(edge.getTargetNode()));
			}
		}
		
		return out;
	}
}
