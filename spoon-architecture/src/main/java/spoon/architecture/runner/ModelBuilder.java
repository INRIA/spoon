package spoon.architecture.runner;

import java.util.HashMap;
import java.util.Map;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class ModelBuilder implements IModelBuilder<CtModel> {

	private Map<String, CtModel> modelByName;
	//default value for non set is -1
	private int javaVersion = -1;
	public ModelBuilder() {
		modelByName = new HashMap<>();
	}

	public ModelBuilder(int javaVersion) {
		modelByName = new HashMap<>();
		this.javaVersion = javaVersion;
	}

	@Override
	public void insertInputPath(String name, String path) {
		Launcher launcher = new Launcher();
		launcher.addInputResource(path);
		if (javaVersion != -1) {
		launcher.getEnvironment().setComplianceLevel(javaVersion);
		}
		CtModel model = launcher.buildModel();

		modelByName.put(name.toLowerCase(), model);
	}

	@Override
	public void insertInputPath(String name, CtModel model) {
		modelByName.put(name.toLowerCase(), model);
	}

	@Override
	public CtModel getModelWithIdentifier(String identifier) {
		return modelByName.get(identifier);
	}


}
