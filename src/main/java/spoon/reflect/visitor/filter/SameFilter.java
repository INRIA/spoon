package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/** Finds the element given in parameter, useful for checking if an element is in an ancestor */
public class SameFilter implements Filter<CtElement> {
	private final CtElement argument2;

	public SameFilter(CtElement argument2) {
		this.argument2 = argument2;
	}

	@Override
	public boolean matches(CtElement element) {
		return element == argument2;
	}
}
