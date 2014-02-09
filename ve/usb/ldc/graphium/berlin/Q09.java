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

public class Q09 extends BerlinQuery {

	int[][] inst = {
		{510,25065}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q09(args[1],args[2]);
		Q.runQuery(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q09(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {

		Vertex iNode;
		Edge rel;
		IteratorGraph it;
		String relStr, x, reviewer,
			otherStrI = bsbminst+"dataFromRatingSite"+inst[ind][0]+"/Review"+inst[ind][1];

		HashSet<Vertex> setX = new HashSet<Vertex>();

		// ?x rdf:type foaf:Person
		iNode = g.getVertexURI(foaf+"Person");
		if (iNode == null) return;
		it = iNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			relStr = rel.getURI();
			if (relStr.equals(rdf+"type"))
				setX.add(rel.getStart());
		}
		it.close();

		for (Vertex xNode : setX) {

			boolean found = false;
			HashSet<Vertex> setReviewer = new HashSet<Vertex>();

			it = xNode.getEdgesIn();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(rev+"reviewer")) {
					// bsbminst:dataFromRatingSite8/Review75011 rev:reviewer ?x
					if (rel.getStart().getAny().equals(otherStrI))
						found = true;
				} else if (relStr.equals(rev+"reviewer")) {
					// ?reviewer rev:reviewer ?x
					setReviewer.add(rel.getStart());
				}
			}
			it.close();

			if (!found) continue;

			x = xNode.getAny();

			HashSet<String>
				setName = new HashSet<String>(),
				setMbox = new HashSet<String>(),
				setCountry = new HashSet<String>();

			it = xNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(foaf+"name")) {
					// ?x foaf:name ?name
					setName.add(rel.getEnd().getAny());
				} else if (relStr.equals(foaf+"mbox_sha1sum")) {
					// ?x foaf:mbox_sha1sum ?mbox
					setMbox.add(rel.getEnd().getAny());
				} else if (relStr.equals(bsbm+"country")) {
					// ?x bsbm:country ?country
					setCountry.add(rel.getEnd().getAny());
				}
			}
			it.close();

			for (Vertex rNode : setReviewer) {
				reviewer = rNode.getAny();

				HashSet<String>
					setProduct = new HashSet<String>(),
					setTitle = new HashSet<String>();

				it = rNode.getEdgesOut();
				while (it.hasNext()) {
					rel = it.next();
					relStr = rel.getURI();
					if (relStr.equals(bsbm+"reviewFor")) {
						// ?reviewer bsbm:reviewFor ?product
						setProduct.add(rel.getEnd().getAny());
					} else if (relStr.equals(dc+"title")) {
						// ?reviewer dc:title ?title
						setTitle.add(rel.getEnd().getAny());
					}
				}
				it.close();

				// Print results
				for (String name : setName)
				for (String mbox : setMbox)
				for (String country : setCountry)
				for (String product : setProduct)
				for (String title : setTitle)
					(new ResultTuple(x,name,mbox,country,reviewer,product,title)).print();
			}
		}
	}
}
