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

public class EdgeBFS implements GraphIterator<Edge> {

	LinkedList<Edge> list;
	HashSet<Vertex> visited;

	public EdgeBFS(Vertex src) {
		list    = new LinkedList<Edge>();
		visited = new HashSet<Vertex>();
		visited.add(src);
		GraphIterator<Edge> it = src.getEdgesOut();
		while (it.hasNext()) {
			Edge e = it.next();
			list.add(e);
			visited.add(e.getEnd());
		}
		it.close();
	}
	public boolean hasNext() {
		return !list.isEmpty();
	}
	public Edge next() {
		Edge e = list.poll(), temp;
		Vertex v;
		GraphIterator<Edge> it = e.getEnd().getEdgesOut();
		while (it.hasNext()) {
			temp = it.next();
			v = temp.getEnd();
			if (!visited.contains(v)) {
				list.add(temp);
				visited.add(v);
			}
		}
		it.close();
		return e;
	}
	public void close() {
		list.clear();
		visited.clear();
	}
}
