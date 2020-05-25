/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import spoon.metamodel.Metamodel;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.MetamodelProperty;
import spoon.pattern.internal.node.ConstantNode;
import spoon.pattern.internal.node.ElementNode;
import spoon.pattern.internal.node.InlineNode;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern.internal.node.ParameterNode;
import spoon.pattern.internal.node.RootNode;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.PrinterHelper;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.util.ImmutableMap;

/**
 * Generates the source code corresponding to a Pattern's RootNode
 */
public class PatternPrinter extends DefaultGenerator {

	private static final Factory DEFAULT_FACTORY = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
	static {
		DEFAULT_FACTORY.getEnvironment().setCommentEnabled(true);
	}

	private List<ParamOnElement> params = new ArrayList<>();
	private boolean printParametersAsComments = true;

	public PatternPrinter() {
		super(DEFAULT_FACTORY, null);
	}

	public String printNode(RootNode node) {
		List<Object> generated = generateTargets(node, (ImmutableMap) null, null);
		StringBuilder sb = new StringBuilder();
		for (Object ele : generated) {
			sb.append(ele.toString()).append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	@Override
	public <T> void generateTargets(RootNode node, ResultHolder<T> result, ImmutableMap parameters) {
		int firstResultIdx = result.getResults().size();
		if (node instanceof InlineNode) {
			//this is an inline node. Does not generate nodes normally, but generates origin inline statements
			((InlineNode) node).generateInlineTargets(this, result, parameters);
		} else {
			super.generateTargets(node, result, parameters);
		}
		T firstResult = getFirstResult(result, firstResultIdx);
		if (firstResult instanceof CtElement) {
			if (node instanceof ElementNode) {
				ElementNode elementNode = (ElementNode) node;
				List<ParamOnElement> paramsOnElement = new ArrayList<>();
				for (Map.Entry<MetamodelProperty, RootNode> e : elementNode.getRoleToNode().entrySet()) {
					MetamodelProperty mmField = e.getKey();
					foreachNode(e.getValue(), attrNode -> {
						if (attrNode instanceof ConstantNode || attrNode instanceof ElementNode) {
							return;
						}
						//it is an attribute with an substitution
						//it will be added only if it is not already added linked to the CtElement
						paramsOnElement.add(new ParamOnElement((CtElement) firstResult, mmField.getRole(), attrNode));
					});
				}
				addParameterCommentTo((CtElement) firstResult, paramsOnElement.toArray(new ParamOnElement[0]));
			} else if (node instanceof ParameterNode) {
				addParameterCommentTo((CtElement) firstResult, new ParamOnElement((CtElement) firstResult, node));
			}
		}
	}

	private void foreachNode(RootNode rootNode, Consumer<RootNode> consumer) {
		if (rootNode instanceof ListOfNodes) {
			ListOfNodes list = (ListOfNodes) rootNode;
			for (RootNode node : list.getNodes()) {
				foreachNode(node, consumer);
			}
		} else {
			consumer.accept(rootNode);
		}
	}

	private boolean isCommentVisible(Object obj) {
		if (obj instanceof CtElement) {
			MetamodelConcept mmType = Metamodel.getInstance().getConcept((Class) obj.getClass());
			MetamodelProperty mmCommentField = mmType.getProperty(CtRole.COMMENT);
			return mmCommentField != null && mmCommentField.isDerived() == false;
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
	public <T> void getValueAs(ParameterInfo parameterInfo, ResultHolder<T> result, ImmutableMap parameters) {
		Object obj = generatePatternParameterElement(parameterInfo, result.getRequiredClass());
		if (obj != null) {
			result.addResult((T) obj);
		}
	}

	private void addParameterCommentTo(CtElement ele, ParamOnElement... paramsOnElement) {
		for (ParamOnElement paramOnElement : paramsOnElement) {
			if (isNodeContained(paramOnElement.node) == false) {
				params.add(paramOnElement);
			}
		}
		if (isPrintParametersAsComments() && isCommentVisible(ele) && !params.isEmpty()) {
			ele.addComment(ele.getFactory().Code().createComment(getSubstitutionRequestsDescription(ele, params), CommentType.BLOCK));
			params.clear();
		}
	}

	private boolean isNodeContained(RootNode node) {
		for (ParamOnElement paramOnElement : params) {
			if (paramOnElement.node == node) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates an element which will be printed in source code of pattern as marker of parameter
	 * @param parameterInfo describes a pattern parameter to be printed
	 * @param type class of the generated element
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
			if (type.isAssignableFrom(String.class)) {
				return (T) parameterInfo.getName();
			}
			if (type.isAssignableFrom(CtTypeReference.class)) {
				return (T) factory.Type().createReference(parameterInfo.getName());
			}
		}
		return null;
	}

	private static class ParamOnElement {
		final CtElement sourceElement;
		final RootNode node;
		final CtRole role;
		ParamOnElement(CtElement sourceElement, RootNode node) {
			this(sourceElement, null, node);
		}
		ParamOnElement(CtElement sourceElement, CtRole role, RootNode node) {
			this.sourceElement = sourceElement;
			this.role = role;
			this.node = node;
		}

		@Override
		public String toString() {
			if (role == null) {
				return sourceElement.getClass().getName() + ": ${" + node.toString() + "}";
			} else {
				return sourceElement.getClass().getName() + "/" + role + ": " + node.toString();
			}
		}
	}
	private String getSubstitutionRequestsDescription(CtElement ele, List<ParamOnElement> requestsOnPos) {
		//sort requestsOnPos by their path
		Map<String, ParamOnElement> reqByPath = new TreeMap<>();
		StringBuilder sb = new StringBuilder();
		for (ParamOnElement reqPos : requestsOnPos) {
			sb.setLength(0);
			appendPathIn(sb, reqPos.sourceElement, ele);
			if (reqPos.role != null) {
				sb.append("/").append(reqPos.role.getCamelCaseName());
			}
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
			printer.write(" <= ").write(e.getValue().node.toString());
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
	}

	static String getElementTypeName(CtElement element) {
		String name = element.getClass().getSimpleName();
		if (name.endsWith("Impl")) {
			return name.substring(0, name.length() - 4);
		}
		return name;
	}

	public PatternPrinter setPrintParametersAsComments(boolean printParametersAsComments) {
		this.printParametersAsComments = printParametersAsComments;
		return this;
	}

	public boolean isPrintParametersAsComments() {
		return printParametersAsComments;
	}
}
