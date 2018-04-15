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
package spoon.pattern.matcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.SpoonException;
import spoon.pattern.ModelNode;
import spoon.pattern.parameter.ParameterValueProviderFactory;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * Represents a Match of TemplateMatcher
 */
public class MatchingScanner extends EarlyTerminatingScanner<Void> {
	private final ModelNode pattern;
	private ParameterValueProviderFactory parameterValueProviderFactory;
	private CtConsumer<? super Match> matchConsumer;

	public MatchingScanner(ModelNode pattern, ParameterValueProviderFactory parameterValueProviderFactory, CtConsumer<? super Match> matchConsumer) {
		this.pattern = pattern;
		this.parameterValueProviderFactory = parameterValueProviderFactory;
		this.matchConsumer = matchConsumer;
	}

	@Override
	public void scan(CtRole role, CtElement element) {
		//This is called only for elements which are in single value attribute. Like `CtType#superClass`
		if (searchMatchInList(role, Collections.singletonList(element), false) == 0) {
			super.scan(role, element);
		}
	}

	@Override
	public void scan(CtRole role, Collection<? extends CtElement> elements) {
		if (elements == null) {
			return;
		}
		if (elements instanceof List<?>) {
			searchMatchInList(role, (List<? extends CtElement>) elements, true);
		} else if (elements instanceof Set<?>) {
			searchMatchInSet(role, (Set<? extends CtElement>) elements);
		} else {
			throw new SpoonException("Unexpected Collection type " + elements.getClass());
		}
	}

	private int searchMatchInList(CtRole role, List<? extends CtElement> list, boolean scanChildren) {
		int matchCount = 0;
		if (list.size() > 0) {
			TobeMatched tobeMatched = TobeMatched.create(
					parameterValueProviderFactory.createParameterValueProvider(),
					ContainerKind.LIST,
					list);
			while (tobeMatched.hasTargets()) {
				TobeMatched nextTobeMatched = pattern.matchAllWith(tobeMatched);
				if (nextTobeMatched != null) {
					List<?> matchedTargets = tobeMatched.getMatchedTargets(nextTobeMatched);
					if (matchedTargets.size() > 0) {
						matchCount++;
						//send information about match to client
						matchConsumer.accept(new Match(matchedTargets, nextTobeMatched.getParameters()));
						//do not scan children of matched elements. They already matched, so we must not scan them again
						//use targets of last match together with new parameters for next match
						tobeMatched = nextTobeMatched.copyAndSetParams(parameterValueProviderFactory.createParameterValueProvider());
						continue;
					} //else the template matches nothing. Understand it as no match in this context
				}
				if (scanChildren) {
					//scan children of each not matched element too
					super.scan(role, tobeMatched.getTargets().get(0));
				}
				//try match with sub list starting on second element
				tobeMatched = tobeMatched.removeTarget(0);
			}
		}
		return matchCount;
	}

	private void searchMatchInSet(CtRole role, Set<? extends CtElement> set) {
		if (set.size() > 0) {
			//copy targets, because it might be modified by call of matchConsumer, when refactoring spoon model
			//use List, because Spoon uses Sets with predictable order - so keep the order
			TobeMatched tobeMatched = TobeMatched.create(
					parameterValueProviderFactory.createParameterValueProvider(),
					ContainerKind.SET,
					set);
			while (tobeMatched.hasTargets()) {
				TobeMatched nextTobeMatched = pattern.matchAllWith(tobeMatched);
				if (nextTobeMatched != null) {
					List<?> matchedTargets = tobeMatched.getMatchedTargets(nextTobeMatched);
					if (matchedTargets.size() > 0) {
						//send information about match to client
						matchConsumer.accept(new Match(matchedTargets, nextTobeMatched.getParameters()));
						//do not scan children of matched elements. They already matched, so we must not scan them again
						tobeMatched = nextTobeMatched;
						//we have found a match. Try next match
						continue;
					} //else the template matches nothing. Understand it as no more match in this context
				}
				//there was no match. Do not try it again
				break;
			}
			//scan remaining not matched items of the Set
			for (Object object : tobeMatched.getTargets()) {
				//scan children of each not matched element too
				super.scan(role, object);
			}
		}
	}

	@Override
	public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
		// TODO Auto-generated method stub
		super.scan(role, elements);
	}
}
