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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import spoon.Metamodel;
import spoon.SpoonException;
import spoon.pattern.node.LiveNode;
import spoon.pattern.node.RootNode;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.PrinterHelper;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

/**
 */
public class PatternPrinter extends DefaultGenerator {

	private static final Factory DEFAULT_FACTORY = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
	static {
		DEFAULT_FACTORY.getEnvironment().setCommentEnabled(true);
	}

	private List<ParamOnElement> params = new ArrayList<>();

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
		int firstResultIdx = result.getResults().size();
		if (node instanceof LiveNode) {
			//this is a Live node. Do not generated nodes normally, but generate origin live statements
			((LiveNode) node).generateLiveTargets(this, result, parameters);
		} else {
			super.generateTargets(node, result, parameters);
		}
		T firstResult = getFirstResult(result, firstResultIdx);
		if (firstResult instanceof CtElement) {
			addParameterCommentTo((CtElement) firstResult, null);
		}
	}

	private boolean isCommentVisible(Object obj) {
		if (obj instanceof CtElement) {
			Metamodel.Type mmType = Metamodel.getMetamodelTypeByClass((Class) obj.getClass());
			Metamodel.Field mmCommentField = mmType.getField(CtRole.COMMENT);
			if (mmCommentField != null && mmCommentField.isDerived() == false) {
				return true;
			}
		}
		return false;
	}

	private <T> T getFirstResult(ResultHolder<T> result, int firstResultIdx) {
		List<T> results = result.getResults();
		if (firstResultIdx < results.size()) {
			return results.get(firstResultIdx);
		}
		return null;
	}

	@Override
	public <T> void getValueAs(ParameterInfo parameterInfo, ResultHolder<T> result, ParameterValueProvider parameters) {
		CtElement ele = (CtElement) generatePatternParameterElement(parameterInfo, result.getRequiredClass());
		addParameterCommentTo(ele, parameterInfo);
		result.addResult((T) ele);
	}

	private void addParameterCommentTo(CtElement ele, ParameterInfo parameterInfo) {
		if (parameterInfo != null) {
			params.add(new ParamOnElement((CtElement) ele, parameterInfo));
		}
		if (isCommentVisible(ele) && params.size() > 0) {
			ele.addComment(ele.getFactory().Code().createComment(getSubstitutionRequestsDescription(ele, params), CommentType.BLOCK));
			params.clear();
		}
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

	private static class ParamOnElement {
		final CtElement sourceElement;
		final ParameterInfo param;
		ParamOnElement(CtElement sourceElement, ParameterInfo param) {
			this.sourceElement = sourceElement;
			this.param = param;
		}

		@Override
		public String toString() {
			return sourceElement.getClass().getName() + ": ${" + param.getName() + "}";
		}
	}
	private String getSubstitutionRequestsDescription(CtElement ele, List<ParamOnElement> requestsOnPos) {
		//sort requestsOnPos by their path
		Map<String, ParamOnElement> reqByPath = new TreeMap<>();
		StringBuilder sb = new StringBuilder();
		for (ParamOnElement reqPos : requestsOnPos) {
			sb.setLength(0);
			appendPathIn(sb, reqPos.sourceElement, ele);
			String path = sb.toString();
			reqByPath.put(path, reqPos);
		}

		PrinterHelper printer = new PrinterHelper(getFactory().getEnvironment());
		//all comments in Spoon are using \n as separator
		printer.setLineSeparator("\n");
		printer.write(getElementTypeName(ele)).incTab();
		for (Map.Entry<String, ParamOnElement> e : reqByPath.entrySet()) {
			printer.writeln();
			printer.write(e.getKey()).write('/');
			printer.write(" <= ${").write(e.getValue().param.getName() + "}");
		}
		return printer.toString();
	}

	private boolean appendPathIn(StringBuilder sb, CtElement element, CtElement parent) {
		if (element != parent && element != null) {
			CtRole roleInParent = element.getRoleInParent();
			if (roleInParent == null) {
				return false;
			}
			if (appendPathIn(sb, element.getParent(), parent)) {
				sb.append("/").append(getElementTypeName(element.getParent()));
			}
			sb.append(".").append(roleInParent.getCamelCaseName());
			return true;
		}
		return false;
	};

	static String getElementTypeName(CtElement element) {
		String name = element.getClass().getSimpleName();
		if (name.endsWith("Impl")) {
			return name.substring(0, name.length() - 4);
		}
		return name;
	}
}
