package spoon.test.annotation.testclasses;

public class Foo {
	@OuterAnnotation({ @MiddleAnnotation(@InnerAnnotation("hello")), @MiddleAnnotation(@InnerAnnotation("hello again")) })
	public void test() {
	}

	public @interface OuterAnnotation {
		public MiddleAnnotation[] value();
	}

	public @interface MiddleAnnotation {
		public InnerAnnotation value();
	}

	public @interface InnerAnnotation {
		public String value();
	}
}
