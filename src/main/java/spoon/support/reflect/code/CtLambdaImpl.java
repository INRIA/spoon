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
import java.util.Set;
import java.util.function.Predicate;
import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.QualifiedNameBasedSortedSet;
import spoon.support.visitor.SignaturePrinter;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.EXPRESSION;
import static spoon.reflect.path.CtRole.NAME;
import static spoon.reflect.path.CtRole.PARAMETER;
import static spoon.reflect.path.CtRole.THROWN;




public class CtLambdaImpl<T> extends CtExpressionImpl<T> implements CtLambda<T> {
	@MetamodelPropertyField(role = NAME)
	String simpleName = "";
	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<T> expression;
	@MetamodelPropertyField(role = BODY)
	CtBlock<?> body;
	@MetamodelPropertyField(role = PARAMETER)
	List<CtParameter<?>> parameters = emptyList();
	@MetamodelPropertyField(role = THROWN)
	Set<CtTypeReference<? extends Throwable>> thrownTypes = emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLambda(this);
	}

	@Override
	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public CtLambdaImpl<T> setSimpleName(String simpleName) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NAME, simpleName, this.simpleName);
		this.simpleName = simpleName;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CtBlock<T> getBody() {
		return (CtBlock<T>) body;
	}

	@Override
	public CtLambdaImpl<T> setBody(CtStatement statement) {
		if (statement != null) {
			CtBlock<?> body = getFactory().Code().getOrCreateCtBlock(statement);
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, BODY, body, this.body);
			if (expression != null && body != null) {
				throw new SpoonException("A lambda can't have two bodys.");
			}
			if (body != null) {
				body.setParent(this);
			}
			this.body = body;
		} else {
			getFactory().getEnvironment().getModelChangeListener().onObjectDelete(this, BODY, this.body);
			this.body = null;
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> CtMethod<R> getOverriddenMethod() {
		//The type of this lambda expression. For example: `Consumer<Integer>`
		CtTypeReference<T> lambdaTypeRef = getType();
		if (lambdaTypeRef == null) {
			//it can be null in noclasspath mode, so we do not know which method is called, by lambda
			return null;
		}
		CtType<T> lambdaType = lambdaTypeRef.getTypeDeclaration();
		if (lambdaType.isInterface() == false) {
			throw new SpoonException("The lambda can be based on interface only. But type " + lambdaTypeRef.getQualifiedName() + " is not an interface");
		}
		Set<CtMethod<?>> lambdaTypeMethods = lambdaType.getAllMethods();
		CtMethod<?> lambdaExecutableMethod = null;
		if (lambdaTypeMethods.size() == 1) {
			//even the default method can be used, if it is the only one
			lambdaExecutableMethod = lambdaTypeMethods.iterator().next();
		} else {
			for (CtMethod<?> method : lambdaTypeMethods) {
				if (getFactory().Method().OBJECT_METHODS.stream().anyMatch(method::isOverriding)) {
					continue;
				}
				if (method.isDefaultMethod() || method.hasModifier(ModifierKind.PRIVATE) || method.hasModifier(ModifierKind.STATIC)) {
					continue;
				}
				if (lambdaExecutableMethod != null) {
					throw new SpoonException("The lambda can be based on interface, which has only one method. But " + lambdaTypeRef.getQualifiedName() + " has at least two: " + lambdaExecutableMethod.getSignature() + " and " + method.getSignature());
				}
				lambdaExecutableMethod = method;
			}
		}
		if (lambdaExecutableMethod == null) {
			throw new SpoonException("The lambda can be based on interface, which has one method. But " + lambdaTypeRef.getQualifiedName() + " has no one");
		}
		return (CtMethod<R>) lambdaExecutableMethod;
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return unmodifiableList(parameters);
	}

	@Override
	public CtLambdaImpl<T> setParameters(List<CtParameter<?>> params) {
		if (params == null || params.isEmpty()) {
			this.parameters = CtElementImpl.emptyList();
			return this;
		}
		if (this.parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			this.parameters = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, PARAMETER, this.parameters, new ArrayList<>(this.parameters));
		this.parameters.clear();
		for (CtParameter<?> p : params) {
			addParameter(p);
		}
		return this;
	}

	@Override
	public CtLambdaImpl<T> addParameter(CtParameter<?> parameter) {
		if (parameter == null) {
			return this;
		}
		if (parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			parameters = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		parameter.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, PARAMETER, this.parameters, parameter);
		parameters.add(parameter);
		return this;
	}

	@Override
	public boolean removeParameter(CtParameter<?> parameter) {
		if (parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, PARAMETER, parameters, parameters.indexOf(parameter), parameter);
		return parameters.remove(parameter);
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	@UnsettableProperty
	public CtLambdaImpl<T> setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		return this;
	}

	@Override
	public CtLambdaImpl<T> addThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (throwType == null) {
			return this;
		}
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			thrownTypes = new QualifiedNameBasedSortedSet<>();
		}
		throwType.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, THROWN, this.thrownTypes, throwType);
		thrownTypes.add(throwType);
		return this;
	}

	@Override
	public boolean removeThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(this, THROWN, thrownTypes, throwType);
		return thrownTypes.remove(throwType);
	}

	@Override
	public String getSignature() {
		final SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature();
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
	public CtLambdaImpl<T> setExpression(CtExpression<T> expression) {
		if (body != null && expression != null) {
			throw new SpoonException("A lambda can't have two bodies.");
		} else {
			if (expression != null) {
				expression.setParent(this);
			}
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
			this.expression = expression;
		}
		return this;
	}

	@Override
	public CtLambda<T> clone() {
		return (CtLambda<T>) super.clone();
	}
}
