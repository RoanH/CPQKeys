cmake_minimum_required(VERSION 3.10)
project(nauty C)

set(CMAKE_INSTALL_LIBDIR "${PROJECT_SOURCE_DIR}/../../native")
set(CMAKE_FIND_LIBRARY_PREFIXES "lib" "")
set(CMAKE_FIND_LIBRARY_SUFFIXES ".dll.a" ".a" ".lib" ".so")

find_package(JNI)
if(NOT JNI_FOUND)
	message(FATAL_ERROR "Could not determine Java include path. Please set your JAVA_HOME environment variable to the location of the desired JDK.")
endif()

add_library(nauty SHARED
	"./src/nautil.c"
	"./src/naugraph.c"
	"./src/naurng.c"
	"./src/nausparse.c"
	"./src/naututil.c"
	"./src/nauty.c"
	"./src/schreier.c"
	"./dev_roanh_cpqkeys_algo_Nauty.c"
)

set_target_properties(nauty PROPERTIES
	PROPERTY C_STANDARD 11
	PROPERTY C_STANDARD_REQUIRED ON
	OUTPUT_NAME ${CMAKE_PROJECT_NAME}
)

target_include_directories(nauty
	PUBLIC
		$<BUILD_INTERFACE:${JNI_INCLUDE_DIRS}>

	PRIVATE
		${CMAKE_CURRENT_SOURCE_DIR}/include
)

install(TARGETS nauty RUNTIME DESTINATION ${CMAKE_INSTALL_LIBDIR})