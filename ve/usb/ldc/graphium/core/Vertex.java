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

public abstract class Vertex {

	public abstract boolean isURI();
	public abstract boolean isNodeID();
	public abstract boolean isLiteral();
	public abstract String  getURI();
	public abstract String  getNodeID();
	public abstract String  getLiteral();
	public abstract String  getLang();
	public abstract String  getType();
	public abstract Boolean getBoolean();
	public abstract Long    getLong();
	public abstract Double  getDouble();
	public abstract Date    getDate();
	public String getAny() {
		String res = this.getURI();
		if (res==null) res = this.getNodeID();
		if (res==null) res = this.getLiteral();
		return res;
	}
	public abstract IteratorGraph getEdgesOut();
	public abstract IteratorGraph getEdgesIn();

}
