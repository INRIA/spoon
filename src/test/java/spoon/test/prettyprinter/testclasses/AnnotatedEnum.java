package spoon.test.prettyprinter.testclasses;

public enum AnnotatedEnum {
    ONE(1, "one"),
    TWO(2, "two"),
    THREE(3, "three"),
    /**
     * @deprecated
     */
    @Deprecated
    // There was a typo...
    FOR(4, "for"),
    FOUR(4, "four");
    
    private final int value;
    private final String text;
        
    AnnotatedEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
