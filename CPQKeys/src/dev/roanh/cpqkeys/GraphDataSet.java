package dev.roanh.cpqkeys;

import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.roanh.gmark.conjunct.cpq.CPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ;
import dev.roanh.gmark.conjunct.cpq.QueryGraphCPQ.Vertex;
import dev.roanh.gmark.core.graph.Predicate;
import dev.roanh.gmark.util.Graph;

public class GraphDataSet implements Iterable<Graph<Vertex, Predicate>>{
	private List<Graph<Vertex, Predicate>> data;
	
	public GraphDataSet(List<Graph<Vertex, Predicate>> data){
		this.data = data;
	}
	
	public double getNodesAverage(){
		return data.stream().mapToInt(Graph::getNodeCount).average().orElse(0.0D);
	}
	
	public double getNodesStdDev(){
		return stdDev(data.stream().mapToInt(Graph::getNodeCount).summaryStatistics(), Graph::getNodeCount);
	}
	
	public double getEdgesAverage(){
		return data.stream().mapToInt(Graph::getEdgeCount).average().orElse(0.0D);
	}
	
	public double getEdgesStdDev(){
		return stdDev(data.stream().mapToInt(Graph::getEdgeCount).summaryStatistics(), Graph::getEdgeCount);
	}
	
	//sample stddev
	private double stdDev(IntSummaryStatistics stats, Function<Graph<Vertex, Predicate>, Integer> field){
		return Math.sqrt(data.stream().map(field).mapToDouble(x->Math.pow(x - stats.getAverage(), 2.0D)).sum() / (stats.getCount() - 1));
	}
	
	public void print(){
		System.out.println("========== Dataset Report ==========");
		System.out.println("Graphs: " + data.size());
		System.out.println("Nodes: " + getNodesAverage() + " \u00B1 " + getNodesStdDev());
		System.out.println("Edges: " + getEdgesAverage() + " \u00B1 " + getEdgesStdDev());
		System.out.println("====================================");
	}
	
	@Override
	public Iterator<Graph<Vertex, Predicate>> iterator(){
		return data.iterator();
	}
	
	public static GraphDataSet fromCPQ(int n, int rules, int labels){
		return new GraphDataSet(Stream.generate(()->CPQ.generateRandomCPQ(rules, labels)).limit(n).map(CPQ::toQueryGraph).map(QueryGraphCPQ::toGraph).collect(Collectors.toList()));
	}
}
