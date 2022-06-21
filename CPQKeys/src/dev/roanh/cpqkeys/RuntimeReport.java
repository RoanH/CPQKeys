package dev.roanh.cpqkeys;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.StringJoiner;

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

	/**
	 * Constructs a new runtime report for the given algorithm
	 * with the given runtime information.
	 * @param algo The algorithm that was execute.
	 * @param times An array with runtimes, the array is
	 *        expected to contain in order the setup time
	 *        native setup time and lastly the canonization
	 *        time. All times are expected to be in nanoseconds.
	 * @param total The total number of nanoseconds spent executing
	 *        the algorithm and all associated transforms.
	 */
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
	
	/**
	 * Gets the algorithm that was executed to generate this report.
	 * @return The algorithm for this report.
	 */
	public Algorithm getAlgorithm(){
		return algo;
	}
	
	/**
	 * Prints a runtime report with all the times formatted.
	 */
	public void print(){
		print(System.out);
	}
	
	/**
	 * Prints a runtime report with all the times formatted.
	 * @param out The stream to write to.
	 */
	public void print(PrintStream out){
		out.println("========== Runtime Report ==========");
		out.println("Algorithm: " + algo.getName());
		out.println("Setup: " + formatNanos(setupTime));
		out.println("Setup (native): " + formatNanos(nativeSetupTime));
		out.println("Canonization: " + formatNanos(canonTime));
		out.println("Other: " + formatNanos(getOtherTime()));
		out.println("Total: " + formatNanos(totalTime));
		out.println("====================================");
	}
	
	public void writeData(PrintStream out){
		out.print(String.valueOf(setupTime));
		out.print(" ");
		out.print(String.valueOf(nativeSetupTime));
		out.print(" ");
		out.print(String.valueOf(canonTime));
		out.print(" ");
		out.print(String.valueOf(getOtherTime()));
		out.print(" ");
		out.println(String.valueOf(totalTime));
	}
	
	/**
	 * Formats the given number of nanoseconds as a string breaking
	 * the time up into units of increasing magnitude as required.
	 * The used splits are, nanoseconds, milliseconds, seconds and minutes.
	 * @param nanos The nanosecond time to format.
	 * @return A formatted string displaying the given nanosecond time.
	 */
	protected static final String formatNanos(long nanos){
		StringBuilder buffer = new StringBuilder();
		
		buffer.append(nanos % 1_000_000);
		buffer.append("ns");
		nanos /= 1_000_000;
		
		if(nanos > 0){
			buffer.insert(0, "ms ");
			buffer.insert(0, nanos % 1000);
			nanos /= 1000;
			
			if(nanos > 0){
				buffer.insert(0, "s ");
				buffer.insert(0, nanos % 60);
				nanos /= 60;
				
				if(nanos > 0){
					buffer.insert(0, "m ");
					buffer.insert(0, nanos);
				}
			}
		}
		
		return buffer.toString();
	}
}
