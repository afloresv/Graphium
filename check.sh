#!/bin/bash
for i in {0..19}
do
	for j in {1..12}
	do
		q=`printf "%02d" $j`
		diff log/DEX-10M-Q$q-$i.log log/Neo4j-10M-Q$q-$i.log
	done
done
