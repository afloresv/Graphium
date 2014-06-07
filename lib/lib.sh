#!/bin/bash
NEO="concurrentlinkedhashmap-lru-1.3.1.jar:lib/geronimo-jta_1.1_spec-1.1.1.jar:lib/lucene-core-3.6.2.jar:lib/neo4j-cypher-2.1.1.jar:lib/neo4j-cypher-commons-2.1.1.jar:lib/neo4j-cypher-compiler-1.9-2.0.1.jar:lib/neo4j-cypher-compiler-2.0-2.0.1.jar:lib/neo4j-cypher-compiler-2.1-2.1.1.jar:lib/neo4j-graph-algo-2.1.1.jar:lib/neo4j-graph-matching-2.1.1.jar:lib/neo4j-jmx-2.1.1.jar:lib/neo4j-kernel-2.1.1.jar:lib/neo4j-lucene-index-2.1.1.jar:lib/neo4j-primitive-collections-2.1.1.jar:lib/neo4j-shell-2.1.1.jar:lib/neo4j-udc-2.1.1.jar:lib/opencsv-2.3.jar:lib/org.apache.servicemix.bundles.jline-0.9.94_1.jar:lib/parboiled-core-1.1.6.jar:lib/parboiled-scala_2.10-1.1.6.jar:lib/scala-library-2.10.4.jar:lib/server-api-2.1.1.jar"
SPARKSEE="lib/sparkseejava.jar"
LIBS="./:lib/$NEO:$SPARKSEE"
FLAGS="-Xms2g -Xmx2g -XX:PermSize=2g -XX:MaxPermSize=2g -XX:-UseGCOverheadLimit "
