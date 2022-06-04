#include <jni.h>
#include "core.h"

void constructSparseGraph(JNIEnv* env, jobjectArray* adj, sparsegraph* graph){
	jsize len = (*env)->GetArrayLength(env, *adj);
	nauty_check(WORDSIZE, SETWORDSNEEDED(len), len, NAUTYVERSIONID);
	graph->nv = len;

	printf("len: %d\n", len);

	graph->nde = 0;
	for(int i = 0; i < len; i++){
		graph->nde += (*env)->GetArrayLength(env, (jintArray)((*env)->GetObjectArrayElement(env, *adj, i)));
	}

	printf("nde: %d\n", graph->nde);

	SG_ALLOC(*graph, len, graph->nde, "malloc");
	int offset = 0;
	for(int i = 0; i < len; i++){
		jintArray row = (jintArray)((*env)->GetObjectArrayElement(env, *adj, i));
		jsize rlen = (*env)->GetArrayLength(env, row);
		jint* elem = (*env)->GetIntArrayElements(env, row, 0);

		graph->v[i] = offset;
		graph->d[i] = rlen;

		printf("for row %d at offset %d with len %d\n", i, offset, rlen);

		for(int j = 0; j < rlen; j++){
			graph->e[offset] = elem[j];
			printf("add edge to %d\n", graph->e[offset]);
			offset++;
		}

		(*env)->ReleaseIntArrayElements(env, row, elem, 0);
	}
}

void parseColoring(JNIEnv* env, int len, jintArray* colors, int* labels, int* ptn){
	jint* colorData = (*env)->GetIntArrayElements(env, *colors, 0);
	for(int i = 0; i < len; i++){
		if(colorData[i] < 0){
			labels[i] = -colorData[i] - 1;
			ptn[i] = 0;
		}else{
			labels[i] = colorData[i] - 1;
			ptn[i] = 1;
		}
	}
	(*env)->ReleaseIntArrayElements(env, *colors, colorData, 0);
}
