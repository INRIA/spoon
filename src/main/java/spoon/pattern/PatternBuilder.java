/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import spoon.SpoonException;
import spoon.pattern.internal.ValueConvertor;
import spoon.pattern.internal.ValueConvertorImpl;
import spoon.pattern.internal.node.ElementNode;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern.internal.node.RootNode;
import spoon.pattern.internal.parameter.AbstractParameterInfo;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.QueryFactory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.Experimental;

/**
 * The master class to create a {@link Pattern} instance.
 *
 * Main documentation at http://spoon.gforge.inria.fr/pattern.html.
 */
@Experimental
public class PatternBuilder {

	public static final String TARGET_TYPE = "targetType";

	/**
	 * Creates a {@link PatternBuilder} from the List of template elements
	 * @param patternModel a List of Spoon AST nodes, which represents a template of to be generated or to be matched code
	 * @return new instance of {@link PatternBuilder}
	 */
	public static PatternBuilder create(List<? extends CtElement> patternModel) {
		return new PatternBuilder(patternModel);
	}

	public static PatternBuilder create(CtElement... elems) {
		return new PatternBuilder(Arrays.asList(elems));
	}

	private final List<CtElement> patternModel;
	protected final ListOfNodes patternNodes;
	private final Map<CtElement, RootNode> patternElementToSubstRequests = new IdentityHashMap<>();
	private final Set<RootNode> explicitNodes = Collections.newSetFromMap(new IdentityHashMap<>());

	private CtTypeReference<?> templateTypeRef;
	private final Map<String, AbstractParameterInfo> parameterInfos = new HashMap<>();
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

	protected PatternBuilder(List<? extends CtElement> template) {
		if (template == null) {
			throw new SpoonException("Cannot create a Pattern from an null model");
		}
		this.templateTypeRef = getDeclaringTypeRef(template);
		this.patternModel = Collections.unmodifiableList(new ArrayList<>(template));
		this.valueConvertor = new ValueConvertorImpl();
		patternNodes = ElementNode.create(this.patternModel, patternElementToSubstRequests);
		patternQuery = new PatternBuilder.PatternQuery(getFactory().Query(), patternModel);
		if (this.templateTypeRef != null) {
			configurePatternParameters(pb -> {
				pb.parameter(TARGET_TYPE).byType(this.templateTypeRef).setValueType(CtTypeReference.class);
			});
		}
	}

	private CtTypeReference<?> getDeclaringTypeRef(List<? extends CtElement> template) {
		CtType<?> type = null;
		for (CtElement ctElement : template) {
			CtType t;
			if (ctElement instanceof CtType) {
				t = (CtType) ctElement;
				type = mergeType(type, t);
			}
			t = ctElement.getParent(CtType.class);
			if (t != null) {
				type = mergeType(type, t);
			}
		}
		return type == null ? null : type.getReference();
	}

	private CtType<?> mergeType(CtType<?> type, CtType t) {
		if (type == null) {
			return t;
		}
		if (type == t) {
			return type;
		}
		if (type.hasParent(t)) {
			return t;
		}
		if (t.hasParent(type)) {
			return type;
		}
		throw new SpoonException("The pattern on nested types are not supported.");
	}

	/**
	 * @param element a CtElement
	 * @return {@link RootNode}, which handles matching/generation of an `object` from the source spoon AST.
	 * or null, if there is none
	 */
	RootNode getOptionalPatternNode(CtElement element, CtRole... roles) {
		return getPatternNode(true, element, roles);
	}
	RootNode getPatternNode(CtElement element, CtRole... roles) {
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
		handleConflict(conflictMode, oldNode, newNode, tobeUsedNode -> {
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

	/**
	 * Changes the Pattern by way the `node` is used at position of template element.
	 * It is used for example to mark that `element` as pattern parameter
	 * @param element
	 * @param node
	 * @param conflictMode
	 */
	void setNodeOfElement(CtElement element, RootNode node, ConflictResolutionMode conflictMode) {
		modifyNodeOfElement(element, conflictMode, oldNode -> {
			return node;
		});
	}

	/**
	 * Changes the Pattern by way the `node` is used at position of template element attribute of `role`.
	 * It is used for example to mark that `element` as pattern parameter
	 * @param element
	 * @param role
	 * @param node
	 * @param conflictMode
	 */
	void setNodeOfAttributeOfElement(CtElement element, CtRole role, RootNode node, ConflictResolutionMode conflictMode) {
		modifyNodeOfAttributeOfElement(element, role, conflictMode, oldAttrNode -> {
			return node;
		});
	}

	/**
	 * @param element to be checked element
	 * @return true if element `element` is a template or a child of template
	 */
	boolean isInModel(CtElement element) {
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
		return new Pattern(getFactory(), new ListOfNodes(patternNodes.getNodes())).setAddGeneratedBy(isAddGeneratedBy());
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
	ValueConvertor getDefaultValueConvertor() {
		return valueConvertor;
	}

	/**
	 * @param valueConvertor default {@link ValueConvertor}, which will be assigned to all {@link ParameterInfo}s created after this call
	 * @return this to support fluent API
	 */
	PatternBuilder setDefaultValueConvertor(ValueConvertor valueConvertor) {
		this.valueConvertor = valueConvertor;
		return this;
	}

	/**
	 * All the variable references, whose variables are out of the template model
	 * are automatically marked as pattern parameters
	 * @return this to support fluent API
	 */
	public PatternBuilder configurePatternParameters() {
		configurePatternParameters(pb -> {
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
	 * Configure pattern parameters with a {@link PatternParameterConfigurator}
	 * @return this
	 */
	public PatternBuilder configurePatternParameters(Consumer<PatternParameterConfigurator> parametersBuilder) {
		PatternParameterConfigurator pb = new PatternParameterConfigurator(this, parameterInfos);
		parametersBuilder.accept(pb);
		return this;
	}

	/**
	 * Used by inline for each statement to define template parameter which is local in the scope of the inline statement
	 */
	PatternBuilder configureLocalParameters(Consumer<PatternParameterConfigurator> parametersBuilder) {
		PatternParameterConfigurator pb = new PatternParameterConfigurator(this, new HashMap<>());
		parametersBuilder.accept(pb);
		return this;
	}

	/**
	 * Configures inlined statements
	 *
	 * For example if the `for` statement in this pattern model
	 * <pre><code>
	 * for(Object x : $iterable$) {
	 *	System.out.println(x);
	 * }
	 * </code></pre>
	 * is configured as inline statement and a Pattern is substituted
	 * using parameter <code>$iterable$ = new String[]{"A", "B", "C"}</code>
	 * then pattern generated this code
	 * <pre><code>
	 * System.out.println("A");
	 * System.out.println("B");
	 * System.out.println("C");
	 * </code></pre>
	 * because inline statements are executed during substitution process and are not included in generated result.
	 *
	 * The inline statements may be used in PatternMatching process (opposite to Pattern substitution) too.
	 * @param consumer
	 * @return this to support fluent API
	 */
	public PatternBuilder configureInlineStatements(Consumer<InlinedStatementConfigurator> consumer) {
		InlinedStatementConfigurator sb = new InlinedStatementConfigurator(this);
		consumer.accept(sb);
		return this;
	}

	static String getLocalTypeRefBySimpleName(CtType<?> templateType, String typeSimpleName) {
		CtType<?> type = templateType.getNestedType(typeSimpleName);
		if (type != null) {
			return type.getQualifiedName();
		}
		type = templateType.getPackage().getType(typeSimpleName);
		if (type != null) {
			return type.getQualifiedName();
		}
		Set<String> typeQNames = new HashSet<>();
		templateType
			.filterChildren((CtTypeReference<?> ref) -> typeSimpleName.equals(ref.getSimpleName()))
			.forEach((CtTypeReference<?> ref) -> typeQNames.add(ref.getQualifiedName()));
		if (typeQNames.size() > 1) {
			throw new SpoonException("The type parameter " + typeSimpleName + " is ambiguous. It matches multiple types: " + typeQNames);
		}
		if (typeQNames.size() == 1) {
			return typeQNames.iterator().next();
		}
		return null;
	}

	AbstractParameterInfo getParameterInfo(String parameterName) {
		return parameterInfos.get(parameterName);
	}

	protected Factory getFactory() {
		if (templateTypeRef != null) {
			return templateTypeRef.getFactory();
		}
		if (!patternModel.isEmpty()) {
			return patternModel.get(0).getFactory();
		}
		throw new SpoonException("PatternBuilder has no CtElement to provide a Factory");
	}

	/**
	 * @return a {@link CtElement}s which are the template model of this Pattern
	 */
	List<CtElement> getPatternModel() {
		return patternModel;
	}
	/**
	 * Calls `consumer` once for each {@link RootNode} element which uses `parameter`
	 * @param parameter to be checked {@link ParameterInfo}
	 * @param consumer receiver of calls
	 */
	void forEachNodeOfParameter(ParameterInfo parameter, Consumer<RootNode> consumer) {
		patternNodes.forEachParameterInfo((paramInfo, vr) -> {
			if (paramInfo == parameter) {
				consumer.accept(vr);
			}
		});
	}
	/**
	 * @return true if produced Pattern will append generated by comments
	 */
	private boolean isAddGeneratedBy() {
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

	CtTypeReference<?> getTemplateTypeRef() {
		return templateTypeRef;
	}
}
