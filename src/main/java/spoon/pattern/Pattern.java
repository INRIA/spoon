/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.PatternPrinter;
import spoon.pattern.internal.matcher.MatchingScanner;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.support.Experimental;

/**
 * Represents a pattern for matching code. A pattern is composed of a list of AST models, where a model is an AST with some nodes being "pattern parameters".
 *
 * Main documentation at http://spoon.gforge.inria.fr/pattern.html.
 *
 * Instances can created with {@link PatternBuilder}.
 *
 * The {@link Pattern} can also be used to generate new code where
 * (Pattern) + (pattern parameters) =&gt; (copy of pattern where parameters are replaced by parameter values)
 * This is done with {@link #generator()} and it's methods
 *
 * Differences with {@link spoon.template.TemplateMatcher}:
 * - it can match sequences of elements
 * - it can match inlined elements
 */
@Experimental
public class Pattern {
	private ListOfNodes modelValueResolver;
	private boolean addGeneratedBy = false;
	private final Factory factory;
	/** package-protected, must use {@link PatternBuilder} */
	Pattern(Factory factory, ListOfNodes modelValueResolver) {
		this.modelValueResolver = modelValueResolver;
		this.factory = factory;
	}

	/**
	 * @return Map of parameter names to {@link ParameterInfo} for each parameter of this {@link Pattern}
	 */
	public Map<String, ParameterInfo> getParameterInfos() {
		Map<String, ParameterInfo> parameters = new HashMap<>();
		modelValueResolver.forEachParameterInfo((parameter, valueResolver) -> {
			ParameterInfo existingParameter = parameters.get(parameter.getName());
			if (existingParameter != null) {
				if (existingParameter == parameter) {
					//OK, this parameter is already there
					return;
				}
				throw new SpoonException("There is already a parameter: " + parameter.getName());
			}
			parameters.put(parameter.getName(), parameter);
		});
		return Collections.unmodifiableMap(parameters);
	}

	/**
	 * @return a {@link Generator}, which can be used to generate a code based on this {@link Pattern}
	 */
	public Generator generator() {
		return new DefaultGenerator(factory, modelValueResolver).setAddGeneratedBy(addGeneratedBy);
	}

	/**
	 * Finds all target program sub-trees that correspond to a template
	 * and calls consumer.accept(Match)
	 * @param input the root of to be searched AST
	 * @param consumer the receiver of matches
	 */
	public void forEachMatch(Object input, CtConsumer<Match> consumer) {
		if (input == null) {
			return;
		}
		if (input.getClass().isArray()) {
			input = Arrays.asList((Object[]) input);
		}

		MatchingScanner scanner = new MatchingScanner(modelValueResolver, consumer);
		if (input instanceof Collection<?>) {
			scanner.scan(null, (Collection<CtElement>) input);
		} else if (input instanceof Map) {
			scanner.scan(null, input);
		} else {
			scanner.scan(null, (CtElement) input);
		}
	}

	/**
	 * Finds all target program sub-trees that correspond to this pattern
	 * and returns them.
	 * @param root the root of to be searched AST. It can be a CtElement or List, Set, Map of CtElements
	 * @return List of {@link Match}
	 */
	public List<Match> getMatches(CtElement root) {
		List<Match> matches = new ArrayList<>();
		forEachMatch(root, matches::add);
		return matches;
	}

	/**
	 * @param addParameterComments if true then it adds comments with parameter names
	 * @return pattern printed as java sources
	 */
	public String print(boolean addParameterComments) {
		return new PatternPrinter().setPrintParametersAsComments(addParameterComments).printNode(modelValueResolver);
	}

	@Override
	public String toString() {
		return modelValueResolver.toString();
	}

	boolean isAddGeneratedBy() {
		return addGeneratedBy;
	}

	// not public because pattern should be immutable (only configured through PatternBuilder
	Pattern setAddGeneratedBy(boolean addGeneratedBy) {
		this.addGeneratedBy = addGeneratedBy;
		return this;
	}
}
