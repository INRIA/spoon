package spoon.test.secondaryclasses.testclasses;

public class ClassWithInternalPublicClassOrInterf {

	public interface InternalInterf {

	}

	public class InternalClass {

	}
}

class Test {
	ClassWithInternalPublicClassOrInterf.InternalInterf test;
	ClassWithInternalPublicClassOrInterf.InternalClass test2;
}