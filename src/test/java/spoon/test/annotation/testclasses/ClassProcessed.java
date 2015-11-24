package spoon.test.annotation.testclasses;

import java.util.ArrayList;

@GlobalAnnotation
public class ClassProcessed<@TypeAnnotation T> {
	@GlobalAnnotation
	public ClassProcessed() {
		@GlobalAnnotation
		String s = new @TypeAnnotation String();
	}

	@GlobalAnnotation
	public @TypeAnnotation Void m() throws @TypeAnnotation Exception {
		new ArrayList<@GlobalAnnotation T>();
		return null;
	}
}
