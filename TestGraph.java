import java.util.*;
import java.lang.*;
import java.io.*;

public class TestGraph {

	public static void main (String[] args) {
		if (args.length != 2) {
			System.err.println("Two arguments needed to TEST: <GDBM (DEX or NEO4J)> <DB location>");
			return;
		}
		if (args[0].equals("NEO4J"))
			(new TestNeo4j()).testGDBM(args[1]);
		else if (args[0].equals("DEX"))
			(new TestDEX()).testGDBM(args[1]);
		else {
			System.err.println("The GDBM argument (first one) must be \"DEX\" or \"NEO4J\".");
			return;
		}
	}
}
