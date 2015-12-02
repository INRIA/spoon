package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.Filter;

/**
 * Filters elements by name (for instance to find a method). Example:
 *
 * <pre>
 * CtMethod&lt;?&gt; normalFor = type.getElements(
 * 		new NameFilter&lt;CtMethod&lt;?&gt;&gt;(&quot;normalFor&quot;)).get(0);
 * </pre>
 */
public class NameFilter<T extends CtNamedElement> implements Filter<T> {
	private final String name;

	public NameFilter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
	}

	public boolean matches(T element) {
		try {
			return name.equals(element.getSimpleName());
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) CtNamedElement.class;
	}
}
