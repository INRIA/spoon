/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.pattern.node;

import java.util.Map;
import java.util.function.BiConsumer;

import spoon.pattern.Generator;
import spoon.pattern.ResultHolder;
import spoon.pattern.matcher.Quantifier;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;

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
		super();
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		if (iterableParameter == oldNode) {
			oldNode = newNode;
			return true;
		}
		if (iterableParameter.replaceNode(oldNode, newNode)) {
			return true;
		}
		if (nestedModel == oldNode) {
			nestedModel = newNode;
			return true;
		}
		if (nestedModel.replaceNode(oldNode, newNode)) {
			return true;
		}
		return false;
	}

	@Override
	public <T> void generateTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters) {
		for (Object parameterValue : generator.generateTargets(iterableParameter, parameters, Object.class)) {
			generator.generateTargets(nestedModel, result, parameters.putValueToCopy(localParameter.getName(), parameterValue));
		}
	}

	@Override
	public Quantifier getMatchingStrategy() {
		return iterableParameter.getMatchingStrategy();
	}

	@Override
	public TobeMatched matchAllWith(TobeMatched tobeMatched) {
		TobeMatched  localMatch = nestedModel.matchAllWith(tobeMatched.copyAndSetParams(tobeMatched.getParameters().createLocalParameterValueProvider()));
		if (localMatch == null) {
			//nested model did not matched.
			return null;
		}
		//it matched.
		ParameterValueProvider newParameters = tobeMatched.getParameters();
		//copy values of local parameters
		for (Map.Entry<String, Object> e : localMatch.getParameters().asLocalMap().entrySet()) {
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
				newParameters = newParameters.putValueToCopy(name, value);
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
	public boolean isMandatory(ParameterValueProvider parameters) {
		return iterableParameter.isMandatory(parameters);
	}

	@Override
	public boolean isTryNextMatch(ParameterValueProvider parameters) {
		return iterableParameter.isTryNextMatch(parameters);
	}

	@Override
	public <T> void generateInlineTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters) {
		Factory f = generator.getFactory();
		CtForEach forEach = f.Core().createForEach();
		forEach.setVariable(f.Code().createLocalVariable(f.Type().objectType(), localParameter.getName(), null));
		forEach.setExpression(generator.generateTarget(iterableParameter, parameters, CtExpression.class));
		CtBlock<?> body = f.createBlock();
		body.setStatements(generator.generateTargets(nestedModel, parameters, CtStatement.class));
		forEach.setBody(body);
		result.addResult((T) forEach);
	}
}
