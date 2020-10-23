package fr.inria.gforge.spoon.architecture;

import spoon.reflect.declaration.CtElement;
   /**
	 * A constraint defines an rule, an element or the property of an element must obey.
	 * For better usage a constraint must have these properties:
	 * <ul>
	 * <li> <br>Idempotent</br> The constraint mustn't change the elements state.
	 * <li> <br>Order independent</br> The execution order of the constraints is not specified and mustn't relay on. 
	 * </ul>
	 * The generic type parameter describes the element the constraint checks. All elements have {@link CtElement} as upper bound for the type. 
   */
public interface IConstraint<T extends CtElement> {
	/**
	 * Checks a constraint for a given element. The constraint mustn't throw an exception if the condition does not hold.
	 * <p>
	 * @param element  the modelElement that gets checked
	 * @param <T>  the spoon-ast element extending {@link CtElement}  
	 */
	void checkConstraint(T element);
}
