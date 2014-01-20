import java.util.*;
import java.lang.*;
import java.io.*;

import com.sparsity.dex.gdb.*;

public class LoadDEX extends LoadNT {

	private DexConfig cfg;
	private Dex dex;
	private Database db;
	private Session sess;
	private com.sparsity.dex.gdb.Graph g;
	private int[] NodeType = new int[4];
	private int[] AttrType = new int[6];
	private int   EdgeType;
	private String licenceDEX = "46YMV-NFXTZ-GCG8K-QZ8ME";
	private int E;

	public LoadDEX(String pathDB) {
		try {
			cfg = new DexConfig();
			cfg.setLicense(licenceDEX);
			dex = new Dex(cfg);
			(new File(pathDB)).mkdirs();
			db = dex.create(pathDB+"/DexDB.dex", "DexBD");
			sess = db.newSession();
			g = sess.getGraph();
			E = 0;
			sess.begin();

			for (int i=0 ; i<3 ; i++) {
				NodeType[i] = g.newNodeType(propString[i]);
				AttrType[i] = g.newAttribute(NodeType[i], propString[i],
					(i==2 ? DataType.Text : DataType.String),
					(i==2 ? AttributeKind.Basic : AttributeKind.Unique));
			}

			for (int i=3 ; i<5 ; i++)
				AttrType[i] = g.newAttribute(NodeType[2], propString[i],
					DataType.String, AttributeKind.Basic);

			EdgeType = g.newEdgeType(propString[5],true,true);
			AttrType[5] = g.newAttribute(EdgeType, propString[5],
					DataType.String, AttributeKind.Indexed);

		} catch (FileNotFoundException e){
			System.err.println("Error: " + e.getMessage());
		}
	}

	public long addNode(int indexType, String value) {
		long newNode;
		if (E%100000==0) {
			sess.commit();
			sess.begin();
		}
		E++;
		if (indexType==2) {
			newNode = g.newNode(NodeType[2]);
			TextStream valStream = new TextStream(false);
			g.setAttributeText(newNode, AttrType[2],valStream);
			char[] buff = value.toCharArray();
			valStream.write(buff, buff.length);
			valStream.close();
			return newNode;
		}
		newNode = g.findObject(AttrType[indexType],
			(new Value()).setString(value));
		if (newNode == Objects.InvalidOID) {
			newNode = g.newNode(NodeType[indexType]);
			g.setAttribute(newNode, AttrType[indexType],
				(new Value()).setString(value));
		}
		return newNode;
	}

	public void addAttr(long node, int indexType, String value) {
		g.setAttribute(node,AttrType[indexType],(new Value()).setString(value));
	}

	public void addRelationship(long src, long dst, String URI) {
		long edgeID = g.newEdge(EdgeType,src,dst);
		g.setAttribute(edgeID,AttrType[5],(new Value()).setString(URI));
	}

	public void close() {
		sess.commit();
		sess.close();
		db.close();
		dex.close();
	}
}
