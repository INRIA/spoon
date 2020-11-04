package spoon.architecture.helper;

import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.CtModel;

public class Models {

	private Models() {

	}
	public static CtModel createModelFromString(String path) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.addInputResource(path);
		return launcher.buildModel();
	}
}
