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

public class Q02 extends BerlinQuery {

	int[][] inst = {
		{101,4762},
		{114,5343},
		{173,8318},
		{227,11037},
		{241,11794},
		{327,15934},
		{360,17573},
		{396,19501},
		{401,19781},
		{409,20208},
		{409,20248},
		{422,20765},
		{431,21233},
		{458,22652},
		{48,2235},
		{508,24956},
		{52,2462},
		{527,26016},
		{539,26589},
		{564,27781}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q02(args[1],args[2]);
		Q.runExperiment(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q02(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {

		r = new ResultGenerator();
		Vertex xNode, vNode;
		Edge rel;
		IteratorGraph it, it2;
		RDFobject relURI, nodeObj;

		HashSet<RDFobject>
			laS = new HashSet<RDFobject>(),
			coS = new HashSet<RDFobject>(),
			prS = new HashSet<RDFobject>(),
			pF = new HashSet<RDFobject>(),
			pPT1 = new HashSet<RDFobject>(),
			pPT2 = new HashSet<RDFobject>(),
			pPT3 = new HashSet<RDFobject>(),
			pPN1 = new HashSet<RDFobject>(),
			pPN2 = new HashSet<RDFobject>(),
			pPT4 = new HashSet<RDFobject>(),
			pPT5 = new HashSet<RDFobject>(),
			pPN4 = new HashSet<RDFobject>();

		xNode = g.getVertexURI(bsbminst+"dataFromProducer"+inst[ind][0]+"/Product"+inst[ind][1]);
		if (xNode == null) return;
		it = xNode.getEdgesOut();
		while (it.hasNext()) {
			rel = it.next();
			relURI = rel.getURI();
			vNode = rel.getEnd();
			nodeObj = vNode.getAny();

			if (relURI.equals(rdfs+"label")) {
				// ?x rdfs:label ?label .
				laS.add(nodeObj);
			} else if (relURI.equals(rdfs+"comment")) {
				// ?x rdfs:comment ?comment .
				coS.add(nodeObj);
			} else if (relURI.equals(bsbm+"producer")) {
				// ?x bsbm:producer ?p .
				// ?p rdfs:label ?producer .
				// ?x dc:publisher ?p .
				boolean found = false;
				it2 = vNode.getEdgesIn();
				while (!found && it2.hasNext()) {
					rel = it2.next();
					if (rel.getURI().equals(dc+"publisher")
						&& rel.getStart().equals(xNode))
						found = true;
				}
				it2.close();
				if (!found) continue;
				it2 = vNode.getEdgesOut();
				while (it2.hasNext()) {
					rel = it2.next();
					if (rel.getURI().equals(rdfs+"label")) {
						prS.add(rel.getEnd().getAny());
						break;
					}
				}
				it2.close();
			} else if (relURI.equals(bsbm+"productFeature")) {
				// ?x bsbm:productFeature ?f .
				// ?f rdfs:label ?productFeature .
				it2 = vNode.getEdgesOut();
				while (it2.hasNext()) {
					rel = it2.next();
					if (rel.getURI().equals(rdfs+"label")) {
						pF.add(rel.getEnd().getAny());
						break;
					}
				}
				it2.close();
			} else if (relURI.equals(bsbm+"productPropertyTextual1")) {
				// ?x bsbm:productPropertyTextual1 ?propertyTextual1 .
				pPT1.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyTextual2")) {
				// ?x bsbm:productPropertyTextual2 ?propertyTextual2 .
				pPT2.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyTextual3")) {
				// ?x bsbm:productPropertyTextual3 ?propertyTextual3 .
				pPT3.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyNumeric1")) {
				// ?x bsbm:productPropertyNumeric1 ?propertyNumeric1 .
				pPN1.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyNumeric2")) {
				// ?x bsbm:productPropertyNumeric2 ?propertyNumeric2 .
				pPN2.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyTextual4")) {
				// OPTIONAL { ?x bsbm:productPropertyTextual4 ?propertyTextual4 }
				pPT4.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyTextual5")) {
				// OPTIONAL { ?x bsbm:productPropertyTextual5 ?propertyTextual5 }
				pPT5.add(nodeObj);
			} else if (relURI.equals(bsbm+"productPropertyNumeric4")) {
				// OPTIONAL { ?x bsbm:productPropertyNumeric4 ?propertyNumeric4 }
				pPN4.add(nodeObj);
			}
		}
		it.close();

		// Results
		if (pPT4.size()==0) pPT4.add(new RDFobject());
		if (pPT5.size()==0) pPT5.add(new RDFobject());
		if (pPN4.size()==0) pPN4.add(new RDFobject());

		for (RDFobject label : laS)
		for (RDFobject comment : coS)
		for (RDFobject producer : prS)
		for (RDFobject productFeature : pF)
		for (RDFobject propertyTextual1 : pPT1)
		for (RDFobject propertyTextual2 : pPT2)
		for (RDFobject propertyTextual3 : pPT3)
		for (RDFobject propertyNumeric1 : pPN1)
		for (RDFobject propertyNumeric2 : pPN2)
		for (RDFobject propertyTextual4 : pPT4)
		for (RDFobject propertyTextual5 : pPT5)
		for (RDFobject propertyNumeric4 : pPN4)
			(r.newResult(label,comment,producer,productFeature,propertyTextual1,
				propertyTextual2,propertyTextual3,propertyNumeric1,propertyNumeric2,
				propertyTextual4,propertyTextual5,propertyNumeric4)).print();
	}
}
