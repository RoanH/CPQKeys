package dev.roanh.cpqkeys;

/**
 * Report summarising various runtimes about an algorithm
 * @author Roan
 * @see Algorithm
 */
public class RuntimeReport{
	/**
	 * The algorithm these runtimes were measured for.
	 */
	private Algorithm algo;
	/**
	 * Setup time for the algorithm (nanoseconds).
	 */
	private long setupTime;
	/**
	 * Native setup time for the algorithm (nanoseconds).
	 */
	private long nativeSetupTime;
	/**
	 * Canonization time for the algorithm (nanoseconds)
	 */
	private long canonTime;
	/**
	 * Raw execution time (nanoseconds).
	 */
	private long totalTime;

	protected RuntimeReport(Algorithm algo, long[] times, long total){
		this.algo = algo;
		setupTime = times[0];
		nativeSetupTime = times[1];
		canonTime = times[2];
		totalTime = total;
	}
	
	/**
	 * Gets the time spent Java side transforming the input
	 * CPQ query graph into a form suitable for the algorithm.
	 * This includes transforms that transform the input into
	 * a form that can be passed to the algorithm. Major time
	 * sinks for this setup include graph transforms to make
	 * the graph undirected or without edge labels and the
	 * transform to turn the graph into a coloured graph.
	 * @return The setup time for this algorithm run in nanoseconds.
	 */
	public long getSetupTime(){
		return setupTime;
	}
	
	/**
	 * Gets the time spent on the side of the native implementation
	 * preparing all the input data for the canonization step. The
	 * major time sink for this step is reading the input graph into
	 * proper data structures.
	 * @return The native setup time for this algorithm run in nanoseconds.
	 */
	public long getNativeSetupTime(){
		return nativeSetupTime;
	}
	
	/**
	 * Gets the time spent by the algorithm computing the
	 * canonical representation of the input graph.
	 * @return The total canonization time in nanoseconds.s
	 */
	public long getCanonizationTime(){
		return canonTime;
	}
	
	/**
	 * Gets the total time spent running the
	 * {@link Algorithm#time(dev.roanh.gmark.util.Graph)} method.
	 * @return The total runtime for this algorithm in nanoseconds.
	 */
	public long getTotalTime(){
		return totalTime;
	}
	
	/**
	 * Gets the total time spend on other tasks. In general this is
	 * the total execution time minus the setup time, native setup
	 * time and canonization time. Main contributors to this measurement
	 * are JNI calls, Python startup times and general function calls.
	 * @return The time spent on other tasks for this algorithm run in nanoseconds.
	 */
	public long getOtherTime(){
		//possibly negative due to timer variance
		return Math.max(0, totalTime - setupTime - nativeSetupTime - canonTime);
	}
	
	public void print(){
		System.out.println("========== Runtime Report ==========");
		System.out.println("algo: " + algo.getName());
		System.out.println("setup: " + formatNanos(setupTime));
		System.out.println("setup (native): " + formatNanos(nativeSetupTime));
		System.out.println("canon: " + formatNanos(canonTime));
		System.out.println("other: " + formatNanos(getOtherTime()));
		System.out.println("total: " + formatNanos(totalTime));
		System.out.println("====================================");
	}
	
	private String formatNanos(long nanos){
		StringBuilder buffer = new StringBuilder();
		
		buffer.append(nanos % 1_000_000);
		buffer.append("ns");
		nanos /= 1_000_000;
		
		buffer.insert(0, "ms ");
		buffer.insert(0, nanos % 1000);
		nanos /= 1000;
		
		buffer.insert(0, "s ");
		buffer.insert(0, nanos % 60);
		nanos /= 60;
		
		buffer.insert(0, "m ");
		buffer.insert(0, nanos);
		
		return buffer.toString();
	}
}
