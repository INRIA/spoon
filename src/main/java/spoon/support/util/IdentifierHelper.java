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
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class IdentifierHelper {
    public enum IdentifierType {
        STRUCTURAL_ELEMENTS,
        PACKAGE_REFERENCE,
        OTHER_REFERENCE
    };

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

    private static String[] structuralIdentifierExceptions = {
            CtPackage.TOP_LEVEL_PACKAGE_NAME,
            CtModule.TOP_LEVEL_MODULE_NAME
    };

    private static String[] otherReferenceIdentifierExceptions = {
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
     * @param identifierType: allow to use different exception strategy depending on the type of element to check:
     *                      if it's a {@link IdentifierType#STRUCTURAL_ELEMENTS} then all JLS rules must be respected, excepted for top level package and module names and anonymous types which are using figure for their name (see {@link CtClass#isAnonymous()});
     *                      if it's a {@link IdentifierType#PACKAGE_REFERENCE} then dots are also accepted;
     *                      if it's a {@link IdentifierType#OTHER_REFERENCE} then primitive types names, numbers and few other exceptions are accepted.
     */
    public static void checkIdentifier(String identifier, IdentifierType identifierType) {
        String[] identifierExceptions;

        // we check first against exception based on the identifier type
        switch (identifierType) {
            case STRUCTURAL_ELEMENTS:
                identifierExceptions = structuralIdentifierExceptions;
                break;

            case OTHER_REFERENCE:
                identifierExceptions = otherReferenceIdentifierExceptions;
                break;

            default:
                identifierExceptions = new String[0];
                break;
        }

        // in case of exception: it's accepted directly
        for (String identifierException : identifierExceptions) {
            if (identifier.equals(identifierException)) {
                return;
            }
        }

        boolean isRight = true;

        // outside exception, java keywords should never be used as identifier
        for (int i = 0; isRight && i < keywords.length; i++) {
            isRight = !identifier.equals(keywords[i]);
        }

        // in case of an anonymous type or type reference, the name can be a simple figure:
        // in that case only we accept it directly
        if (identifierType == IdentifierType.STRUCTURAL_ELEMENTS || identifierType == IdentifierType.OTHER_REFERENCE) {
            try {
                Integer.parseInt(identifier);
                return;
            } catch (NumberFormatException e) {
                // do nothing
            }
        }

        // for other cases we check the name based on JLS rules
        for (int i = 0; isRight && i < identifier.length(); i++) {
            if (i == 0) {
                isRight = Character.isJavaIdentifierStart(identifier.charAt(i));
            } else {

                // if it's a package reference, we allow dots in the name
                if (identifierType == IdentifierType.PACKAGE_REFERENCE) {
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
