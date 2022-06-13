package dev.roanh.cpqkeys;

public class RuntimeReport{
	private Algorithm algo;
	private long setupTime;
	private long nativeSetupTime;
	private long canonTime;
	private long totalTime;

	protected RuntimeReport(Algorithm algo, long[] times, long total){
		this.algo = algo;
		setupTime = times[0];
		nativeSetupTime = times[1];
		canonTime = times[2];
		totalTime = total;
	}
	
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
