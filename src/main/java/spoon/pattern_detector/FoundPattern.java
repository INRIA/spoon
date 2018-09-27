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
package spoon.pattern_detector;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;

import spoon.SpoonException;
import spoon.pattern.Match;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern_detector.internal.ActionToParameter;
import spoon.pattern_detector.internal.UpdateNode;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.PrinterHelper;
import spoon.support.util.internal.MapUtils;

/**
 * Represents a pattern found by a {@link PatternDetector}
 */
public class FoundPattern {
	private final CodeInfo primaryCodeInfo;
	//elements of the code, which produced and matches this pattern
	private final List<DiffCodeInfo> matchingCodeInfos = new ArrayList<>();
	//primary tree nodes mapped to diff actions each matching code. The index of item in matchingCodeInfos fits to index of Action
	private Map<ITree, List<Action>> nodeToActions = new IdentityHashMap<>();
	//pattern based on codeElements, where each action from updateAction is a parameter
	private Pattern pattern;

	FoundPattern(CodeInfo codeInfo) {
		primaryCodeInfo = codeInfo;
	}

	/**
	 * @return count of matching code fragments
	 */
	public int getCountOfMatches() {
		return matchingCodeInfos.size() + 1;
	}

	/**
	 * @param otherGumTree
	 * @param code
	 * @return true if code of other gum tree was merged with this pattern
	 */
	boolean addMerge(CodeInfo codeInfo) {
		List<Action> actions = primaryCodeInfo.getGumTreeDifferences(codeInfo);
		List<UpdateNode> updateActions = (List) actions.stream().filter((Action a) -> (a instanceof UpdateNode)).collect(Collectors.toList());
		if (updateActions.size() != actions.size()) {
			//there are other then Update actions. Cannot actually merge with foundPattern
			return false;
		}
		int idxOfMatchingCodeInfo = matchingCodeInfos.size();
		matchingCodeInfos.add(new DiffCodeInfo(primaryCodeInfo, codeInfo, actions));

		for (UpdateNode action : updateActions) {
			List<Action> update = MapUtils.getOrCreate(nodeToActions, action.getNode(), () -> new ArrayList<>());
			addOnIndex(update, idxOfMatchingCodeInfo, action);
		}
		//reset the pattern cache
		this.pattern = null;
		return true;
	}

	private static <T> void addOnIndex(List<T> list, int idx, T value) {
		while (list.size() < idx) {
			list.add(null);
		}
		list.add(value);
	}

	private Match match(List<? extends CtElement> code) {
		List<Match> matches = new ArrayList<>();
		pattern.forEachMatch(code, match -> {
			matches.add(match);
		});
		if (matches.size() == 1) {
			return matches.get(0);
		}
		return null;
	}

	/**
	 * @return {@link Pattern}, which matches all similar code fragments, which belong to this {@link FoundPattern}
	 */
	public Pattern getPattern() {
		if (pattern == null) {
			PatternBuilder pb = new MyPatternBuilder(primaryCodeInfo.elements);
			ActionToParameter actionToParameterMapper = new ActionToParameter(pb);
			int nrProcessed = 0;
			for (ITree node : primaryCodeInfo.codeGumTree.preOrder()) {
				List<Action> allActions = nodeToActions.get(node);
				Action action = ActionToParameter.getFirstNotNull(allActions);
				if (action != null) {
					//assert that nodeToActions model is consistent
					if (nodeToActions.get(action.getNode()) == null) {
						throw new SpoonException("Unexpected action found");
					}
					actionToParameterMapper.applyAction(action, allActions);
					nrProcessed++;
				}
			}
			if (nrProcessed != nodeToActions.size()) {
				//this would mean a bug in concept. We have to traverse actions in different way
				throw new SpoonException("Some tree actions were not found!");
			}
			pattern = actionToParameterMapper.build();
		}
		return pattern;
	}

	/**
	 * Extended {@link PatternBuilder}, which doesn't automatically creates parameter targetType.
	 */
	private static class MyPatternBuilder extends PatternBuilder {
		MyPatternBuilder(List<? extends CtElement> elements) {
			super(elements);
		}
	}

	@Override
	public String toString() {
		PrinterHelper sb = new PrinterHelper(primaryCodeInfo.elements.get(0).getFactory().getEnvironment());
		sb.write("/*-------------------------------------").writeln();
		int idx = 1;
		for (CodeInfo code : getAllCodeInfos()) {
			sb.write("" + (idx++) + ") " + code.toString()).writeln();
			sb.incTab();
			printMatchingParameters(sb, code);
			sb.decTab();
		}
		sb.write("----------------------------------------*/").writeln();
		sb.write(getPattern().print(false));
		return sb.toString();
	}

	private List<CodeInfo> getAllCodeInfos() {
		List<CodeInfo> allCIs = new ArrayList<>(getCountOfMatches());
		allCIs.add(primaryCodeInfo);
		for (DiffCodeInfo diffCodeInfo : matchingCodeInfos) {
			allCIs.add(diffCodeInfo.secondaryCodeInfo);
		}
		return allCIs;
	}

	private void printMatchingParameters(PrinterHelper sb, CodeInfo code) {
		List<Match> matches = new ArrayList<>();
		getPattern().forEachMatch(code.elements, match -> {
			matches.add(match);
			printMatch(sb, match);
		});
		if (matches.isEmpty()) {
			sb.write("NO MATCH!").writeln();
			return;
		}
	}

	private void printMatch(PrinterHelper sb, Match match) {
		match.printParameters(sb);
	}
}
