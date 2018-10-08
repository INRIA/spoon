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
package spoon.support.reflect.code;


import java.util.ArrayList;
import java.util.List;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.ARGUMENT;
import static spoon.reflect.path.CtRole.EXECUTABLE_REF;
import static spoon.reflect.path.CtRole.LABEL;




public class CtConstructorCallImpl<T> extends CtTargetedExpressionImpl<T, CtExpression<?>> implements CtConstructorCall<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = ARGUMENT)
	List<CtExpression<?>> arguments = emptyList();
	@MetamodelPropertyField(role = EXECUTABLE_REF)
	CtExecutableReference<T> executable;
	@MetamodelPropertyField(role = LABEL)
	String label;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtConstructorCall(this);
	}

	@Override
	public List<CtExpression<?>> getArguments() {
		return arguments;
	}

	@Override
	public CtExecutableReference<T> getExecutable() {
		if (executable == null) {
			// default reference
			executable = getFactory().Core().createExecutableReference();
			executable.setParent(this);
		}
		return executable;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public CtConstructorCallImpl<T> insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
		return this;
	}

	@Override
	public CtConstructorCallImpl<T> insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
		return this;
	}

	@Override
	public CtConstructorCallImpl<T> insertAfter(CtStatementList statements) {
		CtStatementImpl.insertAfter(this, statements);
		return this;
	}

	@Override
	public CtConstructorCallImpl<T> insertBefore(CtStatementList statements) {
		CtStatementImpl.insertBefore(this, statements);
		return this;
	}

	@Override
	public CtConstructorCallImpl<T> setArguments(List<CtExpression<?>> arguments) {
		if (arguments == null || arguments.isEmpty()) {
			this.arguments = CtElementImpl.emptyList();
			return this;
		}
		if (this.arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			this.arguments = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, ARGUMENT, this.arguments, new ArrayList<>(this.arguments));
		this.arguments.clear();
		for (CtExpression<?> expr : arguments) {
			addArgument(expr);
		}
		return this;
	}

	private CtConstructorCallImpl<T> addArgument(int position, CtExpression<?> argument) {
		if (argument == null) {
			return this;
		}
		if (arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			arguments = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		argument.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, ARGUMENT, this.arguments, position, argument);
		arguments.add(position, argument);
		return this;
	}

	@Override
	public CtConstructorCallImpl<T> addArgument(CtExpression<?> argument) {
		return ((CtConstructorCallImpl<T>) (addArgument(arguments.size(), argument)));
	}

	@Override
	public void removeArgument(CtExpression<?> argument) {
		if (arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			return;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, ARGUMENT, arguments, arguments.indexOf(argument), argument);
		arguments.remove(argument);
	}

	@Override
	public CtConstructorCallImpl<T> setExecutable(CtExecutableReference<T> executable) {
		if (executable != null) {
			executable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXECUTABLE_REF, executable, this.executable);
		this.executable = executable;
		return this;
	}

	@Override
	public CtConstructorCallImpl<T> setLabel(String label) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, LABEL, label, this.label);
		this.label = label;
		return this;
	}

	@Override
	@DerivedProperty
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return getExecutable() == null ? CtElementImpl.<CtTypeReference<?>>emptyList() : getExecutable().getActualTypeArguments();
	}

	@Override
	@DerivedProperty
	public CtConstructorCallImpl<T> setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		if (getExecutable() != null) {
			getExecutable().setActualTypeArguments(actualTypeArguments);
		}
		return this;
	}

	@Override
	@DerivedProperty
	public CtConstructorCallImpl<T> addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (getExecutable() != null) {
			getExecutable().addActualTypeArgument(actualTypeArgument);
		}
		return this;
	}

	@Override
	@DerivedProperty
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (getExecutable() != null) {
			return getExecutable().removeActualTypeArgument(actualTypeArgument);
		}
		return false;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<T> getType() {
		return getExecutable() == null ? null : getExecutable().getType();
	}

	@Override
	@DerivedProperty
	public CtConstructorCallImpl<T> setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		if (getExecutable() != null) {
			getExecutable().setType(type);
		}
		return this;
	}

	@Override
	public CtConstructorCall<T> clone() {
		return (CtConstructorCall<T>) super.clone();
	}
}
