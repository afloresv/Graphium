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

import com.sparsity.sparksee.gdb.*;

import ve.usb.ldc.graphium.core.*;

public class LoadSparksee extends LoadNT {

	private SparkseeConfig cfg;
	private Sparksee sparksee;
	private Database db;
	private Session sess;
	private com.sparsity.sparksee.gdb.Graph g;
	private int[] NodeType = new int[3];
	private int[] AttrType = new int[9];
	private int TypeEdge, AttrEdge;
	private int E;
	private Value valdb = new Value();

	public LoadSparksee(String pathDB) {
		try {
			cfg = new SparkseeConfig();
			cfg.setLicense(SparkseeRDF.licence);
			cfg.setCacheMaxSize(10240);
			sparksee = new Sparksee(cfg);
			(new File(pathDB)).mkdirs();
			db = sparksee.create(pathDB+"/SparkseeDB.gdb","SparkseeBD");
			sess = db.newSession();
			g = sess.getGraph();
			E = 0;
			sess.begin();

			// URI Nodes
			NodeType[0] = g.newNodeType(Attr.URI);
			AttrType[0] = g.newAttribute(NodeType[0], Attr.URI,
				DataType.String, AttributeKind.Unique);

			// BlankNode Nodes
			NodeType[1] = g.newNodeType(Attr.BlankNode);
			AttrType[1] = g.newAttribute(NodeType[1], Attr.BlankNode,
				DataType.String, AttributeKind.Unique);

			// Literal Nodes
			NodeType[2] = g.newNodeType(Attr.Literal);
			AttrType[2] = g.newAttribute(NodeType[2], Attr.Literal,
				DataType.Text, AttributeKind.Basic);
			AttrType[3] = g.newAttribute(NodeType[2], Attr.Lang,
				DataType.String, AttributeKind.Basic);
			AttrType[4] = g.newAttribute(NodeType[2], Attr.Type,
				DataType.String, AttributeKind.Basic);
			AttrType[5] = g.newAttribute(NodeType[2], Attr.valBool,
				DataType.Boolean, AttributeKind.Basic);
			AttrType[6] = g.newAttribute(NodeType[2], Attr.valInt,
				DataType.Long, AttributeKind.Basic);
			AttrType[7] = g.newAttribute(NodeType[2], Attr.valDouble,
				DataType.Double, AttributeKind.Basic);
			AttrType[8] = g.newAttribute(NodeType[2], Attr.valDate,
				DataType.Timestamp, AttributeKind.Basic);

			// Edges
			TypeEdge = g.newEdgeType(Attr.Predicate,true,true);
			AttrEdge = g.newAttribute(TypeEdge, Attr.Predicate,
				DataType.String, AttributeKind.Basic);

		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public long addNode(int indexType, String value) {
		long newNode;
		E++;
		if (E % 100000 == 0) {
			sess.commit();
			sess.begin();
		}
		switch (indexType) {
		case 2:
			newNode = g.newNode(NodeType[2]);
			TextStream valStream = new TextStream(false);
			g.setAttributeText(newNode, AttrType[2],valStream);
			char[] buff = value.toCharArray();
			valStream.write(buff, buff.length);
			valStream.close();
			break;
		default:
			newNode = g.findObject(AttrType[indexType],
				valdb.setString(value));
			if (newNode == Objects.InvalidOID) {
				newNode = g.newNode(NodeType[indexType]);
				g.setAttribute(newNode, AttrType[indexType],
					valdb.setString(value));
			}
			break;
		}
		return newNode;
	}

	public void addAttr(long node, int indexType, Object value) {
		if (value instanceof String)
			valdb.setString((String)value);
		else if (value instanceof Boolean)
			valdb.setBoolean((Boolean)value);
		else if (value instanceof Long)
			valdb.setLong((Long)value);
		else if (value instanceof Double)
			valdb.setDouble((Double)value);
		else if (value instanceof Date)
			valdb.setTimestamp((Date)value);
		else throw (new Error("Type Error."));
		g.setAttribute(node,AttrType[indexType],valdb);
	}

	public void addRelationship(long src, long dst, String URI) {
		long edgeID = g.newEdge(TypeEdge,src,dst);
		g.setAttribute(edgeID,AttrEdge,valdb.setString(URI));
	}

	public void close() {
		sess.commit();
		sess.close();
		db.close();
		sparksee.close();
	}
}
