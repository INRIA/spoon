package spoon;

import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;

import java.io.File;

/**
 * Is the core entry point of Spoon. Implemented by Launcher.
 */
public interface SpoonAPI {

	/**
	 * Runs Spoon with these arguments (used by the "main" method)
	 */
	void run(String[] args);

	/**
	 * Adds an input resource to be processed by Spoon (either a file or a folder).
	 */
	void addInputResource(String file);

	/**
	 * Sets the output directory for source generated.
	 *
	 * @param path
	 * 		Path for the output directory.
	 */
	void setSourceOutputDirectory(String path);

	/**
	 * Sets the output directory for source generated.
	 *
	 * @param outputDirectory
	 * 		{@link File} for output directory.
	 */
	void setSourceOutputDirectory(File outputDirectory);

	/**
	 * Applies a filter when the output-type is defined on classes.
	 *
	 * @param typeFilter
	 * 		Filter on CtType to know which type Spoon must print.
	 */
	void setFilteringOutputSource(Filter<CtType<?>> typeFilter);

	/**
	 * Creates a filter from names in argument when the output-type is defined on classes.
	 *
	 * @param names
	 * 		Names of class in fully qualified name to know which type Spoon must print.
	 */
	void setFilteringOutputSource(String... names);

	/**
	 * Sets the output directory for binary generated.
	 *
	 * @param path
	 * 		Path for the binary output directory.
	 */
	void setBinaryOutputDirectory(String path);

	/**
	 * Sets the output directory for binary generated.
	 *
	 * @param outputDirectory
	 * 		{@link File} for the binary output directory.
	 */
	void setBinaryOutputDirectory(File outputDirectory);

	/**
	 * Adds a processor (fully qualified name).
	 */
	void addProcessor(String name);

	/**
	 * Adds an instance of a processor. The user is responsible for keeping a pointer to it for
	 * later retrieving some processing information.
	 */
	<T extends CtElement> void addProcessor(Processor<T> processor);

	/**
	 * Builds the model
	 */
	void buildModel();

	/**
	 * Processes the model with the processors given previously with {@link #addProcessor(String)}
	 */
	void process();

	/**
	 * Write the transformed files to disk
	 */
	void prettyprint();

	/**
	 * Starts the complete Spoon processing (build model, process, write transformed files)
	 */
	void run();

	/**
	 * Returns the current factory
	 */
	Factory getFactory();

	/**
	 * Returns the current environment. This environment is modifiable.
	 */
	Environment getEnvironment();

	/**
	 * Creates a new Spoon factory (may be overridden)
	 */
	Factory createFactory();

	/**
	 * Creates a new Spoon environment (may be overridden)
	 */
	Environment createEnvironment();

	/**
	 * Creates a new Spoon compiler (for building the model)
	 */
	SpoonCompiler createCompiler();
}
