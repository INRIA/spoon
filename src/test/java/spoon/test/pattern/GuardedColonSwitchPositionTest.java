/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2026 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.InstanceOfAssertFactories;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.visitor.filter.TypeFilter;

import static org.assertj.core.api.Assertions.assertThat;

class GuardedColonSwitchPositionTest {
	private static CtModel createModel() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(25);
		launcher.addInputResource("src/test/resources/spoon/test/pattern/GuardedColonSwitch.java");
		return launcher.buildModel();
	}

	@Test
	void guardedFallThroughCaseHasOrderedContainedSourcePosition() {
		// contract: #6806 consecutive guarded colon cases have ordered, contained positions
		CtModel model = createModel();

		assertThat(model.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class)))
			.singleElement()
			.satisfies(ctSwitch -> {
				int switchStart = ctSwitch.getPosition().getSourceStart();
				int switchEnd = ctSwitch.getPosition().getSourceEnd();
				assertThat(ctSwitch.getCases())
					.satisfiesExactly(
						firstCase -> {
							assertThat(firstCase.getGuard()).as("first case guard").isNotNull();
							assertThat(firstCase.getStatements()).as("first case statements").isEmpty();
						},
						secondCase -> {
							assertThat(secondCase.getGuard()).as("second case guard").isNotNull();
							assertThat(secondCase.getCaseExpressions()).as("second case expressions").isNotEmpty();
							assertThat(secondCase.getStatements()).as("second case statements").hasSize(2);
						},
						defaultCase -> assertThat(defaultCase.getGuard()).as("default case guard").isNull())
					.allSatisfy(ctCase -> {
						SourcePosition position = ctCase.getPosition();
						assertThat(position.isValidPosition()).as("position of case <%s> is valid", ctCase).isTrue();
						assertThat(position.getSourceStart())
							.as("start of case <%s>", ctCase)
							.isBetween(switchStart, position.getSourceEnd());
						assertThat(position.getSourceEnd())
							.as("end of case <%s>", ctCase)
							.isLessThanOrEqualTo(switchEnd);
					});
			});
	}

	@Test
	void castGuardIsAttachedToCase() {
		// contract: guard identification follows JDT through casts that Spoon flattens into the expression
		CtModel model = createModel();

		assertThat(model.getElements(new TypeFilter<CtSwitchExpression<?, ?>>(CtSwitchExpression.class)))
			.singleElement()
			.satisfies(ctSwitch -> assertThat(ctSwitch.getCases())
				.first()
				.extracting(CtCase::getGuard)
				.as("guard of the first switch-expression case")
				.isNotNull()
				.extracting(CtExpression::getTypeCasts)
				.asInstanceOf(InstanceOfAssertFactories.LIST)
				.hasSize(1));
	}
}
