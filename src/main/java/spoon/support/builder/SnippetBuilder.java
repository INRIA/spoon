package spoon.support.builder;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

import spoon.reflect.Factory;

public class SnippetBuilder extends SpoonBuildingManager {

	public SnippetBuilder(Factory factory) {
		super(factory);
	}

	@Override
	public boolean build() throws Exception {
		if (factory == null) {
			throw new Exception("Factory not initialized");
		}

		boolean srcSuccess;
		factory.getEnvironment().debugMessage(
				"compiling sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		compiler = new SpoonCompiler();
		compiler.javaCompliance = factory.getEnvironment().getComplianceLevel();
		compiler.setClasspath(factory.getEnvironment().getClasspath());
		initCompiler();
		srcSuccess = compiler.compileSrc(factory, sources.getAllJavaFiles());
		reportProblems();
		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage(
				"compiling templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		return srcSuccess;
	}

	@Override
	protected void report(CategorizedProblem problem) {
		throw new SnippetCompilationError(problem.getMessage() + "at line "
				+ problem.getSourceLineNumber());

	}

}
