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

public class Q11 extends Neo4j implements BerlinQuery {

	int[][] inst = {
		{215,423241}
	};

	public Q11(String path) {
		super(path);
	}

	public static void main(String args[]) {
		Q11 testQ = new Q11(args[0]);
		testQ.runQuery(Integer.parseInt(args[1]));
		testQ.close();
	}

	public void runQuery(int ind) {

		Node iNode;
		Relationship rel;
		Iterator<Relationship> it;

		iNode = getNodeFromURI(bsbminst+"dataFromVendor"+inst[ind][0]+"/Offer"+inst[ind][1]);
		if (iNode == NodeNotFound) return;
		it = iNode.getRelationships(relType,Direction.OUTGOING).iterator();
		while (it.hasNext()) {
			rel = it.next();
			// bsbminst:dataFromVendor215/Offer423241 ?property ?hasValue
			(new ResultTuple(getEdgeURI(rel),getAnyProp(getEndNode(rel)),"")).print();
		}

		// UNION

		it = iNode.getRelationships(relType,Direction.INCOMING).iterator();
		while (it.hasNext()) {
			rel = it.next();
			// ?isValueOf ?property bsbminst:dataFromVendor215/Offer423241
			(new ResultTuple(getEdgeURI(rel),"",getAnyProp(getStartNode(rel)))).print();
		}
	}
}
