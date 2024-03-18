/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.trie;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparingInt;

/**
 * A stateless and immutable trie that maps strings to values.
 * @param <T> the value type.
 */
public interface Trie<T> {

    /**
     * Creates a new immutable trie with the given mappings
     *
     * @param words the mappings
     * @param <T>   the value type
     * @return a new trie
     */
    static <T> Trie<T> ofWords(Map<String, T> words) {
        IntSummaryStatistics statistics = words.keySet().stream()
                .flatMapToInt(String::chars)
                .summaryStatistics();
        char min = (char) statistics.getMin();
        char max = (char) statistics.getMax();
        String[] wordArray = words.keySet().toArray(String[]::new);
        Arrays.sort(wordArray, comparingInt(String::length));
        SimpleTrie<T> trie = new SimpleTrie<>(min, max);
        for (String word : wordArray) {
            trie.insert(trie.root(), word, words.get(word));
        }
        return trie;
    }

    /**
     * Finds the value for the given char array.
     * @param input the array representing the key.
     * @return the value mapped to by the given input.
     */
    default Optional<T> findMatch(char[] input) {
        return findMatch(input, 0, input.length);
    }

    /**
     * Finds the value for the range of given char array.
     * @param input the array representing the key.
     * @return the value mapped to by the given input.
     */
    Optional<T> findMatch(char[] input, int start, int end);
}
