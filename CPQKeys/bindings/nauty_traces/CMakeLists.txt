cmake_minimum_required(VERSION 3.10)
project(nauty_traces C)

set(CMAKE_INSTALL_LIBDIR "${PROJECT_SOURCE_DIR}/../../native")
set(CMAKE_FIND_LIBRARY_PREFIXES "lib" "")
set(CMAKE_FIND_LIBRARY_SUFFIXES ".dll.a" ".a" ".lib" ".so")

find_package(JNI)
if(NOT JNI_FOUND)
	message(FATAL_ERROR "Could not determine Java include path. Please set your JAVA_HOME environment variable to the location of the desired JDK.")
endif()

add_library(ntcore SHARED
	"./src/nautil.c"
	"./src/naugraph.c"
	"./src/naurng.c"
	"./src/nausparse.c"
	"./src/naututil.c"
	"./src/nauty.c"
	"./src/schreier.c"
	"./core.c"
)

add_library(nauty SHARED
	"./src/nautinv.c"
	"./dev_roanh_cpqkeys_algo_Nauty.c"
)

add_library(traces SHARED
	"./src/gtools.c"
	"./src/traces.c"
	"./dev_roanh_cpqkeys_algo_Traces.c"
)

set_target_properties(ntcore PROPERTIES
	PROPERTY C_STANDARD 11
	PROPERTY C_STANDARD_REQUIRED ON
	OUTPUT_NAME ntcore
)

set_target_properties(nauty PROPERTIES
	PROPERTY C_STANDARD 11
	PROPERTY C_STANDARD_REQUIRED ON
	OUTPUT_NAME nauty
)

set_target_properties(traces PROPERTIES
	PROPERTY C_STANDARD 11
	PROPERTY C_STANDARD_REQUIRED ON
	OUTPUT_NAME traces
)

add_compile_options(-Wall -Wextra -pedantic)

target_include_directories(ntcore
	PUBLIC
		$<BUILD_INTERFACE:${JNI_INCLUDE_DIRS}>

	PRIVATE
		${CMAKE_CURRENT_SOURCE_DIR}/include
		${CMAKE_CURRENT_SOURCE_DIR}/include/nauty
)

target_include_directories(nauty
	PUBLIC
		$<BUILD_INTERFACE:${JNI_INCLUDE_DIRS}>

	PRIVATE
		${CMAKE_CURRENT_SOURCE_DIR}/include
		${CMAKE_CURRENT_SOURCE_DIR}/include/nauty
)

target_include_directories(traces
	PUBLIC
		$<BUILD_INTERFACE:${JNI_INCLUDE_DIRS}>

	PRIVATE
		${CMAKE_CURRENT_SOURCE_DIR}/include
		${CMAKE_CURRENT_SOURCE_DIR}/include/nauty
)

target_link_libraries(nauty ntcore)
target_link_libraries(traces ntcore)

install(TARGETS ntcore RUNTIME LIBRARY DESTINATION "${CMAKE_INSTALL_LIBDIR}/lib")
install(TARGETS nauty RUNTIME LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR})
install(TARGETS traces RUNTIME LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR})