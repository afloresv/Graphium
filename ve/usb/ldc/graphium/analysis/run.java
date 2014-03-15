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

	public static void main(String[] args) {

		String gdbm = args[0],
			path = args[1];
		if (gdbm.equals("Neo4j"))
			g = new Neo4jRDF(path);
		else if (gdbm.equals("Sparksee"))
			g = new SparkseeRDF(path);
		else throw (new Error("Wrong GDBM (Neo4j or Sparksee)"));

		Vertex ver;
		GraphIterator<Vertex> itv;
		int n_URI = 0, n_NodeID = 0, n_Literal = 0;

		itv = g.getAllVertex();
		while (itv.hasNext()) {
			ver = itv.next();
			if      (ver.isURI())     n_URI++;
			else if (ver.isLiteral()) n_Literal++;
			else if (ver.isNodeID())  n_NodeID++;
		}
		itv.close();

		System.out.println("URI: " + n_URI);
		System.out.println("NodeID: " + n_NodeID);
		System.out.println("Literal: " + n_Literal);

		g.close();
	}
}
