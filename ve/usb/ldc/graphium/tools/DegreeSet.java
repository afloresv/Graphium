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

public class DegreeSet {

	public static Graphium g;
	public static String rdftype = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	public static void main(String[] args) {

		// Checking arguments
		if (args.length != 3) {
			System.err.println("Tree arguments needed to analize an RDF graph:\n"
				+"<DB location> <edge direction (in out)> <degree threshold>");
			System.exit(1);
		}

		int degree = Integer.parseInt(args[2]);
		boolean in = args[1].equals("in");

		g = GraphiumLoader.open(args[0]);

		Vertex ver;
		GraphIterator<Vertex> it;

		it = g.getAllVertex();
		while (it.hasNext()) {

			int count;
			ver = it.next();
			if (in) count = ver.getInDegree();
			else    count = ver.getOutDegree();

			if (count >= degree)
				System.out.println(ver.getAny());
		}
		it.close();
		g.close();
	}
}
