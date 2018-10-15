/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.pattern.internal;

import spoon.pattern.Generator;
import spoon.pattern.PatternBuilder;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern.internal.node.RootNode;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.util.ImmutableMap;
import spoon.support.util.ImmutableMapImpl;

import java.util.List;
import java.util.Map;

/**
 * Drives generation process
 */
public class DefaultGenerator implements Generator {
	protected final Factory factory;
	private boolean addGeneratedBy = false;
	private ListOfNodes nodes;

	public DefaultGenerator(Factory factory, ListOfNodes nodes) {
		this.nodes = nodes;
		this.factory = factory;
	}

	/**
	 * Generates one target depending on kind of this {@link RootNode}, expected `expectedType` and input `parameters`
	 * @param node to be generated node
	 * @param parameters {@link ImmutableMap}
	 * @param expectedType defines {@link Class} of returned value
	 *
	 * @return a generate value or null
	 */
	public <T> T generateSingleTarget(RootNode node, ImmutableMap parameters, Class<T> expectedType) {
		ResultHolder.Single<T> result = new ResultHolder.Single<>(expectedType);
		generateTargets(node, result, parameters);
		return result.getResult();
	}

	/**
	 * Generates zero, one or more targets depending on kind of this {@link RootNode}, expected `expectedType` and input `parameters`
	 * @param node to be generated node
	 * @param parameters {@link ImmutableMap}
	 * @param expectedType defines {@link Class} of returned value
	 *
	 * @return a {@link List} of generated targets
	 */
	public <T> List<T> generateTargets(RootNode node, ImmutableMap parameters, Class<T> expectedType) {
		ResultHolder.Multiple<T> result = new ResultHolder.Multiple<>(expectedType);
		generateTargets(node, result, parameters);
		return result.getResult();
	}


	/**
	 * Generates zero, one or more target depending on kind of this {@link RootNode}, expected `result` and input `parameters`
	 * @param node to be generated node
	 * @param result the holder which receives the generated node
	 * @param parameters the input parameters
	 */
	public <T> void generateTargets(RootNode node, ResultHolder<T> result, ImmutableMap parameters) {
		node.generateTargets(this, result, parameters);
		if (node.isSimplifyGenerated()) {
			// simplify this element, it contains a substituted element
			result.mapEachResult(element -> {
				if (element instanceof CtCodeElement) {
					CtCodeElement code = (CtCodeElement) element;
					try {
						code = code.partiallyEvaluate();
						if (result.getRequiredClass().isInstance(code)) {
							return (T) code;
						}
						/*
						 * else the simplified code is not matching with required type. For example
						 * statement String.class.getName() was converted to expression
						 * "java.lang.String"
						 */
					} catch (SpoonClassNotFoundException e) {
						// ignore it. Do not simplify this element
						getFactory().getEnvironment()
								.debugMessage("Partial evaluation was skipped because of: " + e.getMessage());
					}
				}
				return element;
			});
		}
	}

	/**
	 * Returns zero, one or more values into `result`. The value comes from `parameters` from the location defined by `parameterInfo`
	 * @param parameterInfo the {@link ParameterInfo}, which describes exact parameter from `parameters`
	 * @param result the holder which receives the generated node
	 * @param parameters the input parameters
	 */
	public <T> void getValueAs(ParameterInfo parameterInfo, ResultHolder<T> result, ImmutableMap parameters) {
		parameterInfo.getValueAs(factory, result, parameters);
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	public DefaultGenerator setAddGeneratedBy(boolean addGeneratedBy) {
		this.addGeneratedBy = addGeneratedBy;
		return this;
	}

	/**
	 * Adds a Generated by comment to the javadoc of generatedElement
	 * @param generatedElement a newly generated element
	 * @param genBy the documentation to be added
	 */
	public void applyGeneratedBy(CtElement generatedElement, String genBy) {
		if (isAddGeneratedBy() && generatedElement instanceof CtTypeMember) {
			if (genBy != null) {
				addGeneratedByComment(generatedElement, genBy);
			}
		}
	}
	public String getGeneratedByComment(CtElement ele) {
		SourcePosition pos = ele.getPosition();
		if (pos != null && pos.isValidPosition()) {
			CompilationUnit cu = pos.getCompilationUnit();
			if (cu != null) {
				CtType<?> mainType = cu.getMainType();
				if (mainType != null) {
					StringBuilder result = new StringBuilder();
					result.append("Generated by ");
					result.append(mainType.getQualifiedName());
					appendInnerTypedElements(result, mainType, ele);
					result.append('(');
					result.append(mainType.getSimpleName());
					result.append(".java:");
					result.append(pos.getLine());
					result.append(')');
					return  result.toString();
				}
			}
		}
		return null;
	}
	private void appendInnerTypedElements(StringBuilder result, CtType<?> mainType, CtElement ele) {
		CtTypeMember typeMember = getFirst(ele, CtTypeMember.class);
		if (typeMember != null && isMainType(typeMember, mainType) == false) {
			if (typeMember.isParentInitialized()) {
				appendInnerTypedElements(result, mainType, typeMember.getParent());
			}
			if (typeMember instanceof CtType) {
				result.append('$');
			} else {
				result.append('#');
			}
			result.append(typeMember.getSimpleName());
		}
	}

	private boolean isMainType(CtTypeMember tm, CtType<?> mainType) {
		if (tm instanceof CtType) {
			return mainType.getQualifiedName().equals(((CtType) tm).getQualifiedName());
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T extends CtElement> T getFirst(CtElement ele, Class<T> clazz) {
		if (ele != null) {
			if (clazz.isAssignableFrom(ele.getClass())) {
				return (T) ele;
			}
			if (ele.isParentInitialized()) {
				return getFirst(ele.getParent(), clazz);
			}
		}
		return null;
	}

	private void addGeneratedByComment(CtElement ele, String generatedBy) {
		if (generatedBy == null) {
			return;
		}
		String EOL = System.getProperty("line.separator");
		CtComment comment = getJavaDoc(ele);
		String content = comment.getContent();
		if (!content.trim().isEmpty()) {
			content += EOL + EOL;
		}
		content += generatedBy;
		comment.setContent(content);
	}

	private CtComment getJavaDoc(CtElement ele) {
		for (CtComment comment : ele.getComments()) {
			if (comment.getCommentType() == CtComment.CommentType.JAVADOC) {
				return comment;
			}
		}
		CtComment c = ele.getFactory().Code().createComment("", CtComment.CommentType.JAVADOC);
		ele.addComment(c);
		return c;
	}

	@Override
	public <T extends CtElement> List<T> generate(Class<T> valueType, ImmutableMap params) {
		return setAddGeneratedBy(isAddGeneratedBy()).generateTargets(nodes, params, valueType);
	}

	@Override
	public <T extends CtElement> List<T> generate(Class<T> valueType, Map<String, Object> params) {
		return generate(valueType, new ImmutableMapImpl(params));
	}

	@Override
	public <T extends CtType<?>> T generateType(String typeQualifiedName, Map<String, Object> params) {
		CtTypeReference<?> newTypeRef = factory.Type().createReference(typeQualifiedName);
		CtPackage ownerPackage = newTypeRef.getFactory().Package().getOrCreate(newTypeRef.getPackage().getQualifiedName());
		return createType(ownerPackage, newTypeRef.getSimpleName(), params);
	}

	/**
	 * Generates type in the package `ownerPackage` with simple name `typeSimpleName` using this {@link Generator} and provided `params`
	 *
	 * Note: the root of pattern element must be one or more types.
	 *
	 * @param ownerPackage the target package
	 * @param typeSimpleName the simple name of future generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	@SuppressWarnings("unchecked")
	private <T extends CtType<?>> T createType(CtPackage ownerPackage, String typeSimpleName, Map<String, Object> params) {
		@SuppressWarnings({ "rawtypes" })
		List<CtType> types = generate(CtType.class, new ImmutableMapImpl(params,
				PatternBuilder.TARGET_TYPE, ownerPackage.getFactory().Type().createReference(getQualifiedName(ownerPackage, typeSimpleName))));
		T result = null;
		for (CtType<?> type : types) {
			ownerPackage.addType(type);
			if (type.getSimpleName().equals(typeSimpleName)) {
				result = (T) type;
			}
		}
		return result;
	}

	@Override
	public <T extends CtTypeMember> List<T> addToType(Class<T> valueType, Map<String, Object> params, CtType<?> targetType) {
		List<T> results = generate(valueType, new ImmutableMapImpl(params, PatternBuilder.TARGET_TYPE, targetType.getReference()));
		for (T result : results) {
			if (result instanceof CtTypeMember) {
				targetType.addTypeMember((CtTypeMember) result);
			}
		}
		return results;
	}

	private static String getQualifiedName(CtPackage pckg, String simpleName) {
		if (pckg.isUnnamedPackage()) {
			return simpleName;
		}
		return pckg.getQualifiedName() + CtPackage.PACKAGE_SEPARATOR + simpleName;
	}

	public boolean isAddGeneratedBy() {
		return addGeneratedBy;
	}

}
