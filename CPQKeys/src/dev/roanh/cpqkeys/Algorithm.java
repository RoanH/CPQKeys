package dev.roanh.cpqkeys;

import java.util.function.Function;

import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

/**
 * Entry point for an algorithm implementation that
 * can canonize an input graph.
 * @author Roan
 */
public class Algorithm{
	/**
	 * The display name of this algorithm.
	 */
	private String name;
	/**
	 * The canonization function for this algorithm.
	 * @see #canonize(Graph)
	 */
	private Function<Graph<Vertex, Predicate>, long[]> canonFun;
	
	/**
	 * Constructs a new algorithm instance with the given
	 * display name and canonization function.
	 * @param name The display name of this algorithm.
	 * @param canonFun The canonization function of this algorithm.
	 */
	public Algorithm(String name, Function<Graph<Vertex, Predicate>, long[]> canonFun){
		this.name = name;
		this.canonFun = canonFun;
	}
	
	/**
	 * Runs this algorithm on the given input graph.
	 * @param graph The input graph.
	 * @return An array of time measurements containing in the first
	 *         index the graph transform time, in the second index the
	 *         native setup time (graph construction) and in the third
	 *         index the canonization time. All times are in nanoseconds.
	 * @see #time(Graph)
	 */
	public long[] canonize(Graph<Vertex, Predicate> graph){
		return canonFun.apply(graph);
	}
	
	/**
	 * Gets the display name of this algorithm.
	 * @return The display name of this algorithm.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Runs this algorithm on the given input graph
	 * and returns a runtime report with runtime information.
	 * @param graph The input graph.
	 * @return A report with runtime information.
	 * @see #canonize(Graph)
	 */
	public RuntimeReport time(Graph<Vertex, Predicate> graph){
		long start = System.nanoTime();
		long[] times = canonize(graph);
		long end = System.nanoTime();
		
		return new RuntimeReport(this, times, end - start);
	}
}
