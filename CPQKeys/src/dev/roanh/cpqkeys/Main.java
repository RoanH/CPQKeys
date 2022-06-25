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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
	 * Directory to store runtime logs in.
	 */
	private static final Path LOGS = Paths.get("logs");
	/**
	 * The maximum amount of time in nanoseconds that an algorithm is allowed
	 * to spend evaluating a complete data set. If the algorithms needs more
	 * time to run on a data set then it will not be given a larger data set.
	 * Conversely, if the algorithm manages to finish processing the whole
	 * data set within this time limit, then it is given a larger data set
	 * to run on next.
	 */
	private static final long MAX_RUNTIME = TimeUnit.SECONDS.toNanos(100);
	/**
	 * Number of graphs in a data set.
	 */
	private static final int DATASET_SIZE = 100;
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
	private static final int MAX_RULES = 8196;
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
	private static final int LABELS = 10;
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
	 * @param args No valid command line arguments.
	 */
	public static void main(String[] args){
		//initialise native bindings
		try{
			loadNatives();
		}catch(IOException | UnsatisfiedLinkError e){
			e.printStackTrace();
			return;
		}
		
		//evaluate all the algorithms
		for(Algorithm algo : algorithms){
			try{
				Path saveFile = LOGS.resolve(algo.getName() + ".log");
				if(Files.notExists(saveFile)){
					evaluateAlgorithm(algo).save(saveFile);
				}else{
					System.out.println("Output log file already exists for " + algo.getName() + ", skipping evaluation.");
				}
			}catch(IOException e){
				System.err.println("Error saving run results for: " + algo.getName());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Evaluates the given algorithm on randomly generated CPQs. The number of
	 * rule applications used to generate dataset CPQs will start from {@link #MIN_RULES}
	 * and go up to at most {@link #MAX_RULES} at a rate of {@link #RULE_GROWTH_FACTOR}.
	 * Each dataset will contain {@link #DATASET_SIZE} CPQs and at most {@link #LABELS}
	 * labels will be used in the CPQs. Each dataset will need to be processed by the
	 * algorithm in {@link #MAX_RUNTIME} nanoseconds. If the algorithm needs more time
	 * it will not be given a larger dataset next effectively ending the evaluation.
	 * The value for {@link #SEED} will be used to generate the random dataset. All
	 * tasks will be executed on a single thread.
	 * @param algo The algorithm to evaluate.
	 * @return The result of the algorithm evaluation.
	 * @see GraphDataSet
	 * @see Algorithm
	 * @see ReportSummaryStatistics
	 * @see RuntimeReport
	 * @see EvaluationResults
	 */
	private static final EvaluationResults evaluateAlgorithm(Algorithm algo){
		Util.setRandomSeed(SEED);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		EvaluationResults results = new EvaluationResults();
		Future<ReportSummaryStatistics> task = null;
		
		try{
			for(int i = MIN_RULES; i <= MAX_RULES; i *= RULE_GROWTH_FACTOR){
				GraphDataSet data = GraphDataSet.fromCPQ(DATASET_SIZE, i, LABELS);
				data.print();

				task = executor.submit(()->new ReportSummaryStatistics(algo, data));
				ReportSummaryStatistics stats = task.get(MAX_RUNTIME, TimeUnit.NANOSECONDS);
				if(stats != null){
					stats.print();
					results.addRun(data, stats);
				}
			}
			
			System.out.println("Not generating the next dataset (dataset size limit reached).");
		}catch(InterruptedException | ExecutionException e){
			System.out.println("Exception running: " + algo.getName());
			e.printStackTrace();
		}catch(TimeoutException e){
			System.out.println("Timeout for " + algo.getName() + " interrupting execution...");
			task.cancel(true);
		}finally{
			executor.shutdown();
			try{
				//wait for interrupted threads to return from native calls
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			}catch(InterruptedException e){
				//not very relevant
				e.printStackTrace();
			}
		}
		
		return results;
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
