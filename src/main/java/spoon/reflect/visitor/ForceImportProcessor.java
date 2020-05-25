/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Experimental;


/**
 * Marks all types references as implicit so all types will get imported.
 */
@Experimental
public class ForceImportProcessor extends ImportAnalyzer<LexicalScope> {

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
		if (reference.getPackage() != null) {
			//force import of package of top level types only
			reference.setSimplyQualified(true);
		} else {
			//it is a reference to an child type
			//if it is a reference in scope of parent type declaration then make it implicit, else keep it as it is
			CtType<?> contextType = reference.getParent(CtType.class);
			if (contextType != null) {
				CtType<?> topLevelType = contextType.getTopLevelType();
				CtTypeReference<?> referenceDeclaringType = reference.getDeclaringType();
				if (referenceDeclaringType != null && referenceDeclaringType.getQualifiedName().equals(topLevelType.getQualifiedName())) {
					//the reference to direct child type has to be made implicit
					reference.setSimplyQualified(true);
					return;
				} else {
					//reference to deeper nested child type or to child type from different type has to be kept as it is
				}
			}
			//else it is a reference to a child type from different compilation unit
			//keep it as it is (usually explicit)
			//but we have to mark top level declaring type as imported, because it is not visited individually
			CtTypeReference<?> topLevelTypeRef = reference;
			while (topLevelTypeRef != null && topLevelTypeRef.getPackage() == null) {
				topLevelTypeRef = topLevelTypeRef.getDeclaringType();
			}
			if (topLevelTypeRef != null) {
				topLevelTypeRef.setSimplyQualified(true);
			}
		}
	}

	@Override
	protected void handleTargetedExpression(CtTargetedExpression<?, ?> targetedExpression, LexicalScope nameScope) {
		CtExpression<?> target = targetedExpression.getTarget();
		if (target == null) {
			if (targetedExpression instanceof CtFieldAccess && ((CtFieldAccess) targetedExpression).getVariable().getDeclaringType() != null) {
				((CtFieldAccess) targetedExpression).getVariable().getDeclaringType().setSimplyQualified(true);
			}
			return;
		}
		if (target.isImplicit()) {
			return;
		}
		if (target instanceof CtTypeAccess) {
			CtTypeAccess<?> typeAccess = (CtTypeAccess<?>) target;
			//we might force import of static fields and methods here ... but it is not wanted by default
		}
	}
}
