package spoon.support.reflect.code;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtConstructorCallImpl<T> extends CtTargetedExpressionImpl<T, CtExpression<?>> implements CtConstructorCall<T> {
	private static final long serialVersionUID = 1L;

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
	public void insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
	}

	@Override
	public void insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
	}

	@Override
	public void insertAfter(CtStatementList statements) {
		CtStatementImpl.insertAfter(this, statements);
	}

	@Override
	public void insertBefore(CtStatementList statements) {
		CtStatementImpl.insertBefore(this, statements);
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
	public void setArguments(List<CtExpression<?>> arguments) {
		this.arguments.clear();
		for (CtExpression<?> expr: arguments) {
			addArgument(expr);
		}
	}

	@Override
	public void addArgument(CtExpression<?> argument) {
		if (arguments == CtElementImpl.<CtExpression<?>> EMPTY_LIST()) {
			arguments = new ArrayList<CtExpression<?>>(
					PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		argument.setParent(this);
		arguments.add(argument);
	}

	@Override
	public void removeArgument(CtExpression<?> argument) {
		if (arguments != CtElementImpl.<CtExpression<?>> EMPTY_LIST()) {
			arguments.remove(argument);
		}
	}

	@Override
	public void setExecutable(CtExecutableReference<T> executable) {
		this.executable = executable;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void replace(CtStatement element) {
		replace((CtElement)element);
	}
}
