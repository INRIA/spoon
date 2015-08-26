package spoon;

import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

/** Is the core entry point of Spoon. Implemented by Launcher. */
public interface SpoonAPI {

	/** Runs Spoon with these arguments (used by the "main" method) */
	public void run(String[] args);
	
	/**
	 * Adds an input resource to be processed by Spoon (either a file or a folder).
	 */
	public void addInputResource(String file);

	/** Sets the output directory */
	public void setOutputDirectory(String string);

	/**
	 * Adds a processor (fully qualified name).
	 */
	public void addProcessor(String name);

	/**
	 * Adds an instance of a processor. The user is responsible for keeping a pointer to it for
	 * later retrieving some processing information.
	 */
	<T extends CtElement> void addProcessor(Processor<T> processor);

	/**
	 * Builds the model
	 */
	public void buildModel();
	
	/**
	 * Processes the model with the processors given previously with {@link #addProcessor(String)}
	 */
	public void process();
	
	/**
	 * Write the transformed files to disk
	 */
	public void prettyprint();
	
	/**
	 * Starts the complete Spoon processing (build model, process, write transformed files) 
	 */
	public void run();
		
	/** Returns the current factory */
	public Factory getFactory();
	
	/** Returns the current environment. This environment is modifiable. */
	public Environment getEnvironment();

	/** Creates a new Spoon factory (may be overridden) */
	public Factory createFactory();

	/** Creates a new Spoon environment (may be overridden) */
	public Environment createEnvironment();

	/** Creates a new Spoon compiler (for building the model) */
	SpoonCompiler createCompiler();

}