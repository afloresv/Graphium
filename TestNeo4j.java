import java.util.*;
import java.lang.*;
import java.io.*;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.factory.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.unsafe.batchinsert.*;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.cypher.javacompat.*;
import org.neo4j.tooling.*;
import org.neo4j.kernel.*;
import org.neo4j.helpers.collection.*;

public class TestNeo4j {

	public GraphDatabaseService graphDB;
	public GlobalGraphOperations globalOP;

	public void testGDBM(String pathDB) {
		graphDB = new GraphDatabaseFactory().
			newEmbeddedDatabaseBuilder(pathDB).
			setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
			setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true").
			newGraphDatabase();
		globalOP = GlobalGraphOperations.at(graphDB);
		Iterator<Node> nodeIt = globalOP.getAllNodes().iterator();
		nodeIt.next();

		String val;
		Node node;
		int V=0, E=0;

		if (nodeIt.hasNext()) {
			while(nodeIt.hasNext()) {
				node = nodeIt.next();
				V++;
				/*if (node.hasProperty("URI"))
					val = "URI | " + (String)node.getProperty("URI");
				else if (node.hasProperty("NodeID"))
					val = "NodeID | " + (String)node.getProperty("NodeID");
				else if (node.hasProperty("Literal"))
					val = "Literal | " + (String)node.getProperty("Literal");
				else
					val = "NOOOOOO";
				System.out.println(val);*/
			}
		}

		Iterator<Relationship> relIt = globalOP.getAllRelationships().iterator();
		while(relIt.hasNext()) {
			relIt.next();
			E++;
		}

		System.out.println("Nodes: "+V);
		System.out.println("Edges: "+E);

		graphDB.shutdown();
	}
}
