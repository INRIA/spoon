/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

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
	public <C extends CtStatement> C insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatementList statements) {
		CtStatementImpl.insertAfter(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatementList statements) {
		CtStatementImpl.insertBefore(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C setArguments(List<CtExpression<?>> arguments) {
		if (arguments == null || arguments.isEmpty()) {
			this.arguments = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			this.arguments = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, ARGUMENT, this.arguments, new ArrayList<>(this.arguments));
		this.arguments.clear();
		for (CtExpression<?> expr : arguments) {
			addArgument(expr);
		}
		return (C) this;
	}

	private <C extends CtAbstractInvocation<T>> C addArgument(int position, CtExpression<?> argument) {
		if (argument == null) {
			return (C) this;
		}
		if (arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			arguments = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		argument.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, ARGUMENT, this.arguments, position, argument);
		arguments.add(position, argument);
		return (C) this;
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C addArgument(CtExpression<?> argument) {
		return addArgument(arguments.size(), argument);
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
	public <C extends CtAbstractInvocation<T>> C setExecutable(CtExecutableReference<T> executable) {
		if (executable != null) {
			executable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXECUTABLE_REF, executable, this.executable);
		this.executable = executable;
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C setLabel(String label) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, LABEL, label, this.label);
		this.label = label;
		return (C) this;
	}

	@Override
	@DerivedProperty
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return getExecutable() == null ? CtElementImpl.<CtTypeReference<?>>emptyList() : getExecutable().getActualTypeArguments();
	}

	@Override
	@DerivedProperty
	public <T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		if (getExecutable() != null) {
			getExecutable().setActualTypeArguments(actualTypeArguments);
		}
		return (T) this;
	}

	@Override
	@DerivedProperty
	public <T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (getExecutable() != null) {
			getExecutable().addActualTypeArgument(actualTypeArgument);
		}
		return (T) this;
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
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		if (getExecutable() != null) {
			getExecutable().setType(type);
		}
		return (C) this;
	}

	@Override
	public CtConstructorCall<T> clone() {
		return (CtConstructorCall<T>) super.clone();
	}
}
