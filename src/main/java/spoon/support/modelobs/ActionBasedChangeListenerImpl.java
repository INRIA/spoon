/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs;

import spoon.support.modelobs.action.Action;
import spoon.support.modelobs.action.AddAction;
import spoon.support.modelobs.action.DeleteAction;
import spoon.support.modelobs.action.DeleteAllAction;
import spoon.support.modelobs.action.UpdateAction;
import spoon.support.modelobs.context.ListContext;
import spoon.support.modelobs.context.MapContext;
import spoon.support.modelobs.context.ObjectContext;
import spoon.support.modelobs.context.SetContext;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This listener will propagate the change to the listener
 */
public abstract class ActionBasedChangeListenerImpl implements ActionBasedChangeListener, FineModelChangeListener {

	private void propagateModelChange(final Action action) {
		this.onAction(action);
		if (action instanceof DeleteAllAction) {
			this.onDeleteAll((DeleteAllAction) action);
		} else if (action instanceof DeleteAction) {
			this.onDelete((DeleteAction) action);
		} else if (action instanceof AddAction) {
			this.onAdd((AddAction) action);
		} else if (action instanceof UpdateAction) {
			this.onUpdate((UpdateAction) action);
		}
	}

	@Override
	public void onObjectUpdate(CtElement currentElement, CtRole role, CtElement newValue, CtElement oldValue) {
		propagateModelChange(new UpdateAction<>(new ObjectContext(currentElement, role), newValue, oldValue));
	}

	@Override
	public void onObjectUpdate(CtElement currentElement, CtRole role, Object newValue, Object oldValue) {
		propagateModelChange(new UpdateAction<>(new ObjectContext(currentElement, role), newValue, oldValue));
	}

	@Override
	public void onObjectDelete(CtElement currentElement, CtRole role, CtElement oldValue) {
		propagateModelChange(new DeleteAction<>(new ObjectContext(currentElement, role), oldValue));
	}

	@Override
	public void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue) {
		propagateModelChange(new AddAction<>(new ListContext(currentElement, role, field), newValue));
	}

	@Override
	public void onListAdd(CtElement currentElement, CtRole role, List field, int index, CtElement newValue) {
		propagateModelChange(new AddAction<>(new ListContext(currentElement, role, field, index), newValue));
	}

	@Override
	public void onListDelete(CtElement currentElement, CtRole role, List field, Collection<? extends CtElement> oldValue) {
		for (CtElement ctElement : oldValue) {
			onListDelete(currentElement, role, field, field.indexOf(ctElement), ctElement);
		}
	}

	@Override
	public void onListDelete(CtElement currentElement, CtRole role, List field, int index, CtElement oldValue) {
		propagateModelChange(new DeleteAction<>(new ListContext(currentElement, role, field, index), oldValue));
	}

	@Override
	public void onListDeleteAll(CtElement currentElement, CtRole role, List field, List oldValue) {
		propagateModelChange(new DeleteAllAction(new ListContext(currentElement, role, field), oldValue));
	}

	@Override
	public <K, V> void onMapAdd(CtElement currentElement, CtRole role, Map<K, V> field, K key, CtElement newValue) {
		propagateModelChange(new AddAction<>(new MapContext<>(currentElement, role, field, key), newValue));
	}

	@Override
	public <K, V> void onMapDeleteAll(CtElement currentElement, CtRole role, Map<K, V> field, Map<K, V> oldValue) {
		propagateModelChange(new DeleteAllAction(new MapContext<>(currentElement, role, field), oldValue));
	}

	@Override
	public void onSetAdd(CtElement currentElement, CtRole role, Set field, CtElement newValue) {
		propagateModelChange(new AddAction<>(new SetContext(currentElement, role, field), newValue));
	}

	@Override
	public  <T extends Enum> void onSetAdd(CtElement currentElement, CtRole role, Set field, T newValue) {
		propagateModelChange(new AddAction<>(new SetContext(currentElement, role, field), newValue));
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field, CtElement oldValue) {
		propagateModelChange(new DeleteAction<>(new SetContext(currentElement, role, field), oldValue));
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue) {
		for (ModifierKind modifierKind : oldValue) {
			onSetDelete(currentElement, role, field, modifierKind);
		}
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field, ModifierKind oldValue) {
		propagateModelChange(new DeleteAction<>(new SetContext(currentElement, role, field), oldValue));
	}

	@Override
	public void onSetDeleteAll(CtElement currentElement, CtRole role, Set field, Set oldValue) {
		propagateModelChange(new DeleteAllAction(new SetContext(currentElement, role, field), oldValue));
	}

	@Override
	public void onDelete(DeleteAction action) {
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
	}

	@Override
	public void onAdd(AddAction action) {
	}

	@Override
	public void onUpdate(UpdateAction action) {
	}

	@Override
	public void onAction(Action action) {
	}
}
