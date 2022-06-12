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
package dev.roanh.cpqkeys.algo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.roanh.cpqkeys.GraphUtil.NumberedGraph;
import dev.roanh.cpqkeys.Main;

public class Scott{

	
	
	
	
	
	
	public static void test(){
		try{
			startSession("directed");
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private static <T> void runUndirected(NumberedGraph<T> graph) throws IOException{
		Process scott = startSession("undirected");
		
		
		
		
	}
	
	private static <T> void runDirected(NumberedGraph<T> graph) throws IOException{
		Process scott = startSession("directed");
		
		
		
		
	}
	
	
	
	private static Process startSession(String entrypoint) throws IOException{
		Path dir = Paths.get("bindings").resolve("scott");
		ProcessBuilder builder = new ProcessBuilder(Main.PYTHON_COMMAND, dir.resolve(entrypoint + ".py").toAbsolutePath().toString());
		builder.redirectError(Redirect.INHERIT);
		builder.redirectOutput(Redirect.INHERIT);
		builder.redirectInput(Redirect.PIPE);
		builder.directory(dir.toAbsolutePath().toFile());
		
//		Process proc = builder.start();
//		PrintWriter out = new PrintWriter(proc.getOutputStream());
//		out.println("test 1");
//		out.println("test 2");
//		out.println("test 3");
//		out.println("test 4");
//		out.println("test 5");
//		out.println("end");
//		out.println("test 6");
//		out.flush();
//		
//		try{
//			proc.waitFor();
//		}catch(InterruptedException e){
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return builder.start();
	}
}
