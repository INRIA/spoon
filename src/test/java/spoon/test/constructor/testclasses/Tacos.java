package spoon.test.constructor.testclasses;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public class Tacos<C> {
	public Tacos() {
		super();
	}

	public <@TypeAnnotation(integer = 2) T extends @TypeAnnotation(integer = 3) Tacos<@TypeAnnotation(integer = 4) ? super @TypeAnnotation(integer = 5) C> & @TypeAnnotation(integer = 6) Serializable> Tacos(Object o) throws @TypeAnnotation(integer = 1) Exception {
	}

	@Target({ ElementType.TYPE_USE })
	public @interface TypeAnnotation {
		int integer() default 1;
	}
}
