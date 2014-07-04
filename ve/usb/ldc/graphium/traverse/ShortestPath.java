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

package ve.usb.ldc.graphium.traverse;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public final class ShortestPath {

	private static HashMap<Vertex,Edge> parent;

	public static final GraphIterator<Edge> run(Vertex src, Vertex dst) {

		parent = new HashMap<Vertex,Edge>();
		Edge e;
		Vertex v;
		GraphIterator<Edge> it = BFS.edge(src);
		while (it.hasNext()) {
			e = it.next();
			v = e.getEnd();
			parent.put(v,e);
			if (v.equals(dst))
				break;
		}
		it.close();

		LinkedList<Edge> path = new LinkedList<Edge>();
		v = dst;
		while (true) {
			e = parent.get(v);
			if (e==null)
				break;
			path.push(e);
			v = e.getStart();
		}

		return (new SimpleGraphIterator<Edge>(path.iterator()));
	}

	public static final void run(Graphium g, String s, String d, String file) {

		Vertex src = g.getVertexURI(s);
		Vertex dst = g.getVertexURI(d);
		GraphIterator<Edge> it = run(src,dst);
		
		try {
			Edge e;
			PrintWriter out = new PrintWriter(new FileWriter(file));
			while (it.hasNext())
				out.println(it.next());
			it.close();
			out.close();
		} catch (Exception e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Graphium g = GraphiumLoader.open(args[0]);
		run(g,args[1],args[2],args[3]);
		g.close();
	}
}
