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

package ve.usb.ldc.graphium.core;

import java.util.*;
import java.lang.*;
import java.io.*;

public class Literal extends RDFobject {
	public String lang, type;
	public Literal(String b) {
		this.base = b;
	}
	@Override
	public String toString() {
		String temp =  "\"" + base + "\"";
		if      (lang != null) temp += "@"  + lang;
		else if (type != null) temp += "^^" + type;
		return temp;
	}
	@Override
	public boolean equals(Object other) {
		if (other instanceof Literal)
			return this.toString().equals(other.toString());
		return false;
	}
}
