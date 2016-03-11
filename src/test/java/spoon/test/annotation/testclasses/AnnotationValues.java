package spoon.test.annotation.testclasses;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@AnnotationValues.Annotation(
		integer = 42,
		integers = { 7, 42 },
		string = "Everyone love tacos!",
		strings = "",
		clazz = AnnotationValues.class,
		classes = { Annotation.class, AnnotationValues.class },
		b = true,
		e = AnnotationValues.Annotation.InnerEnum.R,
		ia = @AnnotationValues.Annotation.InnerAnnotation,
		ias = { @AnnotationValues.Annotation.InnerAnnotation })
public class AnnotationValues {

	public void method() {
		new @AnnotationValues.Annotation(
				integer = 42,
				integers = { 7, 42 },
				string = "Everyone love tacos!",
				strings = "",
				clazz = AnnotationValues.class,
				classes = { java.lang.annotation.Annotation.class, AnnotationValues.class },
				b = true,
				e = AnnotationValues.Annotation.InnerEnum.R,
				ia = @AnnotationValues.Annotation.InnerAnnotation,
				ias = { @AnnotationValues.Annotation.InnerAnnotation }) String();
	}

	@Target({
			ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE, ElementType.TYPE_USE
	})
	public @interface Annotation {
		int integer();

		int[] integers();

		String string();

		String[] strings();

		Class<?> clazz();

		Class<?>[] classes();

		boolean b();

		InnerEnum e();

		InnerAnnotation ia();

		InnerAnnotation[] ias();

		@interface InnerAnnotation {
		}

		enum InnerEnum {
			R;
		}
	}
}
