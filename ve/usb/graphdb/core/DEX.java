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

import com.sparsity.dex.gdb.*;

public class DEX implements GraphDB {

	private DexConfig cfg;
	private Dex dex;
	private Database db;
	private Session sess;
	private com.sparsity.dex.gdb.Graph g;
	private int[] NodeType = new int[4];
	private int[] AttrType = new int[6];
	private int   EdgeType;
	private String licenceDEX = "46YMV-NFXTZ-GCG8K-QZ8ME";

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
				sess.close();
				db.close();
				dex.close();
				return;
			}
			EdgeType = itEdge.nextType();
			AttrType[5] = g.findAttribute(EdgeType, prop[5]);
		} catch (FileNotFoundException e){
			System.err.println("Error: " + e.getMessage());
		} finally {
			this.close();
		}
	}

	public void close() {
		sess.close();
		db.close();
		dex.close();
	}
}

/*
Value value = new Value();
for (int i=0 ; i<3 ; i++) {
	Objects objNodes = g.select(NodeType[i]);
	ObjectsIterator it = objNodes.iterator();
	while (it.hasNext()) {
		V++;
		long NodeID = it.next();
		if (i==2) {
			TextStream valStream = g.getAttributeText(NodeID, AttrType[i]);
			if (!valStream.isNull()) {
				int read;
				StringBuffer str = new StringBuffer();
				do {
					char[] buff = new char[10];
					read = valStream.read(buff, 10);
					str.append(buff, 0, read);
				}
				while (read > 0);
				System.out.println(prop[i]+" | "+str);
			}
			valStream.close();
		} else {
			g.getAttribute(NodeID, AttrType[i], value);
			System.out.println(prop[i]+" | "+value.getString());
		}
	}
	objNodes.close();
	it.close();
}

Objects objEdges = g.select(EdgeType);
ObjectsIterator it = objEdges.iterator();
while (it.hasNext()) {
	it.next();
	E++;
}
objEdges.close();
it.close();

System.out.println("Nodes: "+V);
System.out.println("Edges: "+E);
*/
