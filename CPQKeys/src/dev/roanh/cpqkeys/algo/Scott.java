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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import dev.roanh.cpqkeys.Algorithm;
import dev.roanh.cpqkeys.GraphUtil;
import dev.roanh.cpqkeys.GraphUtil.NumberedGraph;
import dev.roanh.cpqkeys.Main;
import dev.roanh.cpqkeys.VertexData;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;

/**
 * Binding for Scott.
 * @author Roan
 * @see <a href="https://theplatypus.github.io/scott/">Scott website</a>
 */
public class Scott{
	/**
	 * Algorithm binding for Scott that accepts directed graphs as input.
	 */
	public static final Algorithm DIRECTED = new Algorithm("Scott (directed)", Scott::runDirected);
	/**
	 * Algorithm binding for Scott that only accepts undirected graphs as input.
	 */
	public static final Algorithm UNDIRECTED = new Algorithm("Scott (undirected)", Scott::runUndirected);
	
	/**
	 * Runs Scott on an undirected input graph that is constructed
	 * from the given input graph by the undirected transform
	 * {@link GraphUtil#toUndirectedGraph(Graph)}.
	 * @param input The graph to run Scott on.
	 * @return An array of time measurements containing in the first
	 *         index the graph transform time, in the second index the
	 *         native setup time (graph construction) and in the third
	 *         index the canonization time. All times are in nanoseconds.
	 */
	private static long[] runUndirected(Graph<Vertex, Predicate> input){
		try{
			long start = System.nanoTime();
			NumberedGraph<Object, Predicate> graph = GraphUtil.numberVertices(GraphUtil.toUndirectedGraph(input));
			Process scott = startSession("undirected");
			PrintWriter out = new PrintWriter(scott.getOutputStream(), true);

			//write nodes
			for(GraphNode<VertexData<GraphNode<Object, Predicate>>, Predicate> node : graph.getNodes()){
				out.print(node.getData().getID());
				out.print(" ");
				Object label = node.getData().getData().getData();
				if(label instanceof Vertex){
					out.println();
				}else{
					out.println(label.toString());
				}
			}
			out.println("end");

			//write edges
			writeEdges(out, graph.getEdges());
			out.flush();
			long end = System.nanoTime();

			//output
			long[] times = readTimes(scott);
			return new long[]{
				end - start,
				times[0],
				times[1]
			};
		}catch(IOException e){
			//should not normally happen
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Runs Scott on a directed input graph.
	 * @param input The graph to run Scott on.
	 * @return An array of time measurements containing in the first
	 *         index the graph transform time, in the second index the
	 *         native setup time (graph construction) and in the third
	 *         index the canonization time. All times are in nanoseconds.
	 */
	private static long[] runDirected(Graph<Vertex, Predicate> input){
		try{
			long start = System.nanoTime();
			NumberedGraph<Vertex, Predicate> graph = GraphUtil.numberVertices(input);
			Process scott = startSession("directed");
			PrintWriter out = new PrintWriter(scott.getOutputStream(), true);
			
			//write notes
			for(GraphNode<VertexData<GraphNode<Vertex, Predicate>>, Predicate> node : graph.getNodes()){
				out.print(node.getData().getID());
				out.println(" ");
			}
			out.println("end");
			
			//write edges
			writeEdges(out, graph.getEdges());
			out.flush();
			long end = System.nanoTime();
			
			//output
			long[] times = readTimes(scott);
			return new long[]{
				end - start,
				times[0],
				times[1]
			};
		}catch(IOException e){
			//should not normally happen
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Reads the final runtime measurements from the Scott subprocess.
	 * @param scott The Scott subprocess to read from.
	 * @return An array containing two times. First the setup time in
	 *         nanoseconds and second the canonization time in nanoseconds.
	 * @throws IOException When an IOException occurs.
	 */
	private static long[] readTimes(Process scott) throws IOException{
		try(BufferedReader in = new BufferedReader(new InputStreamReader(scott.getInputStream()))){
			return new long[]{
				Long.parseLong(in.readLine().substring(11).trim()),
				Long.parseLong(in.readLine().substring(11).trim())
			};
		}
	}
	
	/**
	 * Writes the given list of edges to the given writer
	 * in a format that can be parsed by Scott.
	 * @param <T> The vertex data type.
	 * @param out The writer to write to.
	 * @param edges The edges to write.
	 */
	private static <T> void writeEdges(PrintWriter out, List<GraphEdge<VertexData<T>, Predicate>> edges){
		int id = 0;
		for(GraphEdge<VertexData<T>, Predicate> edge : edges){
			out.print(id);
			out.print(" ");
			out.print(edge.getSource().getID());
			out.print(" ");
			out.print(edge.getTarget().getID());
			Predicate data = edge.getData();
			if(data == null){
				out.println(" ");
			}else{
				out.print(" ");
				out.println(data.getAlias());
			}
			id++;
		}
		out.println("end");
	}
	
	/**
	 * Starts a new Scott process and returns it.
	 * @param entrypoint The Python script to run. Either
	 *        'undirected' or 'directed'.
	 * @return The started Scott process.
	 * @throws IOException When an IO exception occurs.
	 */
	private static Process startSession(String entrypoint) throws IOException{
		Path dir = Paths.get("bindings").resolve("scott");
		ProcessBuilder builder = new ProcessBuilder(Main.PYTHON_COMMAND, dir.resolve(entrypoint + ".py").toAbsolutePath().toString());
		builder.redirectError(Redirect.INHERIT);
		builder.redirectOutput(Redirect.PIPE);
		builder.redirectInput(Redirect.PIPE);
		builder.directory(dir.toAbsolutePath().toFile());
		return builder.start();
	}
}
