/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2026 INRIA and contributors
 */
package spoon.test.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.JLSViolation;
import spoon.Launcher;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

class SupplementaryIdentifierTest {

	private static final String SUPPLEMENTARY_IDENTIFIER = "\uD801\uDC00abc";

	@ParameterizedTest(name = "source spelling {index}: {0}")
	@ValueSource(strings = { SUPPLEMENTARY_IDENTIFIER, "\\uD801\\uDC00abc" })
	void acceptsSupplementaryIdentifierReferences(String sourceIdentifier) {
		// contract: raw and escaped supplementary Java identifiers are validated as one code point
		// (#6810)
		CtClass<?> type = Launcher.parseClass("""
				class SupplementaryIdentifier {
					int %s = 1;
					boolean valid() { return %s == 1; }
				}
				""".formatted(sourceIdentifier, sourceIdentifier));

		assertThat(type.getElements(new TypeFilter<CtFieldRead<?>>(CtFieldRead.class)))
				.singleElement()
				.extracting(fieldRead -> fieldRead.getVariable().getSimpleName())
				.isEqualTo(SUPPLEMENTARY_IDENTIFIER);
	}

	@Test
	void rejectsSupplementaryCodePointsThatAreNotJavaIdentifierParts() {
		// contract: code-point validation does not make arbitrary supplementary characters legal identifiers
		// (#6810)
		var reference = new Launcher().getFactory().Core().createTypeReference();

		assertThatThrownBy(() -> reference.setSimpleName("\uD83D\uDE00name"))
				.isInstanceOf(JLSViolation.class);
	}
}
