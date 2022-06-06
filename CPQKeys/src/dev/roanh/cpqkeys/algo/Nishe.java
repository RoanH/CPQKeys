package dev.roanh.cpqkeys.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.roanh.cpqkeys.VertexData;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;

public class Nishe{
	//TODO may need to return runtimes as long since things go up fast if it is measured in nanos
	
	public static void test(){
		int[][] graph = new int[][]{
			{1},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1}
		};
		
		//individual levels need to be sorted, +1 it all so negation can be used to denote range starts
		int[] colors = new int[]{-1, 3, -2, 4, 5, -6};
		
		long[] times = computeCanon(graph, colors);
		
		System.out.println("prep time: " + times[0]);
		System.out.println("canon time: " + times[1]);
	}
	
	public static long[] computeCanon(Graph<VertexData<GraphNode<Object, Void>>, Void> graph){
		long start = System.nanoTime();
		Map<Object, List<Integer>> colorMap = new HashMap<Object, List<Integer>>();
		
		int[][] adj = new int[graph.getNodeCount()][];
		for(GraphNode<VertexData<GraphNode<Object, Void>>, Void> node : graph.getNodes()){
			adj[node.getData().getID()] = node.getOutEdges().stream().map(GraphEdge::getTargetNode).map(GraphNode::getData).mapToInt(VertexData::getID).toArray();
			colorMap.computeIfAbsent(node.getData().getData().getData(), k->new ArrayList<Integer>()).add(node.getData().getID());
		}
		
		colorMap.values().forEach(l->l.sort(Integer::compare));
		
		int[] colors = new int[adj.length];
		int idx = 0;
		for(List<Integer> group : colorMap.values()){
			colors[idx++] = -group.get(0) - 1;
			for(int i = 1; i < group.size(); i++){
				colors[idx++] = group.get(i);
			}
		}
		
		long end = System.nanoTime();
		
		long[] times = computeCanon(adj, colors);
		return new long[]{times[0], times[1], end - start};
	}
	
	/**
	 * Simple test function that double the input integer, only
	 * used to validate that the native library linked and compiled successfully.
	 * @param num The input number to double.
	 * @return The input number times two.
	 */
	public static native int test(int num);
	
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
	 * @return An array with two elements, first the time in milliseconds it
	 *         took to construct the partition nest and graph and second the
	 *         time in milliseconds it took to compute the refinement trace.
	 */
	private static native long[] computeCanon(int[][] adj, int[] colors);
}
