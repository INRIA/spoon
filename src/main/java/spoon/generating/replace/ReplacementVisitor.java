/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to replace an element by another one.
 *
 * This class is generated automatically by the processor {@link spoon.generating.ReplacementVisitorGenerator}.
 */
class ReplacementVisitor extends CtScanner {
	public static void replace(CtElement original, CtElement replace) {
		try {
			new ReplacementVisitor(original, replace).scan(original.getParent());
		} catch (SpoonException ignore) {
		}
	}

	private CtElement original;
	private CtElement replace;

	private ReplacementVisitor(CtElement original, CtElement replace) {
		this.original = original;
		this.replace = replace;
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
			if (replace != null) {
				map.put(key, (V) replace);
				replace.setParent(shouldBeDeleted.getParent());
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
			if (replace != null) {
				set.add((T) replace);
				replace.setParent(shouldBeDeleted.getParent());
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
			if (replace != null) {
				list.set(index, (T) replace);
				replace.setParent(shouldBeDeleted.getParent());
			} else {
				list.remove(index);
			}
			listener.set(list);
		}
	}

	private void replaceElementIfExist(CtElement candidate, ReplaceListener listener) {
		if (candidate == original) {
			listener.set(replace);
			if (replace != null) {
				replace.setParent(candidate.getParent());
			}
		}
	}
}
