package spoon.template;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * Inserts all the methods, fields, constructors, initialization blocks (if
 * target is a class), inner types, and super interfaces (except
 * {@link Template}) from a given template by substituting all the template
 * parameters by their values. Members annotated with
 * {@link spoon.template.Local} or {@link Parameter} are not inserted.
 * 
 */
public class ExtensionTemplate extends AbstractTemplate<CtType<?>> {
	@Override 
	public CtType<?> apply(CtType<?> target) { 
		if (!(target instanceof CtType)) {
			throw new TemplateException();
		}
		CtClass<?> targetType = (CtClass<?>) target;
		Substitution.insertAll(targetType, this);
		return targetType;
	}
}
