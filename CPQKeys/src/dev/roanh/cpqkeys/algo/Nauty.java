package dev.roanh.cpqkeys.algo;

public class Nauty{
	
	
	
	
	
	
	
	
	public static void test(){
		int[][] graph = new int[][]{
			{2},
			{1, 2}
		};
		
		computeCanon(graph);
	}

	public static native int test(int num);
	
	public static native void computeCanon(int[][] adj);
}
