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

public class Q02 extends DEX implements BerlinQuery {

	int[] inst = {22652};

	public Q02(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q02 testQ = new Q02(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long xNode, vNode, rel, rel2;
		Value v = new Value();
		Objects setEdge, setEdge2;
		ObjectsIterator it, it2;
		String relStr, nodeStr;

		HashSet<String>
			laS = new HashSet<String>(),
			coS = new HashSet<String>(),
			prS = new HashSet<String>(),
			pF = new HashSet<String>(),
			pPT1 = new HashSet<String>(),
			pPT2 = new HashSet<String>(),
			pPT3 = new HashSet<String>(),
			pPN1 = new HashSet<String>(),
			pPN2 = new HashSet<String>(),
			pPT4 = new HashSet<String>(),
			pPT5 = new HashSet<String>(),
			pPN4 = new HashSet<String>();

		xNode = g.findObject(AttrType[0], v.setString(bsbminst
			+"dataFromProducer458/Product"+inst[ind]));
		if (xNode == Objects.InvalidOID) return;
		setEdge = g.explode(xNode,EdgeType,EdgesDirection.Outgoing);
		it = setEdge.iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = g.getAttribute(rel,AttrType[5]).getString();
			vNode = g.getEdgePeer(rel,xNode);
			nodeStr = getAnyProp(vNode);

			if (relStr.equals(rdfs+"label")) {
				// ?x rdfs:label ?label .
				laS.add(nodeStr);
			} else if (relStr.equals(rdfs+"comment")) {
				// ?x rdfs:comment ?comment .
				coS.add(nodeStr);
			} else if (relStr.equals(bsbm+"producer")) {
				// ?x bsbm:producer ?p .
				// ?p rdfs:label ?producer .
				// ?x dc:publisher ?p .
				boolean found = false;
				setEdge2 = g.explode(vNode,EdgeType,EdgesDirection.Ingoing);
				it2 = setEdge2.iterator();
				while (!found && it2.hasNext()) {
					rel2 = it2.next();
					if (g.getAttribute(rel2,AttrType[5]).getString().equals(dc+"publisher")
						&& g.getEdgePeer(rel2,vNode) == xNode)
						found = true;
				}
				it2.close();
				setEdge2.close();
				if (!found) continue;
				setEdge2 = g.explode(vNode,EdgeType,EdgesDirection.Outgoing);
				it2 = setEdge2.iterator();
				while (it2.hasNext()) {
					rel2 = it2.next();
					if (g.getAttribute(rel2,AttrType[5]).getString().equals(rdfs+"label")) {
						prS.add(getAnyProp(g.getEdgePeer(rel2,vNode)));
						break;
					}
				}
				it2.close();
				setEdge2.close();
			} else if (relStr.equals(bsbm+"productFeature")) {
				// ?x bsbm:productFeature ?f .
				// ?f rdfs:label ?productFeature .
				setEdge2 = g.explode(vNode,EdgeType,EdgesDirection.Outgoing);
				it2 = setEdge2.iterator();
				while (it2.hasNext()) {
					rel2 = it2.next();
					if (g.getAttribute(rel2,AttrType[5]).getString().equals(rdfs+"label")) {
						pF.add(getAnyProp(g.getEdgePeer(rel2,vNode)));
						break;
					}
				}
				it2.close();
				setEdge2.close();
			} else if (relStr.equals(bsbm+"productPropertyTextual1")) {
				// ?x bsbm:productPropertyTextual1 ?propertyTextual1 .
				pPT1.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyTextual2")) {
				// ?x bsbm:productPropertyTextual2 ?propertyTextual2 .
				pPT2.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyTextual3")) {
				// ?x bsbm:productPropertyTextual3 ?propertyTextual3 .
				pPT3.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyNumeric1")) {
				// ?x bsbm:productPropertyNumeric1 ?propertyNumeric1 .
				pPN1.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyNumeric2")) {
				// ?x bsbm:productPropertyNumeric2 ?propertyNumeric2 .
				pPN2.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyTextual4")) {
				// OPTIONAL { ?x bsbm:productPropertyTextual4 ?propertyTextual4 }
				pPT4.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyTextual5")) {
				// OPTIONAL { ?x bsbm:productPropertyTextual5 ?propertyTextual5 }
				pPT5.add(nodeStr);
			} else if (relStr.equals(bsbm+"productPropertyNumeric4")) {
				// OPTIONAL { ?x bsbm:productPropertyNumeric4 ?propertyNumeric4 }
				pPN4.add(nodeStr);
			}
		}
		it.close();
		setEdge.close();

		// Results
		if (pPT4.size()==0) pPT4.add("");
		if (pPT5.size()==0) pPT5.add("");
		if (pPN4.size()==0) pPN4.add("");

		for (String label : laS)
		for (String comment : coS)
		for (String producer : prS)
		for (String productFeature : pF)
		for (String propertyTextual1 : pPT1)
		for (String propertyTextual2 : pPT2)
		for (String propertyTextual3 : pPT3)
		for (String propertyNumeric1 : pPN1)
		for (String propertyNumeric2 : pPN2)
		for (String propertyTextual4 : pPT4)
		for (String propertyTextual5 : pPT5)
		for (String propertyNumeric4 : pPN4)
			System.out.println(
				label +" "+
				comment +" "+
				producer +" "+
				productFeature +" "+
				propertyTextual1 +" "+
				propertyTextual2 +" "+
				propertyTextual3 +" "+
				propertyNumeric1 +" "+
				propertyNumeric2 +" "+
				propertyTextual4 +" "+
				propertyTextual5 +" "+
				propertyNumeric4
			);

	}
}
