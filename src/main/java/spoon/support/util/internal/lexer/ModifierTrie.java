package spoon.support.util.internal.lexer;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CoreFactory;
import spoon.support.compiler.jdt.JDTTreeBuilder;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class ModifierTrie {
	Node root;

	{
		ModifierKind[] values = ModifierKind.values();
		Arrays.sort(values, comparing(Enum::toString, comparingInt(String::length)));
		root = new Node();
		for (ModifierKind value : values) {
			insert(root, value);
		}
	}

	void insert(Node prev, ModifierKind modifierKind) {
		Node current = prev;
		String name = modifierKind.toString();
		for (int i = 0; i < name.length() - 1; i++) {
			int c = name.charAt(i) - 'a';
			Node follow = current.follows[c];
			if (follow == null) {
				follow = new Node();
				current.follows[c] = follow;
				current = follow;
			}
		}
		Node end = new Node();
		end.modifierKind = modifierKind;
		current.follows[name.charAt(name.length() - 1) - 'a'] = end;
	}

	public Set<CtExtendedModifier> collectModifiers(char[] content, int start, int end, JDTTreeBuilder jdtTreeBuilder, CompilationUnit cu) {
		// TODO ignore non-modifiers at the beginning
		Set<CtExtendedModifier> modifiers = new HashSet<>();
		JavaLexer lexer = new JavaLexer(content, start, end);
		CoreFactory cf = jdtTreeBuilder.getFactory().Core();
		while (true) {
			Token lex = lexer.lex();
			if (lex == null) {
				return modifiers;
			}
			// TODO non-sealed
			Node node = root;
			for (int i = lex.start(); i < lex.end(); i++) {
				Node next = node.advance(content[i]);
				if (next == null) {
					if (node.modifierKind != null) {
						CtExtendedModifier modifier = CtExtendedModifier.explicit(node.modifierKind);
						modifier.setPosition(cf.createSourcePosition(cu, lex.start(), lex.end() - 1, jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions()));
						modifiers.add(modifier);
					}
					break;
				}
			}
		}
	}

	class Node {
		Node[] follows = new Node[24];
		ModifierKind modifierKind;

		Node advance(char c) {
			if (c < 'a' || c > 'z') {
				return null;
			}
			return follows[c - 'a'];
		}
	}
}
