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

public class IterDFS implements GraphIterator<Vertex> {

	LinkedList<Vertex> list;
	HashSet<Vertex> visited;

	public IterDFS(Vertex src) {
		list = new LinkedList<Vertex>();
		list.push(src);
		visited = new HashSet<Vertex>();
	}
	public boolean hasNext() {
		Vertex temp;
		while (!list.isEmpty()) {
			temp = list.peek();
			if (!visited.contains(temp))
				return true;
			list.pop();
		}
		return false;
	}
	public Vertex next() {
		Vertex n = list.pop(), temp;
		visited.add(n);
		GraphIterator<Edge> it = n.getEdgesOut();
		while (it.hasNext()) {
			temp = it.next().getEnd();
			if (!visited.contains(temp))
				list.push(temp);
		}
		it.close();
		return n;
	}
	public void close() {
		list.clear();
		visited.clear();
	}
}
