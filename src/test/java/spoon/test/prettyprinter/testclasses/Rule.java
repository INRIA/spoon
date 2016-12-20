/*
 * Copyright (C) 2006-2016 INRIA and contributors
 *  Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and abiding by the rules of distribution of free software. You can use, modify and/or redistribute the software under the terms of the CeCILL-C license as circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.test.prettyprinter.testclasses;

import java.util.*;

/**
 * Created by urli on 19/12/2016.
 */
public class Rule {
    public enum Language {
        DTD, WSDL, OTHER
    }
    public static final class Phoneme implements PhonemeExpr {
        public interface Bidule {}

        public static final Comparator<Phoneme> COMPARATOR = new Comparator<Phoneme>() {
            @Override
            public int compare(final Phoneme o1, final Phoneme o2) {
                for (int i = 0; i < o1.phonemeText.length(); i++) {
                    if (i >= o2.phonemeText.length()) {
                        return +1;
                    }
                    final int c = o1.phonemeText.charAt(i) - o2.phonemeText.charAt(i);
                    if (c != 0) {
                        return c;
                    }
                }

                if (o1.phonemeText.length() < o2.phonemeText.length()) {
                    return -1;
                }

                return 0;
            }
        };

        private final StringBuilder phonemeText;
        private final Language language;

        public Phoneme(final CharSequence phonemeText, final Language language) {
            this.phonemeText = new StringBuilder(phonemeText);
            this.language = language;
        }

        @Override
        public Iterable<Phoneme> getPhonemes() {
            return Collections.singleton(this);
        }
    }

    public interface PhonemeExpr {
        Iterable<Phoneme> getPhonemes();
    }

    public static final class PhonemeList implements PhonemeExpr {
        private final List<Phoneme> phonemes;

        public PhonemeList(final List<Phoneme> phonemes) {
            this.phonemes = phonemes;
        }

        @Override
        public List<Phoneme> getPhonemes() {
            return this.phonemes;
        }
    }

    public static final String ALL = "ALL";

    private static final String DOUBLE_QUOTE = "\"";

    private static final String HASH_INCLUDE = "#include";

    private static Phoneme parsePhoneme(final String ph) {
        final int open = ph.indexOf("[");
        if (open >= 0) {
            if (!ph.endsWith("]")) {
                throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
            }
            final String before = ph.substring(0, open);
            final String in = ph.substring(open + 1, ph.length() - 1);
            final Set<String> langs = new HashSet<String>(Arrays.asList(in.split("[+]")));

            return new Phoneme(before, Language.DTD);
        }
        return new Phoneme(ph, Language.WSDL);
    }
}
