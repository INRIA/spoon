/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.experimental.modelobs;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface FineModelChangeListener {
	void onObjectUpdate(CtElement currentElement, CtRole role, CtElement newValue, CtElement oldValue);

	void onObjectUpdate(CtElement currentElement, CtRole role, Object newValue, Object oldValue);

	void onObjectDelete(CtElement currentElement, CtRole role, CtElement oldValue);

	void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue);

	void onListAdd(CtElement currentElement, CtRole role, List field, int index, CtElement newValue);

	void onListDelete(CtElement currentElement, CtRole role, List field, Collection<? extends CtElement> oldValue);

	void onListDelete(CtElement currentElement, CtRole role, List field, int index, CtElement oldValue);

	void onListDeleteAll(CtElement currentElement, CtRole role, List field, List oldValue);

	<K, V> void onMapAdd(CtElement currentElement, CtRole role, Map<K, V> field, K key, CtElement newValue);

	<K, V> void onMapDeleteAll(CtElement currentElement, CtRole role, Map<K, V> field, Map<K, V> oldValue);

	void onSetAdd(CtElement currentElement, CtRole role, Set field, CtElement newValue);

	void onSetAdd(CtElement currentElement, CtRole role, Set field, ModifierKind newValue);

	void onSetDelete(CtElement currentElement, CtRole role, Set field, CtElement oldValue);

	void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue);

	void onSetDelete(CtElement currentElement, CtRole role, Set field, ModifierKind oldValue);

	void onSetDeleteAll(CtElement currentElement, CtRole role, Set field, Set oldValue);
}
