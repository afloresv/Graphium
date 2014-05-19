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

package test;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class offers {

	public static void main(String[] args) {

		GraphRDF g = GraphiumLoader.open(args[0]);

		Vertex o,v;
		GraphIterator<Edge> ito, it;

		o = g.getVertexURI("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/Offer");
		if (o==null) return;

		ito = o.getEdgesIn();
		while (ito.hasNext()) {
			v = ito.next().getStart();
			int degree_in=0, degree_out=0;

			it = v.getEdgesIn();
			while (it.hasNext()) {
				it.next();
				degree_in++;
			}
			it.close();

			it = v.getEdgesOut();
			while (it.hasNext()) {
				it.next();
				degree_out++;
			}
			it.close();

			System.out.format("%3d%3d ",degree_in,degree_out);
			System.out.println(v.getAny());
		}
		ito.close();

		g.close();
	}
}
