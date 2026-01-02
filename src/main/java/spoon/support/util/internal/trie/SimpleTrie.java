/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.trie;

import java.util.Optional;

record SimpleTrie<T>(
	char min,
	char max,
	Node<T> root
) implements Trie<T> {

	SimpleTrie(char min, char max) {
		this(min, max, new Node<>(createFollows(min, max), null));
	}

	@Override
	public Optional<T> findMatch(char[] input) {
		return findMatch(input, 0, input.length);
	}

	@Override
	public Optional<T> findMatch(char[] input, int start, int end) {
		checkBounds(input.length, start, end);
		if (input.length == 0) {
			return Optional.empty();
		}
		Node<T> current = root;
		int i = start;
		do {
			char c = input[i];
			if (c < min || c > max) {
				return Optional.empty();
			}
			Node<T> advance = current.advance(c - min);
			if (advance == null) {
				return Optional.empty();
			}
			current = advance;
		} while (++i < end);
		return Optional.of(current).map(node -> node.value);
	}

	private static void checkBounds(int length, int start, int end) {
		if (start > end || end > length || start < 0) {
			throw new IndexOutOfBoundsException(String.format("array of length %s, start %s, end %s", length, start, end));
		}
	}

	void insert(Node<T> prev, String name, T value) {
		Node<T> current = prev;
		for (int i = 0; i < name.length() - 1; i++) {
			int c = name.charAt(i) - min;
			Node<T> follow = current.follows[c];
			if (follow == null) {
				follow = new Node<>(createFollows(min, max), null);
			}
			current.follows[c] = follow;
			current = follow;
		}
		Node<T> end = new Node<>(createFollows(min, max), value);
		current.follows[name.charAt(name.length() - 1) - min] = end;
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T>[] createFollows(char min, char max) {
		return new Node[max - min + 1];
	}

	private record Node<T>(
		Node<T>[] follows,
		T value
	) {

		Node<T> advance(int index) {
			return follows[index];
		}
	}
}
