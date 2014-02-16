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

public class Q11 extends BerlinQuery {

	int[][] inst = {
		{215,423241}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q11(args[1],args[2]);
		Q.runQuery(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q11(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {

		r = new ResultGenerator();
		Vertex iNode;
		Edge rel;
		IteratorGraph it;

		iNode = g.getVertexURI(bsbminst+"dataFromVendor"+inst[ind][0]+"/Offer"+inst[ind][1]);
		if (iNode == null) return;
		it = iNode.getEdgesOut();
		while (it.hasNext()) {
			rel = it.next();
			// bsbminst:dataFromVendor215/Offer423241 ?property ?hasValue
			(r.newResult(rel.getURI(),rel.getEnd().getAny(),"")).print();
		}
		it.close();

		// UNION

		it = iNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			// ?isValueOf ?property bsbminst:dataFromVendor215/Offer423241
			(r.newResult(rel.getURI(),"",rel.getStart().getAny())).print();
		}
		it.close();
	}
}
