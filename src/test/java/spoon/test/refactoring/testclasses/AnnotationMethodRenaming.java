package spoon.test.refactoring.testclasses;

@ExampleAnnotation(someAnnotationMethod = 0)
public class AnnotationMethodRenaming {
	@ExampleAnnotation(someAnnotationMethod = 1)
	public void mentioningTheAnnotation() {
		@ExampleAnnotation(someAnnotationMethod = 2)
		class SomeClass {
		}

		var test = new @ExampleAnnotation(someAnnotationMethod = 3) SomeClass();
	}

	public void processingTheAnnotation() {
		var annotation = this.getClass().getAnnotation(ExampleAnnotation.class);
		System.out.println(annotation.someAnnotationMethod());
	}
}

