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
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class EvaluationResults{
	private LinkedHashMap<GraphDataSet, ReportSummaryStatistics> results = new LinkedHashMap<GraphDataSet, ReportSummaryStatistics>();
	private Algorithm algo;
	
	public EvaluationResults(Algorithm algo){
		this.algo = algo;
	}
	
	public void addRun(GraphDataSet data, ReportSummaryStatistics stats){
		results.put(data, stats);
	}
	
	public void save(Path file) throws IOException{
		try(PrintStream out = new PrintStream(Files.newOutputStream(file))){
			for(Entry<GraphDataSet, ReportSummaryStatistics> pair : results.entrySet()){
				pair.getKey().print(out);
				pair.getValue().print(out);
				out.println("Raw report data (setup, native setup, canonization, other, total)");
				for(RuntimeReport report : pair.getValue().getReports()){
					report.writeData(out);
				}
				out.println();
			}
			
			out.println();
			out.println("Raw data (setup, native setup, canonization, other, total)");
			for(ReportSummaryStatistics stats : results.values()){
				stats.writeData(out);
			}
		}
	}
}
