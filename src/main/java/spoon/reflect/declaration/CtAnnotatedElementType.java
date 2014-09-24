package spoon.reflect.declaration;

/**
 * This enum specifies the element type which is annotated by the annotation
 */
public enum CtAnnotatedElementType
{
	/**
	 * Class, interface (including annotation type), or enum declaration
	 */
	TYPE,

	/**
	 * Field declaration (includes enum constants)
	 */
	FIELD,

	/**
	 * Method declaration
	 */
	METHOD,

	/**
	 * Parameter declaration
	 */
	PARAMETER,

	/**
	 * Constructor declaration
	 */
	CONSTRUCTOR,

	/**
	 * Local variable declaration
	 */
	LOCAL_VARIABLE,

	/**
	 * Annotation type declaration
	 */
	ANNOTATION_TYPE,

	/**
	 * Package declaration
	 */
	PACKAGE,

	/**
	 * Type parameter declaration
	 * TODO exists in java 1.8
	 */
	TYPE_PARAMETER,

	/**
	 * Use of a type
	 * TODO exists in java 1.8
	 */
	TYPE_USE
}
