#!/bin/bash
NEO="lib/concurrentlinkedhashmap-lru-1.3.1.jar:lib/neo4j-lucene-index-1.9.jar:lib/geronimo-jta_1.1_spec-1.1.1.jar:lib/neo4j-shell-1.9.jar:lib/lucene-core-3.6.2.jar:lib/neo4j-udc-1.9.jar:lib/neo4j-cypher-1.9.jar:lib/neo4j-graph-algo-1.9.jar:lib/org.apache.servicemix.bundles.jline-0.9.94_1.jar:lib/neo4j-graph-matching-1.9.jar:lib/scala-library-2.10.0.jar:lib/neo4j-jmx-1.9.jar:lib/server-api-1.9.jar:lib/neo4j-kernel-1.9.jar"
SPARKSEE="lib/sparkseejava.jar"
LIBS="./:$NEO:$SPARKSEE"
FLAGS="-Xms4240m -Xmx4240m -XX:PermSize=4240m -XX:MaxPermSize=4240m -XX:-UseGCOverheadLimit "

mkdir -p log
echo "$3 $1 $2" > log/$3-$1-$2.log
touch log/$3-$1-$2.time

for i in {0..19}
do
	java $FLAGS -classpath $LIBS ve.usb.ldc.graphium.berlin.$1 $i $3 Berlin$2 > log/$3-$1-$2-$i.log 2>> log/$3-$1-$2.time
	echo "-------------------------" >> log/$3-$1-$2.log
	sort log/$3-$1-$2-$i.log >> log/$3-$1-$2.log
	rm log/$3-$1-$2-$i.log
	sleep 2
done
