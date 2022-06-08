package dev.roanh.cpqkeys.algo;

public class Nauty{
	
	
	
	
	
	
	
	
	public static void test(){
//		int[][] graph = new int[][]{
//			{1},
//			{0, 1}
//		};
		
		int[][] graph = new int[][]{
			{1},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1},
			{0, 1}
		};
		
		//+1 it all so negation can be used to denote range ends
		int[] colors = new int[]{1, -3, 2, 4, -5, -6};
		
		long[] times = computeCanonDense(graph, colors);
		System.out.println("construct time: " + times[0]);
		System.out.println("runtime: " + times[1]);
	}
	
	

	private static native long[] computeCanonSparse(int[][] adj, int[] colors);
	
	private static native long[] computeCanonDense(int[][] adj, int[] colors);
}
