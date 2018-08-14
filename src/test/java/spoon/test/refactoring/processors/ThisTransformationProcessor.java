package spoon.test.refactoring.processors;

import spoon.processing.AbstractProcessor;
import spoon.refactoring.Refactoring;
import spoon.reflect.declaration.CtClass;

public class ThisTransformationProcessor extends AbstractProcessor<CtClass<?>> {
	@Override
	public void process(CtClass<?> element) {
		Refactoring.changeTypeName(element, element.getSimpleName() + "X");
	}
}
