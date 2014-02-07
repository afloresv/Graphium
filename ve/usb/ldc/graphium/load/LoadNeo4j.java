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

package ve.usb.ldc.graphium.load;

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

public class LoadNeo4j extends LoadNT {

	private BatchInserter inserter;
	private BatchInserterIndexProvider indexProvider;
	private BatchInserterIndex[] indexNode = new BatchInserterIndex[2];
	private Map<String, Object> pMap;
	private DynamicRelationshipType relType;

	public LoadNeo4j(String pathDB) {
		inserter = BatchInserters.inserter(pathDB);
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		for (int i=0 ; i<2 ; i++) {
			indexNode[i] = indexProvider.
				nodeIndex(propString[i], MapUtil.stringMap("type","exact"));
			indexNode[i].setCacheCapacity(propString[i],5000000);
		}
		relType = DynamicRelationshipType.withName(propString[5]);
	}

	public long addNode(int indexType, String value) {
		long newNode;
		if (indexType==2) {
			pMap = MapUtil.map(propString[indexType],value);
			newNode = inserter.createNode(pMap);
			return newNode;
		}
		IndexHits<Long> hitSearch =
			indexNode[indexType].get(propString[indexType],value);
		if (hitSearch.size() == 0) {
			pMap = MapUtil.map(propString[indexType],value);
			newNode = inserter.createNode(pMap);
			indexNode[indexType].add(newNode,pMap);
		} else {
			newNode = hitSearch.getSingle();
		}
		return newNode;
	}

	public void addAttr(long node, int indexType, String value) {
		pMap = inserter.getNodeProperties(node);
		pMap.put(propString[indexType],value);
		inserter.setNodeProperties(node,pMap);
	}

	public void addRelationship(long src, long dst, String URI) {
		pMap = MapUtil.map(propString[0],URI);
		inserter.createRelationship(src,dst,relType,pMap);
	}

	public void close() {
		for (int i=0 ; i<2 ; i++)
			indexNode[i].flush();
		indexProvider.shutdown();
		inserter.shutdown();
	}
}
