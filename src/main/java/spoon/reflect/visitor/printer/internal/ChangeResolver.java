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
package spoon.reflect.visitor.printer.internal;

import java.util.Collections;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.reflect.CtExtendedModifier;

/**
 */
class ChangeResolver {
	private final ChangeCollector changeCollector;
	private final CtElement element;
	private final Set<CtRole> changedRoles;

	ChangeResolver(ChangeCollector changeCollector, CtElement element) {
		super();
		this.changeCollector = changeCollector;
		this.element = element;
		changedRoles = element != null ? changeCollector.getChanges(element) : null;
	}

	/**
	 * @return true if `element` still exist in the printed model. false if it was removed or was never there
	 */
	public boolean isElementExists(SourcePositionHolder element) {
		if (element instanceof CtExtendedModifier) {
			CtExtendedModifier modifier = (CtExtendedModifier) element;
			if (this.element instanceof CtModifiable) {
				return ((CtModifiable) this.element).hasModifier(modifier.getKind());
			}
		}
		EarlyTerminatingScanner<Boolean> scanner = new EarlyTerminatingScanner<Boolean>() {
			@Override
			protected void enter(CtElement e) {
				if (element == e) {
					setResult(Boolean.TRUE);
					terminate();
				}
			}
		};
		scanner.scan(element);
		return scanner.getResult() == Boolean.TRUE;
	}

	public Set<CtRole> getChanges(SourcePositionHolder element) {
		if (element instanceof CtElement) {
			return changeCollector.getChanges((CtElement) element);
		}
		return Collections.emptySet();
	}

	public boolean isRoleModified(CtRole ctRole) {
		if (changedRoles == null) {
			throw new SpoonException("changedRoles are not initialized for this ChangeResolver. Use getChanges(...) instead");
		}
		return changedRoles.contains(ctRole);
	}

	public boolean hasChangedRole() {
		return changedRoles.size() > 0;
	}

	public ChangeCollector getChangeCollector() {
		return changeCollector;
	}

	/**
	 * @return element whose child role changes it detects. Can be null for root {@link ChangeResolver}
	 */
	public CtElement getElement() {
		return element;
	}
}
