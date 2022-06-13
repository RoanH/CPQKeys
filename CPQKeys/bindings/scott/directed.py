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
from core import read_graph
import time

start = time.time_ns()
graph = read_graph(directed = True)

mid = time.time_ns()
canon = st.canonize.to_cgraph(graph)

end = time.time_ns()
print("canonical form:", canon)
print("setup time:", mid - start)
print("canon time:", end - mid)