package spoon.test.arrays.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public class ArrayClass {

	int[][][] i = new @TypeAnnotation(integer = 1) int[0][][];

	int[] x;

	void m() {
		int[][][] i = new int[0][][];
		i[0] = null;
	}

	@Target({ ElementType.TYPE_USE })
	@interface TypeAnnotation {
		int integer() default 0;
	}

}
