package spoon.delegate;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;

public interface ExecutableDelegate<R> {
	/**
	 * Gets the body expression.
	 */
	<B extends R> CtBlock<B> getBody();

	/**
	 * Sets the body expression.
	 */
	<B extends R> void setBody(CtBlock<B> body);

	/**
	 * Gets the parameters list.
	 */
	List<CtParameter<?>> getParameters();

	/**
	 * Sets the parameters.
	 */
	void setParameters(List<CtParameter<?>> parameters);

	/**
	 * Add a parameter for this executable
	 *
	 * @param parameter
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addParameter(CtParameter<?> parameter);

	/**
	 * Remove a parameter for this executable
	 *
	 * @param parameter
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeParameter(CtParameter<?> parameter);

	/**
	 * Returns the exceptions and other throwables listed in this method or
	 * constructor's <tt>throws</tt> clause.
	 */
	Set<CtTypeReference<? extends Throwable>> getThrownTypes();

	/**
	 * Sets the thrown types.
	 */
	void setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	/**
	 * add a thrown type.
	 *
	 * @param throwType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addThrownType(CtTypeReference<? extends Throwable> throwType);

	/**
	 * remove a thrown type.
	 *
	 * @param throwType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeThrownType(CtTypeReference<? extends Throwable> throwType);
}
