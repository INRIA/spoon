package spoon.test.annotation.testclasses;

public class AnnotationsAppliedOnAnyElementInAClass<T>
		implements @TypeAnnotation BasicAnnotation<@TypeAnnotation T> {
	public String m() throws @TypeAnnotation Exception {
		Object s = new @TypeAnnotation String("");
		return (@TypeAnnotation String) s;
	}
}
