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

import dev.roanh.cpqkeys.GraphUtil;
import dev.roanh.cpqkeys.GraphUtil.NumberedGraph;
import dev.roanh.cpqkeys.Main;
import dev.roanh.cpqkeys.VertexData;
import dev.roanh.gmark.conjunct.cpq.CPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.conjunct.cpq.WorkloadCPQ;
import dev.roanh.gmark.core.WorkloadType;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;
import dev.roanh.gmark.util.Graph.GraphEdge;
import dev.roanh.gmark.util.Graph.GraphNode;

public class Scott{

	
	
	
	
	
	
	public static void test(){
		try{
			//startSession("directed");
			Predicate a = new Predicate(1, "a", 0.0D);
			Predicate b = new Predicate(2, "b", 0.0D);
			Predicate c = new Predicate(3, "c", 0.0D);
			CPQ q = CPQ.intersect(CPQ.concat(CPQ.label(a), CPQ.intersect(CPQ.label(b), CPQ.id()), CPQ.label(c)), CPQ.IDENTITY);
			test(q.toQueryGraph());
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void test(QueryGraphCPQ cpq) throws IOException{
		Graph<Vertex, Predicate> graph = cpq.toGraph();
		
		//undirected
		Graph<Object, Predicate> undirected = GraphUtil.toUndirectedGraph(graph);
		NumberedGraph<Object, Predicate> unNum = GraphUtil.numberVertices(undirected);
		runUndirected(unNum);
		
		//directed
		NumberedGraph<Vertex, Predicate> dirNum = GraphUtil.numberVertices(graph);
		runDirected(dirNum);
	}
	
	
	//only has vertex labels for former directed edges
	private static void runUndirected(NumberedGraph<Object, Predicate> graph) throws IOException{
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
		int id = 0;
		for(GraphEdge<VertexData<GraphNode<Object, Predicate>>, Predicate> edge : graph.getEdges()){
			out.print(id);
			out.print(" ");
			out.print(edge.getSource().getID());
			out.print(" ");
			out.print(edge.getTarget().getID());
			out.print(" ");
			out.println(edge.getData().getAlias());
			id++;
		}
		out.println("end");
		
		out.flush();
		
		//TODO read output
	}
	
	//never has vertex labels
	private static void runDirected(NumberedGraph<Vertex, Predicate> graph) throws IOException{
		Process scott = startSession("directed");
		PrintWriter out = new PrintWriter(scott.getOutputStream(), true);
		
		for(GraphNode<VertexData<GraphNode<Vertex, Predicate>>, Predicate> node : graph.getNodes()){
			out.print(node.getData().getID());
			out.println(" ");
		}
		out.println("end");
		
		//write edges
		int id = 0;
		for(GraphEdge<VertexData<GraphNode<Vertex, Predicate>>, Predicate> edge : graph.getEdges()){
			out.print(id);
			out.print(" ");
			out.print(edge.getSource().getID());
			out.print(" ");
			out.print(edge.getTarget().getID());
			out.print(" ");
			out.println(edge.getData().getAlias());
			id++;
		}
		out.println("end");
		
		out.flush();
		
		//TODO read output
		
	}
	
	
	
	private static Process startSession(String entrypoint) throws IOException{
		Path dir = Paths.get("bindings").resolve("scott");
		ProcessBuilder builder = new ProcessBuilder(Main.PYTHON_COMMAND, dir.resolve(entrypoint + ".py").toAbsolutePath().toString());
		builder.redirectError(Redirect.INHERIT);
		builder.redirectOutput(Redirect.INHERIT);//TODO PIPE
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
