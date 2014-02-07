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

public class Q01 extends Neo4j implements BerlinQuery {

	int[][] inst = {
		{477,15422,123,54}
	};

	public Q01(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q01 testQ = new Q01(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		HashSet[] sets = new HashSet[2];
		sets[0] = new HashSet<Node>();
		sets[1] = new HashSet<Node>();

		Node nURI, nProd;
		Relationship rel;
		Iterator<Relationship> it;

		nURI = indexURI.get(prop[0],bsbminst+"ProductType"+inst[ind][0]).getSingle();
		if (nURI == null) return;
		it = nURI.getRelationships(relType,Direction.INCOMING).iterator();
		while (it.hasNext()) {
			rel = it.next();
			if (rel.getProperty(prop[0]).equals(rdf+"type"))
				sets[0].add(rel.getStartNode());
		}

		nURI = indexURI.get(prop[0],bsbminst+"ProductFeature"+inst[ind][1]).getSingle();
		if (nURI == null) return;
		it = nURI.getRelationships(relType,Direction.INCOMING).iterator();
		while (it.hasNext()) {
			rel = it.next();
			nProd = rel.getStartNode();
			if (rel.getProperty(prop[0]).equals(bsbm+"productFeature")
				&& sets[0].contains(nProd))
				sets[1].add(nProd);
		}
		sets[0].clear();

		nURI = indexURI.get(prop[0],bsbminst+"ProductFeature"+inst[ind][2]).getSingle();
		if (nURI == null) return;
		it = nURI.getRelationships(relType,Direction.INCOMING).iterator();
		while (it.hasNext()) {
			rel = it.next();
			nProd = rel.getStartNode();
			if (rel.getProperty(prop[0]).equals(bsbm+"productFeature")
				&& sets[1].contains(nProd))
				sets[0].add(nProd);
		}

		ArrayList<ResultTuple> results = new ArrayList<ResultTuple>();
		Iterator<Node> itProd = sets[0].iterator();
		String product, temp;
		while (itProd.hasNext()) {
			HashSet<String>
				setL = new HashSet<String>(),
				setV = new HashSet<String>();

			nProd = itProd.next();
			it = nProd.getRelationships(relType,Direction.OUTGOING).iterator();
			while (it.hasNext()) {
				rel = it.next();
				temp = (String)rel.getProperty(prop[0]);
				if (temp.equals(rdfs+"label"))
					setL.add(getAnyProp(rel.getEndNode()));
				else if (temp.equals(bsbm+"productPropertyNumeric1"))
					setV.add(getAnyProp(rel.getEndNode()));
			}

			product = getAnyProp(nProd);
			for (String value : setV) { try {
				if (Integer.parseInt(value)>inst[ind][3])
					for (String label : setL)
						results.add(new ResultTuple(1,product,label));
			} catch (NumberFormatException nfe) {} }
		}

		// ORDER BY ?label
		Collections.sort(results);

		// LIMIT 10
		for (int i=0 ; i<10 && i<results.size() ; i++)
			results.get(i).print();
	}
}
