/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * A scanner that calculates the imports for a given model.
 */
public class TypeReferenceScanner extends CtScanner {

	Set<CtTypeReference<?>> references;

	/**
	 * Constructor.
	 */
	public TypeReferenceScanner() {
		references = new HashSet<>();
	}

	/**
	 * Constructor.
	 *
	 * @param references
	 * 		a set to fill with the references
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
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		super.visitCtFieldRead(fieldRead);
		enter(fieldRead);
		scan(fieldRead.getVariable());
		scan(fieldRead.getAnnotations());
		scan(fieldRead.getTypeCasts());
		scan(fieldRead.getVariable());
		scan(fieldRead.getTarget());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		enter(fieldWrite);
		scan(fieldWrite.getVariable());
		scan(fieldWrite.getAnnotations());
		scan(fieldWrite.getTypeCasts());
		scan(fieldWrite.getVariable());
		scan(fieldWrite.getTarget());
		exit(fieldWrite);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		exit(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		scan(reference.getActualTypeArguments());
		exit(reference);
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			addReference(reference);
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
		for (CtTypeMember typeMember : intrface.getTypeMembers()) {
			if (typeMember instanceof CtType) {
				addReference(((CtType) typeMember).getReference());
			}
		}
		super.visitCtInterface(intrface);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		addReference(ctClass.getReference());
		for (CtTypeMember typeMember : ctClass.getTypeMembers()) {
			if (typeMember instanceof CtType) {
				addReference(((CtType) typeMember).getReference());
			}
		}
		super.visitCtClass(ctClass);
	}
}

