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
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;

/**
 * TODO: doc
 */
public class FluentLauncher {

	Launcher launcher;

	public FluentLauncher() {
		this.launcher = new Launcher();
	}

	/**
	 * @param args
	 * @see spoon.Launcher#run(java.lang.String[])
	 */

	public void run(String[] args) {
		launcher.run(args);
	}

	public void printUsage() {
		launcher.printUsage();
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

	// TODO: das sollte in Methoden zum setzen umgewandelt werden
	public Environment getEnvironment() {
		return launcher.getEnvironment();
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
	public SpoonModelBuilder createCompiler() {
		return launcher.createCompiler();
	}

	// needed?
	public SpoonModelBuilder createCompiler(List<SpoonResource> inputSources) {
		return launcher.createCompiler(inputSources);
	}

	// needed?
	public Factory createFactory() {
		return launcher.createFactory();
	}

	// needed???
	public Factory getFactory() {
		return launcher.getFactory();
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

	public void setSourceOutputDirectory(File outputDirectory) {
		launcher.setSourceOutputDirectory(outputDirectory);
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
	// TODO: finish
}
