package spoon.reflect.code;

/**
 * This interface represents snippets of source code that can be used in the AST
 * to represent complex code without having to build the corresponding program
 * model structure. It is mainly provided on simplification purpose in order to
 * avoid having to build the program's model. However, it should be avoided
 * since it is not possible to validate programs containing code snippets.
 */
public interface CtCodeSnippet {

	/**
	 * Sets the textual value of the code.
	 */
	void setValue(String value);

	/**
	 * Gets the textual value of the code.
	 */
	String getValue();

}
