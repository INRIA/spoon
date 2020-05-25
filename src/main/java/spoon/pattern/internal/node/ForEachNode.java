/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.pattern.Quantifier;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;
import spoon.support.util.ImmutableMap;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Pattern node of multiple occurrences of the same model, just with different parameters.
 * Example with three occurrences of model `System.out.println(_x_)`, with parameter `_x_`
 * <pre><code>
 * System.out.println("a")
 * System.out.println("b")
 * System.out.println(getStringOf(p1, p2))
 * </code></pre>
 * where parameter values are _x_ = ["a", "b", getStringOf(p1, p2)]
 */
public class ForEachNode extends AbstractRepeatableMatcher implements InlineNode {

	private PrimitiveMatcher iterableParameter;
	private RootNode nestedModel;
	private ParameterInfo localParameter;

	public ForEachNode() {
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		if (iterableParameter == oldNode) {
			//before defined iterable parameter has to be replaced by another iterable parameter
			//Maybe it makes no sense, because
			//1) the iterable parameter has to be defined first
			//2) then ForEachNode can be created for that
			//3) then this method might be called to replace iterable parameter again
			//... but does that use case makes sense? Probably not.
			iterableParameter = (PrimitiveMatcher) newNode;
			return true;
		}
		if (iterableParameter.replaceNode(oldNode, newNode)) {
			return true;
		}
		if (nestedModel == oldNode) {
			nestedModel = newNode;
			return true;
		}
		return nestedModel.replaceNode(oldNode, newNode);
	}

	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		for (Object parameterValue : generator.generateTargets(iterableParameter, parameters, Object.class)) {
			generator.generateTargets(nestedModel, result, parameters.putValue(localParameter.getName(), parameterValue));
		}
	}

	@Override
	public Quantifier getMatchingStrategy() {
		return iterableParameter.getMatchingStrategy();
	}

	@Override
	public TobeMatched matchAllWith(TobeMatched tobeMatched) {
		TobeMatched  localMatch = nestedModel.matchAllWith(tobeMatched.copyAndSetParams(tobeMatched.getParameters().checkpoint()));
		if (localMatch == null) {
			//nested model did not match.
			return null;
		}
		//it matched.
		ImmutableMap newParameters = tobeMatched.getParameters();
		//copy values of local parameters
		for (Map.Entry<String, Object> e : localMatch.getParameters().getModifiedValues().entrySet()) {
			String name = e.getKey();
			Object value = e.getValue();
			if (name.equals(localParameter.getName())) {
				//value of local parameter has to be added to iterableParameter
				newParameters = iterableParameter.matchTarget(value, newParameters);
				if (newParameters == null) {
					//new value did not passed the iterableParameter matcher
					//do not apply newParameters, which matched only partially, to parameters.
					return null;
				}
			} else {
				//it is new global parameter value. Just set it
				newParameters = newParameters.putValue(name, value);
			}
		}
		//all local parameters were applied to newParameters. We can use newParameters as result of this iteration for next iteration
		return localMatch.copyAndSetParams(newParameters);
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		iterableParameter.forEachParameterInfo(consumer);
		consumer.accept(localParameter, this);
	}

	public void setNestedModel(RootNode valueResolver) {
		this.nestedModel = valueResolver;
	}

	public void setIterableParameter(PrimitiveMatcher substRequestOfIterable) {
		this.iterableParameter = substRequestOfIterable;
	}

	public void setLocalParameter(ParameterInfo parameterInfo) {
		this.localParameter = parameterInfo;
	}

	@Override
	public boolean isRepeatable() {
		return iterableParameter.isRepeatable();
	}

	@Override
	public boolean isMandatory(ImmutableMap parameters) {
		return iterableParameter.isMandatory(parameters);
	}

	@Override
	public boolean isTryNextMatch(ImmutableMap parameters) {
		return iterableParameter.isTryNextMatch(parameters);
	}

	@Override
	public <T> void generateInlineTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		Factory f = generator.getFactory();
		CtForEach forEach = f.Core().createForEach();
		forEach.setVariable(f.Code().createLocalVariable(f.Type().objectType(), localParameter.getName(), null));
		forEach.setExpression(generator.generateSingleTarget(iterableParameter, parameters, CtExpression.class));
		CtBlock<?> body = f.createBlock();
		body.setStatements(generator.generateTargets(nestedModel, parameters, CtStatement.class));
		forEach.setBody(body);
		result.addResult((T) forEach);
	}
}
