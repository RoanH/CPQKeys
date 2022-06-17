package dev.roanh.cpqkeys;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.function.Function;

import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

public class ReportSummaryStatistics{
	private Algorithm algo;
	private List<RuntimeReport> reports = new ArrayList<RuntimeReport>();
	private LongSummaryStatistics setupTime = new LongSummaryStatistics();
	private LongSummaryStatistics nativeSetupTime = new LongSummaryStatistics();
	private LongSummaryStatistics canonTime = new LongSummaryStatistics();
	private LongSummaryStatistics otherTime = new LongSummaryStatistics();
	private LongSummaryStatistics totalTime = new LongSummaryStatistics();
	
	public ReportSummaryStatistics(Algorithm algo, GraphDataSet data){
		for(Graph<Vertex, Predicate> graph : data){
			addReport(algo.time(graph));
		}
	}

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
	
	public double getSetupTimeStdDev(){
		return stdDev(setupTime, RuntimeReport::getSetupTime);
	}
	
	public double getSetupTimeAverage(){
		return setupTime.getAverage();
	}
	
	public double getNativeSetupTimeStdDev(){
		return stdDev(nativeSetupTime, RuntimeReport::getNativeSetupTime);
	}
	
	public double getNativeSetupTimeAverage(){
		return nativeSetupTime.getAverage();
	}
	
	public double getCanonizationTimeStdDev(){
		return stdDev(canonTime, RuntimeReport::getCanonizationTime);
	}
	
	public double getCanonizationTimeAverage(){
		return canonTime.getAverage();
	}
	
	public double getOtherTimeStdDev(){
		return stdDev(otherTime, RuntimeReport::getOtherTime);
	}
	
	public double getOtherTimeAverage(){
		return otherTime.getAverage();
	}
	
	public double getTotalTimeStdDev(){
		return stdDev(totalTime, RuntimeReport::getTotalTime);
	}
	
	public double getTotalTimeAverage(){
		return totalTime.getAverage();
	}
	
	public void print(){
		System.out.println("========== Runtime Report ==========");
		System.out.println("Algorithm: " + algo.getName());
		System.out.println("Setup: " + formatNanos(getSetupTimeAverage()) + " \u00B1 " + formatNanos(getSetupTimeStdDev()));
		System.out.println("Setup (native): " + formatNanos(getNativeSetupTimeAverage()) + " \u00B1 " + formatNanos(getNativeSetupTimeStdDev()));
		System.out.println("Canonization: " + formatNanos(getCanonizationTimeAverage()) + " \u00B1 " + formatNanos(getCanonizationTimeStdDev()));
		System.out.println("Other: " + formatNanos(getOtherTimeAverage()) + " \u00B1 " + formatNanos(getOtherTimeStdDev()));
		System.out.println("Total: " + formatNanos(getTotalTimeAverage()) + " \u00B1 " + formatNanos(getTotalTimeStdDev()));
		System.out.println("====================================");
	}
	
	private static final String formatNanos(double nanos){
		//just round to the nearest nanosecond, that's more than precise enough
		return RuntimeReport.formatNanos(Math.round(nanos));
	}
	
	//sample stddev
	private double stdDev(LongSummaryStatistics stats, Function<RuntimeReport, Long> field){
		return Math.sqrt(reports.stream().map(field).mapToDouble(x->Math.pow(x - stats.getAverage(), 2.0D)).sum() / (stats.getCount() - 1));
	}
}
