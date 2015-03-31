package spoon.reflect.declaration;

/**
 * This interface represents a member of a class (field, method,
 * nested class or static/instance initializer).
 */
public interface CtTypeMember extends CtModifiable {

	/**
	 * Gets the type that declares this class member.
	 * 
	 * @return declaring class
	 */
	CtType<?> getDeclaringType();
	
}
