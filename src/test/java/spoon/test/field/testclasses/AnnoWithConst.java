package spoon.test.field.testclasses;

public @interface AnnoWithConst {
	int VALUE = 42;
}

class User {
	int i = AnnoWithConst.VALUE;
}