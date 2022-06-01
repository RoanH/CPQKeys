package dev.roanh.cpqkeys.algo;

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
		
		//individual levels need to be sorted, +1 it all so negation can be used to denote range ends
		int[] colors = new int[]{1, -3, 2, 4, -5, -6};
		
		computeCanon(graph, colors);
	}
	
	public static native int test(int num);
	
	private static native void computeCanon(int[][] adj, int[] colors);
}
