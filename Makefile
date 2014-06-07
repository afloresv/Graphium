NEO = lib/lucene-core-3.6.2.jar:lib/neo4j-cypher-2.1.1.jar:lib/neo4j-cypher-commons-2.1.1.jar:lib/neo4j-cypher-compiler-1.9-2.0.1.jar:lib/neo4j-cypher-compiler-2.0-2.0.1.jar:lib/neo4j-cypher-compiler-2.1-2.1.1.jar:lib/neo4j-graph-algo-2.1.1.jar:lib/neo4j-graph-matching-2.1.1.jar:lib/neo4j-jmx-2.1.1.jar:lib/neo4j-kernel-2.1.1.jar:lib/neo4j-lucene-index-2.1.1.jar
SPARKSEE = lib/sparkseejava.jar

LIBS = "./:$(NEO):$(SPARKSEE)"
FLAGS = -source 7 -nowarn -cp $(LIBS)

all: load core traverse mining chrysalis-demo

load:
	rm -f ve/usb/ldc/graphium/load/*.class
	javac $(FLAGS) ve/usb/ldc/graphium/load/*.java

core:
	rm -f ve/usb/ldc/graphium/core/*.class
	javac $(FLAGS) ve/usb/ldc/graphium/core/*.java

traverse:
	rm -f ve/usb/ldc/graphium/traverse/*.class
	javac $(FLAGS) ve/usb/ldc/graphium/traverse/*.java

mining:
	rm -f ve/usb/ldc/graphium/mining/*.class
	javac $(FLAGS) ve/usb/ldc/graphium/mining/*.java

chrysalis-demo:
	rm -f ve/usb/ldc/graphium/chrysalis/*.class
	javac $(FLAGS) ve/usb/ldc/graphium/chrysalis/*.java

testing:
	rm -f test/*.class
	javac $(FLAGS) test/*.java
