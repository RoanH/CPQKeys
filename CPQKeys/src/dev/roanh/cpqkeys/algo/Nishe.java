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
		int[] colors = new int[]{-1, 3, -2, 4, 5, -6};
		
		int[] times = computeCanon(graph, colors);
		
		System.out.println("prep time: " + times[0]);
		System.out.println("canon time: " + times[1]);
	}
	
	
	/**
	 * 
	 * @param num
	 * @return
	 */
	public static native int test(int num);
	
	//prep time, canon time
	private static native int[] computeCanon(int[][] adj, int[] colors);
}
