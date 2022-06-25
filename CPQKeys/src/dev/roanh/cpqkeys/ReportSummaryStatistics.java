package dev.roanh.cpqkeys;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.function.Function;

import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

/**
 * Report summarising a collection of individual runtime reports. 
 * @author Roan
 * @see RuntimeReport
 */
public class ReportSummaryStatistics{
	/**
	 * The algorithm that produced the runtimes
	 * for this report summary.
	 */
	private Algorithm algo;
	/**
	 * List of individual runtime reports that make up this summary.
	 */
	private List<RuntimeReport> reports = new ArrayList<RuntimeReport>();
	/**
	 * Summary of the setup runtime statistics.
	 */
	private LongSummaryStatistics setupTime = new LongSummaryStatistics();
	/**
	 * Summary of the native setup runtime statistics.
	 */
	private LongSummaryStatistics nativeSetupTime = new LongSummaryStatistics();
	/**
	 * Summary of the canonization runtime statistics.
	 */
	private LongSummaryStatistics canonTime = new LongSummaryStatistics();
	/**
	 * Summary of other runtime statistics.
	 */
	private LongSummaryStatistics otherTime = new LongSummaryStatistics();
	/**
	 * Summary of the total runtime statistics.
	 */
	private LongSummaryStatistics totalTime = new LongSummaryStatistics();
	
	/**
	 * Constructs a new summary report by running the given
	 * algorithm on the graphs in the given data set.
	 * @param algo The algorithm to run.
	 * @param data The data set of run the algorithm on.
	 * @see Algorithm
	 * @see GraphDataSet
	 */
	public ReportSummaryStatistics(Algorithm algo, GraphDataSet data){
		for(Graph<Vertex, Predicate> graph : data){
			addReport(algo.time(graph));
		}
	}

	/**
	 * Appends a new report to this summary.
	 * @param report The report to append.
	 * @throws IllegalArgumentException When the given report
	 *         was not generated by the same algorithm as the
	 *         other reports in this summary.
	 */
	public void addReport(RuntimeReport report) throws IllegalArgumentException{
		if(algo == null){
			algo = report.getAlgorithm();
		}else if(!algo.equals(report.getAlgorithm())){
			throw new IllegalArgumentException("Runtime reports do not represent the same algorithm.");
		}
		
		setupTime.accept(report.getSetupTime());
		nativeSetupTime.accept(report.getNativeSetupTime());
		canonTime.accept(report.getCanonizationTime());
		otherTime.accept(report.getOtherTime());
		totalTime.accept(report.getTotalTime());
		reports.add(report);
	}
	
	/**
	 * Gets the sample standard deviation for the setup runtime.
	 * @return The sample standard deviation for the setup runtime in nanoseconds.
	 * @see RuntimeReport#getSetupTime()
	 */
	public double getSetupTimeStdDev(){
		return stdDev(setupTime, RuntimeReport::getSetupTime);
	}
	
	/**
	 * Gets the average setup time for all runtime reports.
	 * @return The average setup time in nanoseconds.
	 * @see RuntimeReport#getSetupTime()
	 */
	public double getSetupTimeAverage(){
		return setupTime.getAverage();
	}
	
	/**
	 * Gets the sample standard deviation for the native setup runtime.
	 * @return The sample standard deviation for the native setup runtime in nanoseconds.
	 * @see RuntimeReport#getNativeSetupTime()
	 */
	public double getNativeSetupTimeStdDev(){
		return stdDev(nativeSetupTime, RuntimeReport::getNativeSetupTime);
	}
	
	/**
	 * Gets the average native setup time for all runtime reports.
	 * @return The average native setup time in nanoseconds.
	 * @see RuntimeReport#getNativeSetupTime()
	 */
	public double getNativeSetupTimeAverage(){
		return nativeSetupTime.getAverage();
	}
	
	/**
	 * Gets the sample standard deviation for the canonization runtime.
	 * @return The sample standard deviation for the canonization runtime in nanoseconds.
	 * @see RuntimeReport#getCanonizationTime()
	 */
	public double getCanonizationTimeStdDev(){
		return stdDev(canonTime, RuntimeReport::getCanonizationTime);
	}
	
	/**
	 * Gets the average canonization time for all runtime reports.
	 * @return The average canonization time in nanoseconds.
	 * @see RuntimeReport#getCanonizationTime()
	 */
	public double getCanonizationTimeAverage(){
		return canonTime.getAverage();
	}
	
	/**
	 * Gets the sample standard deviation for the 'other' runtime.
	 * @return The sample standard deviation for the 'other' runtime in nanoseconds.
	 * @see RuntimeReport#getOtherTime()
	 */
	public double getOtherTimeStdDev(){
		return stdDev(otherTime, RuntimeReport::getOtherTime);
	}
	
	/**
	 * Gets the average 'other' time for all runtime reports.
	 * @return The average 'other' time in nanoseconds.
	 * @see RuntimeReport#getOtherTime()
	 */
	public double getOtherTimeAverage(){
		return otherTime.getAverage();
	}
	
	/**
	 * Gets the sample standard deviation for the total runtime.
	 * @return The sample standard deviation for the total runtime in nanoseconds.
	 * @see RuntimeReport#getTotalTime()
	 */
	public double getTotalTimeStdDev(){
		return stdDev(totalTime, RuntimeReport::getTotalTime);
	}
	
	/**
	 * Gets the average total time for all runtime reports.
	 * @return The average total time in nanoseconds.
	 * @see RuntimeReport#getTotalTime()
	 */
	public double getTotalTimeAverage(){
		return totalTime.getAverage();
	}
	
	/**
	 * Gets the individual runtime reports that make up this summary.
	 * @return The individual runtime reports.
	 */
	public List<RuntimeReport> getReports(){
		return reports;
	}
	
	/**
	 * Prints a runtime report with all the times formatted
	 * together with their sample standard deviation.
	 */
	public void print(){
		print(System.out);
	}
	
	/**
	 * Prints a runtime report with all the times formatted
	 * together with their sample standard deviation.
	 * @param out The stream to write to.
	 */
	public void print(PrintStream out){
		out.println("========== Runtime Report ==========");
		out.println("Algorithm: " + algo.getName());
		out.println("Setup: " + formatNanos(getSetupTimeAverage()) + " \u00B1 " + formatNanos(getSetupTimeStdDev()));
		out.println("Setup (native): " + formatNanos(getNativeSetupTimeAverage()) + " \u00B1 " + formatNanos(getNativeSetupTimeStdDev()));
		out.println("Canonization: " + formatNanos(getCanonizationTimeAverage()) + " \u00B1 " + formatNanos(getCanonizationTimeStdDev()));
		out.println("Other: " + formatNanos(getOtherTimeAverage()) + " \u00B1 " + formatNanos(getOtherTimeStdDev()));
		out.println("Total: " + formatNanos(getTotalTimeAverage()) + " \u00B1 " + formatNanos(getTotalTimeStdDev()));
		out.println("====================================");
	}
	
	/**
	 * Writes the data for this summary as a single line to the given stream.
	 * The data will be separated by spaces and the following numbers will
	 * be written in order:
	 * <ol>
	 * <li>Average setup time</li>
	 * <li>Setup time standard deviation</li>
	 * <li>Average native setup time</li>
	 * <li>Native setup time standard deviation</li>
	 * <li>Average canonization time</li>
	 * <li>Canonization time standard deviation</li>
	 * <li>Average other time</li>
	 * <li>Other time standard deviation</li>
	 * <li>Average total time</li>
	 * <li>Total time standard deviation</li>
	 * </ol>
	 * @param out The stream to write to.
	 */
	public void writeData(PrintStream out){
		out.print(getSetupTimeAverage());
		out.print(" ");
		out.print(getNativeSetupTimeStdDev());
		out.print(" ");
		out.print(getNativeSetupTimeAverage());
		out.print(" ");
		out.print(getNativeSetupTimeStdDev());
		out.print(" ");
		out.print(getCanonizationTimeAverage());
		out.print(" ");
		out.print(getCanonizationTimeStdDev());
		out.print(" ");
		out.print(getOtherTimeAverage());
		out.print(" ");
		out.print(getOtherTimeStdDev());
		out.print(" ");
		out.print(getTotalTimeAverage());
		out.print(" ");
		out.println(getTotalTimeStdDev());
	}
	
	/**
	 * Formats the given number of nanoseconds as a string breaking
	 * the time up into units of increasing magnitude as required.
	 * The used splits are, nanoseconds, milliseconds, seconds and minutes.
	 * @param nanos The nanosecond time to format (will be rounded to the nearest long).
	 * @return A formatted string displaying the given nanosecond time.
	 * @see RuntimeReport#formatNanos(long)
	 */
	private static final String formatNanos(double nanos){
		//just round to the nearest nanosecond, that's more than precise enough
		return RuntimeReport.formatNanos(Math.round(nanos));
	}
	
	/**
	 * Computes the sample standard deviation of the given field in the data set.
	 * @param stats Summary statistics of the field to compute the standard deviation for.
	 * @param field The field to compute the standard deviation of.
	 * @return The sample standard deviation of the given field.
	 */
	private double stdDev(LongSummaryStatistics stats, Function<RuntimeReport, Long> field){
		return Math.sqrt(reports.stream().map(field).mapToDouble(x->Math.pow(x - stats.getAverage(), 2.0D)).sum() / (stats.getCount() - 1));
	}
}
