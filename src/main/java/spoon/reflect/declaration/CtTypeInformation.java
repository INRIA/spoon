package spoon.reflect.declaration;

import java.util.Collection;
import java.util.Set;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

/** Returns information that can be obtained both at compile-time and run-time 
 * 
 * For CtElement, the compile-time information is given
 * 
 * For CtTypeReference, the runtime information is given (using the Reflection API) 
 * 
 */
public interface CtTypeInformation {
	/**
	 * Returns the interface types directly implemented by this class or
	 * extended by this interface.
	 */
	Set<CtTypeReference<?>> getSuperInterfaces();

	/**
	 * Returns the fully qualified name of this type declaration.
	 */
	String getQualifiedName();
	
	/**
	 * Gets modifiers of this type.
	 */
	Set<ModifierKind> getModifiers();

	/**
	 * Return {@code true} if the referenced type is a primitive type (int,
	 * double, boolean...).
	 */
	boolean isPrimitive();

	/**
	 * Return {@code true} if the referenced type is a anonymous type
	 */
	boolean isAnonymous();

	/**
	 * Returns true if this type is an interface.
	 */
	boolean isInterface();

	/**
	 * Returns true if the referenced type is a sub-type of the given type.
	 */
	boolean isSubtypeOf(CtTypeReference<?> type);

	/**
	 * Returns <code>true</code> if this referenced type is assignable from an
	 * instance of the given type.
	 */
	boolean isAssignableFrom(CtTypeReference<?> type);

	/**
	 * Returns the class type directly extended by this class.
	 * 
	 * @return the class type directly extended by this class, or null if there
	 *         is none
	 */
	CtTypeReference<?> getSuperclass();

	/**
	 * Gets the fields declared by this type.
	 */
	Collection<CtFieldReference<?>> getDeclaredFields();

	/**
	 * Gets the fields declared by this type and by all its supertypes if
	 * applicable.
	 */
	Collection<CtFieldReference<?>> getAllFields();
	
	/**
	 * Gets the executables declared by this type if applicable.
	 */
	Collection<CtExecutableReference<?>> getDeclaredExecutables();

	/**
	 * Gets the executables declared by this type and by all its supertypes if
	 * applicable.
	 */
	Collection<CtExecutableReference<?>> getAllExecutables();

}
