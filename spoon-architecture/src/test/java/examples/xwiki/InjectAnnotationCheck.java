package examples.xwiki;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.IError;
import spoon.architecture.preconditions.Annotations;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

public class InjectAnnotationCheck {

	private List<String> excludedFieldTypes;

	@Architecture(modelNames =  "yourModel")
	public void injectAnnotationOnlyOnFields(CtModel model) {
		Precondition<CtAnnotation<?>> pre = Precondition.of(DefaultElementFilter.ANNOTATIONS.getFilter());
		Constraint<CtAnnotation<?>> con = Constraint.of(new ErrorReporter<CtAnnotation<?>>("Only fields should use the @Inject annotation"), this::parentIsField);
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	@Architecture(modelNames =  "yourModel")
	public void foo(CtModel model) {
		Precondition<CtField<?>> pre = Precondition.of(DefaultElementFilter.FIELDS.getFilter(),
																		Annotations.hasAnnotation("Inject", false),
																		this::isNotExcluded);
		Constraint<CtField<?>> con = Constraint.of(new ErrorReporter<CtField<?>>("You must inject a component role"),
																	v -> isValidInterface(v.getType()) || isComponentAnnotationWithRoleToSelf(v.getType()));
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	private boolean parentIsField(CtAnnotation<?> annotation) {
		return annotation.getAnnotatedElementType().equals(CtAnnotatedElementType.FIELD);
	}

	private boolean isNotExcluded(CtField<?> ctField) {
		return this.excludedFieldTypes != null
				&& this.excludedFieldTypes.contains(ctField.getType().getQualifiedName());
	}

	private boolean isValidInterface(CtTypeReference<?> ctTypeReference) {
		return ctTypeReference.isInterface() && Annotations
				.hasAnnotation("org.xwiki.component.annotation.Role", true).test(ctTypeReference);
	}

	private boolean isComponentAnnotationWithRoleToSelf(CtTypeReference<?> ctTypeReference)	{
			boolean result = false;
			Optional<CtAnnotation<? extends Annotation>> ctAnnotation =
			getAnnotation(ctTypeReference, "org.xwiki.component.annotation.Component");
			if (ctAnnotation.isPresent()) {
					CtElement ctElement = ctAnnotation.get().getValue("roles");
					if (ctElement != null && ctElement.getReferencedTypes().contains(ctTypeReference)) {
							result = true;
					}
			}
			return result;
	}
	// from https://github.com/xwiki/xwiki-commons/blob/43335107f9117b9e9f5c751dabdcf4f3e482764f/xwiki-commons-tools/xwiki-commons-tool-spoon/xwiki-commons-tool-spoon-checks/src/main/java/org/xwiki/tool/spoon/SpoonUtils.java
	// needed as spoon does not have an getAnnotation(String) method
	private static Optional<CtAnnotation<? extends Annotation>> getAnnotation(
			CtTypeReference<?> ctTypeReference, String annotationFQN) {
		CtAnnotation<? extends Annotation> result = null;
		for (CtAnnotation<? extends Annotation> ctAnnotation : ctTypeReference.getTypeDeclaration()
				.getAnnotations()) {
			if (ctAnnotation.getType().getQualifiedName().equals(annotationFQN)) {
				result = ctAnnotation;
				break;
			}
		}
		return Optional.ofNullable(result);
	}
	private class ErrorReporter<T extends CtElement> implements IError<T> {
		private String error;

		ErrorReporter(String msg) {
			this.error = msg;
		}
		@Override
		public void printError(T element) {
			System.out.println(String.format(error + "Problem at %s", element.getPosition()));
		}
	}
}
