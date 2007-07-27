package spoon.reflect.cu;

import spoon.reflect.reference.CtReference;

/**
 * This interface represents imports in a compilation unit. Imports are not part
 * of the AST and are generated automatically. However, when the auto-import
 * feature of a compilation unit is turned of, the programmer can manually
 * specify the imports to be done.
 */
public interface Import {
	
	/**
	 * Gets the Java string declaration of the import.
	 */
	String toString();

	/**
	 * Gets the reference of the element that is imported.
	 */
	CtReference getReference();

}
