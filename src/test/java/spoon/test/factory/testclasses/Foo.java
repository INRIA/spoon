package spoon.test.factory.testclasses;

@Foo.Bar()
public class Foo {
	public @interface Bar {
		Class<Foo> clazz() default Foo.class;

		Class<Foo>[] classes() default {};
	}
}
