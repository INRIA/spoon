package spoon.testing.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class FooToBarProcessor extends AbstractProcessor<CtClass<?>> {
	public FooToBarProcessor() {
	}

	@Override
	public boolean isToBeProcessed(CtClass<?> candidate) {
		return "Foo".equals(candidate.getSimpleName());
	}

	@Override
	public void process(CtClass<?> element) {
		element.setSimpleName("Bar");
	}
}