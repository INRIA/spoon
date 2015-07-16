package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.CONSTRUCTOR_CALL_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtConstructorCallImpl<T> extends CtTargetedExpressionImpl<T, CtExpression<?>>
		implements CtConstructorCall<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.EMPTY_LIST();
	List<CtExpression<?>> arguments = EMPTY_LIST();
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
		this.arguments.clear();
		for (CtExpression<?> expr: arguments) {
			addArgument(expr);
		}
		return (C) this;
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C addArgument(CtExpression<?> argument) {
		if (arguments == CtElementImpl.<CtExpression<?>> EMPTY_LIST()) {
			arguments = new ArrayList<CtExpression<?>>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		argument.setParent(this);
		arguments.add(argument);
		return (C) this;
	}

	@Override
	public void removeArgument(CtExpression<?> argument) {
		if (arguments != CtElementImpl.<CtExpression<?>> EMPTY_LIST()) {
			arguments.remove(argument);
		}
	}

	@Override
	public <C extends CtAbstractInvocation<T>> C setExecutable(CtExecutableReference<T> executable) {
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
		replace((CtElement)element);
	}

	@Override
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return Collections.unmodifiableList(actualTypeArguments);
	}

	@Override
	public <T extends CtGenericElementReference> T setActualTypeArguments(
			List<CtTypeReference<?>> actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
		return (T) this;
	}

	@Override
	public <T extends CtGenericElementReference> T addActualTypeArgument(
			CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>EMPTY_LIST()) {
			actualTypeArguments = new ArrayList<CtTypeReference<?>>(CONSTRUCTOR_CALL_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		actualTypeArguments.add(actualTypeArgument);
		return (T) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return actualTypeArguments != CtElementImpl.<CtTypeReference<?>>EMPTY_LIST()
				&& actualTypeArguments.remove(actualTypeArgument);
	}
}
