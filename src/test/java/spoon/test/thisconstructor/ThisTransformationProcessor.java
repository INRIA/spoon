package spoon.test.thisconstructor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class ThisTransformationProcessor extends AbstractProcessor<CtClass<?>> {
	@Override
	public void process(CtClass<?> element) {
		element.setSimpleName(element.getSimpleName() + "X");
	}
}