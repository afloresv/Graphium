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

public class Q12 implements BerlinQuery {

	int[][] inst = {
		{117,231837}
	};

	GraphDB g;

	public Q12(GraphDB _g) {
		g = _g;
	}

	public void runQuery(int ind) {

		Vertex iNode;
		Edge rel;
		IteratorGraph it;
		String relStr, nodeStr, vendorURI;

		HashSet<Vertex>
			setProductURI = new HashSet<Vertex>(),
			setVendorURI = new HashSet<Vertex>();
		HashSet<String>
			setOfferURL = new HashSet<String>(),
			setPrice = new HashSet<String>(),
			setDeliveryDays = new HashSet<String>(),
			setValidTo = new HashSet<String>();
		ArrayList<ResultTuple> productTuples = new ArrayList<ResultTuple>();

		// FILTER (?o = bsbminst:dataFromVendor117/Offer231837)
		iNode = g.getVertexURI(bsbminst+"dataFromVendor"+inst[ind][0]+"/Offer"+inst[ind][1]);
		if (iNode == null) return;
		it = iNode.getEdgesOut();
		while (it.hasNext()) {
			rel = it.next();
			relStr = rel.getURI();

			if (relStr.equals(bsbm+"product")) {
				// ?o bsbm:product ?productURI
				setProductURI.add(rel.getEnd());
			} else if (relStr.equals(bsbm+"vendor")) {
				// ?o bsbm:vendor ?vendorURI
				setVendorURI.add(rel.getEnd());
			} else if (relStr.equals(bsbm+"offerWebpage")) {
				// ?o bsbm:offerWebpage ?offerURL
				setOfferURL.add(rel.getEnd().getAny());
			} else if (relStr.equals(bsbm+"price")) {
				// ?o bsbm:price ?price
				setPrice.add(rel.getEnd().getAny());
			} else if (relStr.equals(bsbm+"deliveryDays")) {
				// ?o bsbm:deliveryDays ?deliveryDays
				setDeliveryDays.add(rel.getEnd().getAny());
			} else if (relStr.equals(bsbm+"validTo")) {
				// ?o bsbm:validTo ?validTo
				setValidTo.add(rel.getEnd().getAny());
			}
		}
		it.close();

		for (Vertex puNode : setProductURI) {
			nodeStr = puNode.getAny();
			it = puNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(rdfs+"label")) {
					// ?productURI rdfs:label ?productLabel
					productTuples.add(new ResultTuple(nodeStr,rel.getEnd().getAny()));
				}
			}
			it.close();
		}

		for (Vertex vuNode : setVendorURI) {

			vendorURI = vuNode.getAny();
			HashSet<String>
				setVendorName = new HashSet<String>(),
				setVendorHomePage = new HashSet<String>();

			it = vuNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(rdfs+"label")) {
					// ?vendorURI rdfs:label ?vendorName
					setVendorName.add(rel.getEnd().getAny());
				} else if (relStr.equals(foaf+"homepage")) {
					// ?vendorURI foaf:homepage ?vendorHomePage
					setVendorHomePage.add(rel.getEnd().getAny());
				}
			}
			it.close();

			for (ResultTuple product : productTuples)
			for (String vendorname : setVendorName)
			for (String vendorhomepage : setVendorHomePage)
			for (String offerURL : setOfferURL)
			for (String price : setPrice)
			for (String deliveryDays : setDeliveryDays)
			for (String validTo : setValidTo)
				(new ResultTuple(product.elem[0],product.elem[1],vendorURI,vendorname,
				vendorhomepage,offerURL,price,deliveryDays,validTo)).print();
		}
	}
}
