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
 * Marks package references implicit so all type will get imported
 */
@Experimental
public class ForceImportProcessor extends AbstractCompilationUnitImportsProcessor<LexicalScopeScanner, LexicalScope> {

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
		if (reference.getPackage() != null) {
			//force import of package of top level types only
			reference.setImplicitParent(true);
		} else {
			//it is a reference to an child type
			//if it is a reference in scope of parent type declaration then make it implicit, else keep it as it is
			CtType<?> contextType = reference.getParent(CtType.class);
			if (contextType != null) {
				CtType<?> topLevelType = contextType.getTopLevelType();
				CtTypeReference<?> referenceDeclaringType = reference.getDeclaringType();
				if (referenceDeclaringType != null && referenceDeclaringType.getQualifiedName().equals(topLevelType.getQualifiedName())) {
					//the reference to direct child type has to be made implicit
					reference.setImplicitParent(true);
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
				topLevelTypeRef.setImplicitParent(true);
			}
		}
	}


	protected void handleTargetedExpression(LexicalScope nameScope, CtRole role, CtTargetedExpression<?, ?> targetedExpression, CtExpression<?> target) {
		if (target.isImplicit()) {
			return;
		}
		if (target instanceof CtThisAccess) {
			//force implicit this access
			target.setImplicit(true);
			return;
		}
		if (target instanceof CtTypeAccess) {
			CtTypeAccess<?> typeAccess = (CtTypeAccess<?>) target;
			//we might force import of static fields and methods here ... but it is not wanted by default
		}
	}
}
