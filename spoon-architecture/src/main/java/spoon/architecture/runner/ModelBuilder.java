package spoon.architecture.runner;

import java.util.HashMap;
import java.util.Map;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 * This defines the default meta model builder for spoon models (CtModel). For modelbuilding the default launcher settings are used.
 * The default java version is 8, but can be changed by either {@link #ModelBuilder(int)} or usage of {@link #insertInputPath(String, CtModel)}.
 * <p>
 * This builder is <b>not</b> null safe, does <b>not</b> support removal of models and lookups with {@link #getModelWithIdentifier(String)} uses <b>lowercase only</b>.
 * All added identifier are <b>converted</b> to lower case. Adding a duplicated identifier results in loose of the older model. This class is <b>not</b> thread safe.
 */
public class ModelBuilder implements IModelBuilder<CtModel> {

	private Map<String, CtModel> modelByName;
	//default value for non set is -1
	private int javaVersion = -1;
	/**
	 * Creates a modelbuilder with default settings for source version 8.
	 */
	public ModelBuilder() {
		modelByName = new HashMap<>();
	}
	/**
	 * create a modelbuilder with default settings and the given java version. The number argument is <b>not</b> check for correctness, before the building process.
	 * @param javaVersion  used for model building. Must be a positive signed number greater zero.
	 */
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
		return modelByName.get(identifier.toLowerCase());
	}


}
