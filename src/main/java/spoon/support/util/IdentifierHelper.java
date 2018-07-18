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
package spoon.support.util;

import spoon.SpoonException;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class IdentifierHelper {
    private static String[] keywords = {
            "abstract", "continue", "for", "new", "switch",
            "assert", "default", "if", "package", "synchronized",
            "do", "goto", "private", "this",
            "break", "implements", "protected", "throw",
            "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "try",
            "final", "interface", "static",
            "class", "finally", "strictfp", "volatile",
            "const", "native", "super", "while", "_",
            "true", "false", "null",
            "void", "boolean", "int", "long", "float",
            "double", "byte", "char", "short"
    };

    private static String[] identifierExceptions = {
            CtPackage.TOP_LEVEL_PACKAGE_NAME,
            CtModule.TOP_LEVEL_MODULE_NAME,
            CtExecutableReference.CONSTRUCTOR_NAME,
            CtExecutableReference.UNKNOWN_TYPE,
            CtTypeReference.NULL_TYPE_NAME,
            Void.TYPE.getName(),
            Boolean.TYPE.getName(),
            Integer.TYPE.getName(),
            Long.TYPE.getName(),
            Float.TYPE.getName(),
            Double.TYPE.getName(),
            Byte.TYPE.getName(),
            Character.TYPE.getName(),
            Short.TYPE.getName()
    };

    /**
     * This method checks that the given identifier respects the definition
     * given in the JLS (see: https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8
     *
     * @param withDot : can be used in case of checking a fully qualified identifier. Then dots are also authorized.
     */
    public static void checkIdentifier(String identifier, boolean withDot) {
        for (String identifierException : identifierExceptions) {
            if (identifier.equals(identifierException)) {
                return;
            }
        }

        boolean isRight = true;

        for (int i = 0; isRight && i < keywords.length; i++) {
            isRight = !identifier.equals(keywords[i]);
        }

        for (int i = 0; isRight && i < identifier.length(); i++) {
            if (i == 0) {
                isRight = Character.isJavaIdentifierStart(identifier.charAt(i));
            } else {
                if (withDot) {
                    isRight = Character.isJavaIdentifierPart(identifier.charAt(i)) || identifier.charAt(i) == '.';
                } else {
                    isRight = Character.isJavaIdentifierPart(identifier.charAt(i));
                }
            }
        }

        if (!isRight) {
            throw new SpoonException("The given identifier does not respect Java definition of an identifier: "+identifier);
        }
    }
}
