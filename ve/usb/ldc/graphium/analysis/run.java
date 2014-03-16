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

package ve.usb.ldc.graphium.analysis;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class run {

	public static GraphRDF g;
	public static int[][] iom = new int[51][51];

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
				countOut++;
			}
			ite.close();
			n_Edges += countOut;

			ite = ver.getEdgesIn();
			while (ite.hasNext()) {
				rel = ite.next();
				countIn++;
			}
			ite.close();

			iom[(countIn<50 ? countIn : 50)][(countOut<50 ? countOut : 50)]++;
		}
		itv.close();

		g.close();

		System.out.format("Vertices %14d%n",n_URI + n_NodeID + n_Literal);
		System.out.format("| URI     %13d%n",n_URI);
		System.out.format("| NodeID  %13d%n",n_NodeID);
		System.out.format("| Literal %13d%n",n_Literal);
		System.out.format("Edges    %14d%n",n_Edges);

		for (int i=0 ; i<51 ; i++) {
			for (int j=0 ; j<50 ; j++)
				System.err.print(iom[i][j] + ",");
			System.err.println(iom[i][50]);
		}
	}
}