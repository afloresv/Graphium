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

package ve.usb.graphdb.load;

import java.util.*;
import java.lang.*;
import java.io.*;

public abstract class LoadNT {

	public final String[] propString =
		{"URI","NodeID","Literal","Lang","Type","Predicate"};
	public String objS, lastURI;
	public long lastN;

	public void start (String nt_file) {
		try {
			Scanner sc = new Scanner(new File(nt_file));
			String line,subS,preS;
			long subN = 0, objN = 0;
			
			while (sc.hasNextLine()) {

				line = sc.nextLine();
				Scanner in = new Scanner(line);

				// Subject
				subS = in.next();
				if (subS.charAt(0)=='#')
					continue;
				else if (tryURI(subS) || tryNodeID(subS))
					subN = lastN;
				else System.out.println("NOOOO");

				// Predicate
				preS = in.next();

				// Object
				if (trimObj(in.nextLine())
					&& (tryURI(objS) || tryNodeID(objS) || tryLiteral(objS)))
						objN = lastN;
				else System.out.println("NOOOO");

				// Relationship
				if (tryURI(preS,false)) {
					addRelationship(subN,objN,lastURI);
					//System.out.println(subS+" "+preS+" "+objS);
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe.getMessage());
		} finally {
			close();
		}
	}

	public boolean tryURI (String str) {
		return tryURI(str,true);
	}

	public boolean tryURI (String str, boolean create) {
		int len = str.length();
		if (len<3 || str.charAt(0)!='<' || str.charAt(len-1)!='>')
			return false;
		for (int i=1 ; i<len-1 ; i++)
			if (str.charAt(i)<32 || 126<str.charAt(i))
				return false;
		
		lastURI = str.substring(1,len-1);
		if (!create) return true;

		lastN = addNode(0,str.substring(1,len-1));

		return true;
	}

	public boolean tryNodeID (String str) {
		int len = str.length();
		if (len<3 || str.charAt(0)!='_' || str.charAt(1)!=':')
			return false;
		char cc = str.charAt(2);
		if (!(('A'<=cc && cc<='Z') || ('a'<=cc && cc<='z')))
			return false;
		for (int i=3 ; i<len ; i++) {
			cc = str.charAt(i);
			if (!(('A'<=cc && cc<='Z') || ('a'<=cc && cc<='z') || ('0'<=cc && cc<='9')))
				return false;
		}
		lastN = addNode(1,str.substring(2));
		return true;
	}

	public boolean tryLiteral (String str) {
		
		if (str.charAt(0)!='\"')
			return false;
		int len = str.length(), i;
		for (i=1 ; i<len ; i++) {
			if (str.charAt(i)=='\"' && str.charAt(i-1)!='\\')
				break;
		}
		if (i==len) return false;
		else if (i==len-1) {
			lastN = addNode(2,str.substring(1,i));
		} else if (str.charAt(i+1)=='@'
			&& str.substring(i+2).matches("^[a-z]+(-[a-z0-9]+)*$")) {
			lastN = addNode(2,str.substring(1,i));
			addAttr(lastN,3,str.substring(i+2));
		} else if (i+5<len && str.charAt(i+1)=='^' && str.charAt(i+2)=='^'
			&& tryURI(str.substring(i+3),false)) {
			lastN = addNode(2,str.substring(1,i));
			addAttr(lastN,4,lastURI);
		} else return false;

		return true;
	}

	public boolean trimObj (String str) {
		int l = 0, r = str.length()-1;
		while (r>=0 && (str.charAt(r)==' ' || str.charAt(r)=='\t')) r--;
		if (r<1 || str.charAt(r--)!='.') return false;
		while (r>=0 && (str.charAt(r)==' ' || str.charAt(r)=='\t')) r--;
		while (l<=r && (str.charAt(l)==' ' || str.charAt(l)=='\t')) l++;
		if (l>r) return false;
		objS = str.substring(l,r+1);
		//System.out.println("@"+objS+"@");
		return true;
	}

	abstract long addNode(int indexType, String value);
	abstract void addAttr(long node, int indexType, String value);
	abstract void addRelationship(long src, long dst, String URI);
	abstract void close();
}
