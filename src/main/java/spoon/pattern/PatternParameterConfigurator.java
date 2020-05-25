/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import spoon.SpoonException;
import spoon.metamodel.Metamodel;
import spoon.pattern.internal.ValueConvertor;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern.internal.node.MapEntryNode;
import spoon.pattern.internal.node.ParameterNode;
import spoon.pattern.internal.node.RootNode;
import spoon.pattern.internal.node.StringNode;
import spoon.pattern.internal.parameter.AbstractParameterInfo;
import spoon.pattern.internal.parameter.ComputedParameterInfo;
import spoon.pattern.internal.parameter.ListParameterInfo;
import spoon.pattern.internal.parameter.MapParameterInfo;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.pattern.internal.parameter.SimpleNameOfTypeReferenceParameterComputer;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;
import spoon.reflect.visitor.filter.VariableReferenceFunction;
import spoon.support.Experimental;
import spoon.template.Parameter;
import spoon.template.Template;
import spoon.template.TemplateParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static spoon.pattern.PatternBuilder.getLocalTypeRefBySimpleName;

/**
 * Used to define pattern parameters.
 *
 * Main documentation at http://spoon.gforge.inria.fr/pattern.html.
 */
@Experimental
public class PatternParameterConfigurator {
	private final PatternBuilder patternBuilder;
	private final Map<String, AbstractParameterInfo> parameterInfos;
	private AbstractParameterInfo currentParameter;
	private List<CtElement> substitutedNodes = new ArrayList<>();
	private ConflictResolutionMode conflictResolutionMode = ConflictResolutionMode.FAIL;

	PatternParameterConfigurator(PatternBuilder patternBuilder, Map<String, AbstractParameterInfo> parameterInfos) {
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
	public PatternParameterConfigurator setConflictResolutionMode(ConflictResolutionMode conflictResolutionMode) {
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
	 * @return this {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator parameter(String paramName) {
		currentParameter = getParameterInfo(paramName, true);
		substitutedNodes.clear();
		return this;
	}

	public PatternParameterConfigurator setMinOccurrence(int minOccurrence) {
		currentParameter.setMinOccurrences(minOccurrence);
		return this;
	}

	public PatternParameterConfigurator setMaxOccurrence(int maxOccurrence) {
		if (maxOccurrence == ParameterInfo.UNLIMITED_OCCURRENCES || maxOccurrence > 1 && currentParameter.isMultiple() == false) {
			throw new SpoonException("Cannot set maxOccurrences > 1 for single value parameter. Call setMultiple(true) first.");
		}
		currentParameter.setMaxOccurrences(maxOccurrence);
		return this;
	}

	public PatternParameterConfigurator setMatchingStrategy(Quantifier quantifier) {
		currentParameter.setMatchingStrategy(quantifier);
		return this;
	}

	/**
	 * Set expected type of Parameter. In some cases legacy Template needs to know the type of parameter value to select substituted element.
	 * See {@link ValueConvertor}, which provides conversion between matched element and expected parameter type
	 * @param valueType a expected type of parameter value
	 * @return this {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator setValueType(Class<?> valueType) {
		currentParameter.setParameterValueType(valueType);
		return this;
	}

	/**
	 * Defines type of parameter value (List/Set/Map/single).
	 * If not defined then real value type of property is used. If null, then default is {@link ContainerKind#SINGLE}
	 * @param containerKind to be used {@link ContainerKind}
	 * @return this {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator setContainerKind(ContainerKind containerKind) {
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
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byType(Class<?> type) {
		return byType(type.getName());
	}

	/**
	 * type identified by `typeQualifiedName` itself and all the references (with arbitrary actual type arguments)
	 * to that type are subject for substitution by current parameter
	 * @param typeQualifiedName a fully qualified name of to be substituted Class
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byType(String typeQualifiedName) {
		ParameterInfo pi = getCurrentParameter();
		//substitute all references with same qualified name (ignoring actual type arguments) to that type
		queryModel().filterChildren((CtTypeReference<?> typeRef) -> typeRef.getQualifiedName().equals(typeQualifiedName))
			.forEach((CtTypeReference<?> typeRef) -> {
				addSubstitutionRequest(pi, typeRef);
			});
		/**
		 * If Type itself is found part of model, then substitute it's simple name too
		 */
		CtType<?> type2 = queryModel().filterChildren((CtType<?> t) -> t.getQualifiedName().equals(typeQualifiedName)).first();
		if (type2 != null) {
			//Substitute name of template too
			addSubstitutionRequest(pi, type2, CtRole.NAME);
		}
		return this;
	}

	/**
	 * type referred by {@link CtTypeReference} `type` and all the references (with same actual type arguments)
	 * to that type are subject for substitution by current parameter
	 * @param type a fully qualified name of to be substituted Class
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byType(CtTypeReference<?> type) {
		ParameterInfo pi = getCurrentParameter();
		//substitute all references (with same actual type arguments too) to that type
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
			ComputedParameterInfo piName = new ComputedParameterInfo(SimpleNameOfTypeReferenceParameterComputer.INSTANCE, pi);
			piName.setParameterValueType(String.class);
			addSubstitutionRequest(piName, type2, CtRole.NAME);
		}
		return this;
	}

	/**
	 * Searches for a type visible in scope `templateType`, whose simple name is equal to `localTypeSimpleName`
	 * @param searchScope the Type which is searched for local Type
	 * @param localTypeSimpleName the simple name of to be returned Type
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byLocalType(CtType<?> searchScope, String localTypeSimpleName) {
		byLocalType(searchScope, localTypeSimpleName, false);
		return this;
	}

	PatternParameterConfigurator byLocalType(CtType<?> searchScope, String localTypeSimpleName, boolean optional) {
		String nestedType = getLocalTypeRefBySimpleName(searchScope, localTypeSimpleName);
		if (nestedType == null) {
			//such type doesn't exist
			if (optional) {
				//no problem
				return this;
			}
			throw new SpoonException("Template parameter " + localTypeSimpleName + " doesn't match to any local type");
		}
		//There is a local type with such name. Replace it
		byType(nestedType);
		return this;
	}

	/**
	 * variable read/write of `variable`
	 * @param variableName a variable whose references will be substituted
	 * @return this to support fluent API
	 */
	public PatternParameterConfigurator byVariable(String variableName) {
		CtVariable<?> var = queryModel().map(new PotentialVariableDeclarationFunction(variableName)).first();
		if (var != null) {
			byVariable(var);
		}	//else may be we should fail?
		return this;
	}

	/**
	 * variable read/write of `variable`
	 * @param variable a variable whose references will be substituted
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byVariable(CtVariable<?> variable) {
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
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byInvocation(CtMethod<?> method) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren(new InvocationFilter(method))
			.forEach((CtInvocation<?> inv) -> {
				addSubstitutionRequest(pi, inv);
			});
		return this;
	}

	/**
	 * Add parameters for each field reference to variable named `variableName`
	 * For example this pattern model
	 * class Params {
	 *   int paramA;
	 *   int paramB;
	 * }
	 * void matcher(Params p) {
	 *   return p.paramA + p.paramB;
	 * }
	 *
	 * called with `byFieldRefOfVariable("p")` will create pattern parameters: `paramA` and `paramB`
	 *
	 * @param varName the name of the variable reference
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byFieldAccessOnVariable(String varName) {
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
		return this;
	}

	/**
	 * Add parameters for each variable reference of `variable`
	 * @param variable to be substituted variable
	 * @return this to support fluent API
	 */
	private void createPatternParameterForVariable(CtVariable<?> variable) {
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
	}

	/**
	 * Creates pattern parameter for each field of type {@link TemplateParameter}
	 * @return this to support fluent API
	 */
	public PatternParameterConfigurator byTemplateParameter() {
		return byTemplateParameter(null);
	}

	/**
	 * Creates pattern parameter for each field of type {@link TemplateParameter}.<br>
	 * Note: This method is here for compatibility with obsolete legacy {@link Template} based concept.
	 * We suggest to define each parameter individually using `byXxxx(...)` methods of this class instead.
	 *
	 * @param parameterValues pattern parameter values.
	 * 		Note these values may influence the way how pattern parameters are created.
	 * 		This unclear and ambiguous technique was used in legacy templates
	 * @return this to support fluent API
	 */
	public PatternParameterConfigurator byTemplateParameter(Map<String, Object> parameterValues) {
		CtType<?> templateType = patternBuilder.getTemplateTypeRef().getTypeDeclaration();
		templateType.map(new AllTypeMembersFunction()).forEach((CtTypeMember typeMember) -> {
			configureByTemplateParameter(templateType, parameterValues, typeMember);
		});
		return this;
	}

	private void configureByTemplateParameter(CtType<?> templateType, Map<String, Object> parameterValues, CtTypeMember typeMember) {
		Factory f = typeMember.getFactory();
		CtTypeReference<CtTypeReference> typeReferenceRef = f.Type().createReference(CtTypeReference.class);
		CtTypeReference<CtStatement> ctStatementRef = f.Type().createReference(CtStatement.class);
		CtTypeReference<TemplateParameter> templateParamRef = f.Type().createReference(TemplateParameter.class);
		Parameter param = typeMember.getAnnotation(Parameter.class);
		if (param != null) {
			if (typeMember instanceof CtField) {
				CtField<?> paramField = (CtField<?>) typeMember;
				/*
				 * We have found a CtField annotated by @Parameter.
				 * Use it as Pattern parameter
				 */
				String fieldName = typeMember.getSimpleName();
				String stringMarker = (param.value() != null && !param.value().isEmpty()) ? param.value() : fieldName;
				//for the compatibility reasons with Parameters.getNamesToValues(), use the proxy name as parameter name
				String parameterName = stringMarker;

				CtTypeReference<?> paramType = paramField.getType();

				if (paramType.isSubtypeOf(f.Type().ITERABLE) || paramType instanceof CtArrayTypeReference<?>) {
					//parameter is a multivalue
					// here we need to replace all named element and all references whose simpleName == stringMarker
					parameter(parameterName).setContainerKind(ContainerKind.LIST).byNamedElement(stringMarker).byReferenceName(stringMarker);
				} else if (paramType.isSubtypeOf(typeReferenceRef) || paramType.getQualifiedName().equals(Class.class.getName())) {
					/*
					 * parameter with value type TypeReference or Class, identifies replacement of local type whose name is equal to parameter name
					 */
					String nestedType = getLocalTypeRefBySimpleName(templateType, stringMarker);
					if (nestedType != null) {
						//all references to nestedType has to be replaced
						parameter(parameterName).byType(nestedType);
					}
					//and replace the variable references by class access
					parameter(parameterName).byVariable(paramField);
				} else if (paramType.getQualifiedName().equals(String.class.getName())) {
					String nestedType = getLocalTypeRefBySimpleName(templateType, stringMarker);
					if (nestedType != null) {
						//There is a local type with such name. Replace it
						parameter(parameterName).byType(nestedType);
					}
				} else if (paramType.isSubtypeOf(templateParamRef)) {
					parameter(parameterName)
						.byTemplateParameterReference(paramField);
					//if there is any invocation of method with name matching to stringMarker, then substitute their invocations too.
					templateType.getMethodsByName(stringMarker).forEach(m -> {
						parameter(parameterName).byInvocation(m);
					});
				} else if (paramType.isSubtypeOf(ctStatementRef)) {
					//if there is any invocation of method with name matching to stringMarker, then substitute their invocations too.
					templateType.getMethodsByName(stringMarker).forEach(m -> {
						parameter(parameterName).setContainerKind(ContainerKind.LIST).byInvocation(m);
					});
				} else {
					//it is not a String. It is used to substitute CtLiteral of parameter value
					parameter(parameterName)
						//all occurrences of parameter name in pattern model are subject of substitution
						.byVariable(paramField);
				}
				if (paramType.getQualifiedName().equals(Object.class.getName()) && parameterValues != null) {
					//if the parameter type is Object, then detect the real parameter type from the parameter value
					Object value = parameterValues.get(parameterName);
					if (value instanceof CtLiteral || value instanceof CtTypeReference) {
						/*
						 * the real parameter value is CtLiteral or CtTypeReference
						 * We should replace all method invocations whose name equals to stringMarker
						 * by that CtLiteral or qualified name of CtTypeReference
						 */
						ParameterInfo pi = parameter(parameterName).getCurrentParameter();
						queryModel().filterChildren((CtInvocation<?> inv) -> {
							return inv.getExecutable().getSimpleName().equals(stringMarker);
						}).forEach((CtInvocation<?> inv) -> {
							addSubstitutionRequest(pi, inv);
						});
					}
				}

				//any value can be converted to String. Substitute content of all string attributes
				parameter(parameterName).setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE)
					.bySubstring(stringMarker);

				if (parameterValues != null) {
					//handle automatic inline statements
					addInlineStatements(fieldName, parameterValues.get(parameterName));
				}
			} else {
				//TODO CtMethod was may be supported in old Template engine!!!
				throw new SpoonException("Template Parameter annotation on " + typeMember.getClass().getName() + " is not supported");
			}
		} else if (typeMember instanceof CtField<?> && ((CtField<?>) typeMember).getType().isSubtypeOf(templateParamRef)) {
			CtField<?> field = (CtField<?>) typeMember;
			String parameterName = typeMember.getSimpleName();
			Object value = parameterValues == null ? null : parameterValues.get(parameterName);
			Class valueType = null;
			boolean multiple = false;
			if (value != null) {
				valueType = value.getClass();
				if (value instanceof CtBlock) {
					//the CtBlock in this situation is expected as container of Statements in legacy templates.
					multiple = true;
				}
			}
			parameter(parameterName).setValueType(valueType).setContainerKind(multiple ? ContainerKind.LIST : ContainerKind.SINGLE)
				.byTemplateParameterReference(field);

			if (parameterValues != null) {
				//handle automatic inline statements
				addInlineStatements(parameterName, parameterValues.get(parameterName));
			}
		}
	}

	private void addInlineStatements(String variableName, Object paramValue) {
		if (paramValue != null && paramValue.getClass().isArray()) {
			//the parameters with Array value are meta parameters in legacy templates
			patternBuilder.configureInlineStatements(sb -> {
				//we are adding inline statements automatically from legacy templates,
				//so do not fail if it is sometime not possible - it means that it is not a inline statement then
				sb.setFailOnMissingParameter(false);
				sb.inlineIfOrForeachReferringTo(variableName);
			});
		}
	}

	/**
	 * Creates pattern parameter for each key of parameterValues {@link Map}.
	 * The parameter is created only if doesn't exist yet.
	 * If the parameter value is a CtTypeReference, then all local types whose simple name equals to parameter name are substituted
	 * Then any name in source code which contains a parameter name will be converted to parameter
	 *
	 * Note: This unclear and ambiguous technique was used in legacy templates
	 * We suggest to define each parameter individually using `byXxxx(...)` methods of this class instead.
	 *
	 * @param parameterValues pattern parameter values or null if not known
	 * @return this to support fluent API
	 */
	public PatternParameterConfigurator byParameterValues(Map<String, Object> parameterValues) {
		if (parameterValues != null) {
			CtType<?> templateType = patternBuilder.getTemplateTypeRef().getTypeDeclaration();
			//configure template parameters based on parameter values only - these without any declaration in Template
			parameterValues.forEach((paramName, paramValue) -> {
				if (isSubstituted(paramName) == false) {
					//and only these parameters whose name isn't already handled by explicit template parameters
					//replace types whose name fits to name of parameter
					parameter(paramName)
						.setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE)
						.byLocalType(templateType, paramName, true);
					parameter(paramName)
						.setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE)
						.bySubstring(paramName);
				}
			});
		}
		return this;
	}

	/**
	 * variable read/write of `variable` of type {@link TemplateParameter}
	 * @param variable a variable whose references will be substituted
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byTemplateParameterReference(CtVariable<?> variable) {
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
	 * All spoon model string attributes whose value is equal to `stringMarker`
	 * are subject for substitution by current parameter
	 * @param stringMarker a string value which has to be substituted
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byString(String stringMarker) {
		ParameterInfo pi = getCurrentParameter();
		new StringAttributeScanner() {
			@Override
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String value) {
				if (stringMarker.equals(value)) {
					addSubstitutionRequest(pi, element, roleHandler.getRole());
				}
			}
			@Override
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String mapEntryKey, CtElement mapEntryValue) {
				if (stringMarker.equals(mapEntryKey)) {
					patternBuilder.modifyNodeOfAttributeOfElement(element, roleHandler.getRole(), conflictResolutionMode, oldAttrNode -> {
						if (oldAttrNode instanceof MapEntryNode) {
							MapEntryNode mapEntryNode = (MapEntryNode) oldAttrNode;
							return new MapEntryNode(new ParameterNode(pi), mapEntryNode.getValue());
						}
						return oldAttrNode;
					});

				}
			}
		}.scan(patternBuilder.getPatternModel());
		return this;
	}

	/**
	 * All spoon model string attributes whose value contains whole string or a substring equal to `stringMarker`
	 * are subject for substitution by current parameter. Only the `stringMarker` substring of the string value is substituted!
	 * @param stringMarker a string value which has to be substituted
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator bySubstring(String stringMarker) {
		ParameterInfo pi = getCurrentParameter();
		new StringAttributeScanner() {
			@Override
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String value) {
				if (value != null && value.contains(stringMarker)) {
					addSubstitutionRequest(pi, element, roleHandler.getRole(), stringMarker);
				}
			}
			@Override
			protected void visitStringAttribute(RoleHandler roleHandler, CtElement element, String mapEntryKey, CtElement mapEntryValue) {
				if (mapEntryKey != null && mapEntryKey.contains(stringMarker)) {
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
			}
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
					if (Metamodel.getInstance().getConcept(element.getClass()).getProperty(roleHandler.getRole()).isUnsettable()) {
						//do not visit unsettable string attributes, which cannot be modified by pattern
						continue;
					}
					Object value = roleHandler.getValue(element);
					if (value instanceof String) {
						visitStringAttribute(roleHandler, element, (String) value);
					} else if (value instanceof Map) {
						for (Map.Entry<String, CtElement> e : ((Map<String, CtElement>) value).entrySet()) {
							visitStringAttribute(roleHandler, element, e.getKey(), e.getValue());
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
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byNamedElement(String simpleName) {
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
	 * In some cases, the selected object is actually the parent of the reference (eg the invocation).
	 * This is implemented in {@link PatternParameterConfigurator#getSubstitutedNodeOfElement(ParameterInfo, CtElement)}
	 *
	 *
	 * @param simpleName simple name of {@link CtReference}
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byReferenceName(String simpleName) {
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
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byFilter(Filter<?> filter) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren(filter)
			.forEach((CtElement ele) -> {
				addSubstitutionRequest(pi, ele);
			});
		return this;
	}

	/**
	 * Elements will be substituted by parameter value
	 * @param elements to be substituted elements
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byElement(CtElement... elements) {
		ParameterInfo pi = getCurrentParameter();
		for (CtElement element : elements) {
			addSubstitutionRequest(pi, element);
		}
		return this;
	}

	/**
	 * Attribute defined by `role` of all elements matched by {@link Filter} will be substituted by parameter value
	 * @param role {@link CtRole}, which defines to be substituted elements
	 * @param filter {@link Filter}, which defines to be substituted elements
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byRole(CtRole role, Filter<?> filter) {
		ParameterInfo pi = getCurrentParameter();
		queryModel().filterChildren(filter)
			.forEach((CtElement ele) -> {
				addSubstitutionRequest(pi, ele, role);
			});
		return this;
	}

	/**
	 * Attribute defined by `role` of `element`  will be substituted by parameter value
	 * @param role {@link CtRole}, which defines to be substituted elements
	 * @param elements to be substituted element
	 * @return {@link PatternParameterConfigurator} to support fluent API
	 */
	public PatternParameterConfigurator byRole(CtRole role, CtElement... elements) {
		ParameterInfo pi = getCurrentParameter();
		for (CtElement element : elements) {
			addSubstitutionRequest(pi, element, role);
		}
		return this;
	}

	/**
	 * @param type a required type of the value which matches as value of this parameter
	 * @param matchCondition a {@link Predicate} which selects matching values
	 * @return this to support fluent API
	 */
	public <T> PatternParameterConfigurator byCondition(Class<T> type, Predicate<T> matchCondition) {
		currentParameter.setMatchCondition(type, matchCondition);
		return this;
	}

	/**
	 * marks a CtIf and CtForEach to be matched, even when inlined.
	 * @return this to support fluent API
	 */
	public PatternParameterConfigurator matchInlinedStatements() {
		InlinedStatementConfigurator sb = new InlinedStatementConfigurator(patternBuilder);
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
	 * Adds request to substitute value of `attributeRole` of `element`, by the value of this {@link ListOfNodes} parameter {@link ParameterInfo} value
	 * @param element whose attribute of {@link CtRole} `attributeRole` have to be replaced
	 */
	void addSubstitutionRequest(ParameterInfo parameter, CtElement element, CtRole attributeRole) {
		patternBuilder.setNodeOfAttributeOfElement(element, attributeRole, new ParameterNode(parameter), conflictResolutionMode);
	}

	/**
	 * Adds request to substitute substring of {@link String} value of `attributeRole` of `element`, by the value of this {@link ListOfNodes} parameter {@link ParameterInfo} value
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

	/**
	 * Arguments for that implicit behavior:
	 * - most of the clients doesn't understand the Spoon model deep enough to distinguish between CtInvocation, CtExecutableReference, ... so the implicit fallback is to the elements which are directly visible in source code
	 * - the Pattern builder code is simpler for clients
	 */
	private ParameterElementPair getSubstitutedNodeOfElement(ParameterInfo parameter, CtElement element) {
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
	 * @param pep pair of parameter and element which has to be transformed
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
				if ("S".equals(executableRef.getSimpleName())) {
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
