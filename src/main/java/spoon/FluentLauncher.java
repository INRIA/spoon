/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import java.io.File;

import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;

/**
 * TODO: doc
 */
public class FluentLauncher {

	private Launcher launcher;

	public FluentLauncher() {
		this.launcher = new Launcher();
	}

	public FluentLauncher inputResource(String path) {
		launcher.addInputResource(path);
		return this;
	}

	public FluentLauncher inputResource(Iterable<String> paths) {
		for (String path : paths) {
			launcher.addInputResource(path);
		}
		return this;
	}

	public <T extends CtElement> FluentLauncher processor(Processor<T> processor) {
		launcher.addProcessor(processor);
		return this;
	}

	public CtModel buildModel() {
		launcher.run();
		return launcher.getModel();
	}

	public FluentLauncher outputDirectory(String path) {
		launcher.setSourceOutputDirectory(path);
		return this;
	}

	public FluentLauncher outputDirectory(File outputDirectory) {
		launcher.setSourceOutputDirectory(outputDirectory);
		return this;
	}

	// here are methods from environment as delegates.
	public FluentLauncher autoImports(boolean autoImports) {
		launcher.getEnvironment().setAutoImports(autoImports);
		return this;
	}

	public FluentLauncher disableConsistencyChecks() {
		launcher.getEnvironment().disableConsistencyChecks();
		return this;
	}

	public FluentLauncher complianceLevel(int level) {
		launcher.getEnvironment().setComplianceLevel(level);
		return this;
	}

	public FluentLauncher sourceClassPath(String[] sourceClasspath) {
		launcher.getEnvironment().setSourceClasspath(sourceClasspath);
		return this;
	}

	public FluentLauncher noClasspath(boolean option) {
		launcher.getEnvironment().setNoClasspath(option);
		return this;
	}

}
