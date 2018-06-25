package spoon.test.executable.testclasses;

public class WithEnum {
	enum MYENUM {
		VALUE1, VALUE2
	}

	public MYENUM from(String s) {
		return MYENUM.valueOf(s);
	}
}
