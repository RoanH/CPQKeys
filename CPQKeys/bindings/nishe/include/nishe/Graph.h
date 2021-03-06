#ifndef INCLUDE_NISHE_GRAPH_H_
#define INCLUDE_NISHE_GRAPH_H_

/*
  Copyright 2010 Greg Tener
  Released under the Lesser General Public License v3.
*/

#include <vector>
#include <utility>
#include <map>
#include <cstddef>

// a vertex is any nonnegatve integer
typedef size_t vertex_t;

namespace nishe {

template<typename graph_t>
bool is_automorphism(const graph_t &G, const int *x);

/*
 * The graph class must provide the ability to iterate over the neighbors
 * of a vertex.
 */
template<typename nbhr_t, typename attr_t, typename attr_sum_t>
class Graph {
 public:

  virtual ~Graph() {
  }

  typedef nbhr_t nbhr;
  typedef attr_t attr;
  typedef attr_sum_t attr_sum;
  static const int NOT_FOUND;

  const nbhr_t *get_nbhd(vertex_t u) const {
    return &vNbhds.at(u).front();
  }

  size_t get_nbhd_size(vertex_t u) const {
    return vNbhds.at(u).size();
  }

  int vertex_count() const {
    return vNbhds.size();
  }

  void clear() {
    vNbhds.clear();
  }

  virtual vertex_t nbhr_vertex(const nbhr_t &nbhr) const = 0;
  virtual attr_t nbhr_attr(const nbhr_t &nbhr) const = 0;

  // increases the number of vertices to u if needed
  // (also adds all vertices less than u)
  void add_vertex(vertex_t u) {
    if (vNbhds.size() < u + 1) {
      vNbhds.resize(u + 1);
    }
  }

  // search for v in the nbhd of u
  int find_nbhr(vertex_t u, vertex_t v) {
    for (int i = 0; i < vNbhds.at(u).size(); i++) {
      if (nbhr_vertex(vNbhds.at(u).at(i)) == v) {
        return i;
      }
    }

    return NOT_FOUND;
  }

 protected:
  std::vector<std::vector<nbhr_t> > vNbhds;
};

template<typename nbhr_t, typename attr_t, typename attr_sum_t>
const int Graph<nbhr_t, attr_t, attr_sum_t>::NOT_FOUND = -1;

}  // namespace nishe

#endif  // INCLUDE_NISHE_GRAPH_H_
