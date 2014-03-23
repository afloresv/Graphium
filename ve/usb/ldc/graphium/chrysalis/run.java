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

package ve.usb.ldc.graphium.chrysalis;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class run {

	public static GraphRDF g;
	public static int[][] ioDegree = new int[51][51];
	public static int[] inDegree  = new int[10000];
	public static int[] outDegree = new int[10000];

	public static void main(String[] args) {

		String gdbm = args[0],
			path = args[1];
		if (gdbm.equals("Neo4j"))
			g = new Neo4jRDF(path);
		else if (gdbm.equals("Sparksee"))
			g = new SparkseeRDF(path);
		else throw (new Error("Wrong GDBM (Neo4j or Sparksee)"));

		Vertex ver;
		Edge rel;
		GraphIterator<Edge> ite;
		GraphIterator<Vertex> itv;
		int n_URI = 0,
			n_NodeID = 0,
			n_Literal = 0,
			n_Edges = 0;

		HashSet<URI> predicates = new HashSet<URI>();

		itv = g.getAllVertex();
		while (itv.hasNext()) {

			ver = itv.next();
			int countIn = 0, countOut = 0;

			if      (ver.isURI())     n_URI++;
			else if (ver.isLiteral()) n_Literal++;
			else if (ver.isNodeID())  n_NodeID++;

			ite = ver.getEdgesOut();
			while (ite.hasNext()) {
				rel = ite.next();
				predicates.add(rel.getURI());
				countOut++;
			}
			ite.close();

			ite = ver.getEdgesIn();
			while (ite.hasNext()) {
				rel = ite.next();
				countIn++;
			}
			ite.close();

			n_Edges += countOut;
			ioDegree[Math.min(countIn,50)][Math.min(countOut,50)]++;
			outDegree[Math.min(countOut,9999)]++;
			inDegree [Math.min(countIn ,9999)]++;
		}
		itv.close();

		g.close();

		int inHindex = -1, outHindex = -1, acumIn = 0, acumOut = 0;
		for (int i=9999 ; i>=0 && (inHindex<0 || outHindex<0) ; i--) {
			if (inHindex<0) {
				acumIn += inDegree[i];
				if (acumIn>=i) inHindex = i;
			}
			if (outHindex<0) {
				acumOut += outDegree[i];
				if (acumOut>=i) outHindex = i;
			}
		}

		int n_vertices = n_URI + n_NodeID + n_Literal;
		System.out.format("uri=%d%n",n_URI);
		System.out.format("nodeid=%d%n",n_NodeID);
		System.out.format("literal=%d%n",n_Literal);
		System.out.format("edge=%d%n",n_Edges);
		System.out.format("predicate=%d%n",predicates.size());
		System.out.format("in-h-index=%d%n",inHindex);
		System.out.format("out-h-index=%d%n",outHindex);

		System.out.format("oi-degree%n");
		for (int i=50 ; i>=0 ; i--) {
			for (int j=0 ; j<50 ; j++)
				System.out.print(ioDegree[i][j] + ",");
			System.out.println(ioDegree[i][50]);
		}
	}
}
