package spoon.test.secondaryclasses;

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