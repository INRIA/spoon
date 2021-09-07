package spoon.support.visitor.java.internal;

import java.lang.annotation.Annotation;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtRecordComponent;

public class RecordComponentRuntimeBuilderContext extends AbstractRuntimeBuilderContext {

	private final CtRecordComponent<?> component;
	public RecordComponentRuntimeBuilderContext(CtRecordComponent element) {
		super(element);
		this.component = element;
	}
	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		component.addAnnotation(ctAnnotation);
	}
}
