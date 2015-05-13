package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CtLambdaImpl<T> extends CtExpressionImpl<T> implements CtLambda<T> {
	String simpleName;
	CtExpression<T> expression;
	CtBlock<?> body;
	List<CtParameter<?>> parameters = EMPTY_LIST();
	Set<CtTypeReference<? extends Throwable>> thrownTypes = EMPTY_SET();

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
	@SuppressWarnings("unchecked")
	public <B extends T> CtBlock<B> getBody() {
		return (CtBlock<B>) body;
	}

	@Override
	public <B extends T> void setBody(CtBlock<B> body) {
		if (expression != null) {
			throw new SpoonException("A lambda can't have two bodys.");
		}
		body.setParent(this);
		this.body = body;
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public void setParameters(List<CtParameter<?>> params) {
		this.parameters.clear();
		for (CtParameter p : params) {
			addParameter(p);
		}
	}

	@Override
	public boolean addParameter(CtParameter<?> parameter) {
		if (parameters == CtElementImpl.<CtParameter<?>>EMPTY_LIST()) {
			parameters = new ArrayList<CtParameter<?>>();
		}
		parameter.setParent(this);
		return parameters.add(parameter);
	}

	@Override
	public boolean removeParameter(CtParameter<?> parameter) {
		return parameters.remove(parameter);
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	public void setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		this.thrownTypes = thrownTypes;
	}

	@Override
	public boolean addThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>EMPTY_SET()) {
			thrownTypes = new TreeSet<CtTypeReference<? extends Throwable>>();
		}
		return thrownTypes.add(throwType);
	}

	@Override
	public boolean removeThrownType(CtTypeReference<? extends Throwable> throwType) {
		return thrownTypes.remove(throwType);
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
		if (body != null) {
			throw new SpoonException("A lambda can't have two bodys.");
		}
		expression.setParent(this);
		this.expression = expression;
	}
}
