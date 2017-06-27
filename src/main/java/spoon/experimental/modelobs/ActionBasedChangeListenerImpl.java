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

import spoon.experimental.modelobs.action.Action;
import spoon.experimental.modelobs.action.AddAction;
import spoon.experimental.modelobs.action.DeleteAction;
import spoon.experimental.modelobs.action.DeleteAllAction;
import spoon.experimental.modelobs.action.UpdateAction;
import spoon.experimental.modelobs.context.ListContext;
import spoon.experimental.modelobs.context.MapContext;
import spoon.experimental.modelobs.context.ObjectContext;
import spoon.experimental.modelobs.context.SetContext;
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
	public void onSetAdd(CtElement currentElement, CtRole role, Set field, ModifierKind newValue) {
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
