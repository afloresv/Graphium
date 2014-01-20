/*
 *  Copyright (C) 2014, Universidad Simon Bolivar
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ve.usb.graphdb.core;

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

public class Neo4j implements GraphDB {

	public GraphDatabaseService graphDB;
	public GlobalGraphOperations globalOP;

	public Neo4j(String path) {
		graphDB = new GraphDatabaseFactory().
			newEmbeddedDatabaseBuilder(path).
			setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
			setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true").
			newGraphDatabase();
		globalOP = GlobalGraphOperations.at(graphDB);
	}

	public void close() {
		graphDB.shutdown();
	}
}

/*
Iterator<Node> nodeIt = globalOP.getAllNodes().iterator();
nodeIt.next();

String val;
Node node;
int V=0, E=0;

if (nodeIt.hasNext()) {
	while(nodeIt.hasNext()) {
		node = nodeIt.next();
		V++;
		if (node.hasProperty("URI"))
			val = "URI | " + (String)node.getProperty("URI");
		else if (node.hasProperty("NodeID"))
			val = "NodeID | " + (String)node.getProperty("NodeID");
		else if (node.hasProperty("Literal"))
			val = "Literal | " + (String)node.getProperty("Literal");
		else
			val = "NOOOOOO";
		System.out.println(val);
	}
}

Iterator<Relationship> relIt = globalOP.getAllRelationships().iterator();
while(relIt.hasNext()) {
	relIt.next();
	E++;
}

System.out.println("Nodes: "+V);
System.out.println("Edges: "+E);

this.close();
*/
