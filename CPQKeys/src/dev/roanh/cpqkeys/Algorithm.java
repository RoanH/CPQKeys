package dev.roanh.cpqkeys;

import java.util.function.Function;

import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

public class Algorithm{
	private String name;
	private Function<Graph<Vertex, Predicate>, long[]> canonFun;
	
	public Algorithm(String name, Function<Graph<Vertex, Predicate>, long[]> canonFun){
		this.name = name;
		this.canonFun = canonFun;
	}
	
	public long[] canonize(Graph<Vertex, Predicate> graph){
		return canonFun.apply(graph);
	}
	
	public String getName(){
		return name;
	}
	
	public RuntimeReport time(Graph<Vertex, Predicate> graph){
		long start = System.nanoTime();
		long[] times = canonize(graph);
		long end = System.nanoTime();
		
		return new RuntimeReport(this, times, end - start);
	}
}
