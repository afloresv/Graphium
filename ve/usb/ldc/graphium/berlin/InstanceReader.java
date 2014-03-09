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

public class InstanceReader {
	private int query;
	private int val[];
	private String valQ06;
	public InstanceReader(int size, String gdbm, String graph, int _query, int inst) {
		val = new int[size];
		query = _query;
		try {
			Scanner in = new Scanner(new File("../" + gdbm + "DB/" + graph + "/bsbm.inst"));
			int skip = (query-1) * 20 + inst;
			for (int i=0 ; i<skip ; i++)
				in.nextLine();
			if (query==6)
				valQ06 = in.nextLine().substring(5);
			else {
				in.next();
				for (int i=0 ; i<size ; i++)
					val[i] = in.nextInt();
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: ../" + gdbm + "DB/" + graph + "/bsbm.inst");
			System.exit(1);
		}
	}
	public int get(int i) {
		return val[i];
	}
	public String getStr() {
		return valQ06;
	}
}
