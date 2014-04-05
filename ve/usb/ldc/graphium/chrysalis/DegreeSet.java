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

public class DegreeSet {

	public static GraphRDF g;

	public static void main(String[] args) {

		// Checking arguments
		if (args.length != 4) {
			System.err.println("Four arguments needed to analize an RDF graph:\n"
				+"<GDBM (Sparksee or Neo4j)> <DB location> <edge direction (in out)> <degree threshold>");
			System.exit(1);
		}

		int degree = Integer.parseInt(args[3]);
		boolean in = args[2].equals("in");

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

		itv = g.getAllVertex();
		while (itv.hasNext()) {

			int count = 0;
			ver = itv.next();
			if (in) ite = ver.getEdgesIn();
			else    ite = ver.getEdgesOut();
			while (ite.hasNext()) {
				rel = ite.next();
				count++;
			}
			ite.close();

			if (count >= degree)
				System.out.println(ver.getAny());
		}
		itv.close();
		g.close();
	}
}
