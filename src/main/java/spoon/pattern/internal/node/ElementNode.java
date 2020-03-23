/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.SpoonException;
import spoon.metamodel.Metamodel;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.MetamodelProperty;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.matcher.Matchers;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.support.util.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static spoon.pattern.internal.matcher.TobeMatched.getMatchedParameters;

/**
 * Generates/Matches a copy of a single CtElement AST node with all it's children (whole AST tree of the root CtElement)
 */
public class ElementNode extends AbstractPrimitiveMatcher {

	/**
	 * Creates an implicit {@link ElementNode}, which contains all non derived attributes of `element` and all it's children
	 * @param element source element, which is used to initialize {@link ElementNode}
	 * @param patternElementToSubstRequests the {@link Map}, which will receive mapping between `element` and it's children
	 * and newly created tree of {@link ElementNode}s
	 * @return a tree of {@link ElementNode}s, which reflects tree of `element`
	 */
	public static ElementNode create(CtElement element, Map<CtElement, RootNode> patternElementToSubstRequests) {
		MetamodelConcept mmConcept = Metamodel.getInstance().getConcept(element.getClass());
		ElementNode elementNode = new ElementNode(mmConcept, element);
		if (patternElementToSubstRequests.put(element, elementNode) != null) {
			throw new SpoonException("Each pattern element can have only one implicit Node.");
		}
		//iterate over all attributes of that element
		for (MetamodelProperty  mmField : mmConcept.getProperties()) {
			if (mmField.isDerived()) {
				//skip derived fields, they are not relevant for matching or generating
				continue;
			}
			elementNode.setNodeOfRole(mmField.getRole(), create(mmField.getContainerKind(), mmField.getValue(element), patternElementToSubstRequests));
		}
		return elementNode;
	}

	/**
	 * Same like {@link #create(CtElement, Map)} but with {@link List} of elements or primitive objects
	 *
	 * @param objects List of objects which has to be transformed to nodes
	 * @param patternElementToSubstRequests mapping between {@link CtElement} from `objects` to created `node`
	 * @return a list of trees of nodes, which reflects list of `objects`
	 */
	public static ListOfNodes create(List<?> objects, Map<CtElement, RootNode> patternElementToSubstRequests) {
		if (objects == null) {
			objects = Collections.emptyList();
		}
		return listOfNodesToNode(objects.stream().map(i -> create(i, patternElementToSubstRequests)).collect(Collectors.toList()));
	}

	/**
	 * Same like {@link #create(CtElement, Map)} but with {@link Set} of elements or primitive objects
	 *
	 * @param templates Set of objects which has to be transformed to nodes
	 * @param patternElementToSubstRequests mapping between {@link CtElement} from `templates` to created `node`
	 * @return a list of trees of nodes, which reflects Set of `templates`
	 */
	public static ListOfNodes create(Set<?> templates, Map<CtElement, RootNode> patternElementToSubstRequests) {
		if (templates == null) {
			templates = Collections.emptySet();
		}
		//collect plain template nodes without any substitution request as List, because Spoon Sets have predictable order.
		List<RootNode> constantMatchers = new ArrayList<>(templates.size());
		//collect template nodes with a substitution request
		List<RootNode> variableMatchers = new ArrayList<>();
		for (Object template : templates) {
			RootNode matcher = create(template, patternElementToSubstRequests);
			if (matcher instanceof ElementNode) {
				constantMatchers.add(matcher);
			} else {
				variableMatchers.add(matcher);
			}
		}
		//first match the Set with constant matchers and then with variable matchers
		constantMatchers.addAll(variableMatchers);
		return listOfNodesToNode(constantMatchers);
	}

	/**
	 * Same like {@link #create(CtElement, Map)} but with {@link Map} of String to elements or primitive objects
	 *
	 * @param map Map of objects which has to be transformed to nodes
	 * @param patternElementToSubstRequests mapping between {@link CtElement} from `map` to created `node`
	 * @return a list of {@link MapEntryNode}s, which reflects `map`
	 */
	public static ListOfNodes create(Map<String, ?> map, Map<CtElement, RootNode> patternElementToSubstRequests) {
		if (map == null) {
			map = Collections.emptyMap();
		}
		//collect Entries with constant matcher keys
		List<MapEntryNode> constantMatchers = new ArrayList<>(map.size());
		//collect Entries with variable matcher keys
		List<MapEntryNode> variableMatchers = new ArrayList<>();
		Matchers last = null;
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			MapEntryNode mem = new MapEntryNode(
					create(entry.getKey(), patternElementToSubstRequests),
					create(entry.getValue(), patternElementToSubstRequests));
			if (mem.getKey() == entry.getKey()) {
				constantMatchers.add(mem);
			} else {
				variableMatchers.add(mem);
			}
		}
		//first match the Map.Entries with constant matchers and then with variable matchers
		constantMatchers.addAll(variableMatchers);
		return listOfNodesToNode(constantMatchers);
	}

	private static RootNode create(Object object, Map<CtElement, RootNode> patternElementToSubstRequests) {
		if (object instanceof CtElement) {
			return create((CtElement) object, patternElementToSubstRequests);
		}
		return new ConstantNode<>(object);
	}

	private static RootNode create(ContainerKind containerKind, Object templates, Map<CtElement, RootNode> patternElementToSubstRequests) {
		switch (containerKind) {
		case LIST:
			return create((List) templates, patternElementToSubstRequests);
		case SET:
			return create((Set) templates, patternElementToSubstRequests);
		case MAP:
			return create((Map) templates, patternElementToSubstRequests);
		case SINGLE:
			return create(templates, patternElementToSubstRequests);
		}
		throw new SpoonException("Unexpected RoleHandler containerKind: " + containerKind);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ListOfNodes listOfNodesToNode(List<? extends RootNode> nodes) {
		return new ListOfNodes((List) nodes);
	}

	private CtElement templateElement;
	private MetamodelConcept elementType;
	private Map<MetamodelProperty, RootNode> roleToNode = new HashMap<>();

	/**
	 * @param elementType The type of Spoon node which has to be generated/matched by this {@link ElementNode}
	 * @param templateElement - optional ref to template element which was used to created this {@link ElementNode}.
	 * 	It is used e.g. to generate generatedBy comment
	 */
	public ElementNode(MetamodelConcept elementType, CtElement templateElement) {
		this.elementType = elementType;
		this.templateElement = templateElement;
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		for (Map.Entry<MetamodelProperty, RootNode> e : roleToNode.entrySet()) {
			RootNode node = e.getValue();
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

	public Map<MetamodelProperty, RootNode> getRoleToNode() {
		return roleToNode == null ? Collections.emptyMap() : Collections.unmodifiableMap(roleToNode);
	}

	public RootNode getNodeOfRole(CtRole attributeRole) {
		return roleToNode.get(getFieldOfRole(attributeRole));
	}

	public RootNode setNodeOfRole(CtRole role, RootNode newAttrNode) {
		return roleToNode.put(getFieldOfRole(role), newAttrNode);
	}

	/**
	 * @param role
	 * @return a {@link RootNode}, which exists on the `role` or creates implicit container for that role
	 */
	public RootNode getOrCreateNodeOfRole(CtRole role, Map<CtElement, RootNode> patternElementToSubstRequests) {
		RootNode node = getNodeOfRole(role);
		if (node == null) {
			MetamodelProperty mmField = elementType.getProperty(role);
			if (mmField == null || mmField.isDerived()) {
				throw new SpoonException("The role " + role + " doesn't exist or is derived for " + elementType);
			}
			node = create(mmField.getContainerKind(), null, patternElementToSubstRequests);
			setNodeOfRole(role, node);
		}
		return node;
	}

	/**
	 * @param role to be returned {@link CtRole}
	 * @param type required type of returned value
	 * @return value of {@link ConstantNode} on the `role` attribute of this {@link ElementNode} or null if there is none or has different type
	 */
	public <T> T getValueOfRole(CtRole role, Class<T> type) {
		RootNode node = getNodeOfRole(role);
		if (node instanceof ConstantNode) {
//			FIX it delivers value of StringNode too ... generated by must be added into produced elements
			ConstantNode cn = (ConstantNode) node;
			if (type.isInstance(cn.getTemplateNode())) {
				return (T) cn.getTemplateNode();
			}
		}
		return null;
	}

	private MetamodelProperty getFieldOfRole(CtRole role) {
		MetamodelProperty mmField = elementType.getProperty(role);
		if (mmField == null) {
			throw new SpoonException("CtRole." + role.name() + " isn't available for " + elementType);
		}
		if (mmField.isDerived()) {
			throw new SpoonException("CtRole." + role.name() + " is derived in " + elementType + " so it can't be used for matching or generating");
		}
		return mmField;
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		if (roleToNode != null) {
			for (RootNode node : roleToNode.values()) {
				node.forEachParameterInfo(consumer);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> void generateTargets(DefaultGenerator generator, ResultHolder<U> result, ImmutableMap parameters) {
		//TODO implement create on MetamodelConcept
		@SuppressWarnings("rawtypes")
		CtElement clone = generator.getFactory().Core().create((Class) elementType.getMetamodelInterface().getActualClass());
		generateSingleNodeAttributes(generator, clone, parameters);
		generator.applyGeneratedBy(clone, generator.getGeneratedByComment(templateElement));
		result.addResult((U) clone);
	}

	protected void generateSingleNodeAttributes(DefaultGenerator generator, CtElement clone, ImmutableMap parameters) {
		for (Map.Entry<MetamodelProperty, RootNode> e : getRoleToNode().entrySet()) {
			MetamodelProperty mmField = e.getKey();
			switch (mmField.getContainerKind()) {
			case SINGLE:
				mmField.setValue(clone, generator.generateSingleTarget(e.getValue(), parameters, mmField.getTypeofItems().getActualClass()));
				break;
			case LIST:
				mmField.setValue(clone, generator.generateTargets(e.getValue(), parameters, mmField.getTypeofItems().getActualClass()));
				break;
			case SET:
				mmField.setValue(clone, new LinkedHashSet<>(generator.generateTargets(e.getValue(), parameters, mmField.getTypeofItems().getActualClass())));
				break;
			case MAP:
				mmField.setValue(clone, entriesToMap(generator.generateTargets(e.getValue(), parameters, Map.Entry.class)));
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
	public ImmutableMap matchTarget(Object target, ImmutableMap parameters) {
		if (target == null) {
			return null;
		}
		if (target.getClass() != elementType.getImplementationClass().getActualClass()) {
			return null;
		}

		//it is spoon element, it matches if to be matched attributes matches
		//to be matched attributes must be same or substituted
		//iterate over all attributes of to be matched class
		for (Map.Entry<MetamodelProperty, RootNode> e : roleToNode.entrySet()) {
			parameters = matchesRole(parameters, (CtElement) target, e.getKey(), e.getValue());
			if (parameters == null) {
				return null;
			}
		}
		return parameters;
	}

	protected ImmutableMap matchesRole(ImmutableMap parameters, CtElement target, MetamodelProperty mmField, RootNode attrNode) {
		if (isMatchingRole(mmField.getRole(), elementType.getMetamodelInterface().getActualClass()) == false) {
			return parameters;
		}
		TobeMatched tobeMatched;
		if (attrNode instanceof ParameterNode) {
			//whole attribute value (whole List/Set/Map) has to be stored in parameter
			tobeMatched = TobeMatched.create(parameters, ContainerKind.SINGLE, mmField.getValue(target));
		} else {
			//each item of attribute value (item of List/Set/Map) has to be matched individually
			tobeMatched = TobeMatched.create(parameters, mmField.getContainerKind(), mmField.getValue(target));
		}
		return getMatchedParameters(attrNode.matchTargets(tobeMatched, RootNode.MATCH_ALL));
	}

	private static final Map<CtRole, Class[]> roleToSkippedClass = new HashMap<>();
	static {
		roleToSkippedClass.put(CtRole.COMMENT, new Class[]{Object.class});
		roleToSkippedClass.put(CtRole.POSITION, new Class[]{Object.class});
		roleToSkippedClass.put(CtRole.IS_IMPLICIT, new Class[]{Object.class});
		roleToSkippedClass.put(CtRole.CAST, new Class[]{Object.class});
		roleToSkippedClass.put(CtRole.TYPE, new Class[]{CtExecutableReference.class});
		roleToSkippedClass.put(CtRole.DECLARING_TYPE, new Class[]{CtExecutableReference.class});
	}

	/**
	 * @param role the role to be checked
	 * @param targetClass the class which is going to be checked
	 * @return true if the role is relevant to matching process
	 */
	private static boolean isMatchingRole(CtRole role, Class<?> targetClass) {
		Class<?>[] classes = roleToSkippedClass.get(role);
		if (classes != null) {
			for (Class<?> cls : classes) {
				if (cls.isAssignableFrom(targetClass)) {
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public String toString() {
		return elementType.getName() + ": " + super.toString();
	}

	public MetamodelConcept getElementType() {
		return elementType;
	}

	public void setElementType(MetamodelConcept elementType) {
		this.elementType = elementType;
	}

	@Override
	public Quantifier getMatchingStrategy() {
		return Quantifier.POSSESSIVE;
	}

	@Override
	public boolean isTryNextMatch(ImmutableMap parameters) {
		//it always matches only once
		return false;
	}
}
