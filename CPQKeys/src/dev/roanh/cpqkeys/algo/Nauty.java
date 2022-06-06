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
		
		long[] times = computeCanon(graph, colors);
		System.out.println("construct time: " + times[0]);
		System.out.println("runtime: " + times[1]);
	}

	public static native int test(int num);
	
	private static native long[] computeCanon(int[][] adj, int[] colors);
}
