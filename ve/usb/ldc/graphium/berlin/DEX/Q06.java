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

public class Q06 extends DEX implements BerlinQuery {

	String[] inst = {
		"ambilateral",
		"iterant"
	};

	public Q06(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q06 testQ = new Q06(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long bsbmProductNode, rel;
		Objects edgeSet;
		ObjectsIterator it;
		String relStr, product, label;
		HashSet<Long> setProduct = new HashSet<Long>();

		// bsbm:Product
		bsbmProductNode = getNodeFromURI(bsbm+"Product");
		if (bsbmProductNode == NodeNotFound) return;
		edgeSet = g.explode(bsbmProductNode,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = getEdgeURI(rel);
			// ?product rdf:type bsbm:Product .
			if (relStr.equals(rdf+"type"))
				setProduct.add(getStartNode(rel));
		}
		it.close();
		edgeSet.close();

		for (Long productNode : setProduct) {
			product = getAnyProp(productNode);
			edgeSet = g.explode(productNode,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				label = getAnyProp(getEndNode(rel));
				// ?product rdfs:label ?label .
				// FILTER regex(?label, "string")
				if (getEdgeURI(rel).equals(rdfs+"label")
					&& label.matches(inst[ind]))
					(new ResultTuple(product,label)).print();
			}
			it.close();
			edgeSet.close();
		}
	}
}
