package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.Collection;


/**
 * A CtPathElement that define all roles for matching a unique CtElement from a given root.
 *
 * Created by nharrand on 21/02/2018.
 */
public class CtUniqueRolePathElement extends CtRolePathElement {
	public static final String STRING = "@";

	public CtUniqueRolePathElement(CtRole role) {
		super(role);
	}

	@Override
	public String toString() {
		return STRING + getRole().toString() + getParamString();
	}

	public CtElement getFromSet(Set set, String name) throws CtPathException {
		for (Object o: set) {
			if (o instanceof CtNamedElement) {
				if (((CtNamedElement) o).getSimpleName().equals(name)) {
					return (CtElement) o;
				}
			} else if (o instanceof CtReference) {
				if (((CtReference) o).getSimpleName().equals(name)) {
					return (CtElement) o;
				}
			} else {
				throw new CtPathException();
			}
		}
		throw new CtPathException();
	}

	@Override
	public Collection<CtElement> getElements(Collection<CtElement> roots) {
		Collection<CtElement> matchs = new LinkedList<>();
		for (CtElement root : roots) {
			if (root.getValueByRole(getRole()) instanceof List) {
				if (getArguments().containsKey("index")) {
					int index = Integer.parseInt(getArguments().get("index"));
					matchs.add((CtElement) ((List) root.getValueByRole(getRole())).get(index));
				}
			} else if (root.getValueByRole(getRole()) instanceof Set) {
				if (getArguments().containsKey("name")) {
					String name = getArguments().get("name");
					try {
						matchs.add(getFromSet(root.getValueByRole(getRole()), name));
					} catch (Exception e) {
						System.err.println("[ERROR] Element not found for name: " + name);
					}
				}
			} else if (root.getValueByRole(getRole()) instanceof Map) {
				if (getArguments().containsKey("key")) {
					String name = getArguments().get("key");
					matchs.add((CtElement) ((Map) root.getValueByRole(getRole())).get(name));
				}
			} else {
				CtElement el = root.getValueByRole(getRole());
				matchs.add(el);
			}
		}
		return matchs;
	}
}
