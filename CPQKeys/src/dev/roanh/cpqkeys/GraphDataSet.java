package dev.roanh.cpqkeys;

import java.io.PrintStream;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.roanh.gmark.conjunct.cpq.CPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

/**
 * Represents a data set consisting of graph instances.
 * @author Roan
 * @see #fromCPQ(int, int, int)
 */
public class GraphDataSet implements Iterable<Graph<Vertex, Predicate>>{
	/**
	 * The graphs for this data set.
	 */
	private List<Graph<Vertex, Predicate>> data;
	
	/**
	 * Constructs a new data set with the given data.
	 * @param data The data for this data set.
	 */
	public GraphDataSet(List<Graph<Vertex, Predicate>> data){
		this.data = data;
	}
	
	/**
	 * Gets the average number of nodes in the graphs for this data set.
	 * @return The average number of nodes in the graphs for this data set.
	 */
	public double getNodesAverage(){
		return data.stream().mapToInt(Graph::getNodeCount).average().orElse(0.0D);
	}
	
	/**
	 * Gets the sample standard deviation of the number of nodes in the graphs for this data set.
	 * @return The sample standard deviation of the number of nodes in the graphs for this data set.
	 */
	public double getNodesStdDev(){
		return stdDev(Graph::getNodeCount);
	}
	
	/**
	 * Gets the average number of edges in the graphs for this data set.
	 * @return The average number of edges in the graphs for this data set.
	 */
	public double getEdgesAverage(){
		return data.stream().mapToInt(Graph::getEdgeCount).average().orElse(0.0D);
	}
	
	/**
	 * Gets the sample standard deviation of the number of edges in the graphs for this data set.
	 * @return The sample standard deviation of the number of edges in the graphs for this data set.
	 */
	public double getEdgesStdDev(){
		return stdDev(Graph::getEdgeCount);
	}
	
	/**
	 * Computes the sample standard deviation of the given field in the data set.
	 * @param field The field to compute the standard deviation of.
	 * @return The sample standard deviation of the given field.
	 */
	private double stdDev(ToIntFunction<Graph<Vertex, Predicate>> field){
		IntSummaryStatistics stats = data.stream().mapToInt(field).summaryStatistics();
		return Math.sqrt(data.stream().mapToInt(field).mapToDouble(x->Math.pow(x - stats.getAverage(), 2.0D)).sum() / (stats.getCount() - 1));
	}
	
	/**
	 * Prints some general statistics about this data set, namely the data set
	 * size and average number of nodes and edges together with their standard deviation.
	 */
	public void print(){
		print(System.out);
	}
	
	/**
	 * Prints some general statistics about this data set, namely the data set
	 * size and average number of nodes and edges together with their standard deviation.
	 * @param out The stream to write to.
	 */
	public void print(PrintStream out){
		out.println("========== Dataset Report ==========");
		out.println("Graphs: " + data.size());
		out.println("Nodes: " + getNodesAverage() + " \u00B1 " + getNodesStdDev());
		out.println("Edges: " + getEdgesAverage() + " \u00B1 " + getEdgesStdDev());
		out.println("====================================");
	}
	
	@Override
	public Iterator<Graph<Vertex, Predicate>> iterator(){
		return data.iterator();
	}
	
	/**
	 * Generates a data set consisting of CPQ query graphs using
	 * {@link CPQ#generateRandomCPQ(int, int)}.
	 * @param n The number of CPQ query graphs to generate.
	 * @param rules The maximum number of rule applications
	 *        when generating CPQs (influences the size of the CPQ).
	 * @param labels The maximum number of distinct labels to use
	 *        when generating a CPQ.
	 * @return The newly generated data set of CPQ query graphs.
	 * @see CPQ#generateRandomCPQ(int, int)
	 */
	public static GraphDataSet fromCPQ(int n, int rules, int labels){
		return new GraphDataSet(Stream.generate(()->CPQ.generateRandomCPQ(rules, labels)).limit(n).map(CPQ::toQueryGraph).map(QueryGraphCPQ::toGraph).collect(Collectors.toList()));
	}
}
