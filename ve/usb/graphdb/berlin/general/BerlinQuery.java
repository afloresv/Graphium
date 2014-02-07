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

package ve.usb.graphdb.berlin.general;

import java.util.*;
import java.lang.*;
import java.io.*;

import ve.usb.graphdb.core.*;

public interface BerlinQuery {
	
	// PREFIX
	public String bsbminst = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/";
	public String bsbm = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/";
	public String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public String dc = "http://purl.org/dc/elements/1.1/";
	
	void runQuery(int ind);
}