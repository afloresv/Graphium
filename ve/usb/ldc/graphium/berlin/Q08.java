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

public class Q08 extends BerlinQuery {

	public static void main(String[] args) {
		BerlinQuery Q = new Q08(args[1],"../" + args[1] + "DB/" + args[2]);
		Q.inst = new InstanceReader(2,args[1],args[2],8,Integer.parseInt(args[0]));
		Q.runExperiment();
		Q.close();
	}

	public Q08(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery() {

		r = new ResultGenerator(2);
		Vertex iNode;
		Edge rel;
		GraphIterator<Edge> it;
		RDFobject relURI, reviewer;

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();

		HashSet<Vertex> setReview = new HashSet<Vertex>();

		// ?review bsbm:reviewFor bsbminst:dataFromProducer510/Product25065 .
		iNode = g.getVertexURI(bsbminst+"dataFromProducer"
			+inst.get(0)+"/Product"+inst.get(1));
		if (iNode == null) return;
		it = iNode.getEdgesIn();
		while (it.hasNext()) {
			rel = it.next();
			relURI = rel.getURI();
			if (relURI.equals(bsbm+"reviewFor"))
				setReview.add(rel.getStart());
		}
		it.close();

		for (Vertex reviewNode : setReview) {
			HashSet<Vertex> setReviewer = new HashSet<Vertex>();
			HashSet<RDFobject>
				setTitle = new HashSet<RDFobject>(),
				setText = new HashSet<RDFobject>(),
				setReviewDate = new HashSet<RDFobject>();

			it = reviewNode.getEdgesOut();
			while (it.hasNext()) {
				rel = it.next();
				relURI = rel.getURI();
				if (relURI.equals(dc+"title")) {
					// ?review dc:title ?title .
					setTitle.add(rel.getStart().getAny());
				} else if (relURI.equals(rev+"text")) {
					// ?review rev:text ?text .
					// #FILTER langMatches( lang(?text), "EN" )
					Vertex tempN = rel.getStart();
					String tempL = tempN.getLang();
					if (tempL != null && tempL.matches("(E|e)(N|n)(|-[a-zA-Z]*)"))
						setText.add(tempN.getLiteral());
				} else if (relURI.equals(bsbm+"reviewDate")) {
					// ?review bsbm:reviewDate ?reviewDate .
					setReviewDate.add(rel.getStart().getAny());
				} else if (relURI.equals(rev+"reviewer")) {
					// ?review rev:reviewer ?reviewer .
					setReviewer.add(rel.getStart());
				}
			}
			it.close();

			for (Vertex reviewerNode : setReviewer) {
				reviewer = reviewerNode.getAny();

				HashSet<RDFobject>
					setReviewerName = new HashSet<RDFobject>(),
					setRating1 = new HashSet<RDFobject>(),
					setRating2 = new HashSet<RDFobject>(),
					setRating3 = new HashSet<RDFobject>(),
					setRating4 = new HashSet<RDFobject>();

				it = reviewerNode.getEdgesOut();
				while (it.hasNext()) {
					rel = it.next();
					relURI = rel.getURI();
					if (relURI.equals(foaf+"name")) {
						// ?reviewer foaf:name ?reviewerName .
						setReviewerName.add(rel.getStart().getAny());
					} else if (relURI.equals(bsbm+"rating1")) {
						// OPTIONAL { ?review bsbm:rating1 ?rating1 . }
						setRating1.add(rel.getStart().getAny());
					} else if (relURI.equals(bsbm+"rating2")) {
						// OPTIONAL { ?review bsbm:rating2 ?rating2 . }
						setRating2.add(rel.getStart().getAny());
					} else if (relURI.equals(bsbm+"rating3")) {
						// OPTIONAL { ?review bsbm:rating3 ?rating3 . }
						setRating3.add(rel.getStart().getAny());
					} else if (relURI.equals(bsbm+"rating4")) {
						// OPTIONAL { ?review bsbm:rating4 ?rating4 . }
						setRating4.add(rel.getStart().getAny());
					}
				}
				it.close();

				if (setRating1.size()==0) setRating1.add(new RDFobject());
				if (setRating2.size()==0) setRating2.add(new RDFobject());
				if (setRating3.size()==0) setRating3.add(new RDFobject());
				if (setRating4.size()==0) setRating4.add(new RDFobject());

				for (RDFobject title : setTitle)
				for (RDFobject text : setText)
				for (RDFobject reviewDate : setReviewDate)
				for (RDFobject reviewerName : setReviewerName)
				for (RDFobject rating1 : setRating1)
				for (RDFobject rating2 : setRating2)
				for (RDFobject rating3 : setRating3)
				for (RDFobject rating4 : setRating4)
					results.add(r.newResult(title,text,reviewDate,reviewer,
						reviewerName,rating1,rating2,rating3,rating4));
			}
		}

		// ORDER BY ?reviewDate
		Collections.sort(results);

		// DESC && LIMIT 20
		for (int i=results.size()-1, j=0 ; i>=0 && j<20 ; i--, j++)
			results.get(i).print();
	}
}
