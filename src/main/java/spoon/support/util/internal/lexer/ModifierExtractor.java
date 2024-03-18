/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.lexer;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.util.internal.trie.Trie;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModifierExtractor {
	private static final Trie<ModifierKind> MODIFIER_TRIE = Trie.ofWords(
			Arrays.stream(ModifierKind.values())
					.collect(Collectors.toMap(ModifierKind::toString, Function.identity()))
	);

	public void collectModifiers(
			char[] content,
			int start,
			int end,
			Map<ModifierKind, CtExtendedModifier> modifiers,
			BiFunction<Integer, Integer, SourcePosition> createSourcePosition
	) {
		JavaLexer lexer = new JavaLexer(content, start, end);
		while (!modifiers.isEmpty()) {
			Token lex = lexer.lex();
			if (lex == null) {
				return;
			}
			if (lex.type() != TokenType.KEYWORD) {
				continue;
			}
			char[] decodedContent = new CharStream(content, lex.start(), lex.end()).readAll();
			Optional<ModifierKind> match = MODIFIER_TRIE.findMatch(decodedContent);
			if (match.isPresent()) {
				CtExtendedModifier modifier = modifiers.remove(match.get());
				if (modifier != null) {
					modifier.setPosition(createSourcePosition.apply(lex.start(), lex.end() - 1));
				}
			}
		}
	}
}
