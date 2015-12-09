/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.declaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtInterfaceImpl<T> extends CtTypeImpl<T> implements CtInterface<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtInterface(this);
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInterface() {
		return true;
	}

	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		List<CtExecutableReference<?>> l =
				new ArrayList<CtExecutableReference<?>>(super.getDeclaredExecutables());
		for (CtTypeReference<?> sup : getSuperInterfaces()) {
			l.addAll(sup.getAllExecutables());
		}
		return Collections.unmodifiableCollection(l);
	}

}
