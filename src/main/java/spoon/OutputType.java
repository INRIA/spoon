package spoon;

/**
 * Types of output.
 */
public enum OutputType {
	/** Analysis only, models are not pretty-printed to disk. */
	NO_OUTPUT("noouput"),

	/** One file per top-level class. */
	CLASSES("classes"),

	/** Follows the compilation units given by the input. */
	COMPILATION_UNITS("compilationunits");

	String string;

	private OutputType(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

	/**
	 * Gets the output type from an option string.
	 * 
	 * @param string
	 *            the string, as given in the launcher's options
	 * @return the corresponding output type, null if no match is found
	 * @see Launcher#printUsage()
	 */
	public static OutputType fromString(String string) {
		for (OutputType outputType : OutputType.values()) {
			if (outputType.string.equals(string)) {
				return outputType;
			}
		}
		return null;
	}
}
