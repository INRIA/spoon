package spoon.reflect.cu;

import java.io.File;
import java.util.List;

import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtSimpleType;

/**
 * Defines a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public interface CompilationUnit extends FactoryAccessor {

	/**
	 * Gets the file that corresponds to this compilation unit if any (contains
	 * the source code).
	 *
	 * @return the source file
	 */
	File getFile();

	/**
	 * Sets the file that corresponds to this compilation unit.
	 *
	 * @param file the source file to set
	 */
	void setFile(File file);

	/**
	 * Gets all the types declared in this compilation unit.
	 *
	 * @return the List of declared types
	 */
	List<CtSimpleType<?>> getDeclaredTypes();

	/**
	 * Sets the types declared in this compilation unit.
	 *
	 * @param types the List if declared types to set
	 */
	void setDeclaredTypes(List<CtSimpleType<?>> types);

	/**
	 * Searches and returns the main type (the type which has the same name as
	 * the file).
	 *
	 * @return the main type
	 */
	CtSimpleType<?> getMainType();

	/**
	 * Add a source code fragment to this compilation unit.
	 *
	 * @param fragment the source code fragment to add
	 */
	void addSourceCodeFragment(SourceCodeFragment fragment);

	/**
	 * Gets the source code fragments for this compilation unit.
	 *
	 * @return the List of source code fragments
	 */
	List<SourceCodeFragment> getSourceCodeFraments();

	/**
	 * Gets the original source code as a string.
	 *
	 * @return the original source code as a string
	 */
	String getOriginalSourceCode();

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * given index.
	 * 
	 * @param index
	 *            an arbitrary index in the source code
	 * @return the index where the line starts
	 */
	int beginOfLineIndex(int index);

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * next line of the given index.
	 * 
	 * @param index
	 *            an arbitrary index in the source code
	 * @return the index where the next line starts
	 */
	int nextLineIndex(int index);

	/**
	 * Gets the number of tabulations for a given line.
	 * 
	 * @param index
	 *            the index where the line starts in the source code
	 * @return the number of tabs for this line
	 */
	int getTabCount(int index);


}
