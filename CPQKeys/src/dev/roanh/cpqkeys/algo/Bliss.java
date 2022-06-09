package dev.roanh.cpqkeys.algo;

public class Bliss{

	
	
	
	
	public static void test(){
		int[] edges = new int[]{
			0, 5,
			5, 2,
			2, 4,
			4, 1,
			1, 3,
			1, 6,
			6, 0,
			3, 1
		};
		
		int[] colors = new int[7];
		colors[4] = 0;
		colors[3] = 0;
		colors[2] = 1;
		colors[0] = 1;
		colors[1] = 1;
		colors[5] = 2;
		colors[6] = 3;
		
		computeCanon(edges, colors);
	}
	
	
	private static native long[] computeCanon(int[] edges, int[] colors);
}
