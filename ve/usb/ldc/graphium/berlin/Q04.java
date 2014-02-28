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
		{128,14,3,250,408,35},
		{138,462,451,283,23,52},
		{168,28,5336,278,5323,53},
		{186,5912,636,438,5909,201},
		{247,855,864,144,7905,18},
		{252,897,8047,137,47,93},
		{256,8204,8194,43,8198,122},
		{270,955,8664,322,65,212},
		{279,8949,1005,416,8968,144},
		{287,1053,1025,466,9203,289},
		{296,1073,1071,80,1068,253},
		{433,14007,1615,142,110,81},
		{434,104,113,28,1625,121},
		{435,14067,113,128,14067,463},
		{470,137,133,54,1796,293},
		{497,16106,1885,168,127,139},
		{520,126,16909,168,143,325},
		{544,2080,17667,119,2076,437},
		{556,18110,2136,37,2123,206},
		{87,2648,9,22,2657,89}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q04(args[1],args[2]);
		Q.runExperiment(Integer.parseInt(args[0]));
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
		RDFobject product, relURI;
		while (itProd.hasNext()) {
			HashSet<RDFobject>
				setL = new HashSet<RDFobject>(),
				setPT = new HashSet<RDFobject>();
			HashSet<Long> setP = new HashSet<Long>();

			nProd = itProd.next();
			it = nProd.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relURI = rel.getURI();
				if (relURI.equals(rdfs+"label"))
					setL.add(rel.getEnd().getAny());
				else if (relURI.equals(bsbm+"productPropertyTextual1"))
					setPT.add(rel.getEnd().getAny());
				else if (relURI.equals(bsbm+"productPropertyNumeric"+(off+1)))
					setP.add(rel.getEnd().getLong());
			}
			it.close();

			product = nProd.getAny();
			for (Long value : setP) {
				if (value>inst[ind][3+2*off])
				for (RDFobject label : setL)
					for (RDFobject propertyTextual : setPT)
						results.add(r.newResult(product,label,propertyTextual));
			}
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
