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
package spoon.support.sniper.internal;

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
 * Helper which provides details about changes on provided `element`
 */
public class ChangeResolver {
	private final ChangeCollector changeCollector;
	private final CtElement element;
	private final Set<CtRole> changedRoles;

	public ChangeResolver(ChangeCollector changeCollector, CtElement element) {
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
		scanner.scan(this.element);
		return scanner.getResult() == Boolean.TRUE;
	}

	/**
	 * @param element to be checked element
	 * @return Set of {@link CtRole}s, which are modified for `element`
	 */
	public Set<CtRole> getChanges(SourcePositionHolder element) {
		if (element instanceof CtElement) {
			return changeCollector.getChanges((CtElement) element);
		}
		return Collections.emptySet();
	}

	/**
	 * @param ctRole to be checked {@link CtRole}
	 * @return true if `ctRole` of scope `element` is modified
	 */
	public boolean isRoleModified(CtRole ctRole) {
		if (changedRoles == null) {
			throw new SpoonException("changedRoles are not initialized for this ChangeResolver. Use getChanges(...) instead");
		}
		return changedRoles.contains(ctRole);
	}

	/**
	 * @return true if scope `element` is changed
	 */
	public boolean hasChangedRole() {
		return changedRoles.size() > 0;
	}

	/**
	 * @return {@link ChangeCollector}
	 */
	public ChangeCollector getChangeCollector() {
		return changeCollector;
	}
}
