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

public class Q03 extends BerlinQuery {

	int[][] inst = {
		{240,7647,228,156,7616}
	};

	public Q03(GraphDB _g) {
		g = _g;
	}

	public void runQuery(int ind) {

		HashSet[] sets = new HashSet[2];
		sets[0] = new HashSet<Vertex>();
		sets[1] = new HashSet<Vertex>();

		Vertex nURI, nProd;
		Edge rel;
		IteratorGraph it;

		nURI = g.getVertexURI(bsbminst+"ProductType"+inst[ind][0]);
		if (nURI == null) return;
		it = nURI.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			if (rel.getURI().equals(rdf+"type"))
				sets[0].add(rel.getStart());
		}
		it.close();

		nURI = g.getVertexURI(bsbminst+"ProductFeature"+inst[ind][1]);
		if (nURI == null) return;
		it = nURI.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			nProd = rel.getStart();
			if (rel.getURI().equals(bsbm+"productFeature")
				&& sets[0].contains(nProd))
				sets[1].add(nProd);
		}
		it.close();

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();
		Iterator<Vertex> itProd = sets[1].iterator();
		String product, relStr;
		while (itProd.hasNext()) {
			HashSet<String>
				setL = new HashSet<String>(),
				setP1 = new HashSet<String>(),
				setP3 = new HashSet<String>();

			nProd = itProd.next();
			it = nProd.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(rdfs+"label"))
					setL.add(rel.getEnd().getAny());
				else if (relStr.equals(bsbm+"productPropertyNumeric1"))
					setP1.add(rel.getEnd().getAny());
				else if (relStr.equals(bsbm+"productPropertyNumeric3"))
					setP3.add(rel.getEnd().getAny());
			}
			it.close();

			product = nProd.getAny();
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

		// ORDER BY ?label
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print();
	}
}
