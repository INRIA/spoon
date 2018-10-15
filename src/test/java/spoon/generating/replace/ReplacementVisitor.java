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
		try {
			new ReplacementVisitor(original, replace == null ? EMPTY : new CtElement[]{replace}).scan(original.getParent());
		} catch (InvalidReplaceException e) {
			throw e;
		}
	}
	public static <E extends CtElement> void replace(CtElement original, Collection<E> replaces) {
		try {
			new ReplacementVisitor(original, replaces.toArray(new CtElement[replaces.size()])).scan(original.getParent());
		} catch (InvalidReplaceException e) {
			throw e;
		}
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
