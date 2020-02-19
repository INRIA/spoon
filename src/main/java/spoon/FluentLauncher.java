package spoon;

import java.io.File;

import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import spoon.support.OutputDestinationHandler;

/**
 * TODO: doc
 */
public class FluentLauncher {

	private SpoonAPI launcher;

	/**
	 * i gave Methods numbers 1-4. 4 means mandatory. 3 means all users will use
	 * this. 2 means helper method but not needed. 1 means no clue why we want this.
	 */
	// Level: 4

	public FluentLauncher() {
		this.launcher = new Launcher();
	}

	// Level: 4
	public FluentLauncher inputResource(String path) {
		launcher.addInputResource(path);
		return this;
	}

	// Level: 3

	public FluentLauncher processor(String name) {
		launcher.addProcessor(name);
		return this;
	}

	// Level: 4

	public <T extends CtElement> FluentLauncher processor(Processor<T> processor) {
		launcher.addProcessor(processor);
		return this;
	}

	// Level: 1

	public String toString() {
		return launcher.toString();
	}

	// Level: 4

	public CtModel buildModel() {
		launcher.run();
		return launcher.getModel();
	}

	// Level: 3

	public FluentLauncher outputDirectory(String path) {
		launcher.setSourceOutputDirectory(path);
		return this;
	}

	// Level: 4

	public FluentLauncher outputDirectory(File outputDirectory) {
		launcher.setSourceOutputDirectory(outputDirectory);
		return this;
	}

	// Level: 2

	public FluentLauncher outputFilter(Filter<CtType<?>> typeFilter) {
		launcher.setOutputFilter(typeFilter);
		return this;
	}
	// Level: 2

	public FluentLauncher outputFilter(String... qualifedNames) {
		launcher.setOutputFilter(qualifedNames);
		return this;
	}

	// Level: 1

	public FluentLauncher binaryOutputDirectory(String path) {
		launcher.setBinaryOutputDirectory(path);
		return this;
	}

	// Level: 1
	public FluentLauncher binaryOutputDirectory(File outputDirectory) {
		launcher.setBinaryOutputDirectory(outputDirectory);
		return this;
	}

	// here are methods from environment as delegates.
	// Level: 3
	public FluentLauncher autoImports(boolean autoImports) {
		launcher.getEnvironment().setAutoImports(autoImports);
		return this;
	}

	// Level: 2
	public FluentLauncher level(String level) {
		launcher.getEnvironment().setLevel(level);
		return this;
	}

	// Level: 1
	public FluentLauncher shouldCompile(boolean shouldCompile) {
		launcher.getEnvironment().setShouldCompile(shouldCompile);
		return this;
	}

	// Level: 2
	public FluentLauncher disableConsistencyChecks() {
		launcher.getEnvironment().disableConsistencyChecks();
		return this;
	}

	// Level: 2
	public FluentLauncher complianceLevel(int level) {
		launcher.getEnvironment().setComplianceLevel(level);
		return this;
	}

	// Level: 2
	public FluentLauncher useTabulations(boolean tabulation) {
		launcher.getEnvironment().useTabulations(tabulation);
		return this;
	}

	// Level: 2
	public FluentLauncher tabulationSize(int size) {
		launcher.getEnvironment().setTabulationSize(size);
		return this;
	}

	// Level: 3
	public FluentLauncher sourceClassPath(String[] sourceClasspath) {
		launcher.getEnvironment().setSourceClasspath(sourceClasspath);
		return this;
	}

	// Level: 3
	public FluentLauncher preserveLineNumbers(boolean preserveLineNumbers) {
		launcher.getEnvironment().setPreserveLineNumbers(preserveLineNumbers);
		return this;
	}

	// Level: 2
	public FluentLauncher noClasspath(boolean option) {
		launcher.getEnvironment().setNoClasspath(option);
		return this;
	}

}
