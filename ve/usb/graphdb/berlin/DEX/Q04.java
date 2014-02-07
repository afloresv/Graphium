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

package ve.usb.graphdb.berlin.DEX;

import java.util.*;
import java.lang.*;
import java.io.*;

import com.sparsity.dex.gdb.*;

import ve.usb.graphdb.core.*;
import ve.usb.graphdb.berlin.general.*;

public class Q04 extends DEX implements BerlinQuery {

	int[][] inst = {
		{252,897,8047,137,47,93}
	};
	ArrayList<ResultTuple> results;

	public Q04(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q04 testQ = new Q04(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind, int off) {

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

		nURI = g.findObject(AttrType[0], v.setString(bsbminst+"ProductFeature"+inst[ind][2+off*2]));
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

		ObjectsIterator itProd = productSet.iterator();
		String product, temp;
		while (itProd.hasNext()) {
			HashSet<String>
				setL = new HashSet<String>(),
				setPT = new HashSet<String>(),
				setP = new HashSet<String>();

			nProd = itProd.next();
			edgeSet = g.explode(nProd,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				temp = g.getAttribute(rel,AttrType[5]).getString();
				if (temp.equals(rdfs+"label"))
					setL.add(getAnyProp(g.getEdgePeer(rel,nProd)));
				else if (temp.equals(bsbm+"productPropertyTextual1"))
					setPT.add(getAnyProp(g.getEdgePeer(rel,nProd)));
				else if (temp.equals(bsbm+"productPropertyNumeric"+(off+1)))
					setP.add(getAnyProp(g.getEdgePeer(rel,nProd)));
			}
			it.close();
			edgeSet.close();

			product = getAnyProp(nProd);
			for (String value : setP) { try {
				if (Integer.parseInt(value)>inst[ind][3+2*off])
					for (String label : setL)
						for (String propertyTextual : setPT)
							results.add(new ResultTuple(1,product,label,propertyTextual));
			} catch (NumberFormatException nfe) {} }
		}
		itProd.close();
		productSet.close();
	}

	public void runQuery(int ind) {

		results = new ArrayList<ResultTuple>();
		runQuery(ind,0);
		runQuery(ind,1);

		// ORDER BY ?label
		Collections.sort(results);

		// LIMIT 10
		for (int i=5 ; i<15 && i<results.size() ; i++)
			results.get(i).print();
	}
}
