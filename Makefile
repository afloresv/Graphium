NEO = lib/concurrentlinkedhashmap-lru-1.3.1.jar:lib/neo4j-lucene-index-1.9.jar:lib/geronimo-jta_1.1_spec-1.1.1.jar:lib/neo4j-shell-1.9.jar:lib/lucene-core-3.6.2.jar:lib/neo4j-udc-1.9.jar:lib/neo4j-cypher-1.9.jar:lib/neo4j-graph-algo-1.9.jar:lib/org.apache.servicemix.bundles.jline-0.9.94_1.jar:lib/neo4j-graph-matching-1.9.jar:lib/scala-library-2.10.0.jar:lib/neo4j-jmx-1.9.jar:lib/server-api-1.9.jar:lib/neo4j-kernel-1.9.jar
SPARKSEE = lib/sparkseejava.jar

LIBS = "./:$(NEO):$(SPARKSEE)"
FLAGS = -source 6 -nowarn -cp $(LIBS)

compile: clean
	javac $(FLAGS) ve/usb/ldc/graphium/core/*.java
	javac $(FLAGS) ve/usb/ldc/graphium/load/*.java
	javac $(FLAGS) ve/usb/ldc/graphium/berlin/*.java
	javac $(FLAGS) ve/usb/ldc/graphium/analysis/*.java

clean:
	rm -f ve/usb/ldc/graphium/core/*.class
	rm -f ve/usb/ldc/graphium/load/*.class
	rm -f ve/usb/ldc/graphium/berlin/*.class
	rm -f ve/usb/ldc/graphium/analysis/*.class
