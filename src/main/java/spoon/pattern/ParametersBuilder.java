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
import java.util.function.Predicate;

import spoon.SpoonException;
import spoon.pattern.matcher.Quantifier;
import spoon.pattern.node.ListOfNodes;
import spoon.pattern.node.MapEntryNode;
import spoon.pattern.node.ModelNode;
import spoon.pattern.node.RootNode;
import spoon.pattern.node.ParameterNode;
import spoon.pattern.node.StringNode;
import spoon.pattern.parameter.AbstractParameterInfo;
import spoon.pattern.parameter.ListParameterInfo;
import spoon.pattern.parameter.MapParameterInfo;
import spoon.pattern.parameter.ParameterInfo;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;
import spoon.reflect.visitor.filter.VariableReferenceFunction;
import spoon.template.TemplateParameter;

import static spoon.pattern.PatternBuilder.getLocalTypeRefBySimpleName;

/**
 * Used to define Pattern parameters and their mapping to Pattern model
 */
public class ParametersBuilder {
	private final PatternBuilder patternBuilder;
	private final Map<String, AbstractParameterInfo> parameterInfos;
	private AbstractParameterInfo currentParameter;
	private List<CtElement> substitutedNodes = new ArrayList<>();
	private ConflictResolutionMode conflictResolutionMode = ConflictResolutionMode.FAIL;

	ParametersBuilder(PatternBuilder patternBuilder, Map<String, AbstractParameterInfo> parameterInfos) {
		this.patternBuilder = patternBuilder;
		this.parameterInfos = parameterInfos;
	}

	/**
	 * @return current {@link ConflictResolutionMode}
	 */
	public ConflictResolutionMode getConflictResolutionMode() {
		return conflictResolutionMode;
	}

	/**
	 * Defines what happens when before explicitly added {@link RootNode} has to be replaced by another {@link RootNode}
	 * @param conflictResolutionMode to be applied mode
	 * @return this to support fluent API
	 */
	public ParametersBuilder setConflictResolutionMode(ConflictResolutionMode conflictResolutionMode) {
		this.conflictResolutionMode = conflictResolutionMode;
		return this;
	}

	public CtQueryable queryModel() {
		return patternBuilder.patternQuery;
	}

	private AbstractParameterInfo getParameterInfo(String parameterName, boolean createIfNotExist) {
		AbstractParameterInfo pi = parameterInfos.get(parameterName);
		if (pi == null) {
			pi = new MapParameterInfo(parameterName).setValueConvertor(patternBuilder.getDefaultValueConvertor());
			parameterInfos.put(parameterName, pi);
		}
		return pi;
	}

	/**
	 * Creates a parameter with name `paramName` and assigns it into context, so next calls on builder will be applied to this parameter
	 * @param paramName to be build parameter name
	 * @return this {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder parameter(String paramName) {
		currentParameter = getParameterInfo(paramName, true);
		substitutedNodes.clear();
		return this;
	}

	public ParametersBuilder setMinOccurence(int minOccurence) {
		currentParameter.setMinOccurences(minOccurence);
		return this;
	}
	public ParametersBuilder setMaxOccurence(int maxOccurence) {
		if (maxOccurence == ParameterInfo.UNLIMITED_OCCURENCES || maxOccurence > 1 && currentParameter.isMultiple() == false) {
			throw new SpoonException("Cannot set maxOccurences > 1 for single value parameter. Call setMultiple(true) first.");
		}
		currentParameter.setMaxOccurences(maxOccurence);
		return this;
	}
	public ParametersBuilder setMatchingStrategy(Quantifier quantifier) {
		currentParameter.setMatchingStrategy(quantifier);
		return this;
	}

	/**
	 * Set expected type of Parameter. In some cases legacy Template needs to know the type of parameter value to select substituted element.
	 * See {@link ValueConvertor}, which provides conversion between matched element and expected parameter type
	 * @param valueType a expected type of parameter value
	 * @return this {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder setValueType(Class<?> valueType) {
		currentParameter.setParameterValueType(valueType);
		return this;
	}

	/**
	 * Defines type of parameter value (List/Set/Map/single).
	 * If not defined then real value type of property is used. If null, then default is {@link ContainerKind#SINGLE}
	 * @param containerKind to be used {@link ContainerKind}
	 * @return this {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder setContainerKind(ContainerKind containerKind) {
		currentParameter.setContainerKind(containerKind);
		return this;
	}

	public ParameterInfo getCurrentParameter() {
		if (currentParameter == null) {
			throw new SpoonException("Parameter name must be defined first by call of #parameter(String) method.");
		}
		return currentParameter;
	}

	/**
	 * `type` itself and all the references to the `type` are subject for substitution by current parameter
	 * @param type to be substituted Class
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byType(Class<?> type) {
		return byType(type.getName());
	}
	/**
	 * type identified by `typeQualifiedName` itself and all the references to that type are subject for substitution by current parameter
	 * @param typeQualifiedName a fully qualified name of to be substituted Class
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byType(String typeQualifiedName) {
		return byType(patternBuilder.getFactory().Type().createReference(typeQualifiedName));
	}
	/**
	 * type referred by {@link CtTypeReference} `type` and all the references to that type are subject for substitution by current parameter
	 * @param type a fully qualified name of to be substituted Class
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byType(CtTypeReference<?> type) {
		ParameterInfo pi = getCurrentParameter();
		//substitute all references to that type
		queryModel().filterChildren((CtTypeReference<?> typeRef) -> typeRef.equals(type))
			.forEach((CtTypeReference<?> typeRef) -> {
				addSubstitutionRequest(pi, typeRef);
			});
		/**
		 * If Type itself is found part of model, then substitute it's simple name too
		 */
		String typeQName = type.getQualifiedName();
		CtType<?> type2 = queryModel().filterChildren((CtType<?> t) -> t.getQualifiedName().equals(typeQName)).first();
		if (type2 != null) {
			//Substitute name of template too
			addSubstitutionRequest(pi, type2, CtRole.NAME);
		}
		return this;
	}

	/**
	 * Searches for a type visible in scope `templateType`, whose simple name is equal to `localTypeSimpleName`
	 * @param searchScope the Type which is searched for local Type
	 * @param localTypeSimpleName the simple name of to be returned Type
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byLocalType(CtType<?> searchScope, String localTypeSimpleName) {
		CtTypeReference<?> nestedType = getLocalTypeRefBySimpleName(searchScope, localTypeSimpleName);
		if (nestedType == null) {
			throw new SpoonException("Template parameter " + localTypeSimpleName + " doesn't match to any local type");
		}
		//There is a local type with such name. Replace it
		byType(nestedType);
		return this;
	}

	/**
	 * variable read/write of `variable`
	 * @param variableName a variable whose references will be substituted
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byVariable(String variableName) {
		CtVariable<?> var = queryModel().map(new PotentialVariableDeclarationFunction(variableName)).first();
		if (var != null) {
			byVariable(var);
		}	//else may be we should fail?
		return this;
	}
	/**
	 * variable read/write of `variable`
	 * @param variable a variable whose references will be substituted
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byVariable(CtVariable<?> variable) {
		ParameterInfo pi = getCurrentParameter();
		CtQueryable root = queryModel();
		if (patternBuilder.isInModel(variable)) {
			//variable is part of model, start search from variable
			root = variable;
		}
		root.map(new VariableReferenceFunction(variable))
			.forEach((CtVariableReference<?> varRef) -> {
				addSubstitutionRequest(pi, varRef);
			});
		return this;
	}

	/**
	 * each invocation of `method` will be replaces by parameter value
	 * @param method the method whose invocation has to be substituted
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byInvocation(CtMethod<?> method) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren(new InvocationFilter(method))
			.forEach((CtInvocation<?> inv) -> {
				addSubstitutionRequest(pi, inv);
			});
		return this;
	}

	/**
	 * Add parameters for each field reference to variable named `variableName`
	 * @param variableName the name of the variable reference
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder createPatternParameterForVariable(String... variableName) {
		for (String varName : variableName) {
			CtVariable<?> var = queryModel().map(new PotentialVariableDeclarationFunction(varName)).first();
			if (var != null) {
				createPatternParameterForVariable(var);
			} else {
				List<CtVariable<?>> vars = queryModel().filterChildren(new NamedElementFilter(CtVariable.class, varName)).list();
				if (vars.size() > 1) {
					throw new SpoonException("Ambiguous variable " + varName);
				} else if (vars.size() == 1) {
					createPatternParameterForVariable(vars.get(0));
				} //else may be we should fail when variable is not found?
			}
		}
		return this;
	}
	/**
	 * Add parameters for each variable reference of `variable`
	 * @param variable to be substituted variable
	 * @return this to support fluent API
	 */
	private ParametersBuilder createPatternParameterForVariable(CtVariable<?> variable) {
		CtQueryable searchScope;
		if (patternBuilder.isInModel(variable)) {
			addSubstitutionRequest(
					parameter(variable.getSimpleName()).getCurrentParameter(),
					variable);
			searchScope = variable;
		} else {
			searchScope = queryModel();
		}
		searchScope.map(new VariableReferenceFunction(variable))
		.forEach((CtVariableReference<?> varRef) -> {
			CtFieldRead<?> fieldRead = varRef.getParent(CtFieldRead.class);
			if (fieldRead != null) {
				addSubstitutionRequest(
						parameter(fieldRead.getVariable().getSimpleName()).getCurrentParameter(),
						fieldRead);
			} else {
				addSubstitutionRequest(
						parameter(varRef.getSimpleName()).getCurrentParameter(),
						varRef);
			}
		});
		return this;
	}

	/**
	 * variable read/write of `variable` of type {@link TemplateParameter}
	 * @param variable a variable whose references will be substituted
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byTemplateParameterReference(CtVariable<?> variable) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().map(new VariableReferenceFunction(variable))
			.forEach((CtVariableReference<?> varRef) -> {
				/*
				 * the target of substitution is always the invocation of TemplateParameter#S()
				 */
				CtVariableAccess<?> varAccess = (CtVariableAccess<?>) varRef.getParent();
				CtElement invocationOfS = varAccess.getParent();
				if (invocationOfS instanceof CtInvocation<?>) {
					CtInvocation<?> invocation = (CtInvocation<?>) invocationOfS;
					if ("S".equals(invocation.getExecutable().getSimpleName())) {
						addSubstitutionRequest(pi, invocation);
						return;
					}
				}
				throw new SpoonException("TemplateParameter reference is NOT used as target of invocation of TemplateParameter#S()");
			});
		return this;
	}

	/**
	 * CodeElement element identified by `simpleName`
	 * @param simpleName the name of the element or reference
	 * @return {@link ParametersBuilder} to support fluent API
	 */
//	public ParametersBuilder codeElementBySimpleName(String simpleName) {
//		ParameterInfo pi = getCurrentParameter();
//		pattern.getModel().filterChildren((CtNamedElement named) -> simpleName.equals(named.getSimpleName()))
//			.forEach((CtNamedElement named) -> {
//				if (named instanceof CtCodeElement) {
//					addSubstitutionRequest(pi, named);
//				}
//			});
//		pattern.getModel().filterChildren((CtReference ref) -> simpleName.equals(ref.getSimpleName()))
//		.forEach((CtReference ref) -> {
//			if (ref instanceof CtTypeReference<?>) {
//				return;
//			}
//			CtCodeElement codeElement = ref.getParent(CtCodeElement.class);
//			if (codeElement != null) {
//				addSubstitutionRequest(pi, codeElement);
//			}
//		});
//		return this;
//	}

	/**
	 * All spoon model string attributes whose value is equal to `stringMarker`
	 * are subject for substitution by current parameter
	 * @param stringMarker a string value which has to be substituted
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byString(String stringMarker) {
		ParameterInfo pi = getCurrentParameter();
		new StringAttributeScanner() {
			@Override
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String value) {
				if (stringMarker.equals(value)) {
					addSubstitutionRequest(pi, element, roleHandler.getRole());
				}
			}
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String mapEntryKey, CtElement mapEntryValue) {
				if (stringMarker.equals(mapEntryKey)) {
					patternBuilder.modifyNodeOfAttributeOfElement(element, roleHandler.getRole(), conflictResolutionMode, oldAttrNode -> {
						if (oldAttrNode instanceof MapEntryNode) {
							MapEntryNode mapEntryNode = (MapEntryNode) oldAttrNode;
							return new MapEntryNode(new ParameterNode(pi), ((MapEntryNode) oldAttrNode).getValue());
						}
						return oldAttrNode;
					});

				}
			};
		}.scan(patternBuilder.getPatternModel());
		return this;
	}

	/**
	 * All spoon model string attributes whose value contains whole string or a substring equal to `stringMarker`
	 * are subject for substitution by current parameter. Only the `stringMarker` substring of the string value is substituted!
	 * @param stringMarker a string value which has to be substituted
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder bySubstring(String stringMarker) {
		ParameterInfo pi = getCurrentParameter();
		new StringAttributeScanner() {
			@Override
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String value) {
				if (value != null && value.indexOf(stringMarker) >= 0) {
					addSubstitutionRequest(pi, element, roleHandler.getRole(), stringMarker);
				}
			}
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String mapEntryKey, CtElement mapEntryValue) {
				if (mapEntryKey != null && mapEntryKey.indexOf(stringMarker) >= 0) {
					patternBuilder.modifyNodeOfAttributeOfElement(element, roleHandler.getRole(), conflictResolutionMode, oldAttrNode -> {
						List<RootNode> nodes = ((ListOfNodes) oldAttrNode).getNodes();
						for (int i = 0; i < nodes.size(); i++) {
							RootNode node = nodes.get(i);
							if (node instanceof MapEntryNode) {
								MapEntryNode mapEntryNode = (MapEntryNode) node;
								nodes.set(i, new MapEntryNode(StringNode.setReplaceMarker(mapEntryNode.getKey(), stringMarker, pi), mapEntryNode.getValue()));
							}
						}
						return oldAttrNode;
					});
			}
			};
		}.scan(patternBuilder.getPatternModel());
		return this;
	}

	/**
	 * CtScanner implementation, which calls {@link #visitStringAttribute(RoleHandler, CtElement, String)}
	 * for each String attribute of each {@link CtElement} of scanned AST
	 */
	private abstract static class StringAttributeScanner extends CtScanner {
		/**
		 * List of all Spoon model {@link RoleHandler}s, which provides access to attribute value of type String
		 */
		private static List<RoleHandler> stringAttributeRoleHandlers = new ArrayList<>();
		static {
			RoleHandlerHelper.forEachRoleHandler(rh -> {
				if (rh.getValueClass().isAssignableFrom(String.class)) {
					//accept String and Object class
					stringAttributeRoleHandlers.add(rh);
				}
				if (rh.getContainerKind() == ContainerKind.MAP) {
					//accept Map where key is String too
					stringAttributeRoleHandlers.add(rh);
				}
			});
		}

		@Override
		public void scan(CtElement element) {
			visitStringAttribute(element);
			super.scan(element);
		}
		private void visitStringAttribute(CtElement element) {
			for (RoleHandler roleHandler : stringAttributeRoleHandlers) {
				if (roleHandler.getTargetType().isInstance(element)) {
					Object value = roleHandler.getValue(element);
					if (value instanceof String) {
						visitStringAttribute(roleHandler, element, (String) value);
					} else if (value instanceof Map) {
						for (Map.Entry<String, CtElement> e : ((Map<String, CtElement>) value).entrySet()) {
							visitStringAttribute(roleHandler, element, (String) e.getKey(), e.getValue());
						}
					}
					//else it is a CtLiteral with non string value
				}
			}
		}
		protected abstract void visitStringAttribute(RoleHandler roleHandler, CtElement element, String value);
		protected abstract void visitStringAttribute(RoleHandler roleHandler, CtElement element, String mapEntryKey, CtElement mapEntryValue);
	}

	/**
	 * Any named element by it's simple name
	 * @param simpleName simple name of {@link CtNamedElement}
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byNamedElement(String simpleName) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren((CtNamedElement named) -> simpleName.equals(named.getSimpleName()))
			.forEach((CtNamedElement named) -> {
				addSubstitutionRequest(pi, named);
			});
		return this;
	}

	/**
	 * Any reference identified by it's simple name.
	 *
	 * Can be used to match any method call for instance.
	 *
	 * @param simpleName simple name of {@link CtReference}
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byReferenceName(String simpleName) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren((CtReference ref) -> simpleName.equals(ref.getSimpleName()))
			.forEach((CtReference ref) -> {
				addSubstitutionRequest(pi, ref);
			});
		return this;
	}

	/**
	 * All elements matched by {@link Filter} will be substituted by parameter value
	 * @param filter {@link Filter}, which defines to be substituted elements
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byFilter(Filter<?> filter) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren(filter)
			.forEach((CtElement ele) -> {
				addSubstitutionRequest(pi, ele);
			});
		return this;
	}

	/**
	 * Attribute defined by `role` of all elements matched by {@link Filter} will be substituted by parameter value
	 * @param filter {@link Filter}, which defines to be substituted elements
	 * @param role {@link CtRole}, which defines to be substituted elements
	 * @return {@link ParametersBuilder} to support fluent API
	 */
	public ParametersBuilder byRole(Filter<?> filter, CtRole role) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren(filter)
			.forEach((CtElement ele) -> {
				addSubstitutionRequest(pi, ele, role);
			});
		return this;
	}

	/**
	 * @param type a required type of the value which matches as value of this parameter
	 * @param matchCondition a {@link Predicate} which selects matching values
	 * @return this to support fluent API
	 */
	public <T> ParametersBuilder matchCondition(Class<T> type, Predicate<T> matchCondition) {
		currentParameter.setMatchCondition(type, matchCondition);
		return this;
	}

	/**
	 * marks all CtIf and CtForEach whose expression is substituted by a this pattern parameter as inline statement.
	 * @return this to support fluent API
	 */
	public ParametersBuilder matchInlinedStatements() {
		InlineStatementsBuilder sb = new InlineStatementsBuilder(patternBuilder);
		for (CtElement ctElement : substitutedNodes) {
			sb.byElement(ctElement);
		}
		return this;
	}

	public boolean isSubstituted(String paramName) {
		if (patternBuilder.getParameterInfo(paramName) == null) {
			return false;
		}
		ParameterInfo pi = getParameterInfo(paramName, false);
		if (pi == null) {
			return false;
		}
		class Result {
			boolean isUsed = false;
		}
		Result result = new Result();
		patternBuilder.forEachNodeOfParameter(pi, parameterized -> result.isUsed = true);
		return result.isUsed;
	}

	void addSubstitutionRequest(ParameterInfo parameter, CtElement element) {
		//remember elements substituted by current parameter to be able to use them for marking inline statements
		substitutedNodes.add(element);
		ParameterElementPair pep = getSubstitutedNodeOfElement(parameter, element);
		patternBuilder.setNodeOfElement(pep.element, new ParameterNode(pep.parameter), conflictResolutionMode);
		if (patternBuilder.isAutoSimplifySubstitutions() && pep.element.isParentInitialized()) {
			RootNode node = patternBuilder.getOptionalPatternNode(pep.element.getParent());
			if (node != null) {
				node.setSimplifyGenerated(true);
			}
		}
	}

	/**
	 * Adds request to substitute value of `attributeRole` of `element`, by the value of this {@link ModelNode} parameter {@link ParameterInfo} value
	 * @param element whose attribute of {@link CtRole} `attributeRole` have to be replaced
	 */
	void addSubstitutionRequest(ParameterInfo parameter, CtElement element, CtRole attributeRole) {
		patternBuilder.setNodeOfAttributeOfElement(element, attributeRole, new ParameterNode(parameter), conflictResolutionMode);
	}
	/**
	 * Adds request to substitute substring of {@link String} value of `attributeRole` of `element`, by the value of this {@link ModelNode} parameter {@link ParameterInfo} value
	 * @param element whose part of String attribute of {@link CtRole} `attributeRole` have to be replaced
	 */
	void addSubstitutionRequest(ParameterInfo parameter, CtElement element, CtRole attributeRole, String subStringMarker) {
		patternBuilder.modifyNodeOfAttributeOfElement(element, attributeRole, conflictResolutionMode, oldAttrNode -> {
			return StringNode.setReplaceMarker(oldAttrNode, subStringMarker, parameter);
		});
	}

	public static class ParameterElementPair {
		final ParameterInfo parameter;
		final CtElement element;
		public ParameterElementPair(ParameterInfo parameter, CtElement element) {
			super();
			this.parameter = parameter;
			this.element = element;
		}
		public ParameterElementPair copyAndSet(ParameterInfo param) {
			return new ParameterElementPair(param, element);
		}
		public ParameterElementPair copyAndSet(CtElement element) {
			return new ParameterElementPair(parameter, element);
		}
	}

	public ParameterElementPair getSubstitutedNodeOfElement(ParameterInfo parameter, CtElement element) {
		ParameterElementPair parameterElementPair = new ParameterElementPair(parameter, element);
		parameterElementPair = transformVariableAccessToVariableReference(parameterElementPair);
		parameterElementPair = transformArrayAccess(parameterElementPair);
		parameterElementPair = transformTemplateParameterInvocationOfS(parameterElementPair);
		parameterElementPair = transformExecutableRefToInvocation(parameterElementPair);
		parameterElementPair = transformCtReturnIfNeeded(parameterElementPair);
		//if spoon creates an implicit parent (e.g. CtBlock) around the pattern parameter, then replace that implicit parent
		parameterElementPair = getLastImplicitParent(parameterElementPair);
		return parameterElementPair;
	}

	/**
	 * for input `element` expression `X` in expression `X[Y]` it returns expression `X[Y]`
	 * and registers extra {@link ListParameterInfo} to the parameter assigned to `X`
	 * @param parameter TODO
	 * @param valueResolver
	 * @param element
	 * @return
	 */
	private ParameterElementPair transformArrayAccess(ParameterElementPair pep) {
		CtElement element = pep.element;
		if (element.isParentInitialized()) {
			CtElement parent = element.getParent();
			if (parent instanceof CtArrayAccess<?, ?>) {
				CtArrayAccess<?, ?> arrayAccess = (CtArrayAccess<?, ?>) parent;
				CtExpression<?> expr = arrayAccess.getIndexExpression();
				if (expr instanceof CtLiteral<?>) {
					CtLiteral<?> idxLiteral = (CtLiteral<?>) expr;
					Object idx = idxLiteral.getValue();
					if (idx instanceof Number) {
						return new ParameterElementPair(new ListParameterInfo(((Number) idx).intValue(), pep.parameter), arrayAccess);
					}
				}
			}
		}
		return pep;
	}

	/**
	 * @return a node, which has to be substituted instead of variable reference `varRef`
	 */
	private ParameterElementPair transformVariableAccessToVariableReference(ParameterElementPair pep) {
		if (pep.element instanceof CtVariableReference<?>) {
			CtVariableReference<?> varRef = (CtVariableReference<?>) pep.element;
			/*
			 * the target of substitution is always the parent node of variableReference
			 * - the expression - CtVariableAccess
			 * which can be replaced by any other CtVariableAccess.
			 * For example CtFieldRead can be replaced by CtVariableRead or by CtLiteral
			 */
			return pep.copyAndSet(varRef.getParent());
		}
		return pep;
	}
	/**
	 * @return an invocation of {@link TemplateParameter#S()} if it is parent of `element`
	 */
	private ParameterElementPair transformTemplateParameterInvocationOfS(ParameterElementPair pep) {
		CtElement element = pep.element;
		if (element.isParentInitialized()) {
			CtElement parent = element.getParent();
			if (parent instanceof CtInvocation<?>) {
				CtInvocation<?> invocation = (CtInvocation<?>) parent;
				CtExecutableReference<?> executableRef = invocation.getExecutable();
				if (executableRef.getSimpleName().equals("S")) {
					if (TemplateParameter.class.getName().equals(executableRef.getDeclaringType().getQualifiedName())) {
						/*
						 * the invocation of TemplateParameter#S() has to be substituted
						 */
						return pep.copyAndSet(invocation);
					}
				}
			}
		}
		return pep;
	}

	private ParameterElementPair transformExecutableRefToInvocation(ParameterElementPair pep) {
		CtElement element = pep.element;
		if (element instanceof CtExecutableReference<?>) {
			CtExecutableReference<?> execRef = (CtExecutableReference<?>) element;
			if (element.isParentInitialized()) {
				CtElement parent = execRef.getParent();
				if (parent instanceof CtInvocation<?>) {
					/*
					 * the invocation has to be substituted
					 */
					return pep.copyAndSet(parent);
				}
			}
		}
		return pep;
	}

	private ParameterElementPair transformCtReturnIfNeeded(ParameterElementPair pep) {
		CtElement element = pep.element;
		if (element.isParentInitialized() && element.getParent() instanceof CtReturn<?>) {
			//we are substituting return expression. If the parameter value is CtBlock, then we have to substitute CtReturn instead
			Class<?> valueType = pep.parameter.getParameterValueType();
			if (valueType != null && CtBlock.class.isAssignableFrom(valueType)) {
				//substitute CtReturn
				return pep.copyAndSet(element.getParent());
			}
		}
		return pep;
	}

	/**
	 * @return last implicit parent of element
	 */
	private ParameterElementPair getLastImplicitParent(ParameterElementPair pep) {
		CtElement element = pep.element;
		while (element.isParentInitialized()) {
			CtElement parent = element.getParent();
			if ((parent instanceof CtBlock) == false || parent.isImplicit() == false) {
				break;
			}
			element = parent;
		}
		return pep.copyAndSet(element);
	}
}
