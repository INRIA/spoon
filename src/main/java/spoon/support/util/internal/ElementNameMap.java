/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal;

import static spoon.support.util.internal.ModelCollectionUtils.linkToParent;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.modelobs.FineModelChangeListener;

/**
 * This class is for <strong>internal use only</strong>.
 * <p><br>
 * An implementation of a {@link Map} currently used by packages to manage contained types and
 * sub-packages.
 * <br>>
 * This map is currently specialized to string keys to simplify the types a bit. Nothing
 * fundamentally requires Strings, and it might be changed in the future.
 * <br>
 * It assures:
 * <ul>
 *   <li>each inserted {@link CtElement} gets assigned correct parent</li>
 *   <li> each change is reported in {@link FineModelChangeListener}</li>
 *   <li>The {@link ElementNameMap#entrySet()} method returns elements in the order they were inserted</li>
 * </ul>
 * <br>
 */
public abstract class ElementNameMap<T extends CtElement> extends AbstractMap<String, T>
		implements Serializable {

	private static final long serialVersionUID = 2L;

	// This can not be a "Map" as this class is Serializable and therefore Sorald wants this field
	// to be serializable as well (Rule 1948).
	// It doesn't seem smart enough to realize it is final and only assigned to a Serializable Map
	// in the constructor.
	private final ConcurrentHashMap<String, InsertOrderWrapper<T>> map;

	private final AtomicInteger insertionNumber;

	/**
	 * Wrapper class that allows us to return entries in the order they were inserted.
	 */
	private static class InsertOrderWrapper<T extends Serializable> implements Serializable {
		private static final long serialVersionUID = 1L;

		final long insertionNumber;
		final T value;

		InsertOrderWrapper(T value, long insertionNumber) {
			this.value = value;
			this.insertionNumber = insertionNumber;
		}
	}


	protected ElementNameMap() {
		this.map = new ConcurrentHashMap<>();
		this.insertionNumber = new AtomicInteger();
	}

	protected abstract CtElement getOwner();

	protected abstract CtRole getRole();

	/**
	 * {@inheritDoc }
	 *
	 * @param key {@inheritDoc }
	 * @param e {@inheritDoc }
	 * @return null if the input was changed or an existing element was reused, the previous mapping
	 *     if the element is actually replaced.
	 */
	@Override
	public @Nullable T put(String key, T e) {
		if (e == null) {
			return null;
		}
		CtElement owner = getOwner();
		linkToParent(owner, e);
		getModelChangeListener().onMapAdd(owner, getRole(), map, key, e);

		// We make sure that then last added type is kept (and previous types overwritten) as client
		// code expects that
		long currentInsertNumber = insertionNumber.incrementAndGet();
		var wrapper = new InsertOrderWrapper<T>(e, currentInsertNumber);

		return valueOrNull(map.put(key, wrapper));
	}

	private @Nullable T valueOrNull(InsertOrderWrapper<T> wrapper) {
		return wrapper != null ? wrapper.value : null;
	}

	@Override
	public @Nullable T remove(Object key) {
		T removed = valueOrNull(map.remove(key));

		if (removed == null) {
			return null;
		}

		getModelChangeListener().onMapDelete(
				getOwner(),
				getRole(),
				map,
				(String) key,
				removed
		);

		return removed;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public void clear() {
		if (map.isEmpty()) {
			return;
		}
		// Only an approximation as the concurrent map is only weakly consistent
		var old = toInsertionOrderedMap();
		map.clear();
		var current = toInsertionOrderedMap();
		getModelChangeListener().onMapDeleteAll(
				getOwner(),
				getRole(),
				current,
				old
		);
	}

	private LinkedHashMap<String, T> toInsertionOrderedMap() {
		BinaryOperator<T> mergeFunction = (lhs, rhs) -> rhs;
		return entriesByInsertionOrder().collect(Collectors.toMap(
				Entry::getKey, Entry::getValue, mergeFunction, LinkedHashMap::new
		));
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public @Nullable T get(Object key) {
		InsertOrderWrapper<T> wrapper = map.get(key);
		if (wrapper == null) {
			return null;
		}
		return wrapper.value;
	}

	/**
	 * Updates the mapping for a single key from {@code oldKey} to {@code newKey} if present.
	 *
	 * @param oldKey the old key
	 * @param newKey the new key
	 */
	public void updateKey(String oldKey, String newKey) {
		InsertOrderWrapper<T> wrapper = map.remove(oldKey);
		if (wrapper != null) {
			map.put(newKey, wrapper);
		}
	}

	@Override
	public Set<Entry<String, T>> entrySet() {
		return entriesByInsertionOrder().collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private Stream<Entry<String, T>> entriesByInsertionOrder() {
		return map.entrySet().stream()
				.sorted(Comparator.comparing(entry -> entry.getValue().insertionNumber))
				.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().value));
	}

	private FineModelChangeListener getModelChangeListener() {
		return getOwner().getFactory().getEnvironment().getModelChangeListener();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ElementNameMap<?> that = (ElementNameMap<?>) o;
		return Objects.equals(map, that.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}
}
