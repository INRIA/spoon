/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import spoon.compiler.SpoonResource;
import spoon.compiler.builder.JDTBuilder;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.support.compiler.jdt.CompilationUnitFilter;
import spoon.support.compiler.jdt.FactoryCompilerConfig;
import spoon.support.compiler.jdt.FileCompilerConfig;
import spoon.support.compiler.jdt.JDTBatchCompiler;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Responsible for building a spoon model from Java source code.
 *
 * <p>
 * The Spoon model (see {@link Factory} is built from input sources given as
 * files. Use {@link #build()} to create the Spoon model.
 * Once the model is built and stored in the factory, it
 * can be processed by using a {@link #instantiateAndProcess(List)}.
 * </p>
 *
 * <p>
 * Create an instance of the default implementation of the Spoon compiler by
 * using {@link spoon.Launcher#createCompiler()}. For example:
 * </p>
 */
public interface SpoonModelBuilder {

	/**
	 * Builds the program's model with this compiler's factory and stores the
	 * result into this factory. Note that this method should only be used once
	 * on a given factory.
	 *
	 * @return true if the Java was successfully compiled with the core Java
	 * compiler, false if some errors were encountered while compiling
	 * @throws spoon.SpoonException
	 * 		when a building problem occurs
	 * @see #getSourceClasspath()
	 * @see #getTemplateClasspath()
	 */
	boolean build();

	/**
	 * Builds the program's model with this compiler's factory and stores the
	 * result into this factory. Note that this method should only be used once
	 * on a given factory.
	 *
	 * @param builder
	 * 		Parameters given at JDT compiler.
	 * @return true if the Java was successfully compiled with the core Java
	 * compiler, false if some errors were encountered while compiling
	 * @throws spoon.SpoonException
	 * 		when a building problem occurs
	 * @see #getSourceClasspath()
	 * @see #getTemplateClasspath()
	 */
	boolean build(JDTBuilder builder);

	/** The types of compilable elements
	 * FILES - compiles the java files from the file system, which were registered by {@link #addInputSource(File)} and {@link #addTemplateSource(File)}
	 * CTTYPES - compiles virtual java files, which are dynamically generated from the all top level classes of the CtModel by {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter}
	 */
	interface InputType {
		InputType FILES = FileCompilerConfig.INSTANCE;
		InputType CTTYPES = FactoryCompilerConfig.INSTANCE;
		/**
		 * responsible for setting the parameters of JDTBatchCompiler, must call setCompilationUnits()
		 */
		void initializeCompiler(JDTBatchCompiler compiler);
	}


	/**
	 * Generates the bytecode associated to the classes stored in this
	 * compiler's factory. The bytecode is generated in the directory given by
	 * {@link #getBinaryOutputDirectory()}.
	 *
	 * The array of types must be of size 0 or 1. If it's empty,
	 * the types of the factory are compiled.
	 * If it's InputType.FILES, the files given as input are compiled.
	 *
	 *Note that the varargs ... enables this version to be backward compatible for callers.
	 *
	 * @see #getSourceClasspath()
	 */
	boolean compile(InputType... types);

	/**
	 * Takes a list of fully qualified name processors and instantiates them to process
	 * the Java model.
	 */
	void instantiateAndProcess(List<String> processors);

	/**
	 * Processes the Java model with the given processors.
	 */
	void process(Collection<Processor<? extends CtElement>> processors);

	/**
	 * Generates the source code associated to the classes stored in this
	 * compiler's factory. The source code is generated in the directory given
	 * by {@link #getSourceOutputDirectory()}.
	 *
	 * @param outputType
	 * 		the output method
	 */
	void generateProcessedSourceFiles(OutputType outputType);

	/**
	 * Generates the source code associated to the classes stored in this
	 * compiler's factory. The source code is generated in the directory given
	 * by {@link #getSourceOutputDirectory()}.
	 *
	 * @param outputType
	 * 		the output method
	 * @param typeFilter
	 * 		Filter on CtType to know which type Spoon must print.
	 */
	void generateProcessedSourceFiles(OutputType outputType, Filter<CtType<?>> typeFilter);

	/**
	 * Adds a file/directory to be built. By default, the files could be Java
	 * source files or Jar files. Directories are processed recursively.
	 *
	 * @param source
	 * 		file or directory to add
	 */
	void addInputSource(File source);

	/**
	 * Adds a file/directory (as a {@link SpoonResource}) to be built. By default, the
	 * files could be Java source files or Jar files. Directories are processed
	 * recursively.
	 *
	 * @param source
	 * 		file or directory to add
	 */
	void addInputSource(SpoonResource source);

	/**
	 * Adds a list of files/directories (as a {@link SpoonResource} to be built.
	 * By default, the files could be Java source files of Java files. Directories
	 * are processed recursively.
	 *
	 * @param resources
	 * 		files or directories to add.
	 */
	void addInputSources(List<SpoonResource> resources);

	/**
	 * Gets all the files/directories given as input sources to this builder
	 * (see {@link #addInputSource(File)}).
	 */
	Set<File> getInputSources();

	/**
	 * Adds a file/directory to be used to build templates. By default, the
	 * files should be Java source files or Jar files containing the sources.
	 * Directories are processed recursively. Templates are set apart from the
	 * program to be processed for logical reasons. However, if a template was
	 * needed to be processed, it could be added as an input source.
	 *
	 * @param source
	 * 		file or directory to add
	 */
	void addTemplateSource(File source);

	/**
	 * Adds a file/directory (as a {@link SpoonResource}) to be used to build templates. By
	 * default, the files should be Java source files or Jar files containing
	 * the sources. Directories are processed recursively. Templates are set
	 * apart from the program to be processed for logical reasons. However, if a
	 * template was needed to be processed, it could be added as an input
	 * source.
	 *
	 * @param source
	 * 		file or directory to add
	 */
	void addTemplateSource(SpoonResource source);

	/**
	 * Adds a list of files/directories (as a CtResource) to be used to build templates. By
	 * default, the files should be Java source files or Jar files containing
	 * the sources. Directories are processed recursively. Templates are set
	 * apart from the program to be processed for logical reasons. However, if a
	 * template was needed to be processed, it could be added as an input
	 * source.
	 *
	 * @param resources
	 * 		files or directories to add.
	 */
	void addTemplateSources(List<SpoonResource> resources);

	/**
	 * Gets all the files/directories given as template sources to this builder
	 * (see {@link #addTemplateSource(File)}).
	 */
	Set<File> getTemplateSources();

	/**
	 * Gets the output directory of this compiler.
	 */
	File getSourceOutputDirectory();

	/**
	 * Sets the output directory for binary generated.
	 *
	 * @param binaryOutputDirectory
	 * 		{@link File} for binary output directory.
	 */
	void setBinaryOutputDirectory(File binaryOutputDirectory);

	/**
	 * Gets the binary output directory of the compiler.
	 */
	File getBinaryOutputDirectory();

	/**
	 * Gets the classpath that is used to build/compile the input sources.
	 */
	String[] getSourceClasspath();

	/**
	 * Sets the classpath that is used to build/compile the input sources.
	 *
	 * Each element of the array is either a jar file or a folder containing bytecode files.
	 */
	void setSourceClasspath(String... classpath);

	/**
	 * Gets the classpath that is used to build the template sources.
	 *
	 * See {@link #setSourceClasspath} for the meaning of the returned string.
	 */
	String[] getTemplateClasspath();

	/**
	 * Sets the classpath that is used to build the template sources.
	 */
	void setTemplateClasspath(String... classpath);

	/**
	 * Returns the working factory
	 */
	Factory getFactory();

	/**
	 * Adds {@code filter}.
	 *
	 * @param filter
	 *  	The {@link CompilationUnitFilter} to add.
	 */
	void addCompilationUnitFilter(CompilationUnitFilter filter);

	/**
	 * Removes {@code filter}. Does nothing, if {@code filter} has not been
	 * added beforehand.
	 *
	 * @param filter
	 *  	The {@link CompilationUnitFilter} to remove.
	 */
	void removeCompilationUnitFilter(CompilationUnitFilter filter);

	/**
	 * Returns a copy of the internal list of {@link CompilationUnitFilter}s.
	 *
	 * @return
	 *  	A copy of the internal list of {@link CompilationUnitFilter}s.
	 */
	List<CompilationUnitFilter> getCompilationUnitFilter();
}
