package spoon.test.casts.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class Castings {
	private String s = (@TypeAnnotation(integer = 1) String) "";

	public void test(double a) {
	}

	public void foo() {
		List<Integer> list = new ArrayList<Integer>(1);
		list.add(1);
		test(getValue(list));
	}

	public void bar() {
		String s = (@TypeAnnotation(integer = 1) String) "";
	}

	public final <T> T getValue(List<T> list) {
		return list.get(0);
	}

	@Target({ ElementType.TYPE_USE })
	@interface TypeAnnotation {
		int integer() default 0;
	}
}
