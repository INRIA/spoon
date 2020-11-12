package examples.spoon.annotations;

import java.util.function.Predicate;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnnotation;

/**
 * This class contains multiple example for architecture checks for annotations. This checks allow rules for the values an annotation has set and it's value.
 */
public class AnnotationValueChecks {

	@Architecture(modelNames = "annotationTests")
	public void allSuppressWarningsHaveValues(CtModel testCases) {
		// contract: All suppressWarnings annotations have a value specified e.g. 'all'
		Precondition<CtAnnotation<?>> pre = Precondition.of(DefaultElementFilter.ANNOTATIONS.getFilter(),	isSuppressWarnings());
		Constraint<CtAnnotation<?>> con =  Constraint.of(new ExceptionError<>("Found @SuppressingWarnings without specified values"), v -> !v.getValues().isEmpty());
		ArchitectureTest.of(pre, con).runCheck(testCases);
	}

	private Predicate<? super CtAnnotation<?>> isSuppressWarnings() {
		return (annotation) -> annotation.getAnnotationType().getSimpleName().equals("SuppressWarnings");
	}

	@Architecture(modelNames = "annotationTests")
	public void allDeprecatedHaveForRemovalSetToTrue(CtModel testCases) {
		// contract: All @Deprecated have forRemoval set to true
		Precondition<CtAnnotation<?>> pre = Precondition.of(DefaultElementFilter.ANNOTATIONS.getFilter(), isDeprecatedAnnotation());
		Constraint<CtAnnotation<?>> con =  Constraint.of(new ExceptionError<>("Found @Deprecated without for removal set"),
																										v -> String.valueOf(v.getValue("forRemoval")).equals("true"));
		ArchitectureTest.of(pre, con).runCheck(testCases);
	}

	private Predicate<? super CtAnnotation<?>> isDeprecatedAnnotation() {
		return (annotation) -> annotation.getAnnotationType().getSimpleName().equals("Deprecated");
	}
}
