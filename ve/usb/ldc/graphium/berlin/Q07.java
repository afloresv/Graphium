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

public class Q07 extends BerlinQuery {

	int[][] inst = {
		{510,25065}
	};

	public static void main(String[] args) {
		BerlinQuery Q = new Q07(args[1],args[2]);
		Q.runQuery(Integer.parseInt(args[0]));
		Q.close();
	}

	public Q07(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery(int ind) {
		
		r = new ResultGenerator();
		Vertex pNode;
		Edge rel;
		IteratorGraph it;
		String relStr;

		HashSet<String> setProductLabel = new HashSet<String>();
		HashSet<Vertex>
			setOffer = new HashSet<Vertex>(),
			setReview = new HashSet<Vertex>();

		// FILTER (?product = bsbminst:dataFromProducer497/Product24437)
		pNode = g.getVertexURI(bsbminst+"dataFromProducer"
			+inst[ind][0]+"/Product"+inst[ind][1]);
		it = pNode.getEdgesOut();
		while (it.hasNext()) {
			rel = it.next();
			if (rel.getURI().equals(rdfs+"label")) {
				// ?product rdfs:label ?productLabel
				setProductLabel.add(rel.getEnd().getAny());
			}
		}
		it.close();

		it = pNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			relStr = rel.getURI();
			if (relStr.equals(bsbm+"product")) {
				// ?offer bsbm:product ?product
				setOffer.add(rel.getStart());
			} else if (relStr.equals(bsbm+"reviewFor")) {
				// ?review bsbm:reviewFor ?product
				setReview.add(rel.getStart());
			}
		}
		it.close();

		optBlock1 = new ArrayList<ResultTuple>();
		for (Vertex oNode : setOffer)
			findOffers(oNode);
		if (optBlock1.size()==0)
			optBlock1.add(r.newResult("","","",""));

		optBlock2 = new ArrayList<ResultTuple>();
		for (Vertex rNode : setReview)
			findReview(rNode);
		if (optBlock2.size()==0)
			optBlock2.add(r.newResult("","","","","",""));

		for (String productLabel : setProductLabel)
		for (ResultTuple opt1 : optBlock1)
		for (ResultTuple opt2 : optBlock2)
			(r.newResult(productLabel,opt1.elem[0],opt1.elem[1],
			opt1.elem[2],opt1.elem[3],opt2.elem[0],opt2.elem[1],opt2.elem[2],
			opt2.elem[3],opt2.elem[4],opt2.elem[5])).print();
	}

	ArrayList<ResultTuple> optBlock1, optBlock2;

	private void findOffers(Vertex oNode) {
		Edge rel;
		IteratorGraph it;
		String relStr, offer = oNode.getAny();
		int filterDate = 0;

		HashSet<String> setPrice = new HashSet<String>();
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
					filterDate++;
			} else if (relStr.equals(bsbm+"vendor")) {
				// ?offer bsbm:vendor ?vendor
				setVendor[0].add(rel.getEnd());
			} else if (relStr.equals(dc+"publisher")) {
				// ?offer dc:publisher ?vendor
				setVendor[1].add(rel.getEnd());
			}
		}
		it.close();

		if (filterDate==0 || setPrice.size()==0)
			return;

		int maxInd, minInd;
		minInd = (setVendor[0].size() <= setVendor[1].size() ? 0 : 1);
		maxInd = (minInd + 1) % 2;

		String vendor;
		Iterator<Vertex> itv = setVendor[minInd].iterator();
		while (itv.hasNext()) {
			Vertex vNode = itv.next();
			if (!setVendor[maxInd].contains(vNode))
				continue;
			
			vendor = vNode.getAny();
			HashSet<String> setVendorTitle = new HashSet<String>();

			boolean found = false;
			it = vNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relStr = rel.getURI();
				if (relStr.equals(rdfs+"label")) {
					// ?vendor rdfs:label ?vendorTitle
					setVendorTitle.add(rel.getEnd().getAny());
				} else if (relStr.equals(bsbm+"country")) {
					// ?vendor bsbm:country <http://downlode.org/rdf/iso-3166/countries#DE>
					if (rel.getEnd().getAny().equals(
						"http://downlode.org/rdf/iso-3166/countries#DE"))
						found = true;
				}
			}
			it.close();

			for (String vendorTitle : setVendorTitle)
			for (String price : setPrice)
			for (int i=0 ; i<filterDate ; i++)
				optBlock1.add(r.newResult(offer,price,vendor,vendorTitle));
		}
	}

	private void findReview(Vertex rNode) {
		Edge rel;
		IteratorGraph it;
		String relStr, review = rNode.getAny();

		HashSet<Vertex> setReviewer = new HashSet<Vertex>();
		HashSet<String>
			setRevTitle = new HashSet<String>(),
			setRating1 = new HashSet<String>(),
			setRating2 = new HashSet<String>();

		it = rNode.getEdgesOut();
		while (it.hasNext()) {
			rel = it.next();
			relStr = rel.getURI();
			if (relStr.equals(rev+"reviewer")) {
				// ?review rev:reviewer ?reviewer
				setReviewer.add(rel.getEnd());
			} else if (relStr.equals(dc+"title")) {
				// ?review dc:title ?revTitle
				setRevTitle.add(rel.getEnd().getAny());
			} else if (relStr.equals(bsbm+"rating1")) {
				// OPTIONAL {?review bsbm:rating1 ?rating1 }
				setRating1.add(rel.getEnd().getAny());
			} else if (relStr.equals(bsbm+"rating2")) {
				// OPTIONAL {?review bsbm:rating2 ?rating2 }
				setRating2.add(rel.getEnd().getAny());
			}
		}
		it.close();

		if (setRating1.size()==0) setRating1.add("");
		if (setRating2.size()==0) setRating2.add("");

		for (Vertex rrNode : setReviewer) {
			String reviewer = rrNode.getAny();
			it = rrNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				if (rel.getURI().equals(foaf+"name")) {
					// ?reviewer foaf:name ?revName
					for (String revTitle : setRevTitle)
					for (String rating1 : setRating1)
					for (String rating2 : setRating2)
						optBlock2.add(r.newResult(review,revTitle,
						reviewer,rel.getEnd().getAny(),rating1,rating2));
				}
			}
			it.close();
		}
	}
}
