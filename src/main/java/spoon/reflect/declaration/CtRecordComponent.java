package spoon.reflect.declaration;

public interface CtRecordComponent<T> extends CtTypedElement<T>, CtNamedElement {

	/**
	 * Converts the component to an implicit method.
	 * @return the method
	 */
	CtMethod<?> toMethod();

	/**
	 * Converts the component to an implicit field.
	 * @return  the field
	 */
	CtField<?> toField();
}
