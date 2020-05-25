/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * is the listener that creates the action on the model. This default listener does nothing.
 */
public class EmptyModelChangeListener implements FineModelChangeListener {

	@Override
	public void onObjectUpdate(CtElement currentElement, CtRole role,
			CtElement newValue, CtElement oldValue) {
	}

	@Override
	public void onObjectUpdate(CtElement currentElement, CtRole role,
			Object newValue, Object oldValue) {
	}

	@Override
	public void onObjectDelete(CtElement currentElement, CtRole role,
			CtElement oldValue) {
	}

	@Override
	public void onListAdd(CtElement currentElement, CtRole role, List field,
			CtElement newValue) {
	}

	@Override
	public void onListAdd(CtElement currentElement, CtRole role, List field,
			int index, CtElement newValue) {
	}


	@Override
	public void onListDelete(CtElement currentElement, CtRole role, List field,
			Collection<? extends CtElement> oldValue) {
		for (CtElement ctElement : oldValue) {
			onListDelete(currentElement, role, field, field.indexOf(ctElement), ctElement);
		}
	}

	@Override
	public void onListDelete(CtElement currentElement, CtRole role, List field,
			int index, CtElement oldValue) {
	}


	@Override
	public void onListDeleteAll(CtElement currentElement, CtRole role,
			List field, List oldValue) {
	}


	@Override
	public <K, V> void onMapAdd(CtElement currentElement, CtRole role,
			Map<K, V> field, K key, CtElement newValue) {
	}

	@Override
	public <K, V> void onMapDeleteAll(CtElement currentElement, CtRole role,
			Map<K, V> field, Map<K, V> oldValue) {
	}

	@Override
	public void onSetAdd(CtElement currentElement, CtRole role, Set field,
			CtElement newValue) {
	}

	@Override
	public <T extends Enum> void onSetAdd(CtElement currentElement, CtRole role, Set field,
			T newValue) {
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field,
			CtElement oldValue) {
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue) {
		for (ModifierKind modifierKind : oldValue) {
			onSetDelete(currentElement, role, field, modifierKind);
		}
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field,
			ModifierKind oldValue) {
	}

	@Override
	public void onSetDeleteAll(CtElement currentElement, CtRole role, Set field,
			Set oldValue) {
	}
}
