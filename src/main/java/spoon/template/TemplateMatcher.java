/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import static spoon.pattern.internal.matcher.TobeMatched.getMatchedParameters;

import java.util.List;

import spoon.pattern.Match;
import spoon.pattern.Pattern;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.support.util.ImmutableMap;
import spoon.support.util.ImmutableMapImpl;

/**
 * This class defines an engine for matching a template to pieces of code.
 */
public class TemplateMatcher implements Filter<CtElement> {

	private final Pattern pattern;
	private ListOfNodes patternModel;
	private final CtElement templateRoot;

	/**
	 * Holds matches of template parameters name to matching values.
	 * The values can be:
	 * <ul>
	 * <li>single CtElement</li>
	 * <li>list or set of CtElements</li>
	 * <li>any value of primitive attribute, like String, Enum value, number, ...</li>
	 * </ul>
	 */
	private ImmutableMap matches;

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
		this.pattern = TemplateBuilder.createPattern(templateRoot, templateType, null).build(nodes -> this.patternModel = nodes);
		this.templateRoot = templateRoot;
	}

	@Override
	public boolean matches(CtElement element) {
		//clear all matches from previous run before we start matching with `element`
		if (element == templateRoot) {
			// This case can occur when we are scanning the entire package for example see TemplateTest#testTemplateMatcherWithWholePackage
			// Correct template matches itself of course, but client does not want that
			return false;
		}
		matches = getMatchedParameters(patternModel.matchAllWith(TobeMatched.create(
				new ImmutableMapImpl(),
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
	public ImmutableMap getMatches() {
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
