package dev.roanh.cpqkeys.algo;

import java.util.List;

import dev.roanh.cpqkeys.GraphUtil.ColoredGraph;
import dev.roanh.cpqkeys.GraphUtil.NumberedGraph;

public class Nishe{
	
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
	
	public static <T> long[] computeCanon(NumberedGraph<T> graph){
		long start = System.nanoTime();
		
		ColoredGraph<T> cg = graph.toColoredGraph();
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
		return new long[]{times[0], times[1], end - start};
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
	 * @return An array with two elements, first the time in milliseconds it
	 *         took to construct the partition nest and graph and second the
	 *         time in milliseconds it took to compute the refinement trace.
	 */
	private static native long[] computeCanon(int[][] adj, int[] colors);
}
