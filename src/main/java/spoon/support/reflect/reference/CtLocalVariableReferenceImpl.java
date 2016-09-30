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
package spoon.support.reflect.reference;

import spoon.SpoonException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.List;

/**
 * An implementation for {@link CtLocalVariableReference}.
 */
public class CtLocalVariableReferenceImpl<T>
		extends CtVariableReferenceImpl<T> implements CtLocalVariableReference<T> {

	/**
	 * Id for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public CtLocalVariableReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLocalVariableReference(this);
	}

	@Override
	public CtLocalVariable<T> getDeclaration() {
		// without factory, we are not able to filter for local variables
		final Factory factory = getFactory();
		if (factory == null) {
			return null;
		}
		final SimpleNameFilter filter = new SimpleNameFilter(factory);

		// Successively iterate through all parents of this reference and
		// return first result (which must be the closest declaration
		// respecting current visible scope)
		try {
			CtElement parent = getParent();
			while (parent != null) {
				final List<CtLocalVariable<T>> localVariables =
						parent.getElements(filter);
				if (localVariables.size() > 1) {
					// we are in big trouble
					throw new SpoonException(String.format(
							"found more than one declaration for '%s' in visible scope",
							getSimpleName()));
				} else if (localVariables.size() == 1) {
					return localVariables.get(0);
				}
				parent = parent.getParent();
			}
		} catch (final ParentNotInitializedException e) {
			// handle this case as 'not found'.
		}
		return null;
	}

	@Override
	public CtLocalVariableReference<T> clone() {
		return (CtLocalVariableReference<T>) super.clone();
	}

	/**
	 * A {@link spoon.reflect.visitor.Filter} that filters all
	 * {@link CtLocalVariable}s with simple name equals to
	 * {@link #getSimpleName()}.
	 */
	private final class SimpleNameFilter
			extends AbstractFilter<CtLocalVariable<T>> {

		@SuppressWarnings("unchecked")
		SimpleNameFilter(final Factory pFactory) {
			super((Class<CtLocalVariable<T>>)
					pFactory.Core().createLocalVariable().getClass());
		}

		@Override
		public boolean matches(final CtLocalVariable<T> element) {
			return element.getSimpleName().equals(getSimpleName());
		}
	}
}
