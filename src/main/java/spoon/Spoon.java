package spoon;

import java.util.List;

import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
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
	 * 
	 * @param factory
	 *            the factory this compiler works on
	 */
	public static SpoonCompiler createCompiler(Factory factory) {
		return new JDTCompiler(factory);
	}

	public static SpoonCompiler createCompiler(Factory factory,
			List<SpoonResource> inputSources) {
		SpoonCompiler c = new JDTCompiler(factory);
		c.addInputSources(inputSources);
		return c;
	}

	public static SpoonCompiler createCompiler(Factory factory,
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		SpoonCompiler c = new JDTCompiler(factory);
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory in order to
	 * process and compile Java source code. The compiler's factory can be
	 * accessed with the {@link SpoonCompiler#getFactory()}.
	 */
	public static SpoonCompiler createCompiler() {
		return new JDTCompiler(createFactory());
	}

	public static SpoonCompiler createCompiler(List<SpoonResource> inputSources) {
		SpoonCompiler c = new JDTCompiler(createFactory());
		c.addInputSources(inputSources);
		return c;
	}

	public static SpoonCompiler createCompiler(
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		SpoonCompiler c = new JDTCompiler(createFactory());
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	/**
	 * Creates a default Spoon factory, which holds the Java model (AST)
	 * compiled from the source files and which can be processed by Spoon
	 * processors.
	 */
	public static Factory createFactory() {
		return new Factory(new DefaultCoreFactory(), new StandardEnvironment());
	}

}
