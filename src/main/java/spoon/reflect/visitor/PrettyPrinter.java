package spoon.reflect.visitor;

import java.util.List;
import java.util.Map;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtSimpleType;

/**
 * This interface defines the pretty printers.
 */
public interface PrettyPrinter {

	/**
	 * Gets the package declaration contents.
	 */
	String getPackageDeclaration();

	/**
	 * Gets the contents of the compilation unit.
	 */
	StringBuffer getResult();

	/**
	 * Calculates the resulting source file for a list of types. The source
	 * compilation unit is required for calculating the line numbers mapping.
	 */
	void calculate(CompilationUnit sourceCompilationUnit,
			List<CtSimpleType<?>> types);

	/**
	 * Gets the line number mapping between the generated code and the original
	 * code.
	 */
	Map<Integer, Integer> getLineNumberMapping();

}