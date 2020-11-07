package spoon.architecture.preconditions;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import spoon.reflect.declaration.CtElement;
/**
 * This defines multiple helper methods for annotation matching for elements. It supports class objects with {@link #hasAnnotation(Class)}, regex with {@link #hasAnnotation(Pattern, boolean)} and strings {@link #hasAnnotation(String, boolean)}.
 * A qualified name is an identifier consisting of multiple identifier separated by a dot.
 */
public class Annotations {
	private Annotations() {

	}
	private static class HasAnnotationClass implements Predicate<CtElement> {
		private Class<? extends Annotation> annotation;
		@Override
		public boolean test(CtElement t) {
			return t.hasAnnotation(annotation);
		}
		HasAnnotationClass(Class<? extends Annotation> annotation) {
			this.annotation = annotation;
		}
	}
	public static Predicate<CtElement> hasAnnotation(Class<? extends Annotation> annotation) {
		return new HasAnnotationClass(annotation);
	}

	private static class HasAnnotationString implements Predicate<CtElement> {
		private String annotationName;
		private boolean qualified;
		@Override
		public boolean test(CtElement t) {
			if (qualified) {
				return t.getAnnotations().stream().anyMatch(v -> v.getType().getQualifiedName().equals(annotationName));
			}	else {
				return t.getAnnotations().stream().anyMatch(v -> v.getType().getSimpleName().equals(annotationName));
			}
		}
		HasAnnotationString(String annotationName, boolean qualified) {
			this.annotationName = annotationName;
			this.qualified = qualified;
		}
	}
	public static Predicate<CtElement> hasAnnotation(String annotationName, boolean qualified) {
		return new HasAnnotationString(annotationName, qualified);
	}
	private static class HasAnnotationPattern implements Predicate<CtElement> {
		private Pattern annotationPattern;
		private boolean qualified;
		@Override
		public boolean test(CtElement t) {
			// As asMatchPredicate is a java 11 method we need to build it ourself
			if (qualified) {
				return t.getAnnotations().stream().anyMatch(v -> annotationPattern.matcher(v.getType().getQualifiedName()).matches());
			} else {
				return t.getAnnotations().stream().anyMatch(v ->  annotationPattern.matcher(v.getType().getSimpleName()).matches());
			}
		}
		HasAnnotationPattern(Pattern annotationPattern, boolean qualified) {
			this.annotationPattern = annotationPattern;
			this.qualified = qualified;
		}
	}
	public static Predicate<CtElement> hasAnnotation(Pattern annotationPattern, boolean qualified) {
		return new HasAnnotationPattern(annotationPattern, qualified);
	}
}
