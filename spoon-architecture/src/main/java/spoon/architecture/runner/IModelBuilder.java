package spoon.architecture.runner;

/**
 * This defines a meta model builder. It saves, builds and manages meta models.
 * The builder has methods for input with the model itself or a string as path. If the path is used, a model is created with default values.
 * <p>
 * An implementation is not forced to handle duplicated identifier or null.
 */
public interface IModelBuilder<T> {
	/**
	 * Adds a new meta model to the builder build by default setting with the path as input.
	 * @param identifier a lowercase name for identification.
	 * @param path to the input sources for model building.
	 */
	void insertInputPath(String identifier, String path);


	/**
		* Adds a new meta model to the builder.
		* @param identifier a lowercase name for identification.
		* @param model  the build model for architecture checks used.
		*/
	void insertInputPath(String identifier, T model);
	/**
		* Performs an lookup with the identifier for a meta model.
		* The identifier should by lower case. An implementation can convert the identifier to lower case as convenience but is not forced to do so.
		* @param identifier used for lookup.
		* @return  the meta model or null if no model with given identifier is found.
		*/
	T getModelWithIdentifier(String identifier);
}
