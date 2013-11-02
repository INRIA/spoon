package spoon;

import spoon.compiler.SpoonCompiler;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.JDTCompiler;

/**
 * This helper class defines the Spoon API.
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <pre>
 * SpoonCompiler compiler = Spoon.createCompiler();
 * Factory factory = Spoon.createFactory();
 * List&lt;SpoonFile&gt; files = SpoonResourceHelper.files(&quot;myFile.java&quot;);
 * compiler.build(factory, files);
 * ... process and compile
 * </pre>
 */
public abstract class Spoon {

	private Spoon() {
	}

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 */
	public static SpoonCompiler createCompiler() {
		return new JDTCompiler();
	}

	/**
	 * Creates a new Spoon factory, which holds the Java model (AST) compiled
	 * from the source files and which can be processed by Spoon processors.
	 */
	public static Factory createFactory() {
		return new Factory(new DefaultCoreFactory(), new StandardEnvironment());
	}

}
