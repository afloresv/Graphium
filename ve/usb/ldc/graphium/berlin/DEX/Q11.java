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

package ve.usb.ldc.graphium.berlin.DEX;

import java.util.*;
import java.lang.*;
import java.io.*;

import com.sparsity.dex.gdb.*;

import ve.usb.ldc.graphium.core.*;
import ve.usb.ldc.graphium.berlin.general.*;

public class Q11 extends DEX implements BerlinQuery {

	int[][] inst = {
		{215,423241}
	};

	public Q11(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q11 testQ = new Q11(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long iNode, rel;
		Objects edgeSet;
		ObjectsIterator it;

		iNode = getNodeFromURI(bsbminst+"dataFromVendor"+inst[ind][0]+"/Offer"+inst[ind][1]);
		if (iNode == NodeNotFound) return;
		edgeSet = g.explode(iNode,EdgeType,EdgesDirection.Outgoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			// bsbminst:dataFromVendor215/Offer423241 ?property ?hasValue
			(new ResultTuple(getEdgeURI(rel),getAnyProp(getEndNode(rel)),"")).print();
		}
		it.close();
		edgeSet.close();

		// UNION

		edgeSet = g.explode(iNode,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			// ?isValueOf ?property bsbminst:dataFromVendor215/Offer423241
			(new ResultTuple(getEdgeURI(rel),"",getAnyProp(getStartNode(rel)))).print();
		}
		it.close();
		edgeSet.close();
	}
}
