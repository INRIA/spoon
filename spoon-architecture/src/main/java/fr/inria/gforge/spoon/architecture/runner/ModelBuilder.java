package fr.inria.gforge.spoon.architecture.runner;

import spoon.Launcher;
import spoon.reflect.CtModel;

public class ModelBuilder {

	private CtModel mainModel;
	private CtModel testModel;
	public ModelBuilder(String srcInput, String testInput) {
		mainModel = createModel(srcInput);
		testModel = createModel(testInput);
	}
	private CtModel createModel(String path) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(path);
		return launcher.buildModel();
	}

	/**
	 * @return the mainModel
	 */
	public CtModel getMainModel() {
		return mainModel;
	}
	/**
	 * @return the testModel
	 */
	public CtModel getTestModel() {
		return testModel;
	}
}
