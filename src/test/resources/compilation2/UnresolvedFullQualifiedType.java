package spoon.test.compilation.testclasses;

import static daikon.PptRelation.PptRelationType;

public final class UnresolvedFullQualifiedType {
    static final class ParentRelation implements java.io.Serializable {
        daikon.PptRelation.PptRelationType rel_type;
    }

    /**
     * Parses a ppt parent hierarchy record and returns it. *
     */
    private static void m() {
        String.valueOf(daikon.PptRelation.PptRelationType.class);
    }
}