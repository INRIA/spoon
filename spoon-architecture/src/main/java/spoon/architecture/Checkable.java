/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture;

	/**
	 * A checkable defines an rule, that an element or the property of an element must obey.
	 * For better usage a checkable must have these properties:
	 * <ul>
	 * <li> <b>Idempotent</b> The checkable mustn't change the elements state.
	 * <li> <b>Order independent</b> The execution order of the checkable is not specified and mustn't relay on.
	 * </ul>
	 * @param <T>  describes the element type the checkable checks.
   */
@FunctionalInterface
public interface Checkable<T> {
	/**
	 * Checks a rule for a given element. The rule mustn't throw an exception if the condition does not hold.
	 * <p>
	 * @param element  the modelElement that gets checked
	 */
	void checkConstraint(T element);
}
