package spoon.test.prettyprinter.testclasses;

import spoon.experimental.modelobs.action.Action;
import spoon.reflect.path.CtRole;

public class SniperTemplate {

	public void m(Action action) {
		if (action.getContext().getChangedProperty() == CtRole.NAME) {

		}
	}
}
