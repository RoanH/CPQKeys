# Slightly hacky imports to keep scott as a git submodule
import sys
sys.path.insert(0, './scott')
import scott as st

graph = st.structs.graph.Graph()
n1 = st.structs.node.Node("1", "C")
n2 = st.structs.node.Node("2", "O")
n3 = st.structs.node.Node("3", "H")
n4 = st.structs.node.Node("4", "H")

e1 = st.structs.edge.Edge("1", n1, n2, modality=2)
e2 = st.structs.edge.Edge("2", n1, n3, modality="test")
e4 = st.structs.edge.Edge("4", n1, n3, modality="testb")
e3 = st.structs.edge.Edge("3", n1, n4, directed=False)
e5 = st.structs.edge.Edge("5", n1, n1)


graph.add_node(n1)
graph.add_nodes([n2, n3, n4])
graph.add_edge(e1)
graph.add_edge(e2)
graph.add_edge(e3)
graph.add_edge(e4)
graph.add_edge(e5)


#print(n1)
#print(e1)
print(graph)

print(str(st.canonize.to_cgraph(graph)))

# modality = edge labels?
# directed edges are supported after all? or is this experimental?
# parallel edges are supported