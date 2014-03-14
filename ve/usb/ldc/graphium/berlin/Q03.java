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

	public static void main(String[] args) {
		BerlinQuery Q = new Q03(args[1],"../" + args[1] + "DB/" + args[2]);
		Q.inst = new InstanceReader(5,args[1],args[2],3,Integer.parseInt(args[0]));
		Q.runExperiment();
		Q.close();
	}

	public Q03(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery() {

		r = new ResultGenerator(1);
		HashSet[] sets = new HashSet[2];
		sets[0] = new HashSet<Vertex>();
		sets[1] = new HashSet<Vertex>();

		Vertex nURI, nProd;
		Edge rel;
		GraphIterator<Edge> it;

		nURI = g.getVertexURI(bsbminst+"ProductType"+inst.get(0));
		if (nURI == null) return;
		it = nURI.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			if (rel.getURI().equals(rdf+"type"))
				sets[0].add(rel.getStart());
		}
		it.close();

		nURI = g.getVertexURI(bsbminst+"ProductFeature"+inst.get(1));
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
		RDFobject product, relURI;
		while (itProd.hasNext()) {
			HashSet<RDFobject> setL = new HashSet<RDFobject>();
			HashSet<Long>
				setP1 = new HashSet<Long>(),
				setP3 = new HashSet<Long>();

			nProd = itProd.next();
			it = nProd.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relURI = rel.getURI();
				if (relURI.equals(rdfs+"label"))
					setL.add(rel.getEnd().getAny());
				else if (relURI.equals(bsbm+"productPropertyNumeric1"))
					setP1.add(rel.getEnd().getLong());
				else if (relURI.equals(bsbm+"productPropertyNumeric3"))
					setP3.add(rel.getEnd().getLong());
			}
			it.close();

			product = nProd.getAny();
			for (Long p1 : setP1) {
				if (p1>inst.get(2))
				for (Long p3 : setP3) {
					if (p3>inst.get(3))
					for (RDFobject label : setL)
						results.add(r.newResult(product,label));
				}
			}
		}

		// ORDER BY ?label
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print();
	}
}
