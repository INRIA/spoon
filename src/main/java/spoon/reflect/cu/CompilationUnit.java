package spoon.reflect.cu;

import java.io.File;
import java.util.List;
import java.util.Set;

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
	 */
	File getFile();

	/**
	 * Sets the file that corresponds to this compilation unit.
	 */
	void setFile(File file);

	/**
	 * Gets all the types declared in this compilation unit.
	 */
	List<CtSimpleType<?>> getDeclaredTypes();

	/**
	 * Sets the types declared in this compilation unit.
	 */
	void setDeclaredTypes(List<CtSimpleType<?>> types);

	/**
	 * Searches and returns the main type (the type which has the same name as
	 * the file).
	 */
	CtSimpleType<?> getMainType();

	/**
	 * Add a source code fragment to this compilation unit.
	 */
	void addSourceCodeFragment(SourceCodeFragment fragment);

	/**
	 * Gets the source code fragments for this compilation unit.
	 */
	List<SourceCodeFragment> getSourceCodeFraments();

	/**
	 * Gets the original source code as a string.
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

	/**
	 * Sets this compilation unit auto-import feature on or off (default is on).
	 * Note that setting off auto-import is not recommended as it can lead to
	 * unresolved references in the generated code.
	 * 
	 * @see #getManualImports()
	 * @see #setManualImports(Set)
	 */
	void setAutoImport(boolean autoImport);

	/**
	 * Tells if this compilation unit auto-import feature is on or off (default
	 * is on).
	 * 
	 * @see #getManualImports()
	 * @see #setManualImports(Set)
	 */
	boolean isAutoImport();

	/**
	 * Gets the imports to be forced by this compilation unit when the
	 * auto-import is off. This set can be modified and will impact the imports.
	 * 
	 * @see #isAutoImport()
	 * @see #setAutoImport(boolean)
	 */
	Set<Import> getManualImports();

	/**
	 * Sets the imports to be forced by this compilation unit when the
	 * auto-import is off.
	 * 
	 * @see #isAutoImport()
	 * @see #setAutoImport(boolean)
	 */
	void setManualImports(Set<Import> manualImports);

}
