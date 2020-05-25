/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Experimental;


/**
 * Forces fully qualified identifiers by making many elements explicit (by calling setImplicit(false)).
 */
@Experimental
public class ForceFullyQualifiedProcessor extends ImportAnalyzer<LexicalScope> {

	@Override
	protected LexicalScopeScanner createScanner() {
		return new LexicalScopeScanner();
	}

	@Override
	protected LexicalScope getScannerContextInformation() {
		return ((LexicalScopeScanner) scanner).getCurrentLexicalScope();
	}

	@Override
	protected void handleTypeReference(CtTypeReference<?> reference, LexicalScope nameScope, CtRole role) {
		if (reference.isSimplyQualified() || reference.isImplicit()) {
			if (isThisAccess(reference)) {
				//do not force FQ names in this access
				return;
			}

			if (isTypeReferenceToEnclosingType(nameScope, reference) && reference.getParent(CtAnonymousExecutable.class) != null) {
				// for the java compiler, we must keep short version of field accesses in static blocks
				return;
			}
			if (isSupertypeOfNewClass(reference)) {
				//it is a super type of new anonymous class
				//do not use FQ names for that
				return;
			}
			//force fully qualified name
			reference.setImplicit(false);
			reference.setSimplyQualified(false);
		}
	}

	protected boolean isTypeReferenceToEnclosingType(LexicalScope nameScope, CtTypeReference<?> reference) {
		CtType<?> enclosingType = reference.getParent(CtType.class);
		if (enclosingType == null) {
			return false;
		}

		return reference.getQualifiedName().equals(enclosingType.getQualifiedName());
	}

	private boolean isSupertypeOfNewClass(CtTypeReference<?> typeRef) {
		if (!typeRef.isParentInitialized()) {
			return false;
		}
		CtElement parent = typeRef.getParent();
		if (parent instanceof CtClass && ((CtClass) parent).getSuperclass() == typeRef) {
			CtElement parent2 = parent.getParent();
			if (parent2 instanceof CtNewClass) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void handleTargetedExpression(CtTargetedExpression<?, ?> targetedExpression, LexicalScope nameScope) {
		CtExpression<?> target = targetedExpression.getTarget();
		if (target == null) {
			return;
		}
		if (!target.isImplicit()) {
			return;
		}
		if (target instanceof CtThisAccess) {
			//the non implicit this access is not forced
			return;
		}
		if (target instanceof CtTypeAccess) {
			CtTypeAccess<?> typeAccess = (CtTypeAccess<?>) target;
			if (isThisAccess(typeAccess)) {
				//do not force FQ names for `this` access
				return;
			}
			if (isTypeReferenceToEnclosingType(nameScope, typeAccess.getAccessedType())) {
				//it is the reference to the enclosing class
				//do not use FQ names for that
				return;
			}
		}
		target.setImplicit(false);
	}

	private boolean isThisAccess(CtTypeReference<?> typeRef) {
		return getParentIfType(getParentIfType(typeRef, CtTypeAccess.class), CtThisAccess.class) != null;
	}

	private boolean isThisAccess(CtTypeAccess<?> typeAccess) {
		return getParentIfType(typeAccess, CtThisAccess.class) != null;
	}
}
