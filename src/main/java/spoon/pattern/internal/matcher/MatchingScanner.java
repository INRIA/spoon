/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.matcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.SpoonException;
import spoon.pattern.Match;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.support.util.ImmutableMapImpl;

/**
 * Represents a Match of TemplateMatcher
 */
public class MatchingScanner extends EarlyTerminatingScanner<Void> {
	private final ListOfNodes pattern;
	private CtConsumer<? super Match> matchConsumer;

	public MatchingScanner(ListOfNodes pattern, CtConsumer<? super Match> matchConsumer) {
		this.pattern = pattern;
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
		if (!list.isEmpty()) {
			TobeMatched tobeMatched = TobeMatched.create(
					new ImmutableMapImpl(),
					ContainerKind.LIST,
					list);
			while (tobeMatched.hasTargets()) {
				TobeMatched nextTobeMatched = pattern.matchAllWith(tobeMatched);
				if (nextTobeMatched != null) {
					List<?> matchedTargets = tobeMatched.getMatchedTargets(nextTobeMatched);
					if (!matchedTargets.isEmpty()) {
						matchCount++;
						//send information about match to client
						matchConsumer.accept(new Match(matchedTargets, nextTobeMatched.getParameters()));
						//do not scan children of matched elements. They already matched, so we must not scan them again
						//use targets of last match together with new parameters for next match
						tobeMatched = nextTobeMatched.copyAndSetParams(new ImmutableMapImpl());
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
		if (!set.isEmpty()) {
			//copy targets, because it might be modified by call of matchConsumer, when refactoring spoon model
			//use List, because Spoon uses Sets with predictable order - so keep the order
			TobeMatched tobeMatched = TobeMatched.create(
					new ImmutableMapImpl(),
					ContainerKind.SET,
					set);
			while (tobeMatched.hasTargets()) {
				TobeMatched nextTobeMatched = pattern.matchAllWith(tobeMatched);
				if (nextTobeMatched != null) {
					List<?> matchedTargets = tobeMatched.getMatchedTargets(nextTobeMatched);
					if (!matchedTargets.isEmpty()) {
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
