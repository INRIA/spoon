package spoon.support.util.internal.trie;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class WordTrie<T> {
	private final char min;
	private final char max;
	private final Node root;

	private WordTrie(char min, char max) {
		this.min = min;
		this.max = max;
		this.root = new Node();
	}

	public static <T> WordTrie<T> ofWords(Map<String, T> words) {
		IntSummaryStatistics statistics = words.keySet().stream()
				.flatMapToInt(String::chars)
				.summaryStatistics();
		char min = (char) statistics.getMin();
		char max = (char) statistics.getMax();
		String[] wordArray = words.keySet().toArray(String[]::new);
		Arrays.sort(wordArray, comparingInt(String::length));
		WordTrie<T> wordTrie = new WordTrie<>(min, max);
		for (String word : wordArray) {
			wordTrie.insert(wordTrie.root, word, words.get(word));
		}
		return wordTrie;
	}

	public Optional<T> findMatch(String input) {
		return findMatch(input.toCharArray(), 0, input.length());
	}

	public Optional<T> findMatch(char[] input, int start, int end) {
		checkBounds(input.length, start, end);
		if (input.length == 0) {
			return Optional.empty();
		}
		Node current = root;
		int i = start;
		do {
			Node advance = current.advance(input[i]);
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

	void insert(Node prev, String name, T value) {
		Node current = prev;
		for (int i = 0; i < name.length() - 1; i++) {
			int c = name.charAt(i) - min;
			Node follow = current.follows[c];
			if (follow == null) {
				follow = new Node();
			}
			current.follows[c] = follow;
			current = follow;
		}
		Node end = new Node();
		end.value = value;
		current.follows[name.charAt(name.length() - 1) - min] = end;
	}

	private class Node {
		@SuppressWarnings("unchecked")
		Node[] follows = (Node[]) Array.newInstance(Node.class, max - min + 1);
		T value;

		Node advance(char c) {
			if (c < min || c > max) {
				return null;
			}
			return follows[c - min];
		}
	}
}
