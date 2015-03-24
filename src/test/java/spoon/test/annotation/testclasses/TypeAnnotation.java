package spoon.test.annotation.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE_USE })
public @interface TypeAnnotation {
	int integer() default 1;
	int[] integers() default {};

	String string() default "";
	String[] strings() default {};

	Class<?> clazz() default String.class;
	Class<?>[] classes() default {};

	boolean b() default true;

	AnnotParamTypeEnum e() default AnnotParamTypeEnum.R;
	InnerAnnot ia() default @InnerAnnot("");
	InnerAnnot[] ias() default {};

	Inception inception() default @Inception(@InnerAnnot(""));
	Inception[] inceptions() default {};
}
