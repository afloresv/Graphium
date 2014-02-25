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

public class Q01 extends BerlinQuery {

	int[][] inst = {
		{161,505,25,136},
		{477,15422,123,54}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q01(args[1],args[2]);
		Q.runQuery(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q01(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {

		r = new ResultGenerator(1);
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
		System.out.println("> "+sets[0].size());

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
		sets[0].clear();
		System.out.println("> "+sets[1].size());

		nURI = g.getVertexURI(bsbminst+"ProductFeature"+inst[ind][2]);
		if (nURI == null) return;
		it = nURI.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			nProd = rel.getStart();
			if (rel.getURI().equals(bsbm+"productFeature")
				&& sets[1].contains(nProd))
				sets[0].add(nProd);
		}
		it.close();
		System.out.println("> "+sets[0].size());

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();
		Iterator<Vertex> itProd = sets[0].iterator();
		String product, temp;
		while (itProd.hasNext()) {
			HashSet<String>
				setL = new HashSet<String>(),
				setV = new HashSet<String>();

			nProd = itProd.next();
			it = nProd.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				temp = rel.getURI();
				if (temp.equals(rdfs+"label"))
					setL.add(rel.getEnd().getAny());
				else if (temp.equals(bsbm+"productPropertyNumeric1"))
					setV.add(rel.getEnd().getAny());
			}

			product = nProd.getAny();
			for (String value : setV) { try {
				if (Integer.parseInt(value)>inst[ind][3])
					for (String label : setL)
						results.add(r.newResult(product,label));
			} catch (NumberFormatException nfe) {} }
		}

		// ORDER BY ?label
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print();
	}
}
