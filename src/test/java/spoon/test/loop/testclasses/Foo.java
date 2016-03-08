package spoon.test.loop.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public class Foo {
	public void m() {
		for (@TypeAnnotation int i = 0, j = 0; i < 0; i++) {
		}
		int[] ints = new int[] {};
		for (@TypeAnnotation int i : ints) {
		}
	}

	@Target({ ElementType.TYPE_USE })
	public @interface TypeAnnotation {
		int integer() default 1;
	}
}
