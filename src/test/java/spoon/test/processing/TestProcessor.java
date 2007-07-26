package spoon.test.processing;

import spoon.processing.AbstractProcessor;
import spoon.processing.Severity;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;

public class TestProcessor extends AbstractProcessor<CtElement> {

	public void process(CtElement element) {
		if ((!(element instanceof CtPackage)) && element.getParent() == null) {
			getEnvironment().report(this, Severity.ERROR, element,
					"Element's parent is null (" + element + ")");
			throw new RuntimeException("null parent detected");
		}
		if (element instanceof CtTypedElement) {
			if (((CtTypedElement) element).getType() == null) {
				getEnvironment().report(this, Severity.WARNING, element,
						"Element's type is null (" + element + ")");
			}
		}
	}

}
