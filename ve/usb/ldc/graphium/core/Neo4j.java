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

package ve.usb.ldc.graphium.core;

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

public abstract class Neo4j implements GraphDB {

	public GraphDatabaseService graphDB;
	public GlobalGraphOperations globalOP;
	public IndexManager indexManager;
	public Index<Node> indexURI, indexNodeID;
	public RelationshipType relType;

	public Neo4j(String path) {
		graphDB = new GraphDatabaseFactory().
			newEmbeddedDatabaseBuilder(path).
			setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
			setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true").
			newGraphDatabase();
		globalOP = GlobalGraphOperations.at(graphDB);
		indexManager = graphDB.index();
		indexURI    = indexManager.forNodes(prop[0]);
		indexNodeID = indexManager.forNodes(prop[1]);
		relType = globalOP.getAllRelationshipTypes().iterator().next();
	}

	public void close() {
		graphDB.shutdown();
	}

	public String getAnyProp(Node node) {
		String res = null;
		for (int i=0 ; i<3 ; i++) {
			if (node.hasProperty(prop[i])) {
				res = (String)node.getProperty(prop[i]);
				break;
			}
		}
		return res;
	}

	public final Node NodeNotFound = null;
	public Node getNodeFromURI(String strURI) {
		return indexURI.get(prop[0],strURI).getSingle();
	}

	public String getEdgeURI(Relationship rel) {
		return (String)rel.getProperty(prop[0]);
	}

	public Node getStartNode(Relationship rel) {
		return rel.getStartNode();
	}

	public Node getEndNode(Relationship rel) {
		return rel.getEndNode();
	}
}
