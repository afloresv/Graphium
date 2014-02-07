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

public class Q05 extends DEX implements BerlinQuery {

	int[][] inst = {
		{408,20183}
	};

	public Q05(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q05 testQ = new Q05(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long pNode, vNode, rel;
		String relStr, nodeStr, product;
		Objects edgeSet;
		ObjectsIterator it;

		HashSet<Integer>
			setOP1 = new HashSet<Integer>(),
			setOP2 = new HashSet<Integer>();
		HashSet<Long>
			setPF = new HashSet<Long>(),
			setProd = new HashSet<Long>();

		// FILTER (?p = bsbminst:dataFromProducer408/Product20183)
		pNode = getNodeFromURI(bsbminst+"dataFromProducer"
			+inst[ind][0]+"/Product"+inst[ind][1]);
		if (pNode == NodeNotFound) return;
		edgeSet = g.explode(pNode,EdgeType,EdgesDirection.Outgoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = getEdgeURI(rel);
			vNode = getEndNode(rel);

			try {
				if (relStr.equals(bsbm+"productFeature")) {
					// ?p bsbm:productFeature ?prodFeature .
					setPF.add(vNode);
				} else if (relStr.equals(bsbm+"productPropertyNumeric1")) {
					// ?p bsbm:productPropertyNumeric1 ?origProperty1 .
					setOP1.add(Integer.parseInt(getAnyProp(vNode)));
				} else if (relStr.equals(bsbm+"productPropertyNumeric2")) {
					// ?p bsbm:productPropertyNumeric2 ?origProperty2 .
					setOP2.add(Integer.parseInt(getAnyProp(vNode)));
				}
			} catch (NumberFormatException nfe) {}
		}
		it.close();
		edgeSet.close();

		for (Long nodePF : setPF) {
			edgeSet = g.explode(nodePF,EdgeType,EdgesDirection.Ingoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				vNode = getStartNode(rel);
				// ?product bsbm:productFeature ?prodFeature .
				// FILTER (bsbminst:dataFromProducer408/Product20183 != ?product)
				if (pNode != vNode && getEdgeURI(rel).equals(bsbm+"productFeature"))
					setProd.add(vNode);
			}
			it.close();
			edgeSet.close();
		}

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();

		for (Long nodeProd : setProd) {
			product = getAnyProp(nodeProd);

			HashSet<Integer>
				setSP1 = new HashSet<Integer>(),
				setSP2 = new HashSet<Integer>();
			HashSet<String> setPL = new HashSet<String>();

			edgeSet = g.explode(nodeProd,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				nodeStr = getAnyProp(getEndNode(rel));
				try {
					if (relStr.equals(rdfs+"label")) {
						// ?product rdfs:label ?productLabel .
						setPL.add(nodeStr);
					} else if (relStr.equals(bsbm+"productPropertyNumeric1")) {
						// ?product bsbm:productPropertyNumeric1 ?simProperty1 .
						setSP1.add(Integer.parseInt(nodeStr));
					} else if (relStr.equals(bsbm+"productPropertyNumeric2")) {
						// ?product bsbm:productPropertyNumeric2 ?simProperty2 .
						setSP2.add(Integer.parseInt(nodeStr));
					}
				} catch (NumberFormatException nfe) {}
			}
			it.close();
			edgeSet.close();

			// FILTER (?simProperty1 < (?origProperty1 + 120)
			// && ?simProperty1 > (?origProperty1 - 120))
			boolean passFilter = false;
			filter1:
			for (Integer origProperty1 : setOP1) {
				for (Integer simProperty1 : setSP1) {
					if (simProperty1 < (origProperty1 + 170)
						&& simProperty1 > (origProperty1 - 170))
						passFilter = true;
						break filter1;
				}
			}

			// FILTER (?simProperty2 < (?origProperty2 + 170)
			// && ?simProperty2 > (?origProperty2 - 170))
			if (passFilter) {
				passFilter = false;
				filter2:
				for (Integer origProperty2 : setOP2) {
					for (Integer simProperty2 : setSP2) {
						if (simProperty2 < (origProperty2 + 120)
							&& simProperty2 > (origProperty2 - 120)) {
							passFilter = true;
							break filter2;
						}
					}
				}
			}

			if (passFilter)
				for (String productLabel : setPL)
					results.add(new ResultTuple(1,product,productLabel));
		}

		// ORDER BY ?productLabel
		Collections.sort(results);

		// LIMIT 5
		for (int i=0 ; i<5 && i<results.size() ; i++)
			results.get(i).print();
	}
}
