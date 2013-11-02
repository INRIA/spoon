package spoon.support.compiler;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

import spoon.compiler.Environment;
import spoon.reflect.Factory;

public class JDTSnippetCompiler extends JDTCompiler {

	public JDTSnippetCompiler() {
	}

	@Override
	public boolean build(Factory factory) throws Exception {
		if (factory == null) {
			throw new Exception("Factory not initialized");
		}

		boolean srcSuccess;
		factory.getEnvironment().debugMessage(
				"compiling sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();
		setClasspath(factory.getEnvironment().getClasspath());
		initCompiler();
		srcSuccess = build(factory, sources.getAllJavaFiles());
		reportProblems(factory.getEnvironment());
		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage(
				"compiling templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		return srcSuccess;
	}

	@Override
	protected void report(Environment environment, CategorizedProblem problem) {
		throw new SnippetCompilationError(problem.getMessage() + "at line "
				+ problem.getSourceLineNumber());

	}

}
