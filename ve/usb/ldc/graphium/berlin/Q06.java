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

public class Q06 extends BerlinQuery {

	String[] inst = {
		"adequateness",
		"agileness",
		"ambilateral",
		"archduchesses",
		"basicity",
		"bereavements",
		"catnapers",
		"chittering",
		"coffing",
		"ginny",
		"greco",
		"haltering",
		"iterant",
		"leftisms",
		"ostium",
		"purchased",
		"snubby",
		"spacemen",
		"spinocerebellar",
		"splashdown"
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q06(args[1],args[2]);
		Q.runExperiment(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q06(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {

		r = new ResultGenerator();
		Vertex bsbmProductNode;
		Edge rel;
		IteratorGraph it;
		RDFobject relURI, product, label;
		HashSet<Vertex> setProduct = new HashSet<Vertex>();

		// bsbm:Product
		bsbmProductNode = g.getVertexURI(bsbm+"Product");
		if (bsbmProductNode == null) return;
		it = bsbmProductNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			relURI = rel.getURI();
			// ?product rdf:type bsbm:Product .
			if (relURI.equals(rdf+"type"))
				setProduct.add(rel.getStart());
		}
		it.close();

		for (Vertex productNode : setProduct) {
			product = productNode.getAny();
			it = productNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				label = rel.getEnd().getAny();
				// ?product rdfs:label ?label .
				// FILTER regex(?label, "string")
				if (rel.getURI().equals(rdfs+"label")
					&& label.base.matches(inst[ind]))
					(r.newResult(product,label)).print();
			}
			it.close();
		}
	}
}
