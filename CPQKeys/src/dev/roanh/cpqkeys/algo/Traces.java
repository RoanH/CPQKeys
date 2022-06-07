package dev.roanh.cpqkeys.algo;

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
	
	//NOTE: Traces REQUIRES an undirected graph so make sure to add all edges in both directions
	private static native long[] computeCanon(int[][] adj, int[] colors);
}
