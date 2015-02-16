package spoon.test.annotation.testclasses;

@AnnotationRepeated("First")
@AnnotationRepeated("Second")
public class AnnotationsRepeated {
	@AnnotationRepeated("Field 1")
	@AnnotationRepeated("Field 2")
	private String field;

	@AnnotationRepeated("Constructor 1")
	@AnnotationRepeated("Constructor 2")
	public AnnotationsRepeated() {
	}

	@AnnotationRepeated("Method 1")
	@AnnotationRepeated("Method 2")
	public void method() {
	}

	public void methodWithParameter(@AnnotationRepeated("Param 1") @AnnotationRepeated("Param 2") String param) {
	}

	public void methodWithLocalVariable() {
		@AnnotationRepeated("Local 1") @AnnotationRepeated("Local 2") String s = "";
	}
}
