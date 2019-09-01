/**
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
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Experimental;


/**
 * Removes all imports from compilation unit and forces fully qualified identifiers
 */
@Experimental
public class ForceFullyQualifiedProcessor extends AbstractCompilationUnitImportsProcessor<LexicalScopeScanner, LexicalScope> {

	@Override
	protected LexicalScopeScanner createRawScanner() {
		return new LexicalScopeScanner();
	}

	@Override
	protected LexicalScope getContext(LexicalScopeScanner scanner) {
		return scanner.getCurrentLexicalScope();
	}

	@Override
	protected void handleTypeReference(LexicalScope nameScope, CtRole role, CtTypeReference<?> reference) {
		if (reference.isImplicitParent() || reference.isImplicit()) {
			if (isThisAccess(reference)) {
				//do not force FQ names in this access
				return;
			}
			if (isTypeReferenceToEnclosingType(nameScope, reference)) {
				//it is the reference to the enclosing class
				//do not use FQ names for that
				return;
			}
			if (isSupertypeOfNewClass(reference)) {
				//it is a super type of new anonymous class
				//do not use FQ names for that
				return;
			}
			//force fully qualified name
			reference.setImplicit(false);
			reference.setImplicitParent(false);
		}
	}

	protected boolean isTypeReferenceToEnclosingType(LexicalScope nameScope, CtTypeReference<?> reference) {
//		String qName = reference.getQualifiedName();
//		return Boolean.TRUE == nameScope.forEachElementByName(reference.getSimpleName(), named -> {
//			if (named instanceof CtType) {
//				CtType<?> type = (CtType<?>) named;
//				if (qName.equals(type.getQualifiedName())) {
//					return Boolean.TRUE;
//				}
//			}
//			return null;
//		});
		//expected by LambdaTest
		CtType<?> enclosingType = reference.getParent(CtType.class);
		return enclosingType == null ? false : reference.getQualifiedName().equals(enclosingType.getQualifiedName());
	}

	private boolean isSupertypeOfNewClass(CtTypeReference<?> typeRef) {
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
	protected void handleTargetedExpression(LexicalScope nameScope, CtRole role, CtTargetedExpression<?, ?> targetedExpression, CtExpression<?> target) {
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
