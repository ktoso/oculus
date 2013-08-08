#!/bin/sh
g++ -g -O3 -I. -pthread -I /usr/local/include image_hashes.cpp -L/usr/local/lib -lpHash -o image_hashes
