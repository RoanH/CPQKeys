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

import dev.roanh.cpqkeys.algo.Bliss;
import dev.roanh.cpqkeys.algo.Nauty;
import dev.roanh.cpqkeys.algo.Nishe;
import dev.roanh.cpqkeys.algo.Scott;
import dev.roanh.cpqkeys.algo.Traces;
import dev.roanh.gmark.conjunct.cpq.CPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

public class Main{
	public static final String PYTHON_COMMAND = findPython();
	public static final List<Algorithm> algos = Arrays.asList(
		Scott.DIRECTED,
		Scott.UNDIRECTED,
		Nishe.INSTANCE,
		Nauty.SPARSE,
		Nauty.DENSE,
		Traces.INSTANCE,
		Bliss.INSTANCE
	);

	public static void main(String[] args){
		try{
			loadNatives();
		}catch(IOException | UnsatisfiedLinkError e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Predicate a = new Predicate(1, "a", 0.0D);
		Predicate b = new Predicate(2, "b", 0.0D);
		Predicate c = new Predicate(3, "c", 0.0D);
		CPQ q = CPQ.intersect(CPQ.concat(CPQ.label(a), CPQ.intersect(CPQ.label(b), CPQ.id()), CPQ.label(c)), CPQ.IDENTITY);
		Graph<Vertex, Predicate> graph = q.toQueryGraph().toGraph();
		
		for(Algorithm algo : algos){
			algo.time(graph).print();
		}
	}
	
	private static final void loadNatives() throws IOException, UnsatisfiedLinkError{
		for(Path lib : Files.newDirectoryStream(Paths.get("native"))){
			System.out.println("Loading native library: " + lib.getFileName());
			System.load(lib.toAbsolutePath().toString());
		}
	}
	
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
