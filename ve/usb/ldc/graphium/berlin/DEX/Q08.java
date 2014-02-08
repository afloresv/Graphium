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

public class Q08 extends DEX implements BerlinQuery {

	int[][] inst = {
		{510,25065}
	};

	public Q08(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q08 testQ = new Q08(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		long iNode, vNode, rel;
		Objects edgeSet;
		ObjectsIterator it;
		String relStr, reviewer, temp;

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();

		HashSet<Long> setReview = new HashSet<Long>();

		// ?review bsbm:reviewFor bsbminst:dataFromProducer510/Product25065 .
		iNode = getNodeFromURI(bsbminst+"dataFromProducer"
			+inst[ind][0]+"/Product"+inst[ind][1]);
		if (iNode == NodeNotFound) return;
		edgeSet = g.explode(iNode,EdgeType,EdgesDirection.Ingoing);
		it = edgeSet.iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = getEdgeURI(rel);
			if (relStr.equals(bsbm+"reviewFor"))
				setReview.add(getStartNode(rel));
		}
		it.close();
		edgeSet.close();

		for (Long reviewNode : setReview) {
			HashSet<Long> setReviewer = new HashSet<Long>();
			HashSet<String>
				setTitle = new HashSet<String>(),
				setText = new HashSet<String>(),
				setReviewDate = new HashSet<String>();

			edgeSet = g.explode(reviewNode,EdgeType,EdgesDirection.Outgoing);
			it = edgeSet.iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				vNode = getStartNode(rel);
				if (relStr.equals(dc+"title")) {
					// ?review dc:title ?title .
					setTitle.add(getAnyProp(vNode));
				} else if (relStr.equals(rev+"text")) {
					// ?review rev:text ?text .
					setText.add(getAnyProp(vNode));
				} else if (relStr.equals(bsbm+"reviewDate")) {
					// ?review bsbm:reviewDate ?reviewDate .
					setReviewDate.add(getAnyProp(vNode));
				} else if (relStr.equals(rev+"reviewer")) {
					// ?review rev:reviewer ?reviewer .
					setReviewer.add(vNode);
				}
			}
			it.close();
			edgeSet.close();

			for (Long reviewerNode : setReviewer) {
				reviewer = getAnyProp(reviewerNode);

				HashSet<String>
					setReviewerName = new HashSet<String>(),
					setRating1 = new HashSet<String>(),
					setRating2 = new HashSet<String>(),
					setRating3 = new HashSet<String>(),
					setRating4 = new HashSet<String>();

				edgeSet = g.explode(reviewerNode,EdgeType,EdgesDirection.Outgoing);
				it = edgeSet.iterator();
				while (it.hasNext()) {
					rel = it.next();
					relStr = getEdgeURI(rel);
					vNode = getStartNode(rel);
					temp = getAnyProp(vNode);
					if (relStr.equals(foaf+"name")) {
						// ?reviewer foaf:name ?reviewerName .
						setReviewerName.add(temp);
					} else if (relStr.equals(bsbm+"rating1")) {
						// OPTIONAL { ?review bsbm:rating1 ?rating1 . }
						setRating1.add(temp);
					} else if (relStr.equals(bsbm+"rating2")) {
						// OPTIONAL { ?review bsbm:rating2 ?rating2 . }
						setRating2.add(temp);
					} else if (relStr.equals(bsbm+"rating3")) {
						// OPTIONAL { ?review bsbm:rating3 ?rating3 . }
						setRating3.add(temp);
					} else if (relStr.equals(bsbm+"rating4")) {
						// OPTIONAL { ?review bsbm:rating4 ?rating4 . }
						setRating4.add(temp);
					}
				}
				it.close();
				edgeSet.close();

				if (setRating1.size()==0) setRating1.add("");
				if (setRating2.size()==0) setRating2.add("");
				if (setRating3.size()==0) setRating3.add("");
				if (setRating4.size()==0) setRating4.add("");

				for (String title : setTitle)
				for (String text : setText)
				for (String reviewDate : setReviewDate)
				for (String reviewerName : setReviewerName)
				for (String rating1 : setRating1)
				for (String rating2 : setRating2)
				for (String rating3 : setRating3)
				for (String rating4 : setRating4)
					results.add(new ResultTuple(2,title,text,reviewDate,reviewer,
						reviewerName,rating1,rating2,rating3,rating4));
			}
		}

		// ORDER BY ?reviewDate
		Collections.sort(results);

		// DESC && LIMIT 20
		for (int i=results.size(), j=0 ; i>=0 && j<20 ; i--, j++)
			results.get(i).print();
	}
}
