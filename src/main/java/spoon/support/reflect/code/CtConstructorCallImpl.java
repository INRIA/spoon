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
package spoon.support.reflect.code;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CONSTRUCTOR_CALL_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtConstructorCallImpl<T> extends CtTargetedExpressionImpl<T, CtExpression<?>>
		implements CtConstructorCall<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.emptyList();
	List<CtExpression<?>> arguments = emptyList();
	CtExecutableReference<T> executable;
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
		return executable;
	}

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
	public void replace(CtElement element) {
		if (element instanceof CtStatementList) {
			CtStatementImpl.replace(this, (CtStatementList) element);
		} else {
			super.replace(element);
		}
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C setArguments(List<CtExpression<?>> arguments) {
		if (this.arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			this.arguments = new ArrayList<CtExpression<?>>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.arguments.clear();
		for (CtExpression<?> expr : arguments) {
			addArgument(expr);
		}
		return (C) this;
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C addArgument(CtExpression<?> argument) {
		if (arguments == CtElementImpl.<CtExpression<?>>emptyList()) {
			arguments = new ArrayList<CtExpression<?>>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		argument.setParent(this);
		arguments.add(argument);
		return (C) this;
	}

	@Override
	public void removeArgument(CtExpression<?> argument) {
		if (arguments != CtElementImpl.<CtExpression<?>>emptyList()) {
			arguments.remove(argument);
		}
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C setExecutable(CtExecutableReference<T> executable) {
		if (executable != null) {
			executable.setParent(this);
		}
		this.executable = executable;
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C setLabel(String label) {
		this.label = label;
		return (C) this;
	}

	@Override
	public void replace(CtStatement element) {
		replace((CtElement) element);
	}

	@Override
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return Collections.unmodifiableList(actualTypeArguments);
	}

	@Override
	public <T extends CtGenericElementReference> T setActualTypeArguments(List<CtTypeReference<?>> actualTypeArguments) {
		if (this.actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.actualTypeArguments = new ArrayList<CtTypeReference<?>>(
					CONSTRUCTOR_CALL_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.actualTypeArguments.clear();
		for (CtTypeReference<?> actualTypeArgument : actualTypeArguments) {
			addActualTypeArgument(actualTypeArgument);
		}
		return (T) this;
	}

	@Override
	public <T extends CtGenericElementReference> T addActualTypeArgument(
			CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			actualTypeArguments = new ArrayList<CtTypeReference<?>>(
					CONSTRUCTOR_CALL_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		actualTypeArgument.setParent(this);
		actualTypeArguments.add(actualTypeArgument);
		return (T) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return actualTypeArguments != CtElementImpl.<CtTypeReference<?>>emptyList()
				&& actualTypeArguments.remove(actualTypeArgument);
	}

	@Override
	public CtTypeReference<T> getType() {
		return getExecutable() == null ? null : getExecutable().getType();
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (getExecutable() != null) {
			getExecutable().setType(type);
		}
		return (C) this;
	}
}
