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
package spoon.reflect.factory;

import spoon.diff.Action;
import spoon.diff.ModelChangeListener;
import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;
import spoon.diff.context.ListContext;
import spoon.diff.context.MapContext;
import spoon.diff.context.ObjectContext;
import spoon.diff.context.SetContext;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeFactory extends SubFactory {

	private final List<ModelChangeListener> listeners = new ArrayList<>(2);

	public ChangeFactory(Factory factory) {
		super(factory);
	}

	public void addModelChangeListener(final ModelChangeListener listener) {
		listeners.add(listener);
	}

	public void removeModelChangeListener(final ModelChangeListener listener) {
		listeners.remove(listener);
	}

	private void propagateModelChange(final Action action) {
		for (ModelChangeListener listener : listeners) {
			listener.onAction(action);
			if (action instanceof DeleteAllAction) {
				listener.onDeleteAll((DeleteAllAction) action);
			} else if (action instanceof DeleteAction) {
				listener.onDelete((DeleteAction) action);
			} else if (action instanceof AddAction) {
				listener.onAdd((AddAction) action);
			} else if (action instanceof UpdateAction) {
				listener.onUpdate((UpdateAction) action);
			}
		}
	}

	public void onObjectUpdate(CtElement currentElement, CtRole role, CtElement newValue, CtElement oldValue) {
		Action action = new UpdateAction<>(new ObjectContext(currentElement, role), newValue, oldValue);
		propagateModelChange(action);
	}

	public void onObjectUpdate(CtElement currentElement, CtRole role, Object newValue, Object oldValue) {
		UpdateAction action = new UpdateAction<>(new ObjectContext(currentElement, role), newValue, oldValue);
		propagateModelChange(action);
	}

	public void onObjectDelete(CtElement currentElement, CtRole role, CtElement oldValue) {
		DeleteAction action = new DeleteAction<>(new ObjectContext(currentElement, role), oldValue);
		propagateModelChange(action);
	}

	public void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue) {
		AddAction action = new AddAction<>(new ListContext(currentElement, role, field), newValue);
		propagateModelChange(action);
	}

	public void onListAdd(CtElement currentElement, CtRole role, List field, int index, CtElement newValue) {
		AddAction action = new AddAction<>(new ListContext(currentElement, role, field, index), newValue);
		propagateModelChange(action);
	}


	public void onListDelete(CtElement currentElement, CtRole role, List field, Collection<? extends CtElement> oldValue) {
		for (CtElement ctElement : oldValue) {
			onListDelete(currentElement, role, field, field.indexOf(ctElement), ctElement);
		}
	}

	public void onListDelete(CtElement currentElement, CtRole role, List field, int index, CtElement oldValue) {
		DeleteAction action = new DeleteAction<>(new ListContext(currentElement, role, field, index), oldValue);
		propagateModelChange(action);
	}


	public void onListDeleteAll(CtElement currentElement, CtRole role, List field, List oldValue) {
		DeleteAllAction action = new DeleteAllAction(new ListContext(currentElement, role, field), oldValue);
		propagateModelChange(action);
	}


	public <K, V> void onMapAdd(CtElement currentElement, CtRole role, Map<K, V> field, K key, CtElement newValue) {
		AddAction action = new AddAction<>(new MapContext<>(currentElement, role, field, key), newValue);
		propagateModelChange(action);
	}

	public <K, V> void onMapDeleteAll(CtElement currentElement, CtRole role, Map<K, V> field, Map<K, V> oldValue) {
		DeleteAllAction action = new DeleteAllAction(new MapContext<>(currentElement, role, field), oldValue);
		propagateModelChange(action);
	}

	public void onSetAdd(CtElement currentElement, CtRole role, Set field, CtElement newValue) {
		AddAction action = new AddAction<>(new SetContext(currentElement, role, field), newValue);
		propagateModelChange(action);
	}

	public void onSetAdd(CtElement currentElement, CtRole role, Set field, ModifierKind newValue) {
		AddAction action = new AddAction<>(new SetContext(currentElement, role, field), newValue);
		propagateModelChange(action);
	}


	public void onSetDelete(CtElement currentElement, CtRole role, Set field, CtElement oldValue) {
		DeleteAction action = new DeleteAction<>(new SetContext(currentElement, role, field), oldValue);
		propagateModelChange(action);
	}

	public void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue) {
		for (ModifierKind modifierKind : oldValue) {
			onSetDelete(currentElement, role, field, modifierKind);
		}
	}

	public void onSetDelete(CtElement currentElement, CtRole role, Set field, ModifierKind oldValue) {
		DeleteAction action = new DeleteAction(new SetContext(currentElement, role, field), oldValue);
		propagateModelChange(action);
	}

	public void onSetDeleteAll(CtElement currentElement, CtRole role, Set field, Set oldValue) {
		DeleteAllAction action = new DeleteAllAction(new SetContext(currentElement, role, field), oldValue);
		propagateModelChange(action);
	}
}
