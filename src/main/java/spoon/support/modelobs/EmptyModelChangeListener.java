/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * is the listener that creates the action on the model. This default listener does nothing.
 */
public class EmptyModelChangeListener implements FineModelChangeListener {

	private void updateParent(CtElement currentElement, CtElement oldElement, CtElement newElement) {
		if (currentElement != null && currentElement.isParentInitialized()) {
			onObjectUpdate(currentElement.getParent(), currentElement.getRoleInParent(), oldElement, newElement);
		}
	}

	@Override
	public void onObjectUpdate(CtElement currentElement, CtRole role,
			CtElement newValue, CtElement oldValue) {
		if (currentElement != null) {
			if (oldValue instanceof CtType<?>) {
				currentElement.getFactory().Type().removeCachedType(((CtType<?>) oldValue).getQualifiedName());
			}
			if (newValue instanceof CtType<?>) {
				currentElement.getFactory().Type().addToCache((CtType<?>) newValue);
			}
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onObjectUpdate(CtElement currentElement, CtRole role,
			Object newValue, Object oldValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onObjectDelete(CtElement currentElement, CtRole role,
			CtElement oldValue) {
		if (currentElement != null) {
			if (oldValue instanceof CtType<?>) {
				currentElement.getFactory().Type().removeCachedType(((CtType<?>) oldValue).getQualifiedName());
			}
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);

			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onListAdd(CtElement currentElement, CtRole role, List field,
			CtElement newValue) {
		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
		if (newValue != null) {
			newValue.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
		}

		if (newValue instanceof CtType<?>) {
			currentElement.getFactory().Type().addToCache((CtType<?>) newValue);
		}
	}

	@Override
	public void onListAdd(CtElement currentElement, CtRole role, List field,
			int index, CtElement newValue) {
		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
		if (newValue != null) {
			newValue.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
		}

		if (newValue instanceof CtType<?>) {
			currentElement.getFactory().Type().addToCache((CtType<?>) newValue);
		}
	}


	@Override
	public void onListDelete(CtElement currentElement, CtRole role, List field,
			Collection<? extends CtElement> oldValue) {
		if (currentElement != null) {

			oldValue.forEach( it -> {
				if (it instanceof CtType<?>) {
					currentElement.getFactory().Type().removeCachedType(((CtType) it).getQualifiedName());
				}
			});

			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}

		for (CtElement ctElement : oldValue) {
			onListDelete(currentElement, role, field, field.indexOf(ctElement), ctElement);
		}
	}

	@Override
	public void onListDelete(CtElement currentElement, CtRole role, List field,
			int index, CtElement oldValue) {
		if (currentElement != null) {
			if (oldValue instanceof CtType<?>) {
				currentElement.getFactory().Type().removeCachedType(((CtType<?>) oldValue).getQualifiedName());
			}
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}


	@Override
	public void onListDeleteAll(CtElement currentElement, CtRole role,
			List field, List oldValue) {
		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			for (Object elem : oldValue) {
				if (elem instanceof CtType<?>)
					currentElement.getFactory().Type().removeCachedType(((CtType<?>) elem).getQualifiedName());
			}
			updateParent(currentElement, currentElement, currentElement);
		}
	}


	@Override
	public <K, V> void onMapAdd(CtElement currentElement, CtRole role,
			Map<K, V> field, K key, CtElement newValue) {
		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
		if (newValue != null) {
			newValue.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
		}

		if (newValue instanceof CtType<?>) {
			currentElement.getFactory().Type().addToCache((CtType<?>) newValue);
		}

	}

	@Override
	public <K, V> void onMapDelete(CtElement currentElement, CtRole role, Map<K, V> field, K key,
			CtElement oldValue) {

		if (currentElement != null) {
			if (oldValue instanceof CtType<?>) {
				currentElement.getFactory().Type().removeCachedType(((CtType<?>) oldValue).getQualifiedName());
			}
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public <K, V> void onMapDeleteAll(CtElement currentElement, CtRole role,
			Map<K, V> field, Map<K, V> oldValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onSetAdd(CtElement currentElement, CtRole role, Set field,
			CtElement newValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}

		if (newValue != null) {
			newValue.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
		}
	}

	@Override
	public <T extends Enum> void onSetAdd(CtElement currentElement, CtRole role, Set field,
			T newValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field,
			CtElement oldValue) {

		if (currentElement != null) {
			if (oldValue instanceof CtType<?>) {
				currentElement.getFactory().Type().removeCachedType(((CtType<?>) oldValue).getQualifiedName());
			}
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field, Collection<ModifierKind> oldValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}

		for (ModifierKind modifierKind : oldValue) {
			onSetDelete(currentElement, role, field, modifierKind);
		}
	}

	@Override
	public void onSetDelete(CtElement currentElement, CtRole role, Set field,
			ModifierKind oldValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}

	@Override
	public void onSetDeleteAll(CtElement currentElement, CtRole role, Set field,
			Set oldValue) {

		if (currentElement != null) {
			currentElement.putMetadata(CtElementImpl.META_DIRTY_KEY, true);
			updateParent(currentElement, currentElement, currentElement);
		}
	}
}
