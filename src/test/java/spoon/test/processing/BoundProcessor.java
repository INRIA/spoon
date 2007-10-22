package spoon.test.processing;

import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.test.annotation.Bound;
import spoon.test.template.BoundTestTemplate;

/**
 * This processor (see the code below) processes the
 * {@link spoon.test.annotation.Bound} annotation and uses
 * {@link spoon.examples.stack.template.BoundTestTemplate} to insert a test on
 * the bound in the <code>push</code> method.
 */
public class BoundProcessor extends
		AbstractAnnotationProcessor<Bound, CtClass<?>> {
	public void process(Bound b, CtClass<?> c) {
		CtMethod<?> push = c.getMethod("push", getFactory().Type()
				.createTypeParameterReference("T"));
		BoundTestTemplate template = new BoundTestTemplate(b.max());
		CtStatementList<?> l = template.getSubstitution(c);
		l
				.setPositions(c.getAnnotation(
						getFactory().Type().createReference(Bound.class))
						.getPosition());
		push.getBody().insertBegin(l);
	}
}
