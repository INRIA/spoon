package spoon.reflect.code;

public enum ArrayDeclarationKind {
    /**
     * Brackets are after type.
     * int[] array;
     */
    TYPE,

    /**
     * Brackets are after identifier.
     * int array[];
     */
    IDENTIFIER,
}
