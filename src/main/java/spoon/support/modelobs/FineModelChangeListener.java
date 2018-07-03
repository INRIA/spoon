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


/** Can be subclassed by clients who want to be notified on all changes in AST nodes */
public interface FineModelChangeListener {
	/** a field corresponding to the role is being set in the AST node */
	void onObjectUpdate(CtElement currentElement, CtRole role, CtElement newValue, CtElement oldValue);

	/** a field corresponding to the role is being set in the AST node */
	void onObjectUpdate(CtElement currentElement, CtRole role, Object newValue, Object oldValue);

	/** a field corresponding to the role is being set to null */
	void onObjectDelete(CtElement currentElement, CtRole role, CtElement oldValue);

	/** a newValue is appended to the list corresponding to the role in the AST node */
	void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue);

	/** a newValue is appended to the list corresponding to the role in the AST node */
	void onListAdd(CtElement currentElement, CtRole role, List field, int index, CtElement newValue);

	/** an oldValue is deleted in the list corresponding to the role in the AST node */
	void onListDelete(CtElement currentElement, CtRole role, List field, Collection<? extends CtElement> oldValue);

	/** an oldValue is deleted in the list corresponding to the role in the AST node */
	void onListDelete(CtElement currentElement, CtRole role, List field, int index, CtElement oldValue);

	/** a list corresponding to the role in the AST node is emptied */
	void onListDeleteAll(CtElement currentElement, CtRole role, List field, List oldValue);

	/** a newValue is appended to the map corresponding to the role in the AST node */
	<K, V> void onMapAdd(CtElement currentElement, CtRole role, Map<K, V> field, K key, CtElement newValue);

	/** a map corresponding to the role in the AST node is emptied */
	<K, V> void onMapDeleteAll(CtElement currentElement, CtRole role, Map<K, V> field, Map<K, V> oldValue);

	/** a newValue is appended to the set corresponding to the role in the AST node */
	void onSetAdd(CtElement currentElement, CtRole role, Set field, CtElement newValue);

	/** a newValue is appended to the set corresponding to the role in the AST node */
	<T extends Enum> void onSetAdd(CtElement currentElement, CtRole role, Set field, T newValue);

	/** an oldValue is deleted in the set corresponding to the role in the AST node */
	void onSetDelete(CtElement currentElement, CtRole role, Set field, CtElement oldValue);

	/** an oldValue is deleted in the set corresponding to the role in the AST node */
	void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue);

	/** an oldValue is deleted in the set corresponding to the role in the AST node */
	void onSetDelete(CtElement currentElement, CtRole role, Set field, ModifierKind oldValue);

	/** a set corresponding to the role in the AST node is emptied */
	void onSetDeleteAll(CtElement currentElement, CtRole role, Set field, Set oldValue);
}
