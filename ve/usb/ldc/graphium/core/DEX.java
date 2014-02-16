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

import com.sparsity.dex.gdb.*;

public class DEX implements GraphDB {

	public DexConfig cfg;
	public Dex dex;
	public Database db;
	public Session sess;
	public com.sparsity.dex.gdb.Graph g;
	public int TypeURI, TypeNodeID, TypeLiteral, TypeEdge;
	public int AttrURI, AttrNodeID, AttrLiteral, AttrPredicate,
		AttrLang, AttrType, AttrBool, AttrInt, AttrDouble, AttrDate;
	public static String licence = "Q4GTF-H9X01-EJTM2-9MM89";

	public DEX(String path) {
		try {
			cfg = new DexConfig();
			cfg.setLicense(licence);
			dex = new Dex(cfg);
			db = dex.open(path+"/DexDB.dex", true);
			sess = db.newSession();
			g = sess.getGraph();

			// URI Nodes
			TypeURI = g.findType(Attr.URI);
			AttrURI = g.findAttribute(TypeURI, Attr.URI);

			// NodeID Nodes
			TypeNodeID = g.findType(Attr.NodeID);
			AttrNodeID = g.findAttribute(TypeNodeID, Attr.NodeID);

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
				return;
			}
			TypeEdge = itEdge.nextType();
			AttrPredicate = g.findAttribute(TypeEdge, Attr.Predicate);
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void close() {
		sess.close();
		db.close();
		dex.close();
	}

	public Vertex getVertexURI(String strURI) {
		long id = g.findObject(AttrURI,(new Value()).setString(strURI));
		if (id==Objects.InvalidOID) return null;
		return (new VertexDEX(id));
	}

	public class VertexDEX extends Vertex {
		private long node_id;
		private int node_type;
		public VertexDEX(long _id) {
			node_id = _id;
			node_type = g.getObjectType(node_id);
		}
		public boolean isURI()     { return node_type==TypeURI; }
		public boolean isNodeID()  { return node_type==TypeNodeID; }
		public boolean isLiteral() { return node_type==TypeLiteral; }
		public String getURI() {
			if (this.isURI()) {
				return ("<" + g.getAttribute(node_id,AttrURI).getString() + ">");
			} else return null;
		}
		public String getNodeID() {
			if (this.isNodeID()) {
				return ("_:" + g.getAttribute(node_id,AttrNodeID).getString());
			} else return null;
		}
		public String getLiteral() {
			if (this.isLiteral()) {
				TextStream ts = g.getAttributeText(node_id,AttrLiteral);
				char[] buff = new char[100000];
				ts.read(buff,100000);
				ts.close();
				String lit = (new String(buff)).trim();
				Value extra = g.getAttribute(node_id,AttrLang);
				if (extra.isNull()) {
					extra = g.getAttribute(node_id,AttrType);
					if (!extra.isNull()) lit += "^^<" + extra.getString() + ">";
				} lit += "@" + extra.getString();
				return lit;
			} else return null;
		}
		public IteratorGraph getEdgesOut() {
			return (new IteratorDEX(g.explode(node_id,TypeEdge,EdgesDirection.Outgoing)));
		}
		public IteratorGraph getEdgesIn() {
			return (new IteratorDEX(g.explode(node_id,TypeEdge,EdgesDirection.Ingoing)));
		}
		@Override
		public boolean equals(Object other){
			if (other instanceof VertexDEX)
				return (this.node_id == ((VertexDEX)other).node_id);
			return false;
		}
	}

	public class EdgeDEX implements Edge {
		private long rel_id;
		public EdgeDEX(long _id) {
			rel_id = _id;
		}
		public String getURI() {
			return g.getAttribute(rel_id,AttrPredicate).getString();
		}
		public Vertex getStart() {
			return (new VertexDEX(g.getEdgeData(rel_id).getTail()));
		}
		public Vertex getEnd() {
			return (new VertexDEX(g.getEdgeData(rel_id).getHead()));
		}
	}

	public class IteratorDEX implements IteratorGraph {
		Objects obj;
		ObjectsIterator it;
		public IteratorDEX(Objects _o) {
			obj = _o;
			it = obj.iterator();
		}
		public boolean hasNext() {
			return it.hasNext();
		}
		public Edge next() {
			return (new EdgeDEX(it.next()));
		}
		public void remove() {}
		public void close() {
			it.close();
			obj.close();
		}
	}
}
