/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import org.apache.commons.lang3.tuple.Pair;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.path.CtRole;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for detecting the indentation style used in a compilation unit.
 */
public class IndentationDetector {

	private IndentationDetector() {
	}

	/**
	 * Detect the indentation style of the given compilation unit as 1, 2 or 4 spaces or tabs by
	 * inspecting the whitespace preceding type members of top-level type declarations.
	 *
	 * @param cu A compilation unit.
	 * @return A pair on the form (indentationSize, isTabs)
	 */
	public static Pair<Integer, Boolean> detectIndentation(CtCompilationUnit cu) {
		List<ElementSourceFragment> typeFragments = cu.getOriginalSourceFragment()
				.getGroupedChildrenFragments().stream()
				.filter(fragment -> fragment instanceof CollectionSourceFragment)
				.flatMap(fragment -> extractTypeFragments((CollectionSourceFragment) fragment).stream())
				.collect(Collectors.toList());
		return detectIndentation(typeFragments);
	}

	private static Pair<Integer, Boolean> detectIndentation(List<ElementSourceFragment> topLevelTypeFragments) {
		List<String> wsPrecedingTypeMembers = new ArrayList<>();

		for (ElementSourceFragment typeSource : topLevelTypeFragments) {
			assert typeSource.getRoleInParent() == CtRole.DECLARED_TYPE;

			List<SourceFragment> children = typeSource.getChildrenFragments();
			for (int i = 0; i < children.size() - 1; i++) {
				if (children.get(i) instanceof TokenSourceFragment
						&& children.get(i + 1) instanceof ElementSourceFragment) {

					TokenSourceFragment cur = (TokenSourceFragment) children.get(i);
					ElementSourceFragment next = (ElementSourceFragment) children.get(i + 1);
					if (cur.getType() == TokenType.SPACE && next.getRoleInParent() == CtRole.TYPE_MEMBER) {
						wsPrecedingTypeMembers.add(cur.getSourceCode().replace("\n", ""));
					}
				}
			}
		}

		return guessIndentationStyle(wsPrecedingTypeMembers);
	}

	private static Pair<Integer, Boolean> guessIndentationStyle(List<String> wsPrecedingTypeMembers) {
		double avgIndent = wsPrecedingTypeMembers.stream()
				.map(String::length)
				.map(Double::valueOf)
				.reduce((acc, next) -> (acc + next) / 2).orElse(1d);

		double diff1 = Math.abs(1d - avgIndent);
		double diff2 = Math.abs(2d - avgIndent);
		double diff4 = Math.abs(4d - avgIndent);

		int indentationSize;
		if (diff1 > diff2) {
			indentationSize = diff2 > diff4 ? 4 : 2;
		} else {
			indentationSize = 1;
		}

		boolean usesTabs = (double) wsPrecedingTypeMembers.stream()
				.filter(s -> s.contains("\t"))
				.count() >= (double) wsPrecedingTypeMembers.size() / 2;
		return Pair.of(indentationSize, usesTabs);
	}

	private static List<ElementSourceFragment> extractTypeFragments(CollectionSourceFragment collection) {
		return collection.getItems().stream()
				.filter(fragment -> fragment instanceof ElementSourceFragment)
				.map(fragment -> (ElementSourceFragment) fragment)
				.filter(fragment -> fragment.getRoleInParent() == CtRole.DECLARED_TYPE)
				.collect(Collectors.toList());
	}
}
