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
package spoon.template;

import java.util.List;

import spoon.pattern.Pattern;
import spoon.pattern.matcher.Match;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.node.ModelNode;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.support.util.ParameterValueProvider;
import spoon.support.util.ParameterValueProviderFactory;
import spoon.support.util.UnmodifiableParameterValueProvider;

import static spoon.pattern.matcher.TobeMatched.getMatchedParameters;

/**
 * This class defines an engine for matching a template to pieces of code.
 */
public class TemplateMatcher implements Filter<CtElement> {

	private final Pattern pattern;
	private final CtElement templateRoot;

	/**
	 * Holds matches of template parameters name to matching values.
	 * The values can be:
	 * <li>
	 * <ul>single CtElement
	 * <ul>list or set of CtElements
	 * <ul>any value of primitive attribute, like String, Enum value, number, ...
	 * </li>
	 */
	private ParameterValueProvider matches;
	private ParameterValueProviderFactory parameterValueProviderFactory = UnmodifiableParameterValueProvider.Factory.INSTANCE;

	/**
	 * Constructs a matcher for a given template.
	 *
	 * @param templateRoot the template to match against
	 *
	 */
	public TemplateMatcher(CtElement templateRoot) {
		this(templateRoot, templateRoot.getParent(CtClass.class));
	}

	/**
	 * Constructs a matcher for a given template. All parameters must be declared using Template fields.
	 *
	 * @param templateRoot the template model to match against. It must be a child of `templateType`
	 * @param templateType the class of the template, which contains all the template parameters
	 */
	public TemplateMatcher(CtElement templateRoot, CtClass<?> templateType) {
		this.pattern = TemplateBuilder.createPattern(templateRoot, templateType, null).build();
		this.templateRoot = templateRoot;
	}

	@Override
	public boolean matches(CtElement element) {
		//clear all matches from previous run before we start matching with `element`
		ModelNode patternModel = pattern.getModelValueResolver();
		if (element == templateRoot) {
			// This case can occur when we are scanning the entire package for example see TemplateTest#testTemplateMatcherWithWholePackage
			// Correct template matches itself of course, but client does not want that
			return false;
		}
		matches = getMatchedParameters(patternModel.matchAllWith(TobeMatched.create(
				parameterValueProviderFactory.createParameterValueProvider(),
				ContainerKind.SINGLE,
				element)));
		return matches != null;
	}

	/**
	 * Returns all the matches where the keys are the corresponding
	 * template parameters.
	 * The {@link #matches(CtElement)} method must have been called before and must return true.
	 * Otherwise it returns null.
	 */
	public ParameterValueProvider getMatches() {
		return matches;
	}

	/**
	 * Finds all target program sub-trees that correspond to a template.
	 *
	 * @param targetRoot
	 * 		the target to be tested for match
	 * @return the matched elements
	 */
	public <T extends CtElement> List<T> find(final CtElement targetRoot) {
		return targetRoot.filterChildren(this).list();
	}

	/**
	 * Finds all target program sub-trees that correspond to a template
	 * and calls consumer.accept(matchingElement, )
	 * @param rootElement the root of to be searched AST
	 * @param consumer the receiver of matches
	 */
	public void forEachMatch(CtElement rootElement, CtConsumer<Match> consumer) {
		pattern.forEachMatch(rootElement, consumer);
	}
}
