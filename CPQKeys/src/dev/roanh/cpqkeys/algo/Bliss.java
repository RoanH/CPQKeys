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
		
		long[] times = computeCanon(edges, colors);
		System.out.println("construct time: " + times[0]);
		System.out.println("runtime: " + times[1]);
	}
	
	/**
	 * Computes the canonical labelling of the given coloured graph. Returns
	 * the time in milliseconds required for computations.
	 * @param edges The input graph as a list of edges with two consecutive
	 *        indices representing the ID of the source and target vertex
	 *        of an edge respectively.
	 * @param colors An array with as length the number of vertices in the
	 *        graph and at each index for the vertex with the same ID as the
	 *        index its color (as an integer).
	 * @return An array with two elements, first the time in nanoseconds it
	 *         took to construct the graph and second the time in nanoseconds
	 *         it took to compute the canonical form of the graph.
	 */
	private static native long[] computeCanon(int[] edges, int[] colors);
}
