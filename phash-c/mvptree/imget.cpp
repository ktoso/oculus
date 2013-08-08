/*

    MVPTree c library
    Copyright (C) 2008-2009 Aetilius, Inc.
    All rights reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    D Grant Starkweather - dstarkweather@phash.org

*/

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <assert.h>
#include "pHash.h"

#include "mvptree.h"

#define MVP_BRANCHFACTOR 2
#define MVP_PATHLENGTH   5
#define MVP_LEAFCAP     25

#define MVP_UINT64ARRAY 8
#define UINT64ARRAY 8

static unsigned long long nbcalcs = 0;

float hamming_distance(MVPDP *pointA, MVPDP *pointB){
    if (!pointA || !pointB || pointA->datalen != pointB->datalen) return -1.0f;

    uint64_t a = *((uint64_t*)pointA->data);
    uint64_t b = *((uint64_t*)pointB->data);

    uint64_t x = a^b;
    const uint64_t m1  = 0x5555555555555555ULL;
    const uint64_t m2  = 0x3333333333333333ULL;
    const uint64_t h01 = 0x0101010101010101ULL;
    const uint64_t m4  = 0x0f0f0f0f0f0f0f0fULL;
    x -= (x >> 1) & m1;
    x = (x & m2) + ((x >> 2) & m2);
    x = (x + (x >> 4)) & m4;

    float result = (float)((x*h01)>>56);
    result = exp(result-1);
    nbcalcs++;
    return result;
}


int main(int argc, char **argv){
    if (argc < 3){
	printf("not enough input args\n");
	printf(" %s  command filename [directory] [radius]\n\n", argv[0]);
	printf("  command    - command - e.g. 'add', 'query' or 'print'\n");
	printf("  filename   - file from which to read the tree\n");
	printf("  directory  - directory (for add and query)\n");
	printf("  radius     - radius for query operation (default = 21.0\n");
	return 0;
    }

    const char *command  = argv[1];
    const char *filename = argv[2];
    const char *dirname  = argv[3];
    const float radius   = atof(argv[4]);

    const int knearest = 5;

    printf("command  - %s\n", command);
    printf("filename - %s\n", filename);
    printf("dir      - %s\n", dirname);
    printf("radius   - %f\n", radius);
    printf("knearest - %d\n", knearest);

    CmpFunc distance_func = hamming_distance;

    int nbfiles;
    char **files = ph_readfilenames(dirname, nbfiles);
    assert(files);

    fprintf(stdout,"\n %d files in %s\n\n", nbfiles, dirname);

    MVPDP **points = (MVPDP**)malloc(nbfiles*sizeof(MVPDP*));
    assert(points);

    MVPError err;
    MVPTree *tree = mvptree_read(filename,distance_func,MVP_BRANCHFACTOR,MVP_PATHLENGTH,\
                                                                         MVP_LEAFCAP, &err);
    assert(tree);

    if (!strncasecmp(command,"add",3) || !strncasecmp(command,"query",3)){
	int count = 0;
	ulong64 hashvalue;
	for (int i=0;i < nbfiles;i++){
	    char *name = strrchr(files[i],'/')+1;

	    if (ph_dct_imagehash(files[i], hashvalue) < 0){
		printf("Unable to get hash value.\n");
		continue;
	    }
	    printf("(%d) %llx %s\n", i, (unsigned long long)hashvalue, files[i]);

	    points[count] = dp_alloc((MVPDataType) UINT64ARRAY);
	    points[count]->id = strdup(name);
	    points[count]->data = malloc(1*UINT64ARRAY);
	    points[count]->datalen = 1;
	    memcpy(points[count]->data, &hashvalue, UINT64ARRAY);
	    count++;
	}

	printf("\n");

	if (!strncasecmp(command,"add", 3)){

	    printf("Add %d hashes to tree.\n", count);
	    MVPError error = mvptree_add(tree, points, count);
	    if (error != MVP_SUCCESS){
		printf("Unable to add hash values to tree.\n");
		goto cleanup;
	    }

	    printf("Save file.\n");
	    error = mvptree_write(tree, filename, 00755);
	    if (error != MVP_SUCCESS){
		printf("Unable to save file.\n");
		goto cleanup;
	    }

	} else if (!strncasecmp(command,"query", 3)){

	    unsigned int nbresults;
	    for (int i=0;i<count;i++){
		printf("(%d) looking up %s ...\n", i, files[i]);
		nbcalcs = 0;
		MVPDP **results = mvptree_retrieve(tree,points[i],knearest,\
                                                               radius, &nbresults, &err);
		printf("-----------%d results (%d calcs)--------------\n",nbresults,nbcalcs);
		for (int j=0;j<nbresults;j++){
		    printf("(%d) %s\n", j, results[j]->id);
		}
		printf("-----------------------------------------------\n");
		printf("Hit enter key.\n");
		getchar();
		free(results);
	    }
	}
    } else if (!strncasecmp(command,"print", 3)){
	printf("-----------------------print-------------------------\n");
	mvptree_print(stdout,tree);
	printf("-----------------------------------------------------\n\n");
    }

    mvptree_clear(tree, free);

cleanup:
    for (int i=0;i<nbfiles;i++){
	free(files[i]);
    }
    free(files);


    return 0;
}
