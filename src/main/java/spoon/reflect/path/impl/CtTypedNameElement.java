package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * spoon.reflect.path.impl.CtPathElement that match on CtNamedElement
 */
public class CtTypedNameElement<P extends CtElement, T extends CtElement> extends AbstractPathElement<P, T> {
	public static final String STRING = "/";
	private final Class<T> type;

	public CtTypedNameElement(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public String toString() {
		return STRING + type.getSimpleName()+ getParamString();
	}

	@Override
	public Collection<T> getElements(Collection<P> roots) {
		Collection<T> elements = new ArrayList<T>();
		for (CtElement root : roots) {
			for (CtElement child : getChilds(root)) {
				if (match(child)) {
					elements.add((T) child);
				}
			}
		}
		return elements;
	}

	private boolean match(CtElement element) {
		return element != null &&  type.isAssignableFrom(element.getClass());
	}
}
