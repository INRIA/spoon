package spoon.testing.assertions;
import java.lang.annotation.Annotation;

import org.assertj.core.api.*;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.PrettyPrinter;

public interface CtElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtElement> extends SpoonAssert<A, W> {
	default ListAssert<CtAnnotation<? extends Annotation>> getAnnotations() {
		return Assertions.assertThat(actual().getAnnotations());
	}

	default ListAssert<CtComment> getComments() {
		return Assertions.assertThat(actual().getComments());
	}

	default ObjectAssert<SourcePosition> getPosition() {
		return Assertions.assertThatObject(actual().getPosition());
	}

	default AbstractBooleanAssert<?> isImplicit() {
		return Assertions.assertThat(actual().isImplicit());
	}

	/**
	 * {@return an {@link AbstractStringAssert} as if {@code assertThat(element.toString())} was called}
	 */
	default AbstractStringAssert<?> printed() {
		return Assertions.assertThat(actual().toString());
	}

	/**
	 * {@return an {@link AbstractStringAssert} as if {@code assertThat(printer.prettyPrint(element))} was called}
	 */
	default AbstractStringAssert<?> printed(PrettyPrinter printer) {
		return Assertions.assertThat(printer.printElement(actual()));
	}

	/**
	 * {@return an {@link AbstractStringAssert} as if {@code assertThat(element.prettyPrint())} was called}
	 */
	default AbstractStringAssert<?> prettyPrinted() {
		return Assertions.assertThat(actual().prettyprint());
	}
}
