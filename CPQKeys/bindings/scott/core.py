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

# Slightly hacky imports to keep scott as a git submodule
import sys
sys.path.insert(0, './scott')
import scott as st
import fileinput

def read_graph(directed = False):
	graph = st.structs.graph.Graph()

	#read nodes
	nodes = []
	while True:
		line = input().rstrip("\n")
		if line == "end":
			break
		
		args = line.split(" ", 1)
		node = st.structs.node.Node(args[0], args[1] if size(args) == 2 else "")
		nodes.append(node)
		graph.add_node(node)
	
	#read edges
	while True:
		line = input().rstrip("\n")
		if line == "end":
			break
		
		args = line.split(" ", 4)
		graph.add_edge(st.structs.edge.Edge(args[0], nodes[int(args[1])]), modality = nodes[int(args[2])], args[3] if size(args) == 4 else "", directed = directed)
	
	return graph