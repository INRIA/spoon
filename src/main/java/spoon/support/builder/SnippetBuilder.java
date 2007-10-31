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
		JDTCompiler.JAVA_COMPLIANCE = factory.getEnvironment()
				.getComplianceLevel();
		boolean srcSuccess;
		factory.getEnvironment().debugMessage(
				"compiling sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		compiler = new JDTCompiler();
		initCompiler();
		srcSuccess = compiler.compileSrc(factory, sources.getAllJavaFiles());
		if (!srcSuccess) {
			for (CategorizedProblem[] cps : compiler.probs) {
				for (int i = 0; i < cps.length; i++) {
					CategorizedProblem problem = cps[i];
					if (problem != null)
						getProblems().add(problem.getMessage());
				}
			}
		}
		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage(
				"compiling templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		return srcSuccess;
	}

}
