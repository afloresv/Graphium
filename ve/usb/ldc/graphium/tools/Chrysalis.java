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

package ve.usb.ldc.graphium.tools;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class Chrysalis {

	public static Graphium g;
	public static int[][] ioDegree = new int[51][51];
	public static int[] inDegree  = new int[10000];
	public static int[] outDegree = new int[10000];

	public static void main(String[] args) {

		// Checking arguments
		if (args.length != 1) {
			System.err.println("One argument needed to analize an RDF graph:"
				+" <DB location>");
			System.exit(1);
		}

		g = GraphiumLoader.open(args[0]);

		Vertex ver, temp;
		Edge rel;
		GraphIterator<Edge> ite;
		GraphIterator<Vertex> itv;
		int n_URI = 0,
			n_BlankNode = 0,
			n_Literal = 0,
			n_Edges = 0,
			n_mutual = 0;

		HashSet<URI> predicates = new HashSet<URI>();
		HashSet<Vertex> adj1 = new HashSet<Vertex>();
		HashSet<Vertex> adj2 = new HashSet<Vertex>();

		itv = g.getAllVertex();
		while (itv.hasNext()) {

			ver = itv.next();
			int countIn = 0, countOut = 0;

			if      (ver.isURI())     n_URI++;
			else if (ver.isLiteral()) n_Literal++;
			else if (ver.isBlankNode())  n_BlankNode++;

			ite = ver.getEdgesOut();
			while (ite.hasNext()) {
				rel = ite.next();
				predicates.add(rel.getURI());
				countOut++;
				temp = rel.getEnd();
				if (!ver.equals(temp))
					adj1.add(temp);
			}
			ite.close();

			ite = ver.getEdgesIn();
			while (ite.hasNext()) {
				rel = ite.next();
				countIn++;
				temp = rel.getStart();
				if (adj1.contains(temp))
					adj2.add(temp);
			}
			ite.close();

			n_mutual += adj2.size();
			n_Edges += countOut;
			ioDegree[Math.min(countIn,50)][Math.min(countOut,50)]++;
			outDegree[Math.min(countOut,9999)]++;
			inDegree [Math.min(countIn ,9999)]++;

			adj1.clear();
			adj2.clear();
		}
		itv.close();

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
		System.out.println("{");
		System.out.format("\t\"uri\":%d,%n",n_URI);
		System.out.format("\t\"blanknode\":%d,%n",n_BlankNode);
		System.out.format("\t\"literal\":%d,%n",n_Literal);
		System.out.format("\t\"edge\":%d,%n",n_Edges);
		System.out.format("\t\"predicate\":%d,%n",predicates.size());
		System.out.format("\t\"in_h_index\":%d,%n",inHindex);
		System.out.format("\t\"out_h_index\":%d,%n",outHindex);
		System.out.format("\t\"mutual_edge\":%d,%n",n_mutual/2);
                             
		System.out.format("\t\"io_degree\":[%n");
		for (int i=50 ; i>=0 ; i--) {
			System.out.print("\t\t[");
			for (int j=0 ; j<50 ; j++)
				System.out.print(ioDegree[i][j] + ",");
			System.out.println(ioDegree[i][50] + "]" + (i==0 ? "" : ","));
		}
		System.out.println("\t],");

		int count;
		itv = g.getAllVertex();
		while (itv.hasNext()) {
			ver = itv.next();
			count = ver.getInDegree();
			if (count >= inHindex) adj1.add(ver);
			count = ver.getOutDegree();
			if (count >= outHindex) adj2.add(ver);
		}
		itv.close();

		RDFobject obj;
		String sep = "[";
		System.out.print("\t\"in_h_set\":");
		for (Vertex v : adj1) {
			if      (v.isURI()) obj = v.getURI();
			else if (v.isLiteral()) obj = v.getLiteral();
			else obj = v.getBlankNode();
			System.out.print(sep + "\"" + obj + "\"");
			sep = ",";
		}
		System.out.println("],");
		sep = "[";
		System.out.print("\t\"out_h_set\":");
		for (Vertex v : adj2) {
			if      (v.isURI()) obj = v.getURI();
			else if (v.isLiteral()) obj = v.getLiteral();
			else obj = v.getBlankNode();
			System.out.print(sep + "\"" + obj + "\"");
			sep = ",";
		}
		System.out.println("]");
		System.out.println("}");
		g.close();
	}
}
