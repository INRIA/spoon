/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.lexer;

import java.util.Arrays;

public class CharStream {
    private final char[] content;
    private final int start;
    private final int end;
    private int[] positionRemap;

    public CharStream(char[] content, int start, int end) {
        this.content = content;
        this.start = start;
        this.end = end;
    }

    public char[] readAll() {
        char[] chars = new char[this.end - this.start]; // approximate
        int t = 0;
        for (int i = this.start; i < this.end; i++, t++) {
            if (this.content[i] != '\\') {
                chars[t] = this.content[i];
            } else if (this.end > i + 5 && this.content[i + 1] == 'u') {
                int utf16 = parseHex(i + 2);
                if (utf16 < 0) {
                    chars[t] = '\\';
                } else {
                    chars[t] = (char) utf16;
                    i += 5;
                    if (this.positionRemap == null) {
                        this.positionRemap = createPositionRemap(chars);
                    }
                    this.positionRemap[t] = 6;
                }
            }
        }
        if (t == chars.length) {
            return chars;
        }
        // otherwise, we encountered a unicode sequence
        this.positionRemap[0] += this.start;
        Arrays.parallelPrefix(this.positionRemap, Integer::sum);
        return Arrays.copyOf(chars, t);
    }

    public int remapPosition(int index) {
        if (this.positionRemap == null) {
            return index;
        }
        if (index == 0) {
            return this.start;
        }
        return this.positionRemap[index - 1];
    }

    private int[] createPositionRemap(char[] chars) {
        int[] remap = new int[chars.length];
        Arrays.fill(remap, 1);
        return remap;
    }

    private int parseHex(int start) {
        int result = 0;
        for (int i = start; i < start + 4; i++) {
            result *= 16;
            char c = this.content[i];
            if ('0' <= c && '9' >= c) {
                result += c - '0';
            } else {
                c |= ' '; // lowercase potential letter
                if ('a' <= c && 'f' >= c) {
                    result += (c - 'a') + 10;
                    continue;
                }
                // not a valid symbol, mark result
                result = Integer.MIN_VALUE;
            }
        }
        return result;
    }
}
