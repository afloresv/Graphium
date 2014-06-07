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

package ve.usb.ldc.graphium.load;

import java.util.*;
import java.lang.*;
import java.io.*;

public class LoadGraph {

	public static void main (String[] args) {

		// Checking arguments
		if (args.length != 3) {
			System.err.println("Tree arguments needed to CREATE:"
				+" <GDBM (Sparksee or Neo4j)> <.nt file> <DB location>");
			System.exit(1);
		}

		String path = args[2];
		if (path.charAt(path.length()-1) != '/')
			path += "/";

		// Load Process Init
		int V=0, E=0;
		if (args[0].equals("Neo4j")) {
			LoadNeo4j load = new LoadNeo4j(path);
			load.start(args[1]);
			V = load.V;
			E = load.E;
		} else if (args[0].equals("Sparksee")) {
			LoadSparksee load = new LoadSparksee(path);
			load.start(args[1]);
			V = load.V;
			E = load.E;
		} else {
			System.err.println("The GDBM argument (first one)"
				+" must be \"Sparksee\" or \"Neo4j\".");
			System.exit(1);
		}

		// Graphium information
		try {
			PrintWriter gInfo;
			gInfo = new PrintWriter(new FileWriter(path+"graphium.info"));
			gInfo.println(args[0]);
			gInfo.println(V + " " + E);
			gInfo.close();
		} catch (Exception e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
			System.exit(1);
		}
	}
}
