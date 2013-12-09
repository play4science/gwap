#!/bin/sh
for file in *.md; do
	htmlfile="${file%.md}.html"
	sed 's/\.md/.html/' "$file" | pandoc -f markdown -t html -o "HTML/$htmlfile"
done
