/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.support.visitor;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.util.RtHelper;

/**
 * This scanner replaces a program element with another one in a program element
 * hierarchy.
 */
public class ElementReplacer<T extends FactoryAccessor> extends CtScanner {

	T replacement;

	T toReplace;

	/**
	 * Creates a new replacer.
	 * 
	 * @param toReplace
	 *            the element to be replaced
	 * @param replacement
	 *            the replacing element
	 */
	public ElementReplacer(T toReplace, T replacement) {
		super();
		this.replacement = replacement;
		this.toReplace = toReplace;
	}

	private T getReplacement(Object parent) {
		T ret = replacement.getFactory().Core().clone(replacement);
		if (ret instanceof CtElement && parent instanceof CtElement) {
			((CtElement) ret).setParent((CtElement) parent);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private void replaceIn(Object parent) throws Exception {

		// List<Field> fields =
		// Arrays.asList(RtHelper.getAllFields(parent.getClass()));

		// Class current = parent.getClass();
		// for (Field f : current.getDeclaredFields())
		// fields.add(f);
		//
		// while (current.getSuperclass() != null) {
		// current = current.getSuperclass();
		// for (Field f : current.getDeclaredFields())
		// fields.add(f);
		// }

		// Field[] fields = parent.getClass().getFields();
		for (Field f : RtHelper.getAllFields(parent.getClass())) {
			f.setAccessible(true);
			Object tmp = f.get(parent);

			if (tmp != null) {
				if (tmp instanceof List) {
					List<T> lst = (List<T>) tmp;

					for (int i = 0; i < lst.size(); i++) {
						if (lst.get(i) != null && lst.get(i).equals(toReplace)) {
							lst.remove(i);
							if (replacement != null)
								lst.add(i, getReplacement(parent));
						}
					}
				} else if (tmp instanceof Collection) {
					Collection<T> collect = (Collection<T>) tmp;
					Object[] array = collect.toArray();
					for (Object obj : array) {
						if (obj.equals(toReplace)) {
							collect.remove(obj);
							collect.add(getReplacement(parent));
						}
					}
				} else if (tmp.equals(toReplace)) {
					f.set(parent, getReplacement(parent));
				}
			}
		}
	}

	/**
	 * Do the replacement of the given element if needed.
	 */
	@Override
	public void enter(CtElement e) {
		try {
			replaceIn(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		super.enter(e);
	}

	/**
	 * Do the replacement of the given element reference if needed.
	 */
	@Override
	public void enterReference(CtReference e) {
		try {
			replaceIn(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		super.enterReference(e);
	}

}
