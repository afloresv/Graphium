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

public class Q10 extends BerlinQuery {

	int[][] inst = {
		{510,25065}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q10(args[1],args[2]);
		Q.runQuery(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q10(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {
		
		r = new ResultGenerator(1);
		Vertex pNode;
		Edge rel;
		IteratorGraph it;
		String relStr;

		HashSet<String> setPrice = new HashSet<String>();
		HashSet<Vertex> setOffer = new HashSet<Vertex>();

		pNode = g.getVertexURI(bsbminst+"dataFromProducer"
			+inst[ind][0]+"/Product"+inst[ind][1]);
		it = pNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			if (rel.getURI().equals(bsbm+"product")) {
				// ?offer bsbm:product bsbminst:dataFromProducer134/Product6436
				setOffer.add(rel.getStart());
			}
		}
		it.close();

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();

		String offer;
		for (Vertex oNode : setOffer) {
			boolean foundDate = false,
				foundDeliveryDays = false;
			offer = oNode.getAny();

			HashSet[] setVendor = new HashSet[2];
			setVendor[0] = new HashSet<Vertex>();
			setVendor[1] = new HashSet<Vertex>();

			it = oNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(bsbm+"price")) {
					// ?offer bsbm:price ?price
					setPrice.add(rel.getEnd().getAny());
				} else if (relStr.equals(bsbm+"validTo")) {
					// ?offer bsbm:validTo ?date
					// FILTER (?date > 2008Y06M20D )
					if (/* FILTER */ true)
						foundDate = true;
				} else if (relStr.equals(bsbm+"deliveryDays")) {
					// ?offer bsbm:deliveryDays ?deliveryDays
					// FILTER (?deliveryDays <= 3)
					try {
						if (Integer.parseInt(rel.getEnd().getAny()) <= 3)
							foundDeliveryDays = true;
					} catch (NumberFormatException nfe) {}
				} else if (relStr.equals(bsbm+"vendor")) {
					// ?offer bsbm:vendor ?vendor .
					setVendor[0].add(rel.getEnd());
				} else if (relStr.equals(dc+"publisher")) {
					// ?offer dc:publisher ?vendor .
					setVendor[1].add(rel.getEnd());
				}
			}
			it.close();

			if (!foundDate || !foundDeliveryDays) continue;

			int maxInd, minInd;
			minInd = (setVendor[0].size() <= setVendor[1].size() ? 0 : 1);
			maxInd = (minInd + 1) % 2;

			boolean foundVendor = false;
			Iterator<Vertex> itv = setVendor[minInd].iterator();
			while (itv.hasNext() && !foundVendor) {
				Vertex vNode = itv.next();
				it = vNode.getEdgesOut();
				while (it.hasNext() && !foundVendor) {
					rel = it.next();
					if (rel.getURI().equals(bsbm+"country") && rel.getEnd().getAny()
						.equals("http://downlode.org/rdf/iso-3166/countries#US")) {
						// ?vendor bsbm:country <http://downlode.org/rdf/iso-3166/countries#US>
						foundVendor = true;
					}
				}
				it.close();
			}

			if (foundVendor)
				for (String price : setPrice)
					results.add(r.newResult(offer,price));
		}

		// #ORDER BY xsd:double(str(?price))
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print();
	}
}
