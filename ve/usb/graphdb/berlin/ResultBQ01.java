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

package ve.usb.graphdb.berlin;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.graphdb.core.*;

public class ResultBQ01 implements Comparable<ResultBQ01> {
	public String product;
	public String label;

	public ResultBQ01(
		String _product,
		String _label
	) {
		this.product = _product;
		this.label = _label;
	}

	@Override
	public int compareTo(ResultBQ01 other){
		return this.label.compareTo(other.label);
	}

	public void print() {
		System.out.println(this.product + " " + this.label);
	}
}
