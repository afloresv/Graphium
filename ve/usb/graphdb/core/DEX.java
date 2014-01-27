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

public abstract class DEX implements GraphDB {

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

	public String getAnyProp(long node) {
		String res = null;
		int ntype = g.getObjectType(node);
		for (int i=0 ; i<3 ; i++) {
			if (NodeType[i]==ntype) {
				if (i==2) {
					TextStream ts = g.getAttributeText(node,AttrType[i]);
					char[] buff = new char[100000];
					ts.read(buff,100000);
					ts.close();
					res = (new String(buff)).trim();
				} else {
					res = g.getAttribute(node,AttrType[i]).getString();
				}
				break;
			}
		}
		return res;
	}
}
