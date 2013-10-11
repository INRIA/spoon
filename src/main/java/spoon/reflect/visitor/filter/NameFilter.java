package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.Filter;

/** Filters elements by name (for instance to find a method.
 * Example:
 * <pre>
 * CtMethod normalFor = (CtMethod) type.getElements(new NameFilter("normalFor")).get(0);
 * </pre>
 */
public class NameFilter implements Filter<CtNamedElement> {
	private final String name;
	public NameFilter(String name) {
		if (name==null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
	}

	public boolean matches(CtNamedElement element) {
		return name.equals(element.getSimpleName());
	}

	public Class<CtNamedElement> getType() {
		return CtNamedElement.class;
	}	
}