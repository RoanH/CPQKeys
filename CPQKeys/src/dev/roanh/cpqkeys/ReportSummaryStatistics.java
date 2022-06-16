package dev.roanh.cpqkeys;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.function.Function;

public class ReportSummaryStatistics{
	private List<RuntimeReport> reports = new ArrayList<RuntimeReport>();
	private LongSummaryStatistics setupTime = new LongSummaryStatistics();
	private LongSummaryStatistics nativeSetupTime = new LongSummaryStatistics();
	private LongSummaryStatistics canonTime = new LongSummaryStatistics();
	private LongSummaryStatistics otherTime = new LongSummaryStatistics();
	private LongSummaryStatistics totalTime = new LongSummaryStatistics();
	
	public void addReport(RuntimeReport report){
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
	
	
	
	
	//TODO other fields and easy output
	
	
	
	//sample stddev
	private double stdDev(LongSummaryStatistics stats, Function<RuntimeReport, Long> field){
		return Math.sqrt(reports.stream().map(field).mapToDouble(x->Math.pow(x - stats.getAverage(), 2.0D)).sum() / (stats.getCount() - 1));
	}
}
