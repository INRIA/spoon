package spoon.test.prettyprinter.testclasses;


import static spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.globalField;


public class TypeIdentifierCollision {
    public enum ENUM {
        E1(globalField,spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1);
        final int NUM;

        final Enum<?> e;

        private ENUM(int num, Enum<?> e) {
            NUM = num;
            this.e = e;
        }
    }

    static class Class0 {
        public static class ClassA {
            public static int VAR0;

            public static int getNum() {
                return 0;
            }
        }
    }

    static class Class1 {
        public static class ClassA {
            public static int VAR1;

            public static int getNum() {
                return 0;
            }
        }
    }

    private int localField;

    public void setFieldUsingExternallyDefinedEnumWithSameNameAsLocal() {
        localField = spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1.ordinal();
    }

    public void setFieldUsingLocallyDefinedEnum() {
        localField = ENUM.E1.ordinal();
    }

    public void setFieldOfClassWithSameNameAsTheCompilationUnitClass() {
        globalField = localField;
    }

    public void referToTwoInnerClassesWithTheSameName() {
        Class0.ClassA.VAR0 = Class0.ClassA.getNum();
        Class1.ClassA.VAR1 = Class1.ClassA.getNum();
    }
}