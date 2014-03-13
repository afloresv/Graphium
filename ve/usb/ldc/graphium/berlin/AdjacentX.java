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

package ve.usb.ldc.graphium.berlin;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class AdjacentX extends BerlinQuery {

	public static void main(String[] args) {
		BerlinQuery Q = new AdjacentX(args[1],"../" + args[1] + "DB/" + args[2]);
		Q.runExperiment();
		Q.close();
	}

	public AdjacentX(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery() {

		r = new ResultGenerator();
		Vertex srcNode;
		Edge rel;
		IteratorGraph it;

		srcNode = g.getVertexURI(bsbm+"Product");
		if (srcNode == null) return;
		it = srcNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			// ?product rdf:type bsbm:Product .
			if (rel.getURI().equals(rdf+"type"))
				r.newResult(rel.getEnd().getAny()).print();
		}
		it.close();
	}
}
