import java.util.*;
import java.lang.*;
import java.io.*;

import com.sparsity.dex.gdb.*;

public class TestDEX {

	private DexConfig cfg;
	private Dex dex;
	private Database db;
	private Session sess;
	private com.sparsity.dex.gdb.Graph g;
	private int[] NodeType = new int[4];
	private int[] AttrType = new int[6];
	private int   EdgeType;
	private String licenceDEX = "46YMV-NFXTZ-GCG8K-QZ8ME";

	public final String[] propString =
		{"URI","NodeID","Literal","Lang","Type","Predicate"};

	public void testGDBM(String pathDB) {

		try {
			int V=0, E=0;

			cfg = new DexConfig();
			cfg.setLicense(licenceDEX);
			dex = new Dex(cfg);
			db = dex.open(pathDB+"/DexDB.dex", true);
			sess = db.newSession();
			g = sess.getGraph();

			for (int i=0 ; i<3 ; i++) {
				NodeType[i] = g.findType(propString[i]);
				AttrType[i] = g.findAttribute(NodeType[i], propString[i]);
			}
			for (int i=3 ; i<5 ; i++)
				AttrType[i] = g.findAttribute(NodeType[2], propString[i]);

			TypeListIterator itEdge = g.findEdgeTypes().iterator();
			if (!itEdge.hasNext()) {
				System.err.println("Error: No edge type found.");
				sess.close();
				db.close();
				dex.close();
				return;
			}
			EdgeType = itEdge.nextType();
			AttrType[5] = g.findAttribute(EdgeType, propString[5]);

			Value value = new Value();
			for (int i=0 ; i<3 ; i++) {
				Objects objNodes = g.select(NodeType[i]);
				ObjectsIterator it = objNodes.iterator();
				while (it.hasNext()) {
					V++;
					long NodeID = it.next();
					/*if (i==2) {
						TextStream valStream = g.getAttributeText(NodeID, AttrType[i]);
						if (!valStream.isNull()) {
							int read;
							StringBuffer str = new StringBuffer();
							do {
								char[] buff = new char[10];
								read = valStream.read(buff, 10);
								str.append(buff, 0, read);
							}
							while (read > 0);
							System.out.println(propString[i]+" | "+str);
						}
						valStream.close();
					} else {
						g.getAttribute(NodeID, AttrType[i], value);
						System.out.println(propString[i]+" | "+value.getString());
					}*/
				}
				objNodes.close();
				it.close();
			}

			Objects objEdges = g.select(EdgeType);
			ObjectsIterator it = objEdges.iterator();
			while (it.hasNext()) {
				it.next();
				E++;
			}
			objEdges.close();
			it.close();


			sess.close();
			db.close();
			dex.close();

			System.out.println("Nodes: "+V);
			System.out.println("Edges: "+E);

		} catch (FileNotFoundException e){
			System.err.println("Error: " + e.getMessage());
		}

		/*graphDB = new GraphDatabaseFactory().
			newEmbeddedDatabaseBuilder(args[0]).
			setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
			setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true").
			newGraphDatabase();
		globalOP = GlobalGraphOperations.at(graphDB);
		Iterator<Node> nodeIt = globalOP.getAllNodes().iterator();
		nodeIt.next();

		String val;
		Node node;
		int V=0, E=0;

		if (nodeIt.hasNext()) {
			while(nodeIt.hasNext()) {
				node = nodeIt.next();
				V++;
				if (node.hasProperty("URI"))
					val = "URI     | " + (String)node.getProperty("URI");
				else if (node.hasProperty("NodeID"))
					val = "NodeID  | " + (String)node.getProperty("NodeID");
				else if (node.hasProperty("Literal"))
					val = "Literal | " + (String)node.getProperty("Literal");
				else
					val = "NOOOOOO";
				System.out.println(val);
			}
		}

		Iterator<Relationship> relIt = globalOP.getAllRelationships().iterator();
		while(relIt.hasNext()) {
			relIt.next();
			E++;
		}


		graphDB.shutdown();*/
	}
}
