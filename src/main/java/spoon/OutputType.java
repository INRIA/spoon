package spoon;

/**
 * Types of output
 */
public enum OutputType {
	/** Analysis only, models are not pretty-printed to disk. */
	NO_OUTPUT,

	/** One file per top-level class. */
	CLASSES,

	/** Follows the compilation units given by the input. */
	COMPILATION_UNITS
}
