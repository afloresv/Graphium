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

public class Q12 extends BerlinQuery {

	public static void main(String[] args) {
		BerlinQuery Q = new Q12(args[1],"../" + args[1] + "DB/" + args[2]);
		Q.inst = new InstanceReader(2,args[1],args[2],12,Integer.parseInt(args[0]));
		Q.runExperiment();
		Q.close();
	}

	public Q12(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery() {

		r = new ResultGenerator();
		Vertex iNode;
		Edge rel;
		GraphIterator<Edge> it;
		RDFobject relURI, nodeObj, vendorURI;

		HashSet<Vertex>
			setProductURI = new HashSet<Vertex>(),
			setVendorURI = new HashSet<Vertex>();
		HashSet<RDFobject>
			setOfferURL = new HashSet<RDFobject>(),
			setPrice = new HashSet<RDFobject>(),
			setDeliveryDays = new HashSet<RDFobject>(),
			setValidTo = new HashSet<RDFobject>();
		ArrayList<ResultTuple> productTuples = new ArrayList<ResultTuple>();

		// FILTER (?o = bsbminst:dataFromVendor117/Offer231837)
		iNode = g.getVertexURI(bsbminst+"dataFromVendor"+inst.get(0)+"/Offer"+inst.get(1));
		if (iNode == null) return;
		it = iNode.getEdgesOut();
		while (it.hasNext()) {
			rel = it.next();
			relURI = rel.getURI();
			if (relURI.equals(bsbm+"product")) {
				// ?o bsbm:product ?productURI
				setProductURI.add(rel.getEnd());
			} else if (relURI.equals(bsbm+"vendor")) {
				// ?o bsbm:vendor ?vendorURI
				setVendorURI.add(rel.getEnd());
			} else if (relURI.equals(bsbm+"offerWebpage")) {
				// ?o bsbm:offerWebpage ?offerURL
				setOfferURL.add(rel.getEnd().getAny());
			} else if (relURI.equals(bsbm+"price")) {
				// ?o bsbm:price ?price
				setPrice.add(rel.getEnd().getAny());
			} else if (relURI.equals(bsbm+"deliveryDays")) {
				// ?o bsbm:deliveryDays ?deliveryDays
				setDeliveryDays.add(rel.getEnd().getAny());
			} else if (relURI.equals(bsbm+"validTo")) {
				// ?o bsbm:validTo ?validTo
				setValidTo.add(rel.getEnd().getAny());
			}
		}
		it.close();

		for (Vertex puNode : setProductURI) {
			nodeObj = puNode.getAny();
			it = puNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relURI = rel.getURI();
				if (relURI.equals(rdfs+"label")) {
					// ?productURI rdfs:label ?productLabel
					productTuples.add(r.newResult(nodeObj,rel.getEnd().getAny()));
				}
			}
			it.close();
		}

		for (Vertex vuNode : setVendorURI) {

			vendorURI = vuNode.getAny();
			HashSet<RDFobject>
				setVendorName = new HashSet<RDFobject>(),
				setVendorHomePage = new HashSet<RDFobject>();

			it = vuNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relURI = rel.getURI();
				if (relURI.equals(rdfs+"label")) {
					// ?vendorURI rdfs:label ?vendorName
					setVendorName.add(rel.getEnd().getAny());
				} else if (relURI.equals(foaf+"homepage")) {
					// ?vendorURI foaf:homepage ?vendorHomePage
					setVendorHomePage.add(rel.getEnd().getAny());
				}
			}
			it.close();

			for (ResultTuple product : productTuples)
			for (RDFobject vendorname : setVendorName)
			for (RDFobject vendorhomepage : setVendorHomePage)
			for (RDFobject offerURL : setOfferURL)
			for (RDFobject price : setPrice)
			for (RDFobject deliveryDays : setDeliveryDays)
			for (RDFobject validTo : setValidTo)
				(r.newResult(product.elem[0],product.elem[1],vendorURI,vendorname,
				vendorhomepage,offerURL,price,deliveryDays,validTo)).print();
		}
	}
}
