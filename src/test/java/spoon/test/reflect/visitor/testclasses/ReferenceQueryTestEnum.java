package spoon.test.reflect.visitor.testclasses;


public enum ReferenceQueryTestEnum {
	E0(new Integer(0)),
	E1(new Long(1)),
	;
    Boolean bool;

    ReferenceQueryTestEnum(Number throwable) {
        String t = "true";
        bool = Boolean.valueOf(t);
    }

    public Void getNumber() {
        return null;
    }
}