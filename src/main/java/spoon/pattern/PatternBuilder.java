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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import spoon.SpoonException;
import spoon.pattern.node.ElementNode;
import spoon.pattern.node.ListOfNodes;
import spoon.pattern.node.ModelNode;
import spoon.pattern.node.RootNode;
import spoon.pattern.parameter.AbstractParameterInfo;
import spoon.pattern.parameter.ParameterInfo;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.QueryFactory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.template.Parameter;
import spoon.template.TemplateParameter;

/**
 * The builder which creates a {@link Pattern}
 */
public class PatternBuilder {

	public static final String TARGET_TYPE = "targetType";

	public static PatternBuilder create(Factory factory, Class<?> templateClass) {
		return create(factory, templateClass, null);
	}

	/**
	 * Creates a pattern builder.
	 *
	 * @param factory
	 * @param templateClass
	 * @param selector the code which selects part of templateClass AST, which has to be used as template model
	 * @return new instance of {@link PatternBuilder}
	 */
	public static PatternBuilder create(Factory factory, Class<?> templateClass, Consumer<TemplateModelBuilder> selector) {
		return create(factory.Type().get(templateClass), selector);
	}

	public static PatternBuilder create(CtType<?> templateType) {
		return create(templateType, null);
	}

	public static PatternBuilder create(CtType<?> templateType, Consumer<TemplateModelBuilder> selector) {
		checkTemplateType(templateType);
		List<CtElement> templateModel;
		if (selector != null) {
			TemplateModelBuilder model = new TemplateModelBuilder(templateType);
			selector.accept(model);
			templateModel = model.getTemplateModel();
		} else {
			templateModel = Collections.singletonList(templateType);
		}
		return create(templateType.getReference(), templateModel);
	}

	/**
	 * Creates a {@link PatternBuilder} from the List of template elements
	 * @param templateTypeRef the reference to {@link CtType}, which contains a patternModel elements.
	 * 	These type references have to be replaced by references to type, which receives generated code.
	 * 	If patternModel contains no type references, then `templateTypeRef` may be null
	 * @param patternModel a List of Spoon AST nodes, which represents a template of to be generated or to be matched code
	 * @return new instance of {@link PatternBuilder}
	 */
	public static PatternBuilder create(CtTypeReference<?> templateTypeRef, List<CtElement> patternModel) {
		return new PatternBuilder(templateTypeRef, patternModel);
	}

	public static PatternBuilder create(CtElement patternModel) {
		return new PatternBuilder(null, Collections.singletonList(patternModel));
	}

	private final List<CtElement> patternModel;
	private final ListOfNodes patternNodes;
	private final Map<CtElement, RootNode> patternElementToSubstRequests = new IdentityHashMap<>();
	private final Set<RootNode> explicitNodes = Collections.newSetFromMap(new IdentityHashMap<>());

	private CtTypeReference<?> templateTypeRef;
	private final Map<String, AbstractParameterInfo> parameterInfos = new HashMap<>();
//	ModelNode pattern;
	CtQueryable patternQuery;
	private ValueConvertor valueConvertor;
	private boolean addGeneratedBy = false;
	private boolean autoSimplifySubstitutions = false;
	private boolean built = false;

	static class PatternQuery implements CtQueryable {
		private final QueryFactory queryFactory;
		private final List<CtElement> modelElements;
		PatternQuery(QueryFactory queryFactory, List<CtElement> modelElements) {
			this.queryFactory = queryFactory;
			this.modelElements = modelElements;
		}
		@Override
		public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
			return queryFactory.createQuery(modelElements).filterChildren(filter);
		}
		@Override
		public <I, R> CtQuery map(CtFunction<I, R> function) {
			return queryFactory.createQuery(modelElements).map(function);
		}
		@Override
		public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
			return queryFactory.createQuery(modelElements).map(queryStep);
		}
	}

	private PatternBuilder(CtTypeReference<?> templateTypeRef, List<CtElement> template) {
		this.templateTypeRef = templateTypeRef;
		this.patternModel = template;
		if (template == null) {
			throw new SpoonException("Cannot create a Pattern from an null model");
		}
		this.valueConvertor = new ValueConvertorImpl();
		patternNodes = ElementNode.create(template, patternElementToSubstRequests);
		patternQuery = new PatternBuilder.PatternQuery(getFactory().Query(), patternModel);
		if (templateTypeRef != null) {
			configureParameters(pb -> {
				pb.parameter(TARGET_TYPE).byType(templateTypeRef).setValueType(CtTypeReference.class);
			});
		}
	}

	/**
	 * @param element a CtElement
	 * @return {@link RootNode}, which handles matching/generation of an `object` from the source spoon AST.
	 * or null, if there is none
	 */
	public RootNode getOptionalPatternNode(CtElement element, CtRole... roles) {
		return getPatternNode(true, element, roles);
	}
	public RootNode getPatternNode(CtElement element, CtRole... roles) {
		return getPatternNode(false, element, roles);
	}
	private RootNode getPatternNode(boolean optional, CtElement element, CtRole... roles) {
		RootNode node = patternElementToSubstRequests.get(element);
		for (CtRole role : roles) {
			if (node instanceof ElementNode) {
				ElementNode elementNode = (ElementNode) node;
				node = elementNode.getNodeOfRole(role);
				if (node == null) {
					if (optional) {
						return null;
					}
					throw new SpoonException("The role " + role + " resolved to null Node");
				}
			} else {
				if (optional) {
					return null;
				}
				throw new SpoonException("The role " + role + " can't be resolved on Node of class " + node.getClass());
			}
		}
		if (node == null) {
			if (optional) {
				return null;
			}
			throw new SpoonException("There is no Node for element");
		}
		return node;
	}

	void modifyNodeOfElement(CtElement element, ConflictResolutionMode conflictMode, Function<RootNode, RootNode> elementNodeChanger) {
		RootNode oldNode = patternElementToSubstRequests.get(element);
		RootNode newNode = elementNodeChanger.apply(oldNode);
		if (newNode == null) {
			throw new SpoonException("Removing of Node is not supported");
		}
		handleConflict(conflictMode, oldNode, newNode, (tobeUsedNode) -> {
			if (patternNodes.replaceNode(oldNode, tobeUsedNode) == false) {
				if (conflictMode == ConflictResolutionMode.KEEP_OLD_NODE) {
					//The parent of oldNode was already replaced. OK - Keep that parent old node
					return;
				}
				throw new SpoonException("Old node was not found");
			}
			//update element to node mapping
			patternElementToSubstRequests.put(element, tobeUsedNode);
		});
	}

	void modifyNodeOfAttributeOfElement(CtElement element, CtRole role, ConflictResolutionMode conflictMode, Function<RootNode, RootNode> elementNodeChanger) {
		modifyNodeOfElement(element, conflictMode, node -> {
			if (node instanceof ElementNode) {
				ElementNode elementNode = (ElementNode) node;
				RootNode oldAttrNode = elementNode.getNodeOfRole(role);
				RootNode newAttrNode = elementNodeChanger.apply(oldAttrNode);
				if (newAttrNode == null) {
					throw new SpoonException("Removing of Node is not supported");
				}
				handleConflict(conflictMode, oldAttrNode, newAttrNode, (tobeUsedNode) -> {
					elementNode.setNodeOfRole(role, tobeUsedNode);
				});
				return node;
			}
			if (conflictMode == ConflictResolutionMode.KEEP_OLD_NODE) {
				return node;
			}
			throw new SpoonException("The Node of atttribute of element cannot be set because element has a Node of class: " + node.getClass().getName());
		});
	}

	private void handleConflict(ConflictResolutionMode conflictMode, RootNode oldNode, RootNode newNode, Consumer<RootNode> applyNewNode) {
		if (oldNode != newNode) {
			if (conflictMode == ConflictResolutionMode.APPEND) {
				if (oldNode instanceof ListOfNodes == false) {
					oldNode = new ListOfNodes(new ArrayList<>(Arrays.asList(oldNode)));
				}
				if (newNode instanceof ListOfNodes) {
					((ListOfNodes) oldNode).getNodes().addAll(((ListOfNodes) newNode).getNodes());
				} else {
					((ListOfNodes) oldNode).getNodes().add(newNode);
				}
				explicitNodes.add(oldNode);
				explicitNodes.add(newNode);
				applyNewNode.accept(oldNode);
				return;
			}
			if (explicitNodes.contains(oldNode)) {
				//the oldNode was explicitly added before
				if (conflictMode == ConflictResolutionMode.FAIL) {
					throw new SpoonException("Can't replace once assigned Node " + oldNode + " by a " + newNode);
				}
				if (conflictMode == ConflictResolutionMode.KEEP_OLD_NODE) {
					return;
				}
			}
			explicitNodes.remove(oldNode);
			explicitNodes.add(newNode);
			applyNewNode.accept(newNode);
		}
	}

	public void setNodeOfElement(CtElement element, RootNode node, ConflictResolutionMode conflictMode) {
		modifyNodeOfElement(element, conflictMode, oldNode -> {
			return node;
		});
	}

	public void setNodeOfAttributeOfElement(CtElement element, CtRole role, RootNode node, ConflictResolutionMode conflictMode) {
		modifyNodeOfAttributeOfElement(element, role, conflictMode, oldAttrNode -> {
			return node;
		});
	}

	/**
	 * @param element to be checked element
	 * @return true if element `element` is a template or a child of template
	 */
	public boolean isInModel(CtElement element) {
		if (element != null) {
			for (CtElement patternElement : patternModel) {
				if (element == patternElement || element.hasParent(patternElement)) {
					return true;
				}
			}
		}
		return false;
	}

	public Pattern build() {
		if (built) {
			throw new SpoonException("The Pattern may be built only once");
		}
		built = true;
		//clean the mapping so it is not possible to further modify built pattern using this builder
		patternElementToSubstRequests.clear();
		return new Pattern(new ModelNode(patternNodes.getNodes())).setAddGeneratedBy(isAddGeneratedBy());
	}

	static List<? extends CtElement> bodyToStatements(CtStatement statementOrBlock) {
		if (statementOrBlock instanceof CtBlock) {
			return ((CtBlock<?>) statementOrBlock).getStatements();
		}
		return Collections.singletonList(statementOrBlock);
	}

	/**
	 * @return default {@link ValueConvertor}, which will be assigned to all new {@link ParameterInfo}s
	 */
	public ValueConvertor getDefaultValueConvertor() {
		return valueConvertor;
	}

	/**
	 * @param valueConvertor default {@link ValueConvertor}, which will be assigned to all {@link ParameterInfo}s created after this call
	 * @return this to support fluent API
	 */
	public PatternBuilder setDefaultValueConvertor(ValueConvertor valueConvertor) {
		this.valueConvertor = valueConvertor;
		return this;
	}

	/**
	 * All the variable references, whose variables are out of the template model
	 * are automatically marked as pattern parameters
	 * @return this to support fluent API
	 */
	public PatternBuilder configureAutomaticParameters() {
		configureParameters(pb -> {
			//add this substitution request only if there isn't another one yet
			pb.setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE);
			/*
			 * detect other parameters.
			 * contract: All variable references, which are declared outside of template are automatically considered as template parameters
			 */
			pb.queryModel().filterChildren(new TypeFilter<>(CtVariableReference.class))
				.forEach((CtVariableReference<?> varRef) -> {
					CtVariable<?> var = varRef.getDeclaration();
					if (var == null || isInModel(var) == false) {
						//the varRef has declaration out of the scope of the template. It must be a template parameter.
						ParameterInfo parameter = pb.parameter(varRef.getSimpleName()).getCurrentParameter();
						pb.addSubstitutionRequest(parameter, varRef);
					}
				});
		});
		return this;
	}

	/**
	 * Builder which allows to define which part of AST of template {@link CtType}
	 * has to be used as a model of the {@link Pattern}
	 */
	public static class TemplateModelBuilder {
		/**
		 * The original type, which contains the AST of pattern model
		 */
		private final CtType<?> templateType;
		/**
		 * optional clone of templateType. It is created when AST of CtType has to be modified
		 * before it can become a model of {@link Pattern}
		 */
		private CtType<?> clonedTemplateType;
		/**
		 * holds the built pattern model
		 */
		private List<CtElement> templateModel = null;

		TemplateModelBuilder(CtType<?> templateTemplate) {
			this.templateType = templateTemplate;
		}

		/**
		 * @return origin {@link CtType}, which is the source of the pattern model
		 */
		public CtType<?> getTemplateType() {
			return templateType;
		}

		/**
		 * Returns clone of the templateType.
		 * The clone is done only once. Later calls returns cached clone.
		 * @return
		 */
		private CtType<?> getClonedTemplateType() {
			if (clonedTemplateType == null) {
				clonedTemplateType = templateType.clone();
				if (templateType.isParentInitialized()) {
					//set parent package, to keep origin qualified name of the Template. It is needed for correct substitution of Template name by target type reference
					clonedTemplateType.setParent(templateType.getParent());
				}
			}
			return clonedTemplateType;
		}

		/**
		 * Sets a template model from {@link CtTypeMember} of a template type
		 * @param typeMemberName the name of the {@link CtTypeMember} of a template type
		 */
		public TemplateModelBuilder setTypeMember(String typeMemberName) {
			setTypeMember(tm -> typeMemberName.equals(tm.getSimpleName()));
			return this;
		}
		/**
		 * Sets a template model from {@link CtTypeMember} of a template type
		 * @param filter the {@link Filter} whose match defines to be used {@link CtTypeMember}
		 */
		public TemplateModelBuilder setTypeMember(Filter<CtTypeMember> filter) {
			setTemplateModel(getByFilter(filter));
			return this;
		}

		/**
		 * removes all annotations of type defined by `classes` from the clone of the source {@link CtType}
		 * @param classes list of classes which defines types of to be removed annotations
		 * @return this to support fluent API
		 */
		public TemplateModelBuilder removeTag(Class... classes) {
			List<CtElement> elements = getClonedTemplateModel();
			for (Class class1 : classes) {
				for (CtElement element : elements) {
					CtAnnotation<?> annotation = element.getAnnotation(element.getFactory().Type().createReference(class1));
					if (annotation != null) {
						element.removeAnnotation(annotation);
					}
				}
			}
			return this;
		}

		private List<CtElement> getClonedTemplateModel() {
			if (templateModel == null) {
				throw new SpoonException("Template model is not defined yet");
			}
			for (ListIterator<CtElement> iter = templateModel.listIterator(); iter.hasNext();) {
				CtElement ele = iter.next();
				if (ele.getRoleInParent() != null) {
					iter.set(ele.clone());
				}
			}
			return templateModel;
		}

		/**
		 * Sets a template model from body of the method of template type
		 * @param methodName the name of {@link CtMethod}
		 */
		public void setBodyOfMethod(String methodName) {
			setBodyOfMethod(tm -> methodName.equals(tm.getSimpleName()));
		}
		/**
		 * Sets a template model from body of the method of template type selected by filter
		 * @param filter the {@link Filter} whose match defines to be used {@link CtMethod}
		 */
		public void setBodyOfMethod(Filter<CtMethod<?>> filter) {
			CtBlock<?> body =  getOneByFilter(filter).getBody();
			setTemplateModel(body.getStatements());
		}

		/**
		 * Sets a template model from return expression of the method of template type selected by filter
		 * @param methodName the name of {@link CtMethod}
		 */
		public void setReturnExpressionOfMethod(String methodName) {
			setReturnExpressionOfMethod(tm -> methodName.equals(tm.getSimpleName()));
		}
		/**
		 * Sets a template model from return expression of the method of template type selected by filter
		 * @param filter the {@link Filter} whose match defines to be used {@link CtExecutable}
		 */
		public void setReturnExpressionOfMethod(Filter<CtMethod<?>> filter) {
			CtMethod<?> method = getOneByFilter(filter);
			CtBlock<?> body = method.getBody();
			if (body.getStatements().size() != 1) {
				throw new SpoonException("The body of " + method.getSignature() + " must contain exactly one statement. But there is:\n" + body.toString());
			}
			CtStatement firstStatement = body.getStatements().get(0);
			if (firstStatement instanceof CtReturn<?> == false) {
				throw new SpoonException("The body of " + method.getSignature() + " must contain return statement. But there is:\n" + body.toString());
			}
			setTemplateModel(((CtReturn<?>) firstStatement).getReturnedExpression());
		}

		private <T extends CtElement> List<T> getByFilter(Filter<T> filter) {
			List<T> elements = templateType.filterChildren(filter).list();
			if (elements == null || elements.isEmpty()) {
				throw new SpoonException("Element not found in " + templateType.getShortRepresentation());
			}
			return elements;
		}
		private <T extends CtElement> T getOneByFilter(Filter<T> filter) {
			List<T> elements = getByFilter(filter);
			if (elements.size() != 1) {
				throw new SpoonException("Only one element must be selected, but there are: " + elements);
			}
			return elements.get(0);
		}
		/**
		 * @param filter whose matches will be removed from the template model
		 */
		public TemplateModelBuilder removeTypeMembers(Filter<CtTypeMember> filter) {
			for (CtTypeMember ctTypeMember : new ArrayList<>(getClonedTemplateType().getTypeMembers())) {
				if (filter.matches(ctTypeMember)) {
					ctTypeMember.delete();
				}
			}
			return this;
		}

		/**
		 * Removes all type members which are annotated by `annotationClass`
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public TemplateModelBuilder removeTypeMembersAnnotatedBy(Class<?>... annotationClass) {
			for (Class<?> ac : annotationClass) {
				removeTypeMembers(tm -> tm.getAnnotation((Class) ac) != null);
			}
			return this;
		}

		/**
		 * @param filter whose matches will be kept in the template. All others will be removed
		 */
		public TemplateModelBuilder keepTypeMembers(Filter<? super CtElement> filter) {
			for (CtTypeMember ctTypeMember : new ArrayList<>(getClonedTemplateType().getTypeMembers())) {
				if (filter.matches(ctTypeMember) == false) {
					ctTypeMember.delete();
				}
			}
			return this;
		}

		/**
		 * Keeps only type members, which are annotated by `annotationClass`. All others will be removed
		 */
		public TemplateModelBuilder keepTypeMembersAnnotatedBy(Class<? extends Annotation> annotationClass) {
			keepTypeMembers(tm -> tm.getAnnotation(annotationClass) != null);
			return this;
		}

		/**
		 * removes super class from the template
		 */
		public TemplateModelBuilder removeSuperClass() {
			getClonedTemplateType().setSuperclass(null);
			return this;
		}

		/**
		 * @param filter super interfaces which matches the filter will be removed
		 */
		public TemplateModelBuilder removeSuperInterfaces(Filter<CtTypeReference<?>> filter) {
			Set<CtTypeReference<?>> superIfaces = new HashSet<>(getClonedTemplateType().getSuperInterfaces());
			boolean changed = false;
			for (Iterator<CtTypeReference<?>> iter = superIfaces.iterator(); iter.hasNext();) {
				if (filter.matches(iter.next())) {
					iter.remove();
					changed = true;
				}
			}
			if (changed) {
				getClonedTemplateType().setSuperInterfaces(superIfaces);
			}
			return this;
		}

		/**
		 * @param filter super interfaces which matches the filter will be kept. Others will be removed
		 */
		public TemplateModelBuilder keepSuperInterfaces(Filter<CtTypeReference<?>> filter) {
			Set<CtTypeReference<?>> superIfaces = new HashSet<>(getClonedTemplateType().getSuperInterfaces());
			boolean changed = false;
			for (Iterator<CtTypeReference<?>> iter = superIfaces.iterator(); iter.hasNext();) {
				if (filter.matches(iter.next())) {
					iter.remove();
					changed = true;
				}
			}
			if (changed) {
				getClonedTemplateType().setSuperInterfaces(superIfaces);
			}
			return this;
		}

		/**
		 * @return a List of {@link CtElement}s, which has to be used as pattern model
		 */
		public List<CtElement> getTemplateModel() {
			return templateModel;
		}

		/**
		 * @param template a {@link CtElement}, which has to be used as pattern model
		 */
		public void setTemplateModel(CtElement template) {
			this.templateModel = Collections.singletonList(template);
		}

		/**
		 * @param template a List of {@link CtElement}s, which has to be used as pattern model
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void setTemplateModel(List<? extends CtElement> template) {
			this.templateModel = (List) template;
		}
	}

	public PatternBuilder configureParameters(Consumer<ParametersBuilder> parametersBuilder) {
		ParametersBuilder pb = new ParametersBuilder(this, parameterInfos);
		parametersBuilder.accept(pb);
		return this;
	}

	public PatternBuilder configureLocalParameters(Consumer<ParametersBuilder> parametersBuilder) {
		ParametersBuilder pb = new ParametersBuilder(this, new HashMap<>());
		parametersBuilder.accept(pb);
		return this;
	}
	/**
	 * adds all standard Template parameters based on {@link TemplateParameter} and {@link Parameter} annotation
	 * @return this to support fluent API
	 */
	public PatternBuilder configureTemplateParameters() {
		return configureTemplateParameters(templateTypeRef.getTypeDeclaration(), null);
	}

	/**
	 * adds all standard Template parameters based on {@link TemplateParameter} and {@link Parameter} annotation
	 * @param templateParameters parameters, which will be used in substitution. It is needed here,
	 * 			when parameter value types influences which AST nodes will be the target of substitution in legacy template patterns
	 * @return this to support fluent API
	 */
	public PatternBuilder configureTemplateParameters(Map<String, Object> templateParameters) {
		return configureTemplateParameters(templateTypeRef.getTypeDeclaration(), templateParameters);
	}

	/**
	 * adds all standard Template parameters based on {@link TemplateParameter} and {@link Parameter} annotation
	 * @param templateType the CtType which contains template parameters
	 * @param templateParameters parameters, which will be used in substitution. It is needed here,
	 * 			because parameter value types influences which AST nodes will be the target of substitution
	 * @return this to support fluent API
	 */
	private PatternBuilder configureTemplateParameters(CtType<?> templateType, Map<String, Object> templateParameters) {
		configureParameters(pb -> {
			templateType.map(new AllTypeMembersFunction()).forEach((CtTypeMember typeMember) -> {
				configureTemplateParameter(templateType, templateParameters, pb, typeMember);
			});
			if (templateParameters != null) {
				//configure template parameters based on parameter values only - these without any declaration in Template
				templateParameters.forEach((paramName, paramValue) -> {
					if (pb.isSubstituted(paramName) == false) {
						//and only these parameters whose name isn't already handled by explicit template parameters
						if (paramValue instanceof CtTypeReference<?>) {
							pb.parameter(paramName)
								.setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE)
								.byLocalType(templateType, paramName);
						}
						pb.parameter(paramName)
							.setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE)
							.bySubstring(paramName);
					}
				});
			}
		});
		return this;
	}

	private void configureTemplateParameter(CtType<?> templateType, Map<String, Object> templateParameters, ParametersBuilder pb, CtTypeMember typeMember) {
		Factory f = typeMember.getFactory();
		CtTypeReference<TemplateParameter> templateParamRef = f.Type().createReference(TemplateParameter.class);
		CtTypeReference<CtTypeReference> typeReferenceRef = f.Type().createReference(CtTypeReference.class);
		CtTypeReference<CtStatement> ctStatementRef = f.Type().createReference(CtStatement.class);
		Parameter param = typeMember.getAnnotation(Parameter.class);
		if (param != null) {
			if (typeMember instanceof CtField) {
				CtField<?> paramField = (CtField<?>) typeMember;
				/*
				 * We have found a CtField annotated by @Parameter.
				 * Use it as Pattern parameter
				 */
				String fieldName = typeMember.getSimpleName();
				String stringMarker = (param.value() != null && param.value().length() > 0) ? param.value() : fieldName;
				//for the compatibility reasons with Parameters.getNamesToValues(), use the proxy name as parameter name
				String parameterName = stringMarker;

				CtTypeReference<?> paramType = paramField.getType();

				if (paramType.isSubtypeOf(f.Type().ITERABLE) || paramType instanceof CtArrayTypeReference<?>) {
					//parameter is a multivalue
					pb.parameter(parameterName).setContainerKind(ContainerKind.LIST).bySimpleName(stringMarker);
				} else if (paramType.isSubtypeOf(typeReferenceRef) || paramType.getQualifiedName().equals(Class.class.getName())) {
					/*
					 * parameter with value type TypeReference or Class, identifies replacement of local type whose name is equal to parameter name
					 */
					CtTypeReference<?> nestedType = getLocalTypeRefBySimpleName(templateType, stringMarker);
					if (nestedType != null) {
						//all references to nestedType has to be replaced
						pb.parameter(parameterName).byType(nestedType);
					}
					//and replace the variable references by class access
					pb.parameter(parameterName).byVariable(paramField);
				} else if (paramType.getQualifiedName().equals(String.class.getName())) {
					CtTypeReference<?> nestedType = getLocalTypeRefBySimpleName(templateType, stringMarker);
					if (nestedType != null) {
						//There is a local type with such name. Replace it
						pb.parameter(parameterName).byType(nestedType);
					}
				} else if (paramType.isSubtypeOf(templateParamRef)) {
					pb.parameter(parameterName)
						.byTemplateParameterReference(paramField);
					//if there is any invocation of method with name matching to stringMarker, then substitute their invocations too.
					templateType.getMethodsByName(stringMarker).forEach(m -> {
						pb.parameter(parameterName).byInvocation(m);
					});
				} else if (paramType.isSubtypeOf(ctStatementRef)) {
					//if there is any invocation of method with name matching to stringMarker, then substitute their invocations too.
					templateType.getMethodsByName(stringMarker).forEach(m -> {
						pb.parameter(parameterName).setContainerKind(ContainerKind.LIST).byInvocation(m);
					});
				} else {
					//it is not a String. It is used to substitute CtLiteral of parameter value
					pb.parameter(parameterName)
						//all occurrences of parameter name in pattern model are subject of substitution
						.byVariable(paramField);
				}
				if (paramType.getQualifiedName().equals(Object.class.getName()) && templateParameters != null) {
					//if the parameter type is Object, then detect the real parameter type from the parameter value
					Object value = templateParameters.get(parameterName);
					if (value instanceof CtLiteral || value instanceof CtTypeReference) {
						/*
						 * the real parameter value is CtLiteral or CtTypeReference
						 * We should replace all method invocations whose name equals to stringMarker
						 * by that CtLiteral or qualified name of CtTypeReference
						 */
						ParameterInfo pi = pb.parameter(parameterName).getCurrentParameter();
						pb.queryModel().filterChildren((CtInvocation<?> inv) -> {
							return inv.getExecutable().getSimpleName().equals(stringMarker);
						}).forEach((CtInvocation<?> inv) -> {
							pb.addSubstitutionRequest(pi, inv);
						});
					}
				}

				//any value can be converted to String. Substitute content of all string attributes
				pb.parameter(parameterName).setConflictResolutionMode(ConflictResolutionMode.KEEP_OLD_NODE)
					.bySubstring(stringMarker);

				if (templateParameters != null) {
					//handle automatic live statements
					addLiveStatements(fieldName, templateParameters.get(parameterName));
				}
			} else {
				//TODO CtMethod was may be supported in old Template engine!!!
				throw new SpoonException("Template Parameter annotation on " + typeMember.getClass().getName() + " is not supported");
			}
		} else if (typeMember instanceof CtField<?> && ((CtField<?>) typeMember).getType().isSubtypeOf(templateParamRef)) {
			CtField<?> field = (CtField<?>) typeMember;
			String parameterName = typeMember.getSimpleName();
			Object value = templateParameters == null ? null : templateParameters.get(parameterName);
			Class valueType = null;
			boolean multiple = false;
			if (value != null) {
				valueType = value.getClass();
				if (value instanceof CtBlock) {
					//the CtBlock in this situation is expected as container of Statements in legacy templates.
					multiple = true;
				}
			}
			pb.parameter(parameterName).setValueType(valueType).setContainerKind(multiple ? ContainerKind.LIST : ContainerKind.SINGLE)
				.byTemplateParameterReference(field);

			if (templateParameters != null) {
				//handle automatic live statements
				addLiveStatements(parameterName, templateParameters.get(parameterName));
			}
		}

	}

	private void addLiveStatements(String variableName, Object paramValue) {
		if (paramValue != null && paramValue.getClass().isArray()) {
			//the parameters with Array value are meta parameters in legacy templates
			configureLiveStatements(sb -> {
				//we are adding live statements automatically from legacy templates,
				//so do not fail if it is sometime not possible - it means that it is not a live statement then
				sb.setFailOnMissingParameter(false);
				sb.byVariableName(variableName);
			});
		}
	}

	/**
	 * Configures live statements
	 *
	 * For example if the `for` statement in this pattern model
	 * <pre><code>
	 * for(Object x : $iterable$) {
	 *	System.out.println(x);
	 * }
	 * </code></pre>
	 * is configured as live statement and a Pattern is substituted
	 * using parameter <code>$iterable$ = new String[]{"A", "B", "C"}</code>
	 * then pattern generated this code
	 * <pre><code>
	 * System.out.println("A");
	 * System.out.println("B");
	 * System.out.println("C");
	 * </code></pre>
	 * because live statements are executed during substitution process and are not included in generated result.
	 *
	 * The live statements may be used in PatternMatching process (opposite to Pattern substitution) too.
	 * @param consumer
	 * @return this to support fluent API
	 */
	public PatternBuilder configureLiveStatements(Consumer<LiveStatementsBuilder> consumer) {
		LiveStatementsBuilder sb = new LiveStatementsBuilder(this);
		consumer.accept(sb);
		return this;
	}

	static CtTypeReference<?> getLocalTypeRefBySimpleName(CtType<?> templateType, String typeSimpleName) {
		CtType<?> type = templateType.getNestedType(typeSimpleName);
		if (type != null) {
			return type.getReference();
		}
		type = templateType.getPackage().getType(typeSimpleName);
		if (type != null) {
			return type.getReference();
		}
		Set<String> typeQNames = new HashSet<>();
		templateType
			.filterChildren((CtTypeReference<?> ref) -> typeSimpleName.equals(ref.getSimpleName()))
			.forEach((CtTypeReference<?> ref) -> typeQNames.add(ref.getQualifiedName()));
		if (typeQNames.size() > 1) {
			throw new SpoonException("The type parameter " + typeSimpleName + " is ambiguous. It matches multiple types: " + typeQNames);
		}
		if (typeQNames.size() == 1) {
			return templateType.getFactory().Type().createReference(typeQNames.iterator().next());
		}
		return null;
	}

	public boolean hasParameterInfo(String parameterName) {
		return parameterInfos.containsKey(parameterName);
	}

	protected Factory getFactory() {
		if (templateTypeRef != null) {
			return templateTypeRef.getFactory();
		}
		if (patternModel.size() > 0) {
			return patternModel.get(0).getFactory();
		}
		throw new SpoonException("PatternBuilder has no CtElement to provide a Factory");
	}

	private static void checkTemplateType(CtType<?> type) {
		if (type == null) {
			throw new SpoonException("Cannot create Pattern from null Template type.");
		}
		if (type.isShadow()) {
			throw new SpoonException("Cannot create Pattern from shadow Template type. Add sources of Template type into spoon model.");
		}
	}
	public List<CtElement> getPatternModel() {
		return patternModel;
	}
	/**
	 * Calls `consumer` once for each {@link RootNode} element which uses `parameter`
	 * @param parameter to be checked {@link ParameterInfo}
	 * @param consumer receiver of calls
	 */
	public void forEachNodeOfParameter(ParameterInfo parameter, Consumer<RootNode> consumer) {
		patternNodes.forEachParameterInfo((paramInfo, vr) -> {
			if (paramInfo == parameter) {
				consumer.accept(vr);
			}
		});
	}
	/**
	 * @return true if produced Pattern will append generated by comments
	 */
	public boolean isAddGeneratedBy() {
		return addGeneratedBy;
	}
	/**
	 * @param addGeneratedBy true when generated by comments have to be appended to each generated type member
	 * @return this to support fluent API
	 */
	public PatternBuilder setAddGeneratedBy(boolean addGeneratedBy) {
		this.addGeneratedBy = addGeneratedBy;
		return this;
	}

	/**
	 * @return true if generated result has to be evaluated to apply simplifications.
	 */
	public boolean isAutoSimplifySubstitutions() {
		return autoSimplifySubstitutions;
	}
	/**
	 * @param autoSimplifySubstitutions true if generated result of each substituted has to be evaluated to apply simplifications.
	 * 	The rule is applied only to substitutions defined after this call
	 * @return this to support fluent API
	 */
	public PatternBuilder setAutoSimplifySubstitutions(boolean autoSimplifySubstitutions) {
		this.autoSimplifySubstitutions = autoSimplifySubstitutions;
		return this;
	}
}
