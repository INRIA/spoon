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
package spoon.pattern;

import java.util.List;

import spoon.SpoonException;
import spoon.pattern.node.LiveNode;
import spoon.pattern.node.RootNode;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

/**
 */
public class PatternPrinter extends DefaultGenerator {

	private static final Factory DEFAULT_FACTORY = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());

	public PatternPrinter() {
		super(DEFAULT_FACTORY);
	}

	public String printNode(RootNode node) {
		List<CtElement> generated = generateTargets(node, (ParameterValueProvider) null, null);
		StringBuilder sb = new StringBuilder();
		for (CtElement ele : generated) {
			sb.append(ele.toString()).append('\n');
		}
		return sb.toString();
	}

	@Override
	public <T> void generateTargets(RootNode node, ResultHolder<T> result, ParameterValueProvider parameters) {
		if (node instanceof LiveNode) {
			//this is a Live node. Do not generated nodes normally, but generate origin live statements
			((LiveNode) node).generateLiveTargets(this, result, parameters);
			return;
		}
		super.generateTargets(node, result, parameters);
	}

	@Override
	public <T> void getValueAs(ParameterInfo parameterInfo, ResultHolder<T> result, ParameterValueProvider parameters) {
		result.addResult(generatePatternParameterElement(parameterInfo, result.getRequiredClass()));
	}

	/**
	 * Creates a element which will be printed in source code of pattern as marker of parameter
	 * @param factory a SpoonFactory which has to be used to create new elements
	 * @param potentialParameterMarker
	 * @param type
	 * @return dummy template element, which represents a template type in source of generated Pattern.
	 * Or null if potentialParameterMarker is not a marker of parameter
	 */
	private <T> T generatePatternParameterElement(ParameterInfo parameterInfo, Class<T> type) {
		if (type != null) {
			if (type.isAssignableFrom(CtInvocation.class)) {
				return (T) factory.createInvocation(factory.createThisAccess(factory.Type().objectType(), true), factory.createExecutableReference().setSimpleName(parameterInfo.getName()));
			}
			if (type.isAssignableFrom(CtLocalVariable.class)) {
				return (T) factory.createLocalVariable(factory.Type().objectType(), parameterInfo.getName(), null);
			}
		}
		throw new SpoonException("Pattern Parameter is on Unsupported place");
	}
}
