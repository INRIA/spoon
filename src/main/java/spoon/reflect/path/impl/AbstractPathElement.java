package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Partial implementation for CtPathElement
 */
public abstract class AbstractPathElement<P extends CtElement, T extends CtElement> implements CtPathElement<P, T> {

	private Map<String, String> arguments = new TreeMap<String, String>();

	public Map<String, String> getArguments() {
		return arguments;
	}

	@Override
	public <C extends CtPathElement<P, T>> C addArgument(String key, String value) {
		arguments.put(key, value);
		return (C) this;
	}

	Collection<CtElement> getChilds(CtElement element) {
		final Collection<CtElement> elements = new ArrayList<CtElement>();
		if (element != null) {
			element.accept(new CtScanner() {
				@Override
				public void scan(CtElement element) {
					elements.add(element);
				}
			});
		}
		return elements;
	}

	protected String getParamString() {
		if (arguments.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder("[");

		for (Iterator<Map.Entry<String, String>> iter = arguments.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> value = iter.next();
			builder.append(value.getKey() + "=" + value.getValue());
			if (iter.hasNext()) {
				builder.append(";");
			}
		}

		return builder.append("]").toString();
	}
}
