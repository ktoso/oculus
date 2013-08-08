/*

   pHash, the open source perceptual hash library
   Copyright (C) 2009 Aetilius, Inc.
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

   Evan Klinger - eklinger@phash.org
   David Starkweather - dstarkweather@phash.org

 */

#include <stdio.h>
#include <dirent.h>
#include <errno.h>
#include <vector>
#include <algorithm>
#include "pHash.h"

using namespace std;

#define TRUE 1
#define FALSE 0

//data structure for a hash and id
struct ph_imagepoint{
	ulong64 hash;
	char *id;
};

//aux function to create imagepoint data struct
struct ph_imagepoint* ph_malloc_imagepoint(){

	return (struct ph_imagepoint*)malloc(sizeof(struct ph_imagepoint));

}

//auxiliary function for sorting list of hashes 
bool cmp_lt_imp(struct ph_imagepoint dpa, struct ph_imagepoint dpb){
	int result = strcmp(dpa.id, dpb.id);
	if (result < 0)
		return TRUE;
	return FALSE;
}

void p(uint8_t* ints, int len) {
	for(int i=0; i<len; i++)
		printf("%u ", ints[i]);
	printf("\n");
}

int main(int argc, char **argv){
	if (argc < 2){
		printf("no input args\n");
		printf("expected: \"image_hashes [file name]\"\n");
		exit(1);
	}

	const char *path = argv[1];
	struct dirent *dir_entry;
	ph_imagepoint *dp = NULL;

	//first directory
	errno = 0;
	int i = 0;
	ulong64 tmphash;

	int mhLen = 0;
	uint8_t* mhHash = ph_mh_imagehash(path, mhLen);

	printf("mh_hash: ");
	p(mhHash, mhLen);

	if (ph_dct_imagehash(path, tmphash) < 0)  //calculate the hash
		return -1;

	dp = ph_malloc_imagepoint();              //store in structure with file name
	dp->id = dir_entry->d_name;
	dp->hash = tmphash;
	printf("dct_hash: %llu  \n\n", dp->hash);
	i++;

	return 0;
}
