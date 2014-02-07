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

package ve.usb.ldc.graphium.berlin.general;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.ldc.graphium.core.*;

public class ResultTuple implements Comparable<ResultTuple> {
	public String[] elem;
	private int ind;

	public ResultTuple(String ... _elem) {
		this(0,_elem);
	}

	public ResultTuple(int _ind, String ... _elem) {
		this.elem = _elem;
		this.ind = _ind;
	}

	@Override
	public int compareTo(ResultTuple other){
		return this.elem[ind].compareTo(other.elem[ind]);
	}

	public void print() {
		String tupleStr = elem[0];
		for (int i=1, t=elem.length ; i<t ; i++)
			tupleStr += "\t"+elem[i];
		System.out.println(tupleStr);
	}
}
