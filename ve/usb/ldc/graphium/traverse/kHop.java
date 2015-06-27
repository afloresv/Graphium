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

public final class kHop {

	public static final GraphIterator<Vertex> run(int k, Vertex... src) {
		HashSet set = new HashSet<Vertex>();
		for (int s=src.length-1 ; s>=0 ; s--)
			set.add(src[s]);
		return run(k, new SimpleGraphIterator(set.iterator()));
	}

	public static final GraphIterator<Vertex> run(int k, GraphIterator<Vertex> src) {

		GraphIterator<Edge> it;

		HashSet[] set = new HashSet[2];
		set[0] = new HashSet<Vertex>();
		set[1] = new HashSet<Vertex>();
		Iterator<Vertex> hop;

		while (src.hasNext())
			set[0].add(src.next());
		src.close();

		Vertex v;
		int i = 0, j = 1, ind = 0;
		while (ind < k) {
			hop = set[i].iterator();
			while (hop.hasNext()) {
				v = hop.next();
				it = v.getEdgesOut();
				while (it.hasNext())
					set[j].add(it.next().getEnd());
				it.close();
				it = v.getEdgesIn();
				while (it.hasNext())
					set[j].add(it.next().getStart());
				it.close();
			}
			set[i].clear();
			i = (i==0 ? 1 : 0);
			j = (j==0 ? 1 : 0);
			ind++;
		}

		return (new SimpleGraphIterator(set[i].iterator()));
	}
 
	public static void main(String[] args) {

		Graphium g = GraphiumLoader.open(args[0]);

		Vertex nut = g.getVertexURI("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/micronutrient");

		GraphIterator<Edge> it1, it2, it3;
		it1 = nut.getEdgesIn();
		while (it1.hasNext()) {
			Vertex drug = it1.next().getStart();
			it2 = drug.getEdgesOut();
			while (it2.hasNext()) {
				Edge e = it2.next();
				Vertex dn = e.getEnd();
				if (dn.isLiteral()) continue;
				System.out.println(drug.getURI() + "\t<a>\t" + dn.getURI() + " .");
				if (e.getURI().equals("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target")) {
					it3 = dn.getEdgesOut();
					while (it3.hasNext()) {
						Vertex a = it3.next().getEnd();
						if (a.isLiteral()) continue;
						System.out.println(dn.getURI() + "\t<a>\t" + a.getURI() + " .");
					}
					it3.close();
				}
			}
			it2.close();
		}
		it1.close();

		g.close();
	}
}
