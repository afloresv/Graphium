#!/bin/bash

for i in {1..12}
do
	q=`printf "Q%02d" $i`
	echo "Running $q"
	./berlin.sh $q $1 Neo4j
	./berlin.sh $q $1 Sparksee
	diff log/Neo4j-$q-$1.log log/Sparksee-$q-$1.log
done