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

public abstract class Edge {

	public Vertex _s = null,
	              _e = null;

	public abstract URI getURI();
	protected abstract Vertex start();
	protected abstract Vertex end();

	public Vertex getStart() {
		if (_s!=null) return _s;
		else return (_s = this.start());
	}
	public Vertex getEnd() {
		if (_e!=null) return _e;
		else return (_e = this.end());
	}
	@Override
	public String toString() {
		return (
			getStart().getAny()
			+ " " + getURI() + " " +
			getEnd().getAny()
		);
	}
}
