package saveCacheIssue3404;

import soot.*;
import soot.Body;
import soot.NormalUnitPrinter;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPrinter;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.jimple.internal.*;

public class A {
public static void main(String[] args) {
		String[] sootArgs = {
			"-pp", 	// sets the class path for Soot
			"-w", 						// Whole program analysis, necessary for using Transformer
			"-src-prec", "java",		// Specify type of source file
			"-main-class", "Main",	// Specify the main class 
			"-f", "J", 					// Specify type of output file
			"Main" 
		};
		soot.Main.main(sootArgs);
	}
}