#include <nishe/PartitionNest.h>
#include <nishe/Refiner-inl.h>
#include <nishe/DirectedGraph.h>

using namespace nishe;

int main(int argc, char** argv){


	//construct pi
	PartitionNest pi;


	//construct graph
	DirectedGraph graph;

	//TODO various add_arc(u, v) calls (ints), 0-indexed


	//refine graph
	RefineTraceValue<DirectedGraph> trace;
	Refiner<DirectedGraph> refiner;

	refiner.refine(graph, &pi, &trace);

	//in theory do something with the reordered pi and refinement trace


}
