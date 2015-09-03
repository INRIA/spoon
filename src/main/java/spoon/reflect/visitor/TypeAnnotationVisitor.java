package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Visitor used to get all type annotations (integrated by the Java 8 version).
 */
public class TypeAnnotationVisitor extends CtInheritanceScanner {
	private final Set<CtAnnotation<? extends Annotation>> annotations =
			new HashSet<CtAnnotation<? extends Annotation>>();
	private boolean isToBeProcessed;

	public TypeAnnotationVisitor(boolean isToBeProcessed) {
		this.isToBeProcessed = isToBeProcessed;
	}

	@Override
	public <T> void scanCtExpression(CtExpression<T> expression) {
		super.scanCtExpression(expression);

		if (!isToBeProcessed && expression.getTypeCasts().size() > 0) {
			isToBeProcessed = iterateOnTypeReference(expression.getTypeCasts());
		}
	}

	@Override
	public <R> void scanCtExecutable(CtExecutable<R> e) {
		super.scanCtExecutable(e);

		if (!isToBeProcessed &&  e.getThrownTypes().size() > 0) {
			isToBeProcessed = iterateOnTypeReference(((CtExecutable) e).getThrownTypes());
		}
	}

	@Override
	public void scanCtGenericElement(CtGenericElement e) {
		super.scanCtGenericElement(e);

		if (!isToBeProcessed) {
			isToBeProcessed = iterateOnTypeReference(e.getFormalTypeParameters());
		}
	}

	@Override
	public <T> void scanCtTypeInformation(CtTypeInformation typeInfo) {
		super.scanCtTypeInformation(typeInfo);

		if (!isToBeProcessed) {
			if (typeInfo.getSuperclass() != null) {
				isToBeProcessed = typeInfo.getSuperclass().getTypeAnnotations().size() > 0;
				if (isToBeProcessed) {
					annotations.addAll(typeInfo.getSuperclass().getTypeAnnotations());
				}
			}
			if (typeInfo.getSuperInterfaces() != null && !isToBeProcessed) {
				isToBeProcessed = iterateOnTypeReference(typeInfo.getSuperInterfaces());
			}
		}
	}

	@Override
	public <T> void scanCtTypedElement(CtTypedElement<T> e) {
		super.scanCtTypedElement(e);

		if (!isToBeProcessed && e.getType() != null) {
			isToBeProcessed = e.getType().getTypeAnnotations().size() > 0;
			if (isToBeProcessed) {
				annotations.addAll(e.getType().getTypeAnnotations());
			}
			final List<CtTypeReference<?>> typeArguments = e.getType().getActualTypeArguments();
			if (typeArguments.size() > 0 && !isToBeProcessed) {
				isToBeProcessed = iterateOnTypeReference(typeArguments);
			}
		}
	}

	private boolean iterateOnTypeReference(Iterable<CtTypeReference<?>> typeReferences) {
		boolean isToBeProcessed = false;
		for (CtTypeReference<?> ref: typeReferences) {
			isToBeProcessed = ref.getTypeAnnotations().size() > 0;
			if (isToBeProcessed) {
				annotations.addAll(ref.getTypeAnnotations());
				break;
			}
		}
		return isToBeProcessed;
	}

	public boolean isToBeProcessed() {
		return isToBeProcessed;
	}

	public Set<CtAnnotation<? extends Annotation>> getAnnotations() {
		return annotations;
	}
}
