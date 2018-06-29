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
