/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import spoon.SpoonException;
import spoon.reflect.meta.ContainerKind;
import spoon.support.util.ImmutableMap;

/**
 * Describes what next has to be matched.
 * It consists of current `parameters` represented by {@link ImmutableMap}
 * and by a to be matched target elements.
 * See children of {@link TobeMatched} for supported collections of target elements.
 */
public class TobeMatched {
	//TODO remove parameters. Send them individually into matching methods and return MatchResult
	private final ImmutableMap parameters;
	//Use list for everything because Spoon uses Sets with predictable iteration order
	private final List<?> targets;
	private final boolean ordered;

	/**
	 * @param parameters to be matched parameters
	 * @param containerKind the type of container in `target` value
	 * @param target the to be matched target data. List, Set, Map or single value
	 * @return new instance of {@link TobeMatched}, which contains `parameters` and `target` mapped using containerKind
	 */
	public static TobeMatched create(ImmutableMap parameters, ContainerKind containerKind, Object target) {
		switch (containerKind) {
		case LIST:
			return new TobeMatched(parameters, (List<Object>) target, true);
		case SET:
			return new TobeMatched(parameters, (Set<Object>) target, false);
		case MAP:
			return new TobeMatched(parameters, (Map<String, Object>) target);
		case SINGLE:
			return new TobeMatched(parameters, target);
		}
		throw new SpoonException("Unexpected RoleHandler containerKind: " + containerKind);
	}

	private TobeMatched(ImmutableMap parameters, Object target) {
		//It is correct to put whole container as single value in cases when ParameterNode matches agains whole attribute value
		this.parameters = parameters;
		//make a copy of origin collection, because it might be modified during matching process (by a refactoring algorithm)
		this.targets = Collections.singletonList(target);
		this.ordered = true;
	}
	/**
	 * @param parameters current parameters
	 * @param targets List or Set of to be matched targets
	 * @param ordered defines the way how targets are matched. If true then first target is matched with first ValueResolver.
	 * If false then all targets are tried with first ValueResolver.
	 */
	private TobeMatched(ImmutableMap parameters, Collection<?> targets, boolean ordered) {
		this.parameters = parameters;
		//make a copy of origin collection, because it might be modified during matching process (by a refactoring algorithm)
		this.targets = targets == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(targets));
		this.ordered = ordered;
	}

	private TobeMatched(ImmutableMap parameters, Map<String, ?> targets) {
		this.parameters = parameters;
		//make a copy of origin collection, because it might be modified during matching process (by a refactoring algorithm)
		this.targets = targets == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(targets.entrySet()));
		this.ordered = false;
	}

	private TobeMatched(ImmutableMap parameters, List<?> targets, boolean ordered, int tobeRemovedIndex) {
		this.parameters = parameters;
		this.targets = new ArrayList<>(targets);
		if (tobeRemovedIndex >= 0) {
			this.targets.remove(tobeRemovedIndex);
		}
		this.ordered = ordered;
	}

	/**
	 * @return parameters of last successful match.
	 */
	public ImmutableMap getParameters() {
		return parameters;
	}

	/**
	 * @return {@link List} of to be matched targets, which
	 * A) have to be matched by current matching step
	 * B) remained after matching of all template nodes
	 */
	public List<?> getTargets() {
		return targets;
	}

	/**
	 * @param tobeMatchedTargets {@link TobeMatched} with targets, which didn't matched yet. These which has to be matched next.
	 * @return matched targets. It means these targets, which are not contained in `notMatchedTargets`
	 */
	public List<?> getMatchedTargets(TobeMatched tobeMatchedTargets) {
		int nrOfMatches = getTargets().size() - tobeMatchedTargets.getTargets().size();
		if (nrOfMatches >= 0) {
			if (nrOfMatches == 0) {
				return Collections.emptyList();
			}
			List<Object> matched = new ArrayList(nrOfMatches);
			for (Object target : getTargets()) {
				if (containsSame(tobeMatchedTargets.getTargets(), target)) {
					//this origin target is still available in this to be matched targets
					continue;
				}
				//this origin target is NOT available in this to be matched targets. It means it matched
				matched.add(target);
			}
			if (matched.size() == nrOfMatches) {
				return matched;
			}
		}
		throw new SpoonException("Invalid input `originTobeMatched`");
	}

	private boolean containsSame(List<?> items, Object object) {
		for (Object item : items) {
			if (item == object) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if there is anything to match.
	 */
	public boolean hasTargets() {
		return !targets.isEmpty();
	}

	/**
	 * Makes a copy of this match context with the same targets, but with new `parameters`
	 * @param newParams to be used parameters
	 * @return copy of {@link TobeMatched} with new parameters
	 */
	public TobeMatched copyAndSetParams(ImmutableMap newParams) {
		if (parameters == newParams) {
			return this;
		}
		return new TobeMatched(newParams, targets, ordered, -1);
	}

	/**
	 * Calls matcher algorithm to match target item
	 * @param matcher a matching algorithm
	 * @return {@link TobeMatched} with List of remaining (to be matched) targets or null if there is no match
	 */
	public TobeMatched matchNext(BiFunction<Object, ImmutableMap, ImmutableMap> matcher) {
		if (targets.isEmpty()) {
			//no target -> no match
			return null;
		}
		if (ordered) {
			//handle ordered list of targets - match with first target
			ImmutableMap parameters = matcher.apply(targets.get(0), getParameters());
			if (parameters != null) {
				//return remaining match
				return removeTarget(parameters, 0);
			}
			return null;
		} else {
			//handle un-ordered list of targets - match with all targets, stop at first matching
			int idxOfMatch = 0;
			while (idxOfMatch < targets.size()) {
				ImmutableMap parameters = matcher.apply(targets.get(idxOfMatch), getParameters());
				if (parameters != null) {
					return removeTarget(parameters, idxOfMatch);
				}
				//try to match next target
				idxOfMatch++;
			}
			return null;
		}
	}

	/**
	 * @param remainingMatch the {@link TobeMatched} whose parameters has to be returned
	 * @return parameters from `remainingMatch`, if it exists. Else returns null
	 */
	public static ImmutableMap getMatchedParameters(TobeMatched remainingMatch) {
		return remainingMatch == null ? null : remainingMatch.getParameters();
	}
	/**
	 * @param idxOfTobeRemovedTarget index of to be removed target
	 * @return new {@link TobeMatched} without the target on the index `idxOfTobeRemovedTarget`
	 */
	public TobeMatched removeTarget(int idxOfTobeRemovedTarget) {
		return removeTarget(parameters, idxOfTobeRemovedTarget);
	}
	public TobeMatched removeTarget(ImmutableMap parameters, int idxOfTobeRemovedTarget) {
		return new TobeMatched(parameters, targets, ordered, idxOfTobeRemovedTarget);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Parameters:\n----------------\n")
		.append(parameters)
		.append("\nTobe matched target elements\n-----------------------\n");
		for (int i = 0; i < targets.size(); i++) {
			sb.append('\n').append(i + 1).append('/').append(targets.size()).append(": ").append(targets.get(i));
		}
		return sb.toString();
	}
}
