package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.delegate.ExecutableDelegate;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.delegate.ExecutableDelegateImpl;

import java.util.List;
import java.util.Set;

public class CtLambdaImpl<T> extends CtExpressionImpl<T> implements CtLambda<T> {
	String simpleName;
	ExecutableDelegate<T> executableDelegate = new ExecutableDelegateImpl<T>();
	CtExpression<T> expression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLambda(this);
	}

	@Override
	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	@Override
	public <B extends T> CtBlock<B> getBody() {
		return executableDelegate.getBody();
	}

	@Override
	public <B extends T> void setBody(CtBlock<B> body) {
		if (expression != null) {
			throw new SpoonException("A lambda can't have two bodys.");
		}
		executableDelegate.setBody(body);
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return executableDelegate.getParameters();
	}

	@Override
	public void setParameters(List<CtParameter<?>> parameters) {
		executableDelegate.setParameters(parameters);
	}

	@Override
	public boolean addParameter(CtParameter<?> parameter) {
		return executableDelegate.addParameter(parameter);
	}

	@Override
	public boolean removeParameter(CtParameter<?> parameter) {
		return executableDelegate.removeParameter(parameter);
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return executableDelegate.getThrownTypes();
	}

	@Override
	public void setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		executableDelegate.setThrownTypes(thrownTypes);
	}

	@Override
	public boolean addThrownType(CtTypeReference<? extends Throwable> throwType) {
		return executableDelegate.addThrownType(throwType);
	}

	@Override
	public boolean removeThrownType(CtTypeReference<? extends Throwable> throwType) {
		return executableDelegate.removeThrownType(throwType);
	}

	@Override
	public CtExecutableReference<T> getReference() {
		return getFactory().Executable().createReference(this);
	}

	@Override
	public CtExpression<T> getExpression() {
		return expression;
	}

	@Override
	public void setExpression(CtExpression<T> expression) {
		if (executableDelegate.getBody() != null) {
			throw new SpoonException("A lambda can't have two bodys.");
		}
		this.expression = expression;
	}
}
