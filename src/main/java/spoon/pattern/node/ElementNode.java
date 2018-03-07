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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import spoon.Metamodel;
import spoon.SpoonException;
import spoon.pattern.ResultHolder;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;

import static spoon.pattern.matcher.TobeMatched.getMatchedParameters;

/**
 * Generates/Matches a copy of a single CtElement AST node with all it's children (whole AST tree of the root CtElement)
 */
public class ElementNode extends AbstractPrimitiveMatcher {

	private Metamodel.Type elementType;
	private Map<Metamodel.Field, Node> attributeSubstititionRequests = new HashMap<>();

	public ElementNode(Metamodel.Type elementType) {
		super();
		this.elementType = elementType;
	}

	@Override
	public boolean replaceNode(Node oldNode, Node newNode) {
		for (Map.Entry<Metamodel.Field, Node> e : attributeSubstititionRequests.entrySet()) {
			Node node = e.getValue();
			if (node == oldNode) {
				e.setValue(newNode);
				return true;
			}
			if (node.replaceNode(oldNode, newNode)) {
				return true;
			}
		}
		return false;
	}

	public Map<Metamodel.Field, Node> getAttributeSubstititionRequests() {
		return attributeSubstititionRequests == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributeSubstititionRequests);
	}

	public Node getAttributeSubstititionRequest(CtRole attributeRole) {
		return attributeSubstititionRequests.get(getFieldOfRole(attributeRole));
	}

	public Node setNodeOfRole(CtRole role, Node newAttrNode) {
		return attributeSubstititionRequests.put(getFieldOfRole(role), newAttrNode);
	}

	private Metamodel.Field getFieldOfRole(CtRole role) {
		Metamodel.Field mmField = elementType.getField(role);
		if (mmField == null) {
			throw new SpoonException("CtRole." + role.name() + " isn't available for " + elementType);
		}
		if (mmField.isDerived()) {
			throw new SpoonException("CtRole." + role.name() + " is derived in " + elementType + " so it can't be used for matching or generating");
		}
		return mmField;
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer) {
		if (attributeSubstititionRequests != null) {
			for (Node node : attributeSubstititionRequests.values()) {
				node.forEachParameterInfo(consumer);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> void generateTargets(Factory factory, ResultHolder<U> result, ParameterValueProvider parameters) {
		//TODO implement create on Metamodel.Type
		CtElement clone = factory.Core().create(elementType.getModelInterface());
		generateSingleNodeAttributes(clone, parameters);
		result.addResult((U) clone);
	}

	protected void generateSingleNodeAttributes(CtElement clone, ParameterValueProvider parameters) {
		for (Map.Entry<Metamodel.Field, Node> e : getAttributeSubstititionRequests().entrySet()) {
			Metamodel.Field mmField = e.getKey();
			switch (mmField.getContainerKind()) {
			case SINGLE:
				mmField.setValue(clone, e.getValue().generateTarget(clone.getFactory(), parameters, mmField.getValueClass()));
				break;
			case LIST:
				mmField.setValue(clone, e.getValue().generateTargets(clone.getFactory(), parameters, mmField.getValueClass()));
				break;
			case SET:
				mmField.setValue(clone, new LinkedHashSet<>(e.getValue().generateTargets(clone.getFactory(), parameters, mmField.getValueClass())));
				break;
			case MAP:
				mmField.setValue(clone, entriesToMap(e.getValue().generateTargets(clone.getFactory(), parameters, Map.Entry.class)));
				break;
			}
		}
	}

	private <T> Map<String, T> entriesToMap(List<Map.Entry> entries) {
		Map<String, T> map = new LinkedHashMap<>(entries.size());
		for (Map.Entry<String, T> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	@Override
	public ParameterValueProvider matchTarget(Object target, ParameterValueProvider parameters) {
		if (target == null) {
			return null;
		}
		if (target.getClass() != elementType.getModelClass()) {
			return null;
		}

		//it is spoon element, it matches if to be matched attributes matches
		//to be matched attributes must be same or substituted
		//iterate over all attributes of to be matched class
		for (Map.Entry<Metamodel.Field, Node> e : attributeSubstititionRequests.entrySet()) {
			parameters = matchesRole(parameters, (CtElement) target, e.getKey(), e.getValue());
			if (parameters == null) {
				return null;
			}
		}
		return parameters;
	}

	protected ParameterValueProvider matchesRole(ParameterValueProvider parameters, CtElement target, Metamodel.Field mmField, Node attrNode) {
		TobeMatched tobeMatched;
		if (attrNode instanceof ParameterNode) {
			//whole attribute value (whole List/Set/Map) has to be stored in parameter
			tobeMatched = TobeMatched.create(parameters, ContainerKind.SINGLE, mmField.getValue(target));
		} else {
			//each item of attribute value (item of List/Set/Map) has to be matched individually
			tobeMatched = TobeMatched.create(parameters, mmField.getContainerKind(), mmField.getValue(target));
		}
		return getMatchedParameters(attrNode.matchTargets(tobeMatched, Node.MATCH_ALL));
	}

//	@Override
//	public String toString() {
//		PrinterHelper printer = new PrinterHelper(getTemplateNode().getFactory().getEnvironment());
//		printer.write(NodeAttributeSubstitutionRequest.getElementTypeName(getTemplateNode().getParent())).writeln().incTab();
//		appendDescription(printer);
//		return printer.toString();
//	}
//
//	public void appendDescription(PrinterHelper printer) {
//		if (attributeSubstititionRequests == null || attributeSubstititionRequests.values().isEmpty()) {
//			printer.write("** no attribute substitution **");
//		} else {
//			boolean multipleAttrs = attributeSubstititionRequests.size() > 1;
//			if (multipleAttrs) {
//				printer.incTab();
//			}
//			for (Node node : attributeSubstititionRequests.values()) {
//				if (multipleAttrs) {
//					printer.writeln();
//				}
//				printer.write(node.toString());
//			}
//			if (multipleAttrs) {
//				printer.decTab();
//			}
//		}
//	}
}
