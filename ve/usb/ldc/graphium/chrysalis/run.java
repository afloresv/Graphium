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

		// Checking arguments
		if (args.length != 2) {
			System.err.println("Two arguments needed to analize an RDF graph:"
				+" <GDBM (Sparksee or Neo4j)> <DB location>");
			System.exit(1);
		}

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
			n_BlankNode = 0,
			n_Literal = 0,
			n_Edges = 0,
			n_mutual = 0;

		HashSet<URI> predicates = new HashSet<URI>();
		HashSet<Vertex> adj = new HashSet<Vertex>();

		itv = g.getAllVertex();
		while (itv.hasNext()) {

			ver = itv.next();
			int countIn = 0, countOut = 0;

			if      (ver.isURI())     n_URI++;
			else if (ver.isLiteral()) n_Literal++;
			else if (ver.isBlankNode())  n_BlankNode++;

			adj.clear();

			ite = ver.getEdgesOut();
			while (ite.hasNext()) {
				rel = ite.next();
				predicates.add(rel.getURI());
				countOut++;
				adj.add(rel.getEnd());
			}
			ite.close();

			ite = ver.getEdgesIn();
			while (ite.hasNext()) {
				rel = ite.next();
				countIn++;
				if (adj.contains(rel.getStart()))
					n_mutual++;
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

		int n_vertices = n_URI + n_BlankNode + n_Literal;
		System.out.format("uri=%d%n",n_URI);
		System.out.format("blanknode=%d%n",n_BlankNode);
		System.out.format("literal=%d%n",n_Literal);
		System.out.format("edge=%d%n",n_Edges);
		System.out.format("predicate=%d%n",predicates.size());
		System.out.format("in-h-index=%d%n",inHindex);
		System.out.format("out-h-index=%d%n",outHindex);
		System.out.format("mutual-edge=%d%n",n_mutual);

		System.out.format("io-degree%n");
		for (int i=50 ; i>=0 ; i--) {
			for (int j=0 ; j<50 ; j++)
				System.out.print(ioDegree[i][j] + ",");
			System.out.println(ioDegree[i][50]);
		}
	}
}
