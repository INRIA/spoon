/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
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
