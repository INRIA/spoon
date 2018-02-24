package spoon.reflect.path;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.reflect.reference.CtReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This builder allow to create some CtPath from CtElements
 *
 * Created by nharrand on 21/02/2018.
 */
public class CtElementPathBuilder {
	/**
	 * Build path to a CtElement el, from one of its parent.
	 *
	 * @throws CtPathException is thrown when root is not a parent of el.
	 *
	 * @param el : the element to which the CtPath leads to
	 * @param root : Starting point of the CtPath
	 * @return CtPath from root to el
	 */
	public CtPath fromElement(CtElement el, CtElement root) throws CtPathException {
		CtPathImpl path = new CtPathImpl();
		CtElement cur = el;
		while (cur != root) {
			CtElement parent = cur.getParent();
			CtRole role = cur.getRoleInParent();
			if (parent == null || role == null) {
				throw new CtPathException();
			}
			CtPathElement pathElement = new CtRolePathElement(role);
			if (parent.getValueByRole(role) instanceof List) {
				//Element needs to be differentiated from its brothers
				List list = parent.getValueByRole(role);
				//Assumes that List's order is deterministic.
				int index = 0;
				for (Object o : list) {
					if (o == cur) {
						break;
					}
					index++;
				}
				pathElement.addArgument("index", index + "");
			} else if (parent.getValueByRole(role) instanceof Set) {
				if (!(cur instanceof CtNamedElement) && !(cur instanceof CtReference)) {
					throw new CtPathException();
				}
				//Element needs to be differentiated from its brothers
				Set set = parent.getValueByRole(role);
				String name = null;
				for (Object o : set) {
					if (o == cur) {
						if (cur instanceof CtNamedElement) {
							name = ((CtNamedElement) cur).getSimpleName();
						} else {
							name = ((CtReference) cur).getSimpleName();
						}
						break;
					}
				}
				if (name == null) {
					throw new CtPathException();
				} else {
					pathElement.addArgument("name", name);
				}

			} else if (parent.getValueByRole(role) instanceof Map) {
				Map map = parent.getValueByRole(role);
				String key = null;
				for (Object o : map.keySet()) {
					if (map.get(o) == cur) {
						key = (String) o;
						break;
					}
				}
				if (key == null) {
					throw new CtPathException();
				} else {
					pathElement.addArgument("key", key);
				}
			}
			cur = parent;
			path.addFirst(pathElement);
		}
		return path;
	}
}
