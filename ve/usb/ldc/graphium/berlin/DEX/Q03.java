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

public class Q03 extends DEX implements BerlinQuery {

	int[][] inst = {
		{240,7647,228,156,7616}
	};

	public Q03(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q03 testQ = new Q03(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		Objects productSet, edgeSet, tempSet;
		ObjectsIterator it;

		long nURI, nProd, rel;

		nURI = getNodeFromURI(bsbminst+"ProductType"+inst[ind][0]);
		if (nURI == NodeNotFound) return;
		edgeSet = g.explode(nURI,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			if (!getEdgeURI(rel).equals(rdf+"type"))
				edgeSet.remove(rel);
		}
		productSet = g.tails(edgeSet);
		it.close();
		edgeSet.close();

		nURI = getNodeFromURI(bsbminst+"ProductFeature"+inst[ind][1]);
		if (nURI == NodeNotFound) return;
		edgeSet = g.explode(nURI,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			if (!getEdgeURI(rel).equals(bsbm+"productFeature"))
				edgeSet.remove(rel);
		}
		tempSet = g.tails(edgeSet);
		productSet.intersection(tempSet);
		tempSet.close();
		it.close();
		edgeSet.close();

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();
		ObjectsIterator itProd = productSet.iterator();
		String product, relStr;
		while (itProd.hasNext()) {
			HashSet<String>
				setL = new HashSet<String>(),
				setP1 = new HashSet<String>(),
				setP3 = new HashSet<String>();

			nProd = itProd.next();
			edgeSet = g.explode(nProd,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				if (relStr.equals(rdfs+"label"))
					setL.add(getAnyProp(getEndNode(rel)));
				else if (relStr.equals(bsbm+"productPropertyNumeric1"))
					setP1.add(getAnyProp(getEndNode(rel)));
				else if (relStr.equals(bsbm+"productPropertyNumeric3"))
					setP3.add(getAnyProp(getEndNode(rel)));
			}
			it.close();
			edgeSet.close();

			product = getAnyProp(nProd);
			for (String p1 : setP1) { try {
				if (Integer.parseInt(p1)>inst[ind][2]) {
					for (String p3 : setP3) { try {
						if (Integer.parseInt(p3)>inst[ind][3]) {
							for (String label : setL)
								results.add(new ResultTuple(1,product,label));
						}
					} catch (NumberFormatException nfe) {} }
				}
			} catch (NumberFormatException nfe) {} }
		}
		itProd.close();
		productSet.close();

		// ORDER BY ?label
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print();

	}
}
