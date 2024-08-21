/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.replace;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.support.visitor.replace.InvalidReplaceException;
import spoon.support.visitor.replace.ReplaceListListener;
import spoon.support.visitor.replace.ReplaceListener;
import spoon.support.visitor.replace.ReplaceMapListener;
import spoon.support.visitor.replace.ReplaceSetListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to replace an element by another one.
 *
 * This class is generated automatically by the processor spoon.generating.ReplacementVisitorGenerator.
 */
class ReplacementVisitor extends CtScanner {
	public static void replace(CtElement original, CtElement replace) {
		new ReplacementVisitor(original, replace == null ? EMPTY : new CtElement[]{replace}).scan(original.getParent());
	}
	public static <E extends CtElement> void replace(CtElement original, Collection<E> replaces) {
			new ReplacementVisitor(original, replaces.toArray(new CtElement[0])).scan(original.getParent());
	}

	private CtElement original;
	private CtElement[] replace;

	private static final CtElement[] EMPTY = new CtElement[0];

	private ReplacementVisitor(CtElement original, CtElement... replace) {
		this.original = original;
		this.replace = replace == null ? EMPTY : replace;
	}

	private <K, V extends CtElement> void replaceInMapIfExist(Map<K, V> mapProtected, ReplaceMapListener listener) {
		Map<K, V> map = new HashMap<>(mapProtected);
		V shouldBeDeleted = null;
		K key = null;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue() == original) {
				shouldBeDeleted = entry.getValue();
				key = entry.getKey();
				break;
			}
		}
		if (shouldBeDeleted != null) {
			if (replace.length > 0) {
				if (replace.length > 1) {
					throw new InvalidReplaceException("Cannot replace single value by multiple values in " + listener.getClass().getSimpleName());
				}
				V val = (V) replace[0];
				if (val != null) {
					map.put(key, val);
					val.setParent(shouldBeDeleted.getParent());
				} else {
					map.remove(key);
				}
			} else {
				map.remove(key);
			}
			listener.set(map);
		}
	}

	private <T extends CtElement> void replaceInSetIfExist(Set<T> setProtected, ReplaceSetListener listener) {
		Set<T> set = new HashSet<>(setProtected);
		T shouldBeDeleted = null;
		for (T element : set) {
			if (element == original) {
				shouldBeDeleted = element;
				break;
			}
		}
		if (shouldBeDeleted != null) {
			set.remove(shouldBeDeleted);
			for (CtElement ele : replace) {
				if (ele != null) {
					set.add((T) ele);
					ele.setParent(shouldBeDeleted.getParent());
				}
			}
			listener.set(set);
		}
	}

	private <T extends CtElement> void replaceInListIfExist(List<T> listProtected, ReplaceListListener listener) {
		List<T> list = new ArrayList<>(listProtected);
		T shouldBeDeleted = null;
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == original) {
				index = i;
				shouldBeDeleted = list.get(i);
				break;
			}
		}
		if (shouldBeDeleted != null) {
			list.remove(index);
			if (replace.length > 0) {
				for (CtElement aReplace : replace) {
					T ele = (T) aReplace;
					if (ele != null) {
						list.add(index, ele);
						ele.setParent(shouldBeDeleted.getParent());
						index = index + 1;
					}
				}
			}
			listener.set(list);
		}
	}

	private void replaceElementIfExist(CtElement candidate, ReplaceListener listener) {
		if (candidate == original) {
			CtElement val = null;
			if (replace.length > 0) {
				if (replace.length > 1) {
					throw new InvalidReplaceException("Cannot replace single value by multiple values in " + listener.getClass().getSimpleName());
				}
				val = replace[0];
			}
			if (val != null) {
				val.setParent(candidate.getParent());
			}
			listener.set(val);
		}
	}
}
