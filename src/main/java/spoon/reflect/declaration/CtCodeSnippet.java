package spoon.reflect.declaration;


/**
 * This interface represents snippets of source code that can be used in the AST
 * to represent complex code without having to build the corresponding program
 * model structure. It is mainly provided on simplification purpose in order to
 * avoid having to build the program's model. Code snippets should be compiled
 * to validate their contents and the result of the compilation should be used
 * to replace the code snippet in the final AST.
 * 
 * @see CtType#compileAndReplaceSnippets()
 */
public interface CtCodeSnippet {

	/**
	 * Sets the textual value of the code.
	 */
	<C extends CtCodeSnippet> C setValue(String value);

	/**
	 * Gets the textual value of the code.
	 */
	String getValue();

}
