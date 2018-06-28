package spoon.test.annotation.testclasses;

public class Foo {
	@OuterAnnotation({ @MiddleAnnotation(@InnerAnnotation("hello")), @MiddleAnnotation(@InnerAnnotation("hello again")) })
	public void test() {
	}

	public @interface OuterAnnotation {
		MiddleAnnotation[] value();
	}

	public @interface MiddleAnnotation {
		InnerAnnotation value();
	}

	public @interface InnerAnnotation {
		String value();
	}
}
