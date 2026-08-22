package sniperPrinter;

@EnableAutoConfiguration(exclude = {
		java.lang.String.class,
		java.lang.Integer.class
})
class ClassKeywordAnnotation {

	static class Nested {

		void notFound() {
			throw new RuntimeException("boom");
		}
	}
}

@interface EnableAutoConfiguration {
	Class<?>[] exclude() default {};
}
