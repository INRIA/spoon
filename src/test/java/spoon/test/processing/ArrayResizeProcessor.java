package spoon.test.processing;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.template.Template;
import spoon.test.template.ArrayResizeTemplate;

public class ArrayResizeProcessor extends AbstractProcessor<CtField<?>> {

	public void process(CtField<?> field) {
		if ((field.getDeclaringType() instanceof CtClass)
				&& field.getType() instanceof CtArrayTypeReference) {
			Template<?> t = new ArrayResizeTemplate(field, 10);
			t.apply(field.getDeclaringType());
		}
	}

}
