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

package ve.usb.ldc.graphium.mining;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public final class DenseSubgraph {

	private static HashMap<Vertex,Integer> index;
	private static int[] degreeIn, degreeOut;
	private static boolean[] validIn, validOut;
	private static Vertex[] vArray;
	private static int V, E, S, T;

	private static double CalcDensity() {
		if (S*T != 0)
			return (((double)E)/Math.sqrt(S*T));
		return 0.0;
	}

	private static void RemoveOut(int i) {
		degreeOut[i] = 0;
		S--;
		Integer v;
		GraphIterator<Edge> it = vArray[i].getEdgesOut();
		while (it.hasNext()) {
			v = index.get(it.next().getEnd());
			if (degreeIn[v]!=0) {
				E--;
				degreeIn[v]--;
				if (degreeIn[v]==0) T--;
			}
		}
		it.close();
	}

	private static void RemoveIn(int i) {
		degreeIn[i] = 0;
		T--;
		Integer v;
		GraphIterator<Edge> it = vArray[i].getEdgesIn();
		while (it.hasNext()) {
			v = index.get(it.next().getStart());
			if (degreeOut[v]!=0) {
				E--;
				degreeOut[v]--;
				if (degreeOut[v]==0) S--;
			}
		}
		it.close();
	}

	private static int OptimumOut() {
		int opt = Integer.MAX_VALUE, ind=V;
		for (int i=0 ; i<V ; i++)
		if (degreeOut[i]!=0 && degreeOut[i]<opt) {
			opt = degreeOut[i];
			ind = i;
		}
		return ind;
	}

	private static int OptimumIn() {
		int opt = Integer.MAX_VALUE, ind=V;
		for (int i=0 ; i<V ; i++)
		if (degreeIn[i]!=0 && degreeIn[i]<opt) {
			opt = degreeIn[i];
			ind = i;
		}
		return ind;
	}

	public static final void run(Graphium g) {
		S = T = V = g.V();
		E = g.E();
		index     = new HashMap<Vertex,Integer>();
		validIn   = new boolean[V];
		validOut  = new boolean[V];
		degreeIn  = new int[V];
		degreeOut = new int[V];
		vArray    = new Vertex[V];

		int i = 0, j;
		Vertex v;
		GraphIterator<Vertex> it = g.getAllVertex();
		while (it.hasNext()) {
			v = it.next();
			index.put(v,i);
			vArray[i]    = v;
			degreeIn[i]  = v.getInDegree();
			degreeOut[i] = v.getOutDegree();
			if (degreeIn[i] > 0)
				validIn[i] = true;
			else T--;
			if (degreeOut[i] > 0)
				validOut[i] = true;
			else S--;
			i++;
		}
		it.close();

		double density = CalcDensity();

		int h=0;
		while (h<20 && E!=0) {
			h++;
			i = OptimumIn();
			j = OptimumOut();
			if (degreeIn[i] <= degreeOut[j])
				RemoveIn(i);
			else RemoveOut(j);

			double d = CalcDensity();
			if (d > density) {
				density = d;
				for (i=0 ; i<V ; i++) {
					validIn[i]  = degreeIn[i]  > 0;
					validOut[i] = degreeOut[i] > 0;
				}
			}
		}
	}

	public static final void run(Graphium g, String file) {

		run(g);
		
		try {
			PrintWriter subgraph = new PrintWriter(new FileWriter(file));
			GraphIterator<Edge> it;
			Edge rel;
			Vertex v;
			for (int i=0 ; i<V ; i++)
			if (validOut[i]) {
				it = vArray[i].getEdgesOut();
				while (it.hasNext()) {
					rel = it.next();
					v = rel.getEnd();
					if (validIn[index.get(v)])
					subgraph.println(
						vArray[i].getAny() + " " +
						rel.getURI() + " " +
						v.getAny() + " ."
					);
				}
				it.close();
			}
			subgraph.close();
		} catch (Exception e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Graphium g = GraphiumLoader.open(args[0]);
		run(g,args[1]);
		g.close();
	}
}
