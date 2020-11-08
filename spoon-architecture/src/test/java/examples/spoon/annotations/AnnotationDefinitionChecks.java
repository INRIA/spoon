package examples.spoon.annotations;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnnotationType;

public class AnnotationDefinitionChecks {

	@Architecture(modelNames = "annotationTests")
	public void allAnnotationsHaveDoc(CtModel testCases) {
		// contract: All annotations have any javadoc
		Precondition<CtAnnotationType<?>> pre = Precondition.of(DefaultElementFilter.ANNOTATIONS_DEFINITIONS.getFilter());
		Constraint<CtAnnotationType<?>> con =  Constraint.of(new ExceptionError<>("Found annotation without doc"), v -> !v.getDocComment().isEmpty());
		ArchitectureTest.of(pre, con).runCheck(testCases);
	}

	@Architecture(modelNames = "annotationTests")
	public void allNonMarkerAnnotationsHaveMethods(CtModel testCases) {
		// contract: All annotations have either "marker" in the doc or methods.
		Precondition<CtAnnotationType<?>> pre = Precondition.of(DefaultElementFilter.ANNOTATIONS_DEFINITIONS.getFilter());
		Constraint<CtAnnotationType<?>> con =  Constraint.of(new ExceptionError<>("Found annotation without doc"),
																												v -> v.getDocComment().contains("marker")
																												|| !v.getMethods().isEmpty());
		ArchitectureTest.of(pre, con).runCheck(testCases);
	}
}
