/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.visitor.Filter;
import spoon.support.Experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to select parts of AST to be used as a model of a {@link PatternBuilder}.
 *
 * Main documentation at http://spoon.gforge.inria.fr/pattern.html.
 */
@Experimental
public class PatternBuilderHelper {
	/**
	 * The original type, which contains the AST of pattern model
	 */
	private final CtType<?> patternType;
	/**
	 * optional clone of patternType. It is created when AST of CtType has to be modified
	 * before it can become a model of {@link Pattern}
	 */
	private CtType<?> clonedPatternType;
	/**
	 * holds the built pattern model
	 */
	private List<CtElement> elements = null;

	public PatternBuilderHelper(CtType<?> templateTemplate) {
		this.patternType = templateTemplate;
	}

	/**
	 * Returns clone of the patternType.
	 * The clone is done only once. Later calls returns cached clone.
	 * @return
	 */
	private CtType<?> getClonedPatternType() {
		if (clonedPatternType == null) {
			clonedPatternType = patternType.clone();
			if (patternType.isParentInitialized()) {
				//set parent package, to keep origin qualified name of the Template. It is needed for correct substitution of Template name by target type reference
				clonedPatternType.setParent(patternType.getParent());
			}
			setElements(Collections.singletonList(clonedPatternType));
		}
		return clonedPatternType;
	}

	/**
	 * Sets a template model from {@link CtTypeMember} of a template type
	 * @param typeMemberName the name of the {@link CtTypeMember} of a template type
	 */
	public PatternBuilderHelper setTypeMember(String typeMemberName) {
		setTypeMember(tm -> typeMemberName.equals(tm.getSimpleName()));
		return this;
	}
	/**
	 * Sets a template model from {@link CtTypeMember} of a template type
	 * @param filter the {@link Filter} whose match defines to be used {@link CtTypeMember}
	 */
	private PatternBuilderHelper setTypeMember(Filter<CtTypeMember> filter) {
		setElements(getByFilter(filter));
		return this;
	}

	/**
	 * Sets a template model from body of the method of template type
	 * @param methodName the name of {@link CtMethod}
	 */
	public PatternBuilderHelper setBodyOfMethod(String methodName) {
		setBodyOfMethod(tm -> methodName.equals(tm.getSimpleName()));
		return this;
	}
	/**
	 * Sets a template model from body of the method of template type selected by filter
	 * @param filter the {@link Filter} whose match defines to be used {@link CtMethod}
	 */
	private void setBodyOfMethod(Filter<CtMethod<?>> filter) {
		CtBlock<?> body =  getOneByFilter(filter).getBody();
		setElements(body.getStatements());
	}

	/**
	 * Sets a template model from return expression of the method of template type selected by filter
	 * @param methodName the name of {@link CtMethod}
	 */
	public PatternBuilderHelper setReturnExpressionOfMethod(String methodName) {
		setReturnExpressionOfMethod(tm -> methodName.equals(tm.getSimpleName()));
		return this;
	}
	/**
	 * Sets a template model from return expression of the method of template type selected by filter
	 * @param filter the {@link Filter} whose match defines to be used {@link CtExecutable}
	 */
	private void setReturnExpressionOfMethod(Filter<CtMethod<?>> filter) {
		CtMethod<?> method = getOneByFilter(filter);
		CtBlock<?> body = method.getBody();
		if (body.getStatements().size() != 1) {
			throw new SpoonException("The body of " + method.getSignature() + " must contain exactly one statement. But there is:\n" + body.toString());
		}
		CtStatement firstStatement = body.getStatements().get(0);
		if (firstStatement instanceof CtReturn<?> == false) {
			throw new SpoonException("The body of " + method.getSignature() + " must contain return statement. But there is:\n" + body.toString());
		}
		setElements(Collections.singletonList(((CtReturn<?>) firstStatement).getReturnedExpression()));
	}

	private <T extends CtElement> List<T> getByFilter(Filter<T> filter) {
		List<T> elements = patternType.filterChildren(filter).list();
		if (elements == null || elements.isEmpty()) {
			throw new SpoonException("Element not found in " + patternType.getShortRepresentation());
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
	 * @param filter whose matches will be kept in the template. All others will be removed
	 */
	public PatternBuilderHelper keepTypeMembers(Filter<? super CtElement> filter) {
		for (CtTypeMember ctTypeMember : new ArrayList<>(getClonedPatternType().getTypeMembers())) {
			if (filter.matches(ctTypeMember) == false) {
				ctTypeMember.delete();
			}
		}
		return this;
	}

	/**
	 * removes super class from the template
	 */
	public PatternBuilderHelper removeSuperClass() {
		getClonedPatternType().setSuperclass(null);
		return this;
	}

	/**
	 * @return a List of {@link CtElement}s, which has to be used as pattern model
	 */
	public List<CtElement> getPatternElements() {
		return elements;
	}

	/**
	 * @param template a List of {@link CtElement}s, which has to be used as pattern model
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setElements(List<? extends CtElement> template) {
		this.elements = (List) template;
	}
}
