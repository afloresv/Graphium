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

public class Q09 extends DEX implements BerlinQuery {

	int[][] inst = {
		{510,25065}
	};

	public Q09(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q09 testQ = new Q09(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long iNode, rel;
		Objects edgeSet;
		ObjectsIterator it;
		String relStr, x, reviewer,
			otherStrI = bsbminst+"dataFromRatingSite"+inst[ind][0]+"/Review"+inst[ind][1];

		HashSet<Long> setX = new HashSet<Long>();

		// ?x rdf:type foaf:Person
		iNode = getNodeFromURI(foaf+"Person");
		if (iNode == NodeNotFound) return;
		edgeSet = g.explode(iNode,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = getEdgeURI(rel);
			if (relStr.equals(rdf+"type"))
				setX.add(getStartNode(rel));
		}
		it.close();
		edgeSet.close();

		for (Long xNode : setX) {

			boolean found = false;
			HashSet<Long> setReviewer = new HashSet<Long>();

			edgeSet = g.explode(xNode,EdgeType,EdgesDirection.Ingoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				iNode = getStartNode(rel);
				if (relStr.equals(rdf+"type")) {
					// bsbminst:dataFromRatingSite8/Review75011 rev:reviewer ?x
					if (getAnyProp(iNode).equals(otherStrI))
						found = true;
				} else if (relStr.equals(rev+"reviewer")) {
					// ?reviewer rev:reviewer ?x
					setReviewer.add(iNode);
				}
			}
			it.close();
			edgeSet.close();

			if (!found) continue;

			x = getAnyProp(xNode);

			HashSet<String>
				setName = new HashSet<String>(),
				setMbox = new HashSet<String>(),
				setCountry = new HashSet<String>();

			edgeSet = g.explode(xNode,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				if (relStr.equals(foaf+"name")) {
					// ?x foaf:name ?name
					setName.add(getAnyProp(getEndNode(rel)));
				} else if (relStr.equals(foaf+"mbox_sha1sum")) {
					// ?x foaf:mbox_sha1sum ?mbox
					setMbox.add(getAnyProp(getEndNode(rel)));
				} else if (relStr.equals(bsbm+"country")) {
					// ?x bsbm:country ?country
					setCountry.add(getAnyProp(getEndNode(rel)));
				}
			}
			it.close();
			edgeSet.close();

			for (Long rNode : setReviewer) {
				reviewer = getAnyProp(rNode);

				HashSet<String>
					setProduct = new HashSet<String>(),
					setTitle = new HashSet<String>();

				edgeSet = g.explode(rNode,EdgeType,EdgesDirection.Outgoing);
				it = edgeSet.iterator();
				while (it.hasNext()) {
					rel = it.next();
					relStr = getEdgeURI(rel);
					if (relStr.equals(bsbm+"reviewFor")) {
						// ?reviewer bsbm:reviewFor ?product
						setProduct.add(getAnyProp(getEndNode(rel)));
					} else if (relStr.equals(dc+"title")) {
						// ?reviewer dc:title ?title
						setTitle.add(getAnyProp(getEndNode(rel)));
					}
				}
				it.close();
				edgeSet.close();

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
