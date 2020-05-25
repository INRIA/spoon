/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.SpoonException;
import spoon.pattern.internal.matcher.Matchers;
import spoon.pattern.internal.matcher.TobeMatched;

/**
 * Defines algorithm of repeatable matcher.
 */
abstract class AbstractRepeatableMatcher extends AbstractNode implements RepeatableMatcher {

	@Override
	public TobeMatched matchTargets(TobeMatched targets, Matchers next) {
		if (isRepeatable() == false) {
			//handle non repeatable Nodes
			boolean isMandatory = isMandatory(targets.getParameters());
			//match maximum one value
			TobeMatched tmp = matchAllWith(targets);
			if (tmp == null) {
				if (isMandatory) {
					//this mandatory valueResolver didn't match
					return null;
				}
				//no match - OK, it was optional
			} else {
				targets = tmp;
			}
			//the `matcher` has all values. Match next
			return next.matchAllWith(targets);
		}
		//it is repeatable node
		//first match mandatory targets
		while (isMandatory(targets.getParameters())) {
			TobeMatched tmp = matchAllWith(targets);
			if (tmp == null) {
				//this mandatory valueResolver didn't match
				return null;
			}
			//check whether we have to match next
			//we need this check because #isMandatory()==true for each state. In such case the #isTryNextMatch must be able to finish the cycle
			if (isTryNextMatch(tmp.getParameters()) == false) {
				//the `matcher` has all values. Match next
				return next.matchAllWith(tmp);
			}
			//use new matching request to match next mandatory parameter
			targets = tmp;
		}
		//then continue optional targets
		return matchOptionalTargets(targets, next);
	}

	private TobeMatched matchOptionalTargets(TobeMatched targets, Matchers next) {
		if (isTryNextMatch(targets.getParameters()) == false) {
			//the `matcher` has all values. Match next
			return next.matchAllWith(targets);
		}
		switch (getMatchingStrategy()) {
		case GREEDY: {
			{ //first try to match using this matcher
				TobeMatched match = matchAllWith(targets);
				if (match != null) {
					//this matcher passed, try to match next one using current SimpleValueResolver
					match = matchOptionalTargets(match, next);
					if (match != null) {
						//all next passed too, return that match
						return match;
					}
				}
			}
			//greedy matching with current nodeSubstRequest didn't pass. Try to match using remaining templates
			return next.matchAllWith(targets);
		}
		case RELUCTANT: {
			{ //first try to match using next matcher.
				TobeMatched match = next.matchAllWith(targets);
				if (match != null) {
					return match;
				}
			}
			//reluctant matching didn't pass on next elements. Try to match using this matcher
			TobeMatched match = matchAllWith(targets);
			if (match == null) {
				//nothing matched
				return null;
			}
			//this matcher passed. Match next one using current SimpleValueResolver
			return matchOptionalTargets(match, next);
		}
		case POSSESSIVE:
			//match everything using this matcher. Never try other way
			//Check if we should try next match
			while (isTryNextMatch(targets.getParameters())) {
				TobeMatched tmp = matchAllWith(targets);
				if (tmp == null) {
					if (isMandatory(targets.getParameters())) {
						//this mandatory valueResolver didn't match
						return null;
					}
					//it was optional. Ignore this valueResolver and continue with next
					break;
				}
				//use new matching request
				targets = tmp;
			}
			return next.matchAllWith(targets);
		}
		throw new SpoonException("Unsupported quantifier " + getMatchingStrategy());
	}
}
