package dev.roanh.cpqkeys.algo;

public class Nauty{
	
	
	
	
	
	
	
	
	public static void test(){
		int[][] graph = new int[][]{
			{1},
			{0, 1}
		};
		
		computeCanon(graph);
	}

	public static native int test(int num);
	
	private static native void computeCanon(int[][] adj);
}
