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

public class ResultGenerator {
	private int ind;
	public ResultGenerator() {}
	public ResultGenerator(int _ind) {
		this.ind = _ind;
	}
	public ResultTuple newResult(Comparable ... _elem) {
		return (new TupleGen(_elem));
	}
	public class TupleGen extends ResultTuple {
		public TupleGen(Comparable ... _elem) {
			this.elem = _elem;
		}
		@Override
		public int compareTo(ResultTuple other){
			return this.elem[ind].compareTo(other.elem[ind]);
		}
		public void print() {
			this.print(elem.length);
		}
		public void print(int lim) {
			String tupleStr = elem[0].toString();
			for (int i=1, t=lim ; i<t ; i++)
				tupleStr += " "+elem[i].toString();
			System.out.println(tupleStr);
		}
	}
}
