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

import com.sparsity.sparksee.gdb.*;

public class SparkseeRDF implements Graphium {

	public SparkseeConfig cfg;
	public Sparksee sparksee;
	public Database db;
	public Session sess;
	public com.sparsity.sparksee.gdb.Graph g;
	public int TypeURI, TypeBlankNode, TypeLiteral, TypeEdge;
	public int AttrURI, AttrBlankNode, AttrLiteral, AttrPredicate,
		AttrLang, AttrType, AttrBool, AttrInt, AttrDouble, AttrDate;
	public Value val = new Value();

	public SparkseeRDF(String path) {
		try {
			SparkseeProperties.load("conf/sparksee.cfg");
			cfg = new SparkseeConfig();
			sparksee = new Sparksee(cfg);
			db = sparksee.open(path+"/SparkseeDB.gdb", true);
			sess = db.newSession();
			g = sess.getGraph();

			// URI Nodes
			TypeURI = g.findType(Attr.URI);
			AttrURI = g.findAttribute(TypeURI, Attr.URI);

			// BlankNode Nodes
			TypeBlankNode = g.findType(Attr.BlankNode);
			AttrBlankNode = g.findAttribute(TypeBlankNode, Attr.BlankNode);

			// Literal Nodes
			TypeLiteral = g.findType(Attr.Literal);
			AttrLiteral = g.findAttribute(TypeLiteral, Attr.Literal);
			AttrLang    = g.findAttribute(TypeLiteral, Attr.Lang);
			AttrType    = g.findAttribute(TypeLiteral, Attr.Type);
			AttrBool    = g.findAttribute(TypeLiteral, Attr.valBool);
			AttrInt     = g.findAttribute(TypeLiteral, Attr.valInt);
			AttrDouble  = g.findAttribute(TypeLiteral, Attr.valDouble);
			AttrDate    = g.findAttribute(TypeLiteral, Attr.valDate);

			TypeListIterator itEdge = g.findEdgeTypes().iterator();
			if (!itEdge.hasNext()) {
				System.err.println("Error: No edge type found.");
				this.close();
				System.exit(1);
			}
			TypeEdge = itEdge.nextType();
			AttrPredicate = g.findAttribute(TypeEdge, Attr.Predicate);
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}

	public Vertex getVertexURI(String str) {
		long id = g.findObject(AttrURI,val.setString(str));
		if (id==Objects.InvalidOID) return null;
		return (new VertexSparksee(id));
	}

	public Vertex getVertexBlankNode(String str) {
		long id = g.findObject(AttrBlankNode,val.setString(str));
		if (id==Objects.InvalidOID) return null;
		return (new VertexSparksee(id));
	}

	public GraphIterator<Vertex> getAllVertex() {
		// URI's
		Objects allObj = g.select(TypeURI), tempObj;
		// BlankNode's
		tempObj = g.select(TypeBlankNode);
		allObj.union(tempObj);
		tempObj.close();
		// Literals
		tempObj = g.select(TypeLiteral);
		allObj.union(tempObj);
		tempObj.close();
		return (new IterVertexSparksee(allObj));
	}

	public void close() {
		sess.close();
		db.close();
		sparksee.close();
	}

	// +----------------+
	// | Useful classes |
	// +----------------+

	public class VertexSparksee extends Vertex {
		private long node_id;
		private int node_type;
		public VertexSparksee(long _id) {
			node_id = _id;
			node_type = g.getObjectType(node_id);
		}
		public boolean isURI()       { return node_type==TypeURI; }
		public boolean isBlankNode() { return node_type==TypeBlankNode; }
		public boolean isLiteral()   { return node_type==TypeLiteral; }
		public URI getURI() {
			if (this.isURI()) {
				return (new URI(g.getAttribute(node_id,AttrURI).getString()));
			} else return null;
		}
		public BlankNode getBlankNode() {
			if (this.isBlankNode()) {
				return (new BlankNode(g.getAttribute(node_id,AttrBlankNode).getString()));
			} else return null;
		}
		public String getStr() {
			if (this.isLiteral()) {
				TextStream ts = g.getAttributeText(node_id,AttrLiteral);
				char[] buff = new char[100000];
				ts.read(buff,100000);
				ts.close();
				return (new String(buff)).trim();
			} else return null;
		}
		public String getType() {
			if (!this.isLiteral()) return null;
			val = g.getAttribute(node_id,AttrType);
			if (!val.isNull()) {
				return val.getString();
			} else return null;
		}
		public String getLang() {
			if (!this.isLiteral()) return null;
			val = g.getAttribute(node_id,AttrLang);
			if (!val.isNull()) {
				return val.getString();
			} else return null;
		}
		public Boolean getBoolean() {
			if (!this.isLiteral()) return null;
			val = g.getAttribute(node_id,AttrBool);
			if (!val.isNull()) {
				return val.getBoolean();
			} else return null;
		}
		public Long getLong() {
			if (!this.isLiteral()) return null;
			val = g.getAttribute(node_id,AttrInt);
			if (!val.isNull()) {
				return val.getLong();
			} else return null;
		}
		public Double getDouble() {
			if (!this.isLiteral()) return null;
			val = g.getAttribute(node_id,AttrDouble);
			if (!val.isNull()) {
				return val.getDouble();
			} else return null;
		}
		public Date getDate() {
			if (!this.isLiteral()) return null;
			val = g.getAttribute(node_id,AttrDate);
			if (!val.isNull()) {
				return val.getTimestampAsDate();
			} else return null;
		}
		public GraphIterator<Edge> getEdgesOut() {
			return (new IterEdgeSparksee(g.explode(node_id,TypeEdge,EdgesDirection.Outgoing)));
		}
		public GraphIterator<Edge> getEdgesIn() {
			return (new IterEdgeSparksee(g.explode(node_id,TypeEdge,EdgesDirection.Ingoing)));
		}
		@Override
		public boolean equals(Object other){
			if (other instanceof VertexSparksee)
				return (this.node_id == ((VertexSparksee)other).node_id);
			return false;
		}
		public int hashCode() {
			return (new Long(this.node_id)).hashCode();
		}
	}

	public class EdgeSparksee implements Edge {
		private long rel_id;
		public EdgeSparksee(long _id) {
			rel_id = _id;
		}
		public URI getURI() {
			return (new URI(g.getAttribute(rel_id,AttrPredicate).getString()));
		}
		public Vertex getStart() {
			return (new VertexSparksee(g.getEdgeData(rel_id).getTail()));
		}
		public Vertex getEnd() {
			return (new VertexSparksee(g.getEdgeData(rel_id).getHead()));
		}
	}

	public class IterVertexSparksee implements GraphIterator<Vertex> {
		Objects obj;
		ObjectsIterator it;
		public IterVertexSparksee(Objects _o) {
			obj = _o;
			it = obj.iterator();
		}
		public boolean hasNext() { return it.hasNext(); }
		public Vertex next() { return (new VertexSparksee(it.next())); }
		public void close() {
			it.close();
			obj.close();
		}
	}

	public class IterEdgeSparksee implements GraphIterator<Edge> {
		Objects obj;
		ObjectsIterator it;
		public IterEdgeSparksee(Objects _o) {
			obj = _o;
			it = obj.iterator();
		}
		public boolean hasNext() { return it.hasNext(); }
		public Edge next() { return (new EdgeSparksee(it.next())); }
		public void close() {
			it.close();
			obj.close();
		}
	}
}
