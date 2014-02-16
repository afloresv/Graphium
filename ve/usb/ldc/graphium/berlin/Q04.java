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

public class Q04 extends BerlinQuery {

	int[][] inst = {
		{252,897,8047,137,47,93}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q04(args[1],args[2]);
		Q.runQuery(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q04(String gdbm, String path) {
		super(gdbm,path);
	}
	
	ArrayList<ResultTuple> results;

	public void runQuery(int ind, int off) {

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

		nURI = g.getVertexURI(bsbminst+"ProductFeature"+inst[ind][2+off*2]);
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

		Iterator<Vertex> itProd = sets[0].iterator();
		String product, relStr;
		while (itProd.hasNext()) {
			HashSet<String>
				setL = new HashSet<String>(),
				setPT = new HashSet<String>(),
				setP = new HashSet<String>();

			nProd = itProd.next();
			it = nProd.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(rdfs+"label"))
					setL.add(rel.getEnd().getAny());
				else if (relStr.equals(bsbm+"productPropertyTextual1"))
					setPT.add(rel.getEnd().getAny());
				else if (relStr.equals(bsbm+"productPropertyNumeric"+(off+1)))
					setP.add(rel.getEnd().getAny());
			}
			it.close();

			product = nProd.getAny();
			for (String value : setP) { try {
				if (Integer.parseInt(value)>inst[ind][3+2*off])
					for (String label : setL)
						for (String propertyTextual : setPT)
							results.add(r.newResult(product,label,propertyTextual));
			} catch (NumberFormatException nfe) {} }
		}
	}

	public void runQuery(int ind) {

		results = new ArrayList<ResultTuple>();
		runQuery(ind,0);
		runQuery(ind,1);

		// ORDER BY ?label
		Collections.sort(results);

		// OFFSET 5, LIMIT 10
		for (int i=5 ; i<15 && i<results.size() ; i++)
			results.get(i).print();
	}
}
