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

public class Q12 extends DEX implements BerlinQuery {

	int[][] inst = {
		{117,231837}
	};

	public Q12(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q12 testQ = new Q12(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long iNode, vNode, rel;
		Objects edgeSet;
		ObjectsIterator it;
		String relStr, nodeStr1, nodeStr2, vendorURI;

		HashSet<Long>
			setProductURI = new HashSet<Long>(),
			setVendorURI = new HashSet<Long>();
		HashSet<String>
			setOfferURL = new HashSet<String>(),
			setPrice = new HashSet<String>(),
			setDeliveryDays = new HashSet<String>(),
			setValidTo = new HashSet<String>();
		ArrayList<ResultTuple> productTuples = new ArrayList<ResultTuple>();

		// FILTER (?o = bsbminst:dataFromVendor117/Offer231837)
		iNode = getNodeFromURI(bsbminst+"dataFromVendor"+inst[ind][0]+"/Offer"+inst[ind][1]);
		if (iNode == NodeNotFound) return;
		edgeSet = g.explode(iNode,EdgeType,EdgesDirection.Outgoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = getEdgeURI(rel);
			vNode = getEndNode(rel);

			if (relStr.equals(bsbm+"product")) {
				// ?o bsbm:product ?productURI
				setProductURI.add(vNode);
			} else if (relStr.equals(bsbm+"vendor")) {
				// ?o bsbm:vendor ?vendorURI
				setVendorURI.add(vNode);
			} else if (relStr.equals(bsbm+"offerWebpage")) {
				// ?o bsbm:offerWebpage ?offerURL
				setOfferURL.add(getAnyProp(vNode));
			} else if (relStr.equals(bsbm+"price")) {
				// ?o bsbm:price ?price
				setPrice.add(getAnyProp(vNode));
			} else if (relStr.equals(bsbm+"deliveryDays")) {
				// ?o bsbm:deliveryDays ?deliveryDays
				setDeliveryDays.add(getAnyProp(vNode));
			} else if (relStr.equals(bsbm+"validTo")) {
				// ?o bsbm:validTo ?validTo
				setValidTo.add(getAnyProp(vNode));
			}
		}
		it.close();
		edgeSet.close();

		for (Long puNode : setProductURI) {
			nodeStr1 = getAnyProp(puNode);
			edgeSet = g.explode(puNode,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				nodeStr2 = getAnyProp(getEndNode(rel));
				if (relStr.equals(rdfs+"label")) {
					// ?productURI rdfs:label ?productLabel
					productTuples.add(new ResultTuple(nodeStr1,nodeStr2));
				}
			}
			it.close();
			edgeSet.close();
		}

		for (Long vuNode : setVendorURI) {

			vendorURI = getAnyProp(vuNode);
			HashSet<String>
				setVendorName = new HashSet<String>(),
				setVendorHomePage = new HashSet<String>();

			edgeSet = g.explode(vuNode,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				nodeStr2 = getAnyProp(getEndNode(rel));
				if (relStr.equals(rdfs+"label")) {
					// ?vendorURI rdfs:label ?vendorName
					setVendorName.add(nodeStr2);
				} else if (relStr.equals(foaf+"homepage")) {
					// ?vendorURI foaf:homepage ?vendorHomePage
					setVendorHomePage.add(nodeStr2);
				}
			}
			it.close();
			edgeSet.close();

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
