# CPQKeys: An evaluation of various graph canonization algorithms.
# Copyright (C) 2022  Roan Hofland (roan@roanh.dev).  All rights reserved.
# GitHub Repository: https://github.com/RoanH/CPQKeys
#
# CPQKeys is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# CPQKeys is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Slightly hacky imports to keep Scott as a git submodule
import sys
sys.path.insert(0, './scott')
import scott as st
import fileinput

def read_graph(directed = False):
	'''
	Reads a graph from standard input. The expected format is as follows:
	1. First several node declarations of the form '<id> <label>' here
	   label is allowed to be absent to indicate that the node has no
	   label. The space between the id and label is not allowed to be
	   omitted. All the given node ids have to be unique for the graph.
	2. The string 'end' to indicate the end of the node declarations.
	3. Several edge declarations of the form '<id> <source> <target> <label>'
	   here the label is again allowed to be absent to indicate that the
	   edge has no label. The space between target and label is not allowed
	   to be omitted. The edge will be directed from the node with the given
	   source id to the node with the given target id unless the graph is
	   not directed. The given id has to be unique for the graph.
	4. The string 'end' to indicate the end of the edge declarations.
	
		Parameters:
			directed (bool): Whether the input graph should be interpreted as directed
		
		Returns:
			The parsed graph as a Scott graph.
	'''
	
	graph = st.structs.graph.Graph()

	#read nodes
	nodes = []
	while True:
		line = input().rstrip("\n")
		if line == "end":
			break
		
		args = line.split(" ", 1)
		node = st.structs.node.Node(args[0], args[1])
		nodes.append(node)
		graph.add_node(node)
	
	#read edges
	while True:
		line = input().rstrip("\n")
		if line == "end":
			break
		
		args = line.split(" ", 4)
		graph.add_edge(st.structs.edge.Edge(args[0], nodes[int(args[1])], nodes[int(args[2])], modality = args[3], directed = directed))
	
	return graph