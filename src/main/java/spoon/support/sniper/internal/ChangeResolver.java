/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
		return Boolean.TRUE.equals(scanner.getResult());
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
