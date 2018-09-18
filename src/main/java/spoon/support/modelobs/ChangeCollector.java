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
package spoon.support.modelobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * Listens on changes  on the spoon model and remembers them
 */
public class ChangeCollector {
	private final Map<CtElement, Set<CtRole>> elementToChangeRole = new IdentityHashMap<>();
	private final ChangeListener changeListener = new ChangeListener();

	/**
	 * @param env to be checked {@link Environment}
	 * @return {@link ChangeCollector} attached to the `env` or null if there is none
	 */
	public static ChangeCollector getChangeCollector(Environment env) {
		FineModelChangeListener mcl = env.getModelChangeListener();
		if (mcl instanceof ChangeListener) {
			return ((ChangeListener) mcl).getChangeCollector();
		}
		return null;
	}

	/**
	 * Allows to run code using change collector switched off.
	 * It means that any change of spoon model done by the `runnable` is ignored by the change collector.
	 * Note: it is actually needed to wrap CtElement#toString() calls which sometime modifies spoon model.
	 * See TestSniperPrinter#testPrintChangedReferenceBuilder()
	 * @param env Spoon environment
	 * @param runnable the code to be run
	 */
	public static void runWithoutChangeListener(Environment env, Runnable runnable) {
		FineModelChangeListener mcl = env.getModelChangeListener();
		if (mcl instanceof ChangeListener) {
			env.setModelChangeListener(new EmptyModelChangeListener());
			try {
				runnable.run();
			} finally {
				env.setModelChangeListener(mcl);
			}
		}
	}

	/**
	 * Attaches itself to {@link CtModel} to listen to all changes of it's child elements
	 * TODO: it would be nicer if we might listen on changes on {@link CtElement}
	 * @param env to be attached to {@link Environment}
	 * @return this to support fluent API
	 */
	public ChangeCollector attachTo(Environment env) {
		env.setModelChangeListener(changeListener);
		return this;
	}

	/**
	 * @param currentElement the {@link CtElement} whose changes has to be checked
	 * @return set of {@link CtRole}s whose attribute was directly changed on `currentElement` since this {@link ChangeCollector} was attached
	 * The 'directly' means that value of attribute of `currentElement` was changed.
	 * Use {@link #getChanges(CtElement)} to detect changes in child elements too
	 */
	public Set<CtRole> getDirectChanges(CtElement currentElement) {
		Set<CtRole> changes = elementToChangeRole.get(currentElement);
		if (changes == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(changes);
	}

	/**
	 * @param currentElement the {@link CtElement} whose changes has to be checked
	 * @return set of {@link CtRole}s whose attribute was changed on `currentElement`
	 * or any child of this attribute was changed
	 * since this {@link ChangeCollector} was attached
	 */
	public Set<CtRole> getChanges(CtElement currentElement) {
		final Set<CtRole> changes = new HashSet<>(getDirectChanges(currentElement));
		final Scanner scanner = new Scanner();
		scanner.setListener(new CtScannerListener() {
			int depth = 0;
			CtRole checkedRole;
			@Override
			public ScanningMode enter(CtElement element) {
				if (depth == 0) {
					//we are checking children of role checkedRole
					checkedRole = scanner.getScannedRole();
				}
				if (changes.contains(checkedRole)) {
					//we already know that some child of `checkedRole` attribute is modified. Skip others
					return ScanningMode.SKIP_ALL;
				}
				if (elementToChangeRole.containsKey(element)) {
					//we have found a modified element in children of `checkedRole`
					changes.add(checkedRole);
					return ScanningMode.SKIP_ALL;
				}
				depth++;
				//continue searching for an modification
				return ScanningMode.NORMAL;
			}
			@Override
			public void exit(CtElement element) {
				depth--;
			}
		});
		currentElement.accept(scanner);
		return Collections.unmodifiableSet(changes);
	}

	private static class Scanner extends EarlyTerminatingScanner<Void> {
		CtRole getScannedRole() {
			return scannedRole;
		}
	}

	/**
	 * Called whenever anything changes in the spoon model
	 * @param currentElement the modified element
	 * @param role the modified attribute of that element
	 */
	protected void onChange(CtElement currentElement, CtRole role) {
		Set<CtRole> roles = elementToChangeRole.get(currentElement);
		if (roles == null) {
			roles = new HashSet<>();
			elementToChangeRole.put(currentElement, roles);
		}
		if (role.getSuperRole() != null) {
			role = role.getSuperRole();
		}
		roles.add(role);
	}

	private class ChangeListener implements FineModelChangeListener {
		private ChangeCollector getChangeCollector() {
			return ChangeCollector.this;
		}

		@Override
		public void onObjectUpdate(CtElement currentElement, CtRole role, CtElement newValue, CtElement oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onObjectUpdate(CtElement currentElement, CtRole role, Object newValue, Object oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onObjectDelete(CtElement currentElement, CtRole role, CtElement oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onListAdd(CtElement currentElement, CtRole role, List field, int index, CtElement newValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onListDelete(CtElement currentElement, CtRole role, List field, Collection<? extends CtElement> oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onListDelete(CtElement currentElement, CtRole role, List field, int index, CtElement oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onListDeleteAll(CtElement currentElement, CtRole role, List field, List oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public <K, V> void onMapAdd(CtElement currentElement, CtRole role, Map<K, V> field, K key, CtElement newValue) {
			onChange(currentElement, role);
		}

		@Override
		public <K, V> void onMapDeleteAll(CtElement currentElement, CtRole role, Map<K, V> field, Map<K, V> oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onSetAdd(CtElement currentElement, CtRole role, Set field, CtElement newValue) {
			onChange(currentElement, role);
		}

		@Override
		public <T extends Enum> void onSetAdd(CtElement currentElement, CtRole role, Set field, T newValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onSetDelete(CtElement currentElement, CtRole role, Set field, CtElement oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onSetDelete(CtElement currentElement, CtRole role, Set field, ModifierKind oldValue) {
			onChange(currentElement, role);
		}

		@Override
		public void onSetDeleteAll(CtElement currentElement, CtRole role, Set field, Set oldValue) {
			onChange(currentElement, role);
		}
	}
}
