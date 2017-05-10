package spoon.test.ctTypedElement.testclass;

import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

public class CtTypedElementError {

	private static void changeType(CtTypedElement<?> typedElem) {
		CtTypeReference<?> type = typedElem.getType();
		typedElem.setType(type);
	}

}
