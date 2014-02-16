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

public class Neo4j implements GraphDB {

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
		indexURI    = indexManager.forNodes(Attr.URI);
		indexNodeID = indexManager.forNodes(Attr.NodeID);
		relType = globalOP.getAllRelationshipTypes().iterator().next();
	}

	public Vertex getVertexURI(String strURI) {
		Node id = indexURI.get(Attr.URI,strURI).getSingle();
		if (id==null) return null;
		return (new VertexNeo4j(id));
	}

	public void close() {
		graphDB.shutdown();
	}

	public class VertexNeo4j extends Vertex {
		private Node node_id;
		public VertexNeo4j(Node _id) {
			node_id = _id;
		}
		public boolean isURI()     { return node_id.hasProperty(Attr.URI); }
		public boolean isNodeID()  { return node_id.hasProperty(Attr.NodeID); }
		public boolean isLiteral() { return node_id.hasProperty(Attr.Literal); }
		public String getURI() {
			if (this.isURI()) {
				return ("<" + (String)node_id.getProperty(Attr.URI) + ">");
			} else return null;
		}
		public String getNodeID() {
			if (this.isNodeID()) {
				return ("_:" + (String)node_id.getProperty(Attr.NodeID));
			} else return null;
		}
		public String getLiteral() {
			if (this.isLiteral()) {
				String lit = (String)node_id.getProperty(Attr.Literal);
				Object extra = node_id.getProperty(Attr.Lang,null);
				if (extra==null) {
					extra = node_id.getProperty(Attr.Type,null);
					if (extra!=null) lit += "^^<" + (String)extra + ">";
				} lit += "@" + (String)extra;
				return lit;
			} else return null;
		}
		public IteratorGraph getEdgesOut() {
			return (new IteratorNeo4j(node_id.getRelationships
				(relType,Direction.OUTGOING).iterator()));
		}
		public IteratorGraph getEdgesIn() {
			return (new IteratorNeo4j(node_id.getRelationships
				(relType,Direction.INCOMING).iterator()));
		}
		@Override
		public boolean equals(Object other){
			if (other instanceof VertexNeo4j)
				return (this.node_id.equals(((VertexNeo4j)other).node_id));
			return false;
		}
	}

	public class EdgeNeo4j implements Edge {
		private Relationship rel_id;
		public EdgeNeo4j(Relationship _id) {
			rel_id = _id;
		}
		public String getURI() {
			return (String)rel_id.getProperty(Attr.Predicate);
		}
		public Vertex getStart() {
			return (new VertexNeo4j(rel_id.getStartNode()));
		}
		public Vertex getEnd() {
			return (new VertexNeo4j(rel_id.getEndNode()));
		}
	}

	public class IteratorNeo4j implements IteratorGraph {
		Iterator<Relationship> it;
		public IteratorNeo4j(Iterator<Relationship> _it) {
			it = _it;
		}
		public boolean hasNext() {
			return it.hasNext();
		}
		public Edge next() {
			return (new EdgeNeo4j(it.next()));
		}
		public void remove() {}
		public void close() {}
	}
}
