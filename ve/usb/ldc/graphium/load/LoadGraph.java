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
			return;
		}

		// Load Process Init
		LoadNT loadProcess;
		if (args[0].equals("Neo4j"))
			loadProcess = new LoadNeo4j(args[2]);
		else if (args[0].equals("Sparksee"))
			loadProcess = new LoadSparksee(args[2]);
		else {
			System.err.println("The GDBM argument (first one)"
				+" must be \"Sparksee\" or \"Neo4j\".");
			return;
		}

		// Start Load Process
		loadProcess.start(args[1]);
	}
}
