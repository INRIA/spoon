package spoon;

import java.io.File;
import java.util.List;

import spoon.compiler.Environment;
import spoon.compiler.SpoonResource;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.support.OutputDestinationHandler;

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

	public FluentLauncher inputResource(SpoonResource resource) {
		launcher.addInputResource(resource);
		return this;
	}

	public FluentLauncher processor(String name) {
		launcher.addProcessor(name);
		return this;
	}

	public <T extends CtElement> FluentLauncher processor(Processor<T> processor) {
		launcher.addProcessor(processor);
		return this;
	}

	public FluentLauncher templateResource(SpoonResource resource) {
		launcher.addTemplateResource(resource);
		return this;
	}

	public String toString() {
		return launcher.toString();
	}

	// needed???
	public SpoonModelBuilder compiler(Factory factory) {
		return launcher.createCompiler(factory);
	}

	// needed???
	public SpoonModelBuilder compiler(Factory factory, List<SpoonResource> inputSources) {
		return launcher.createCompiler(factory, inputSources);
	}

	// needed???
	public SpoonModelBuilder createCompiler(Factory factory, List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		return launcher.createCompiler(factory, inputSources, templateSources);
	}

	// needed?
	public Environment createEnvironment() {
		return launcher.createEnvironment();
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

	public FluentLauncher outputFilter(Filter<CtType<?>> typeFilter) {
		launcher.setOutputFilter(typeFilter);
		return this;
	}

	public FluentLauncher outputFilter(String... qualifedNames) {
		launcher.setOutputFilter(qualifedNames);
		return this;
	}

	public FluentLauncher binaryOutputDirectory(String path) {
		launcher.setBinaryOutputDirectory(path);
		return this;
	}

	public void binaryOutputDirectory(File outputDirectory) {
		launcher.setBinaryOutputDirectory(outputDirectory);
	}

	// here are methods from environment as delegates.
	public FluentLauncher autoImports(boolean autoImports) {
		launcher.getEnvironment().setAutoImports(autoImports);
		return this;
	}

	public FluentLauncher level(String level) {
		launcher.getEnvironment().setLevel(level);
		return this;
	}

	public FluentLauncher shouldCompile(boolean shouldCompile) {
		launcher.getEnvironment().setShouldCompile(shouldCompile);
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

	public FluentLauncher useTabulations(boolean tabulation) {
		launcher.getEnvironment().useTabulations(tabulation);
		return this;
	}

	public FluentLauncher tabulationSize(int size) {
		launcher.getEnvironment().setTabulationSize(size);
		return this;
	}

	public FluentLauncher sourceClassPath(String[] sourceClasspath) {
		launcher.getEnvironment().setSourceClasspath(sourceClasspath);
		return this;
	}

	public FluentLauncher preserveLineNumbers(boolean preserveLineNumbers) {
		launcher.getEnvironment().setPreserveLineNumbers(preserveLineNumbers);
		return this;
	}

	public FluentLauncher noClasspath(boolean option) {
		launcher.getEnvironment().setNoClasspath(option);
		return this;
	}

	public FluentLauncher copyResources(boolean copyResources) {
		launcher.getEnvironment().setCopyResources(copyResources);
		return this;
	}

	public FluentLauncher enableComments(boolean commentEnable) {
		launcher.getEnvironment().setCommentEnabled(commentEnable);
		return this;
	}

	public FluentLauncher outputType(OutputType type) {
		launcher.getEnvironment().setOutputType(type);
		return this;
	}

	public FluentLauncher outputDestinationHandler(OutputDestinationHandler handler) {
		launcher.getEnvironment().setOutputDestinationHandler(handler);
		return this;
	}
}
