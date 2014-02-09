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
	public int[] NodeType = new int[3];
	public int[] AttrType = new int[6];
	public int   EdgeType;
	public String licenceDEX = "46YMV-NFXTZ-GCG8K-QZ8ME";

	public DEX(String path) {
		try {
			cfg = new DexConfig();
			cfg.setLicense(licenceDEX);
			dex = new Dex(cfg);
			db = dex.open(path+"/DexDB.dex", true);
			sess = db.newSession();
			g = sess.getGraph();

			for (int i=0 ; i<3 ; i++) {
				NodeType[i] = g.findType(prop[i]);
				AttrType[i] = g.findAttribute(NodeType[i], prop[i]);
			}
			for (int i=3 ; i<5 ; i++)
				AttrType[i] = g.findAttribute(NodeType[2], prop[i]);

			TypeListIterator itEdge = g.findEdgeTypes().iterator();
			if (!itEdge.hasNext()) {
				System.err.println("Error: No edge type found.");
				this.close();
				return;
			}
			EdgeType = itEdge.nextType();
			AttrType[5] = g.findAttribute(EdgeType, prop[0]);
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
		long id = g.findObject(AttrType[0],(new Value()).setString(strURI));
		if (id==Objects.InvalidOID) return null;
		return (new VertexDEX(id));
	}

	public class VertexDEX implements Vertex {
		private long node_id;
		private int node_type;
		public VertexDEX(long _id) {
			node_id = _id;
			node_type = g.getObjectType(node_id);
		}
		public boolean isURI() {
			return NodeType[0]==node_type;
		}
		public boolean isNodeID() {
			return NodeType[1]==node_type;
		}
		public boolean isLiteral() {
			return NodeType[2]==node_type;
		}
		public String getURI() {
			if (this.isURI()) {
				return g.getAttribute(node_id,AttrType[0]).getString();
			} else return null;
		}
		public String getNodeID() {
			if (this.isNodeID()) {
				return g.getAttribute(node_id,AttrType[1]).getString();
			} else return null;
		}
		public String getLiteral() {
			if (this.isLiteral()) {
				TextStream ts = g.getAttributeText(node_id,AttrType[2]);
				char[] buff = new char[100000];
				ts.read(buff,100000);
				ts.close();
				return (new String(buff)).trim();
			} else return null;
		}
		public String getAny() {
			String res = this.getURI();
			if (res==null) res = this.getNodeID();
			if (res==null) res = this.getLiteral();
			return res;
		}
		public IteratorGraph getEdgesOut() {
			return (new IteratorDEX(g.explode(node_id,EdgeType,EdgesDirection.Outgoing)));
		}
		public IteratorGraph getEdgesIn() {
			return (new IteratorDEX(g.explode(node_id,EdgeType,EdgesDirection.Ingoing)));
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
			return g.getAttribute(rel_id,AttrType[5]).getString();
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
