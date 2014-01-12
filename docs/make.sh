#!/bin/sh
pdflatex thesis.tex
bibtex thesis.aux
pdflatex thesis.tex
pdflatex thesis.tex
