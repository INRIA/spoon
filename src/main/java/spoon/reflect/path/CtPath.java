package spoon.reflect.path;

import spoon.reflect.declaration.CtElement;

import java.util.Collection;

/**
 * A CtPath allow top define the path to a CtElement in the Spoon Model.
 */
public interface CtPath {

	/**
	 * Search some element matching this CtPatch from given nodes.
	 *
	 * @param startNode
	 * @return
	 */
	<T extends CtElement> Collection<T> evaluateOn(Collection<? extends CtElement> startNode);

}
