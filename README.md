# CPQ Keys [![](https://img.shields.io/github/release/RoanH/CPQKeys.svg)](https://github.com/RoanH/CPQKeys/releases)
CPQ Keys is a project that aims to compare existing graph canonization algorithms on their suitability to canonize query graphs of conjunctive path queries (CPQ). This repository aims to provide a framework based on [gMark](https://github.com/RoanH/gMark) that can be used to easily evaluate and compare various canonization algorithms. More details about the project and codebase can be found in my technical report titled [CPQ Keys: a survey of graph canonization algorithms](https://research.roanh.dev/cpqkeys/CPQ%20Keys%20v1.1.pdf). A list of canonization algorithms set up for evaluation in this repository is given below, information for adding your own algorithms is in [a section near the end of this readme](#adding-algorithms).

- [Bliss](https://users.aalto.fi/~tjunttil/bliss/index.html) (v0.77, released 2021-02-18)
- [Nauty](https://pallini.di.uniroma1.it/) (v2.7 from 2.7r4, released 2022-07-01)
- [Nishe](https://github.com/b0ri5/nishe-googlecode) (36b97d3, released 2010-07-20)
- [Scott](https://theplatypus.github.io/scott/) (01d26aa, released 2020-05-10)
- [Traces](https://pallini.di.uniroma1.it/) (v2.2 from 2.7r4, released 2022-07-01)

## Getting Started
Running the project can be done either by using docker or by compiling the project from source. Here docker is primarily provided to offer an easy way to run the project without having to worry about any dependencies. Configuring the settings used to run the evaluation should be done by changing the constants in the [main class](CPQKeys/src/dev/roanh/cpqkeys/Main.java).

- [Running with docker](#docker)
- [Running from source](#from-source)

### Docker
The repository contains a docker configuration that can be used to build an image and run it. The docker image can be built be running the following command:

```sh
docker build -t cpqkeys .
```

You can then run the created image as follows:

```sh
docker run --rm cpqkeys
```

### From Source
At the moment running the project is only supported on Linux. Docker can be used to run the project on other operating systems. To compile and run the project from source it is first required to install various dependencies. The following dependencies are required:

- **Java 8 or higher**: required to run the framework, make sure to install the JDK and not only a JRE.
- **CMake**: required to compile Bliss, Nauty, Nishe, and Traces.
- **A compiler for C**: e.g. `gcc` is required to compile Nauty and Traces.
- **A compiler for C++**: e.g. `g++` is required to compile Bliss and Nishe.
- **Python**: python 3 is required to run Scott.

On a system with `apt` the following command can be used to install all dependencies:

```sh
apt install default-jdk cmake g++ gcc python3
```

After installing all the dependencies the JNI natives for the project can be compiled and installed by going into the [CPQKeys](CPQKeys) folder and running the following script:

```sh
./compileNatives.sh
```

Finally, the project as a whole can be compiled and executed using Gradle by running the following command:

```sh
./gradlew run
```

## Development
This repository contain an [Eclipse](https://www.eclipse.org/) & [Gradle](https://gradle.org/) project with [gMark](https://github.com/RoanH/gMark) as the only dependency. Development work can be done using the Eclipse IDE or using any other Gradle compatible IDE. A hosted version of the javadoc for this repository can be found at [cpqkeys.docs.roanh.dev](https://cpqkeys.docs.roanh.dev/) and the javadoc for gMark can be found at [gmark.docs.roanh.dev](https://gmark.docs.roanh.dev/). Both documentation pages should be useful when implementing your own algorithms. For compiling and running the project refer to [the section on compiling from source](#from-source).

### Adding Algorithms
Adding an algorithm to be evaluated is a fairly straightforward process and essentially comes down to implementing the [Algorithm interface](https://cpqkeys.docs.roanh.dev/index.html?dev/roanh/cpqkeys/Algorithm.html) and adding it to the list of algorithms in the [main class](CPQKeys/src/dev/roanh/cpqkeys/Main.java). For more information refer to the [technical report](https://research.roanh.dev/cpqkeys/CPQ%20Keys%20v1.1.pdf) or one of the existing algorithm implementations. An example template is also given below. Feel free to submit pull requests to add more algorithms to this repository.

```java
public class MyAlgorithm{
	/**
	 * Algorithm instance, using static instances the makes it easier
	 * to have multiple variants of the same algorithm (e.g., see nauty).
	 */
	public static final Algorithm INSTANCE = new Algorithm("MyAlgorithm", MyAlgorithm::computeCanon);
	
	/**
	 * Runs the algorithm on the given input graph.
	 * @param input The input graph.
	 * @return An array of time measurements containing in the first
	 *         index the graph transform time, in the second index the
	 *         native setup time (graph construction) and in the third
	 *         index the canonization time. All times are in nanoseconds.
	 */
	private static long[] computeCanon(Graph<Vertex, Predicate> input){
		return new long[]{
			0, //setup time in nanoseconds
			0, //native setup in nanoseconds
			0  //canonization time in nanoseconds
		};
	}
}
```

## History
Project development started: 28th of April, 2022.
