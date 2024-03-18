package spoon.support.util.internal.lexer;

/**
 * Valid Java (contextual) keywords
 */
enum JavaKeyword {
    ABSTRACT,
    ASSERT,
    BOOLEAN,
    BREAK,
    BYTE,
    CASE,
    CATCH,
    CHAR,
    CLASS,
    CONTINUE,
    DEFAULT,
    DO,
    DOUBLE,
    ELSE,
    EXTENDS,
    FALSE,
    FINAL,
    FINALLY,
    FLOAT,
    FOR,
    IF,
    IMPLEMENTS,
    IMPORT,
    INSTANCEOF,
    INT,
    INTERFACE,
    LONG,
    NATIVE,
    NEW,
    NON_SEALED {
        @Override
        public String toString() {
            return "non-sealed";
        }
    },
    NULL,
    PACKAGE,
    PERMITS,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    RECORD,
    RETURN,
    SEALED,
    SHORT,
    STATIC,
    STRICTFP,
    SUPER,
    SWITCH,
    SYNCHRONIZED,
    THIS,
    THROW,
    THROWS,
    TRANSIENT,
    TRUE,
    TRY,
    VOID,
    VOLATILE,
    WHILE,
    YIELD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}