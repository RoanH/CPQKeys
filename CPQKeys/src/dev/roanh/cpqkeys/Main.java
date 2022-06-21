/*
 * CPQKeys: An evaluation of various graph canonization algorithms.
 * Copyright (C) 2022  Roan Hofland (roan@roanh.dev).  All rights reserved.
 * GitHub Repository: https://github.com/RoanH/CPQKeys
 *
 * CPQKeys is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CPQKeys is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.cpqkeys;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import dev.roanh.cpqkeys.algo.Bliss;
import dev.roanh.cpqkeys.algo.Nauty;
import dev.roanh.cpqkeys.algo.Nishe;
import dev.roanh.cpqkeys.algo.Scott;
import dev.roanh.cpqkeys.algo.Traces;
import dev.roanh.gmark.util.Util;

/**
 * Main class for the CPQ Keys canonization
 * algorithm evaluation framework.
 * @author Roan
 */
public class Main{
	/**
	 * Executor used to run the algorithms.
	 */
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	/**
	 * The maximum amount of time in nanoseconds that an algorithm is allowed
	 * to spend evaluating a complete data set. If the algorithms needs more
	 * time to run on a data set then it will not be given a larger data set.
	 * Conversely, if the algorithm manages to finish processing the whole
	 * data set within this time limit, then it is given a larger data set
	 * to run on next.
	 */
	private static final long MAX_RUNTIME = TimeUnit.SECONDS.toNanos(10);
	/**
	 * Number of graphs in a data set.
	 */
	private static final int DATASET_SIZE = 10;
	/**
	 * Number of rule applications to use to generate the initial data set.
	 * @see GraphDataSet#fromCPQ(int, int, int)
	 */
	private static final int MIN_RULES = 4;
	/**
	 * Maximum number of rule applications to use to generate a data set.
	 * Algorithms will not be given a larger data set to work with even
	 * if they manage to complete their work on a data set of this size
	 * within the set time limit.
	 * @see GraphDataSet#fromCPQ(int, int, int)
	 */
	private static final int MAX_RULES = 8196 * 2 * 2;
	/**
	 * Increase in data set size after an algorithm processes a data set
	 * within the set time limit. Expressed as the number of rule applications.
	 * @see GraphDataSet#fromCPQ(int, int, int)
	 */
	private static final int RULE_GROWTH_FACTOR = 2;
	/**
	 * Number of labels to use for the data set.
	 * @see GraphDataSet#fromCPQ(int, int, int)
	 */
	private static final int LABELS = 5;
	/**
	 * Random seed to use to generate data sets.
	 */
	private static final int SEED = 1234;
	/**
	 * Command to start a Python process on the host system.
	 */
	public static final String PYTHON_COMMAND = findPython();
	/**
	 * List of algorithms to evaluate.
	 */
	public static final List<Algorithm> algorithms = Arrays.asList(
		Scott.DIRECTED,//known to have issues with certain inputs
		Scott.UNDIRECTED,
		Nishe.INSTANCE,
		Nauty.SPARSE,
		Nauty.DENSE,
		Traces.INSTANCE,
		Bliss.INSTANCE
	);

	/**
	 * Starts the algorithm evaluation process.
	 * @param args No valid commandline arguments.
	 */
	public static void main(String[] args){
		try{
			loadNatives();
		}catch(IOException | UnsatisfiedLinkError e){
			e.printStackTrace();
		}
		
		for(Algorithm algo : algorithms){
			evaluateAlgorithm(algo);
		}
		
		executor.shutdown();
	}
	
	private static final void saveResults(Set<Entry<GraphDataSet, ReportSummaryStatistics>> results, Path file) throws IOException{
		try(PrintStream out = new PrintStream(Files.newOutputStream(file))){
			for(Entry<GraphDataSet, ReportSummaryStatistics> pair : results){
				pair.getKey().print(out);
				pair.getValue().print(out);
				out.println("Raw report data (setup, native setup, canonization, other, total)");
				for(RuntimeReport report : pair.getValue().getReports()){
					report.writeData(out);
				}
			}
			
			out.println();

			
			
			
			
			
			
		}
	}
	
	private static final Set<Entry<GraphDataSet, ReportSummaryStatistics>> evaluateAlgorithm(Algorithm algo){
		Util.setRandomSeed(SEED);
		
		LinkedHashMap<GraphDataSet, ReportSummaryStatistics> results = new LinkedHashMap<GraphDataSet, ReportSummaryStatistics>();
		Future<ReportSummaryStatistics> task = null;
		for(int i = MIN_RULES; i <= MAX_RULES; i *= RULE_GROWTH_FACTOR){
			GraphDataSet data = GraphDataSet.fromCPQ(DATASET_SIZE, i, LABELS);
			data.print();
			
			task = executor.submit(()->new ReportSummaryStatistics(algo, data));
			try{
				ReportSummaryStatistics stats = task.get(MAX_RUNTIME, TimeUnit.NANOSECONDS);
				if(stats != null){
					stats.print();
					results.put(data, stats);
				}
			}catch(InterruptedException | ExecutionException e){
				System.out.println("Error running: " + algo.getName());
				e.printStackTrace();
				return results.entrySet();
			}catch(TimeoutException e){
				System.out.println("Timeout for " + algo.getName() + " awaiting last result...");
				
				try{
					ReportSummaryStatistics stats = task.get();
					stats.print();
					results.put(data, stats);
				}catch(InterruptedException | ExecutionException e1){
					System.out.println("Error running: " + algo.getName());
					e1.printStackTrace();
				}
				
				return results.entrySet();
			}
		}
		
		System.out.println("Not generating the next dataset as this would be very expensive...");
		return results.entrySet();
	}
	
	/**
	 * Loads the compiled JNI libraries required for certain algorithm implementations.
	 * Loads JNI dynamic libraries from the 'native' folder with libraries inside the
	 * 'native/lib' being loaded first so they can be used as dependencies for other
	 * libraries without causing errors.
	 * @throws IOException When an IOException occurs.
	 * @throws UnsatisfiedLinkError When loading a native library fails.
	 */
	private static final void loadNatives() throws IOException, UnsatisfiedLinkError{
		for(Path lib : Files.newDirectoryStream(Paths.get("native").resolve("lib"))){
			System.out.println("Loading native core library: " + lib.getFileName());
			System.load(lib.toAbsolutePath().toString());
		}
		
		for(Path lib : Files.newDirectoryStream(Paths.get("native"), Files::isRegularFile)){
			System.out.println("Loading native library: " + lib.getFileName());
			System.load(lib.toAbsolutePath().toString());
		}
	}
	
	/**
	 * Finds a Python 3 installation on the host system. This will
	 * typically return the <code>python3</code> command unless an
	 * anaconda 3 installation is detected.
	 * @return The Python executable.
	 */
	private static final String findPython(){
		if(System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")){
			Path anaconda = Paths.get(System.getProperty("user.home")).resolve("anaconda3").resolve("python.exe");
			if(Files.exists(anaconda)){
				return anaconda.toAbsolutePath().toString();
			}
		}

		return "python3";
	}
}
