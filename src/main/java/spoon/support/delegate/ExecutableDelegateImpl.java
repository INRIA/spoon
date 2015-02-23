package spoon.support.delegate;

import spoon.delegate.ExecutableDelegate;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ExecutableDelegateImpl<R> implements ExecutableDelegate<R> {
	CtBlock<?> body;

	List<CtParameter<?>> parameters = CtElementImpl.EMPTY_LIST();

	Set<CtTypeReference<? extends Throwable>> thrownTypes = CtElementImpl.EMPTY_SET();

	@Override
	@SuppressWarnings("unchecked")
	public <B extends R> CtBlock<B> getBody() {
		return (CtBlock<B>) body;
	}

	@Override
	public <B extends R> void setBody(CtBlock<B> body) {
		this.body = body;
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return parameters;
	}

	@Override
	public void setParameters(List<CtParameter<?>> parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean addParameter(CtParameter<?> parameter) {
		if (parameters == CtElementImpl.<CtParameter<?>>EMPTY_LIST()) {
			parameters = new ArrayList<CtParameter<?>>();
		}
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
}
