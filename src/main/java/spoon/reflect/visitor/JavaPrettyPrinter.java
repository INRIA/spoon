package spoon.reflect.visitor;

import java.util.List;
import java.util.Map;

import spoon.reflect.declaration.CtSimpleType;

/**
 * This interface defines the Java pretty printers.
 */
public interface JavaPrettyPrinter {

	/**
	 * Java file extension (.java).
	 */
	final String FILE_EXTENSION = ".java";

	/**
	 * Package declaration file name.
	 */
	final String PACKAGE_DECLARATION = "package-info" + FILE_EXTENSION;

	/**
	 * Gets the package declaration contents.
	 */
	String getPackageDeclaration();

	/**
	 * Gets the contents of the compilation unit.
	 */
	StringBuffer getResult();

	/**
	 * Calculates the resulting source file for a list of types.
	 */
	void calculate(List<CtSimpleType<?>> types);

	/**
	 * Gets the line number mapping between the generated code and the original
	 * code.
	 */
	Map<Integer, Integer> getLineNumberMapping();

}