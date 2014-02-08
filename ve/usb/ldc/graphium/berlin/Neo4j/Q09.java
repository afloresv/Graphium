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

package ve.usb.ldc.graphium.berlin.Neo4j;

import java.util.*;
import java.lang.*;
import java.io.*;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.factory.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.unsafe.batchinsert.*;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.cypher.javacompat.*;
import org.neo4j.tooling.*;
import org.neo4j.kernel.*;
import org.neo4j.helpers.collection.*;

import ve.usb.ldc.graphium.core.*;
import ve.usb.ldc.graphium.berlin.general.*;

public class Q09 extends Neo4j implements BerlinQuery {

	int[][] inst = {
		{510,25065}
	};

	public Q09(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q09 testQ = new Q09(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		Node iNode;
		Relationship rel;
		Iterator<Relationship> it;
		String relStr, x, reviewer,
			otherStrI = bsbminst+"dataFromRatingSite"+inst[ind][0]+"/Review"+inst[ind][1];

		HashSet<Node> setX = new HashSet<Node>();

		// ?x rdf:type foaf:Person
		iNode = getNodeFromURI(foaf+"Person");
		if (iNode == NodeNotFound) return;
		it = iNode.getRelationships(relType,Direction.INCOMING).iterator();
		while (it.hasNext()) {
			rel = it.next();
			relStr = getEdgeURI(rel);
			if (relStr.equals(rdf+"type"))
				setX.add(getStartNode(rel));
		}

		for (Node xNode : setX) {

			boolean found = false;
			HashSet<Node> setReviewer = new HashSet<Node>();

			it = xNode.getRelationships(relType,Direction.INCOMING).iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				iNode = getStartNode(rel);
				if (relStr.equals(rdf+"type")) {
					// bsbminst:dataFromRatingSite8/Review75011 rev:reviewer ?x
					if (getAnyProp(iNode).equals(otherStrI))
						found = true;
				} else if (relStr.equals(rev+"reviewer")) {
					// ?reviewer rev:reviewer ?x
					setReviewer.add(iNode);
				}
			}

			if (!found) continue;

			x = getAnyProp(xNode);

			HashSet<String>
				setName = new HashSet<String>(),
				setMbox = new HashSet<String>(),
				setCountry = new HashSet<String>();

			it = xNode.getRelationships(relType,Direction.OUTGOING).iterator();
			while (it.hasNext()) {
				rel = it.next();
				relStr = getEdgeURI(rel);
				if (relStr.equals(foaf+"name")) {
					// ?x foaf:name ?name
					setName.add(getAnyProp(getEndNode(rel)));
				} else if (relStr.equals(foaf+"mbox_sha1sum")) {
					// ?x foaf:mbox_sha1sum ?mbox
					setMbox.add(getAnyProp(getEndNode(rel)));
				} else if (relStr.equals(bsbm+"country")) {
					// ?x bsbm:country ?country
					setCountry.add(getAnyProp(getEndNode(rel)));
				}
			}

			for (Node rNode : setReviewer) {
				reviewer = getAnyProp(rNode);

				HashSet<String>
					setProduct = new HashSet<String>(),
					setTitle = new HashSet<String>();

				it = rNode.getRelationships(relType,Direction.OUTGOING).iterator();
				while (it.hasNext()) {
					rel = it.next();
					relStr = getEdgeURI(rel);
					if (relStr.equals(bsbm+"reviewFor")) {
						// ?reviewer bsbm:reviewFor ?product
						setProduct.add(getAnyProp(getEndNode(rel)));
					} else if (relStr.equals(dc+"title")) {
						// ?reviewer dc:title ?title
						setTitle.add(getAnyProp(getEndNode(rel)));
					}
				}

				// Print results
				for (String name : setName)
				for (String mbox : setMbox)
				for (String country : setCountry)
				for (String product : setProduct)
				for (String title : setTitle)
					(new ResultTuple(x,name,mbox,country,reviewer,product,title)).print();
			}
		}
	}
}
