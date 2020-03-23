/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import java.io.File;
import java.nio.charset.Charset;

import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;

/**
 * FluentLauncher provides a different, fluent interface for the launcher class.
 * Setting options is done via calls like {@link #noClasspath(boolean)}. This
 * class exists for giving a user a simpler fluent api, which is useable for
 * most use cases, but not all.
 */
public class FluentLauncher {
	/**
	 * wrapped launcher object used for delegating calls.
	 */
	private final SpoonAPI launcher;

	/**
	 * Creates a new FluentLauncher, wrapping default {@link Launcher}. After
	 * setting options, call {@link #buildModel()} for creating the CtModel with
	 * given settings.
	 *
	 */
	public FluentLauncher() {
		this.launcher = new Launcher();
	}

	/**
	 * Creates a new FluentLauncher, wrapping the given launcher. This constructor
	 * allows using different launchers eg. {@link MavenLauncher} with fluent api.
	 * After setting options, call {@link #buildModel()} for creating the CtModel
	 * with given settings.
	 *
	 * @param launcher used for delegating methods.
	 */
	public FluentLauncher(SpoonAPI launcher) {
		this.launcher = launcher;
	}

	/**
	 * Adds an input resource to be processed by Spoon (either a file or a folder).
	 */
	public FluentLauncher inputResource(String path) {
		launcher.addInputResource(path);
		return this;
	}

	/**
	 * Adds an input resource to be processed by Spoon (either a file or a folder).
	 */
	public FluentLauncher inputResource(Iterable<String> paths) {
		for (String path : paths) {
			launcher.addInputResource(path);
		}
		return this;
	}

	/**
	 * Adds an instance of a processor. The user is responsible for keeping a
	 * pointer to it for later retrieving some processing information.
	 */
	public <T extends CtElement> FluentLauncher processor(Processor<T> processor) {
		launcher.addProcessor(processor);
		return this;
	}

	/**
	 * Adds an instance of a processor. The user is responsible for keeping a
	 * pointer to it for later retrieving some processing information.
	 */
	public <T extends CtElement> FluentLauncher processor(Iterable<Processor<T>> processors) {
		for (Processor<T> processor : processors) {
			launcher.addProcessor(processor);
		}
		return this;
	}

	/**
	 * Builds the model
	 */

	public CtModel buildModel() {
		launcher.run();
		return launcher.getModel();
	}

	/**
	 * Sets the output directory for source generated.
	 *
	 * @param path Path for the output directory.
	 */
	public FluentLauncher outputDirectory(String path) {
		launcher.setSourceOutputDirectory(path);
		return this;
	}

	/**
	 * Sets the output directory for source generated.
	 *
	 * @param outputDirectory {@link File} for output directory.
	 */
	public FluentLauncher outputDirectory(File outputDirectory) {
		launcher.setSourceOutputDirectory(outputDirectory);
		return this;
	}

	/**
	 * Tell to the Java printer to automatically generate imports and use simple
	 * names instead of fully-qualified name.
	 *
	 * @param autoImports toggles autoImports on or off.
	 * @return the launcher after setting the option.
	 */
	public FluentLauncher autoImports(boolean autoImports) {
		launcher.getEnvironment().setAutoImports(autoImports);
		return this;
	}

	/**
	 * Disable all consistency checks on the AST. Dangerous! The only valid usage of
	 * this is to keep full backward-compatibility.
	 *
	 * @return the launcher after setting the option.
	 */
	public FluentLauncher disableConsistencyChecks() {
		launcher.getEnvironment().disableConsistencyChecks();
		return this;
	}

	/**
	 * Sets the Java version compliance level.
	 *
	 * @param level of java version
	 * @return the launcher after setting the option.
	 */
	public FluentLauncher complianceLevel(int level) {
		launcher.getEnvironment().setComplianceLevel(level);
		return this;
	}

	/**
	 * Sets the source class path of the Spoon model. Only .jar files or directories
	 * with *.class files are accepted. The *.jar or *.java files contained in given
	 * directories are ignored.
	 *
	 * @throws InvalidClassPathException if a given classpath does not exists or
	 *                                   does not have the right format (.jar file
	 *                                   or directory)
	 * @param sourceClasspath path to sources.
	 * @return the launcher after setting the option.
	 */
	public FluentLauncher sourceClassPath(String[] sourceClasspath) {
		launcher.getEnvironment().setSourceClasspath(sourceClasspath);
		return this;
	}

	/**
	 * Sets the option "noclasspath", use with caution (see explanation below).
	 *
	 * With this option, Spoon does not require the full classpath to build the
	 * model. In this case, all references to classes that are not in the classpath
	 * are handled with the reference mechanism. The "simplename" of the reference
	 * object refers to the unbound identifier.
	 *
	 * This option facilitates the use of Spoon when is is hard to have the complete
	 * and correct classpath, for example for mining software repositories.
	 *
	 * For writing analyses, this option works well if you don't cross the reference
	 * by a call to getDeclaration() (if you really want to do so, then check for
	 * nullness of the result before).
	 *
	 * In normal mode, compilation errors are signaled as exception, with this
	 * option enabled they are signaled as message only. The reason is that in most
	 * cases, there are necessarily errors related to the missing classpath
	 * elements.
	 *
	 * @return the launcher after setting the option.
	 */
	public FluentLauncher noClasspath(boolean option) {
		launcher.getEnvironment().setNoClasspath(option);
		return this;
	}

	/**
	 * Set the encoding to use for parsing source code
	 *
	 * @param encoding used for parsing.
	 * @return the launcher after setting the option.
	 */
	public FluentLauncher encoding(Charset encoding) {
		launcher.getEnvironment().setEncoding(encoding);
		return this;
	}
}
