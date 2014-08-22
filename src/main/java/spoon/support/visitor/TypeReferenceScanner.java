package spoon.support.visitor;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import spoon.reflect.code.CtTargetedAccess;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

/**
 * A scanner that calculates the imports for a given model.
 */
public class TypeReferenceScanner extends CtScanner {

	Set<CtTypeReference<?>> references;

	/**
	 * Constructor.
	 */
	public TypeReferenceScanner() {
		references = new HashSet<CtTypeReference<?>>();
	}

	/**
	 * Constructor.
	 * @param references a set to fill with the references
	 */
	public TypeReferenceScanner(HashSet<CtTypeReference<?>> references) {
		this.references = references;
	}
	
	/**
	 * Returns the set of calculated references.
	 */
	public Set<CtTypeReference<?>> getReferences() {
		return references;
	}
	
	/**
	 * Adds a reference.
	 */
	private <T> boolean addReference(CtTypeReference<T> ref) {
		return references.add(ref);
	}

	@Override
	public <T> void visitCtTargetedAccess(CtTargetedAccess<T> targetedAccess) {
		enter(targetedAccess);
		scan(targetedAccess.getVariable());
		// scan(fieldAccess.getType());
		scan(targetedAccess.getAnnotations());
		scanReferences(targetedAccess.getTypeCasts());
		scan(targetedAccess.getVariable());
		scan(targetedAccess.getTarget());
		exit(targetedAccess);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enterReference(reference);
		scan(reference.getDeclaringType());
		// scan(reference.getType());
		exitReference(reference);
	}

//	public <T> boolean isImported(CtTypeReference<T> ref) {
//		if (imports.containsKey(ref.getSimpleName())) {
//			CtTypeReference<?> exist = imports.get(ref.getSimpleName());
//			if (exist.getQualifiedName().equals(ref.getQualifiedName()))
//				return true;
//		}
//		return false;
//	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enterReference(reference);
		scanReferences(reference.getParameterTypes());
		scanReferences(reference.getActualTypeArguments());
		exitReference(reference);
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			if (reference.getDeclaringType() == null)
				addReference(reference);
			else
				addReference(reference.getDeclaringType());
		}
		super.visitCtTypeReference(reference);

	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		addReference(annotationType.getReference());
		super.visitCtAnnotationType(annotationType);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		addReference(ctEnum.getReference());
		super.visitCtEnum(ctEnum);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		addReference(intrface.getReference());
		for (CtSimpleType<?> t : intrface.getNestedTypes()) {
			addReference(t.getReference());
		}
		super.visitCtInterface(intrface);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		addReference(ctClass.getReference());
		for (CtSimpleType<?> t : ctClass.getNestedTypes()) {
			addReference(t.getReference());
		}
		super.visitCtClass(ctClass);
	}
}

