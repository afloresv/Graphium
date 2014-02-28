#!/bin/bash
for i in {0..19}
do
	diff log/DEX-10M-$1-$i.log log/Neo4j-10M-$1-$i.log
done
