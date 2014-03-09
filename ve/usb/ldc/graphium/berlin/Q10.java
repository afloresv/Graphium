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

	// 2008Y06M20D
	Date dateF = (new GregorianCalendar(2008,5,20)).getTime();

	public static void main(String[] args) {
		BerlinQuery Q = new Q10(args[1],"../" + args[1] + "DB/" + args[2]);
		Q.inst = new InstanceReader(2,args[1],args[2],10,Integer.parseInt(args[0]));
		Q.runExperiment();
		Q.close();
	}

	public Q10(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery() {
		
		r = new ResultGenerator(2);
		Vertex pNode;
		Edge rel;
		IteratorGraph it;
		RDFobject relURI;

		HashSet<Vertex> setOffer = new HashSet<Vertex>();

		pNode = g.getVertexURI(bsbminst+"dataFromProducer"
			+inst.get(0)+"/Product"+inst.get(1));
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

		RDFobject offer;
		for (Vertex oNode : setOffer) {
			boolean foundDate = false,
				foundDeliveryDays = false;
			offer = oNode.getAny();

			HashSet[] setVendor = new HashSet[2];
			setVendor[0] = new HashSet<Vertex>();
			setVendor[1] = new HashSet<Vertex>();
			HashSet<Vertex> setPrice = new HashSet<Vertex>();
			Date dateV;

			it = oNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relURI = rel.getURI();
				if (relURI.equals(bsbm+"price")) {
					// ?offer bsbm:price ?price
					setPrice.add(rel.getEnd());
				} else if (relURI.equals(bsbm+"validTo")) {
					// ?offer bsbm:validTo ?date
					dateV = rel.getEnd().getDate();
					if (dateV!=null && dateF.compareTo(dateV)<0)
						foundDate = true;
				} else if (relURI.equals(bsbm+"deliveryDays")) {
					// ?offer bsbm:deliveryDays ?deliveryDays
					// FILTER (?deliveryDays <= 3)
					if (rel.getEnd().getLong() <= 3)
						foundDeliveryDays = true;
				} else if (relURI.equals(bsbm+"vendor")) {
					// ?offer bsbm:vendor ?vendor .
					setVendor[0].add(rel.getEnd());
				} else if (relURI.equals(dc+"publisher")) {
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
				if (!setVendor[maxInd].contains(vNode)) continue;
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
			for (Vertex price : setPrice) {
				try {
					Double val = new Double(price.getString());
					results.add(r.newResult(offer,price.getLiteral(),val));
				} catch (NumberFormatException e) {}
			}
		}

		// #ORDER BY xsd:double(str(?price))
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print(2);
	}
}
