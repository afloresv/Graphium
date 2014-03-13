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

package ve.usb.ldc.graphium.berlin;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class kHops extends BerlinQuery {

	int k;

	public static void main(String[] args) {
		kHops Q = new kHops(args[1],"../" + args[1] + "DB/" + args[2]);
		Q.k = Integer.parseInt(args[0]);
		Q.runExperiment();
		Q.close();
	}

	public kHops(String gdbm, String path) {
		super(gdbm,path);
	}

	public void runQuery() {

		r = new ResultGenerator();
		IteratorGraph it;
		Vertex srcNode = g.getVertexURI(bsbm+"Product");
		if (srcNode == null) return;

		HashSet[] set = new HashSet[2];
		set[0] = new HashSet<Vertex>();
		set[1] = new HashSet<Vertex>();
		set[0].add(srcNode);
		Iterator<Vertex> hopN;

		int i = 0, j = 1, ind = 0;
		while (ind < k) {
			hopN = set[i].iterator();
			while (hopN.hasNext()) {
				it = hopN.next().getEdgesIn();
				while (it.hasNext())
					set[j].add(it.next().getStart());
				it.close();
			}
			set[i].clear();
			i = (i==0 ? 1 : 0);
			j = (j==0 ? 1 : 0);
			ind++;
		}

		hopN = set[i].iterator();
		while (hopN.hasNext())
			r.newResult(hopN.next().getAny()).print();
	}
}
