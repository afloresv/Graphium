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

package ve.usb.graphdb.berlin;

import java.util.*;
import java.lang.*;
import java.io.*;

import com.sparsity.dex.gdb.*;

import ve.usb.graphdb.core.*;

public class DEXQ03 extends DEX implements BerlinQuery {

	int[][] inst = {
		{240,7647,228,156,7616}
	};

	public DEXQ03(String path) {
		super(path);
	}

	public static void main(String args[]) {
		DEXQ03 testQ = new DEXQ03(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		Objects productSet, edgeSet, tempSet;
		ObjectsIterator it;
		Value v = new Value();

		long nURI, nProd, rel;

		nURI = g.findObject(AttrType[0], v.setString(bsbminst+"ProductType"+inst[ind][0]));
		if (nURI == Objects.InvalidOID) return;
		edgeSet = g.explode(nURI,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			if (!g.getAttribute(rel,AttrType[5]).getString().equals(rdf+"type"))
				edgeSet.remove(rel);
		}
		productSet = g.tails(edgeSet);
		it.close();
		edgeSet.close();

		nURI = g.findObject(AttrType[0], v.setString(bsbminst+"ProductFeature"+inst[ind][1]));
		if (nURI == Objects.InvalidOID) return;
		edgeSet = g.explode(nURI,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			if (!g.getAttribute(rel,AttrType[5]).getString().equals(bsbm+"productFeature"))
				edgeSet.remove(rel);
		}
		tempSet = g.tails(edgeSet);
		productSet.intersection(tempSet);
		tempSet.close();
		it.close();
		edgeSet.close();

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();
		ObjectsIterator itProd = productSet.iterator();
		String product, temp;
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
				temp = g.getAttribute(rel,AttrType[5]).getString();
				if (temp.equals(rdfs+"label"))
					setL.add(getAnyProp(g.getEdgePeer(rel,nProd)));
				else if (temp.equals(bsbm+"productPropertyNumeric1"))
					setP1.add(getAnyProp(g.getEdgePeer(rel,nProd)));
				else if (temp.equals(bsbm+"productPropertyNumeric3"))
					setP3.add(getAnyProp(g.getEdgePeer(rel,nProd)));
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
