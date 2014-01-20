import java.util.*;
import java.lang.*;
import java.io.*;

public class LoadGraph {

	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println("Tree arguments needed to CREATE: <GDBM (DEX or NEO4J)> <.nt file> <DB location>");
			return;
		}
		LoadNT loadProcess;
		if (args[0].equals("NEO4J"))
			loadProcess = new LoadNeo4j(args[2]);
		else if (args[0].equals("DEX"))
			loadProcess = new LoadDEX(args[2]);
		else {
			System.err.println("The GDBM argument (first one) must be \"DEX\" or \"NEO4J\".");
			return;
		}
		loadProcess.start(args[1]);
	}
}
