cmake_minimum_required(VERSION 3.10)
project(nishe CXX)

set(CMAKE_INSTALL_LIBDIR "${PROJECT_SOURCE_DIR}/../../native")
set(CMAKE_FIND_LIBRARY_PREFIXES "lib" "")
set(CMAKE_FIND_LIBRARY_SUFFIXES ".dll.a" ".a" ".lib" ".so")

find_package(JNI)
if(NOT JNI_FOUND)
	message(FATAL_ERROR "Could not determine Java include path. Please set your JAVA_HOME environment variable to the location of the desired JDK.")
endif()

add_library(nishe SHARED
	"./src/DirectedGraph.cc"
	"./src/PartitionNest.cc"
	"./dev_roanh_cpqkeys_algo_Nishe.cpp"
)

set_target_properties(nishe PROPERTIES
	PROPERTY CMAKE_CXX_STANDARD 11
	PROPERTY CMAKE_CXX_STANDARD_REQUIRED ON
	OUTPUT_NAME ${CMAKE_PROJECT_NAME}
)

add_compile_options(-Wall -Wextra -pedantic)

target_include_directories(nishe
	PUBLIC
		$<BUILD_INTERFACE:${JNI_INCLUDE_DIRS}>

	PRIVATE
		${CMAKE_CURRENT_SOURCE_DIR}/include
)

install(TARGETS nishe RUNTIME LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR})