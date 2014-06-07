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
import org.neo4j.tooling.*;
import org.neo4j.kernel.*;
import org.neo4j.helpers.collection.*;

public class Neo4jRDF extends Graphium {

	private GraphDatabaseService graphDB;
	private GlobalGraphOperations globalOP;
	private IndexManager indexManager;
	private Index<Node> indexURI, indexBlankNode;
	private RelationshipType relType;
	private Transaction tx;

	public Neo4jRDF(String path, int _V, int _E) {
		V = _V;
		E = _E;
		graphDB = new GraphDatabaseFactory().
			newEmbeddedDatabaseBuilder(path).
			loadPropertiesFromFile("conf/neo4j.properties").
			newGraphDatabase();
		tx = graphDB.beginTx();
		globalOP = GlobalGraphOperations.at(graphDB);
		indexManager   = graphDB.index();
		indexURI       = indexManager.forNodes(Attr.URI);
		indexBlankNode = indexManager.forNodes(Attr.BlankNode);
		relType = globalOP.getAllRelationshipTypes().iterator().next();
	}

	public Vertex getVertexURI(String str) {
		Node id = indexURI.get(Attr.URI,str).getSingle();
		if (id==null) return null;
		return (new VertexNeo4j(id));
	}

	public Vertex getVertexBlankNode(String str) {
		Node id = indexBlankNode.get(Attr.BlankNode,str).getSingle();
		if (id==null) return null;
		return (new VertexNeo4j(id));
	}

	public GraphIterator<Vertex> getAllVertex() {
		Iterator<Node> itAll = globalOP.getAllNodes().iterator();
		return (new IterVertexNeo4j(itAll));
	}

	public void close() {
		tx.success();
		tx.close();
		graphDB.shutdown();
	}

	// +----------------+
	// | Useful classes |
	// +----------------+

	public class VertexNeo4j extends Vertex {
		private Node node_id;
		public VertexNeo4j(Node _id) {
			node_id = _id;
		}
		public boolean isURI()       { return node_id.hasProperty(Attr.URI); }
		public boolean isBlankNode() { return node_id.hasProperty(Attr.BlankNode); }
		public boolean isLiteral()   { return node_id.hasProperty(Attr.Literal); }
		public URI getURI() {
			Object prop = node_id.getProperty(Attr.URI,null);
			if (prop == null) return null;
			return (new URI((String)prop));
		}
		public BlankNode getBlankNode() {
			Object prop = node_id.getProperty(Attr.BlankNode,null);
			if (prop == null) return null;
			return (new BlankNode((String)prop));
		}
		public String getStr() {
			Object prop = node_id.getProperty(Attr.Literal,null);
			if (prop == null) return null;
			return (String)prop;
		}
		public String getType() {
			return (String)node_id.getProperty(Attr.Type,null);
		}
		public String getLang() {
			return (String)node_id.getProperty(Attr.Lang,null);
		}
		public Boolean getBoolean() {
			return (Boolean)node_id.getProperty(Attr.valBool,null);
		}
		public Long getLong() {
			return (Long)node_id.getProperty(Attr.valInt,null);
		}
		public Double getDouble() {
			return (Double)node_id.getProperty(Attr.valDouble,null);
		}
		public Date getDate() {
			Object prop = node_id.getProperty(Attr.valDate,null);
			if (prop == null) return null;
			return (new Date((Long)prop));
		}
		public GraphIterator<Edge> getEdgesOut() {
			return (new IterEdgeNeo4j(node_id.getRelationships
				(relType,Direction.OUTGOING).iterator()));
		}
		public GraphIterator<Edge> getEdgesIn() {
			return (new IterEdgeNeo4j(node_id.getRelationships
				(relType,Direction.INCOMING).iterator()));
		}
		public int getOutDegree() {
			return node_id.getDegree(Direction.OUTGOING);
		}
		public int getInDegree() {
			return node_id.getDegree(Direction.INCOMING);
		}
		@Override
		public boolean equals(Object other) {
			if (other instanceof VertexNeo4j)
				return (this.node_id.equals(((VertexNeo4j)other).node_id));
			return false;
		}
		public int hashCode() {
			return this.node_id.hashCode();
		}
	}

	public class EdgeNeo4j implements Edge {
		private Relationship rel_id;
		public EdgeNeo4j(Relationship _id) {
			rel_id = _id;
		}
		public URI getURI() {
			return (new URI((String)rel_id.getProperty(Attr.Predicate)));
		}
		public Vertex getStart() {
			return (new VertexNeo4j(rel_id.getStartNode()));
		}
		public Vertex getEnd() {
			return (new VertexNeo4j(rel_id.getEndNode()));
		}
	}

	public class IterVertexNeo4j implements GraphIterator<Vertex> {
		Iterator<Node> it;
		public IterVertexNeo4j(Iterator<Node> _it) { it = _it; }
		public boolean hasNext() { return it.hasNext(); }
		public Vertex next() { return (new VertexNeo4j(it.next())); }
		public void close() {}
	}

	public class IterEdgeNeo4j implements GraphIterator<Edge> {
		Iterator<Relationship> it;
		public IterEdgeNeo4j(Iterator<Relationship> _it) { it = _it; }
		public boolean hasNext() { return it.hasNext(); }
		public Edge next() { return (new EdgeNeo4j(it.next())); }
		public void close() {}
	}
}
