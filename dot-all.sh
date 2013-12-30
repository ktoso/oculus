#!/bin/sh

rm *.dot.png

for i in `ls | grep .dot`; do
  dot -Tpng "$i" > $i.png
done
