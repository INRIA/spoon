package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SwitchPatternTest {

	private static CtModel createModelFromString(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(20);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	private static CtSwitch<?> createFromSwitchStatement(String cases) {
		return createModelFromString("""
									class Foo {
										void foo(Object arg) {
											switch (arg) {
												%s -> {};
											}
									}
						""".formatted(cases))
						.getElements(new TypeFilter<>(CtSwitch.class)).iterator().next();
	}

	@Test
	void testTypePatternInSwitch() {
		CtSwitch<?> sw = createFromSwitchStatement("case Integer i");
		CtCase<?> ctCase = sw.getCases().get(0);
		CtExpression<?> caseExpression = ctCase.getCaseExpression();
		assertThat(caseExpression).isInstanceOf(CtTypePattern.class); // TODO or CtCasePattern?
	}

	@Test
	void testCasePatternWithGuardInSwitch() {
		// contract: CasePattern holds guard and its inner pattern
		CtSwitch<?> sw = createFromSwitchStatement("case Integer i when i > 0");
		CtCase<?> ctCase = sw.getCases().get(0);
		CtExpression<?> caseExpression = ctCase.getCaseExpression();
		assertThat(caseExpression).isInstanceOf(CtCasePattern.class);
		CtExpression<?> guard = ((CtCasePattern) caseExpression).getGuard();
		assertThat(guard).isInstanceOf(CtBinaryOperator.class);

		CtPattern pattern = ((CtCasePattern) caseExpression).getPattern();
		assertThat(pattern).isInstanceOf(CtTypePattern.class);
	}

	@Test
	void testCaseNull() {
		// contract: "case null" is represented by a null literal
		CtSwitch<?> sw = createFromSwitchStatement("case null");
		CtCase<?> ctCase = sw.getCases().get(0);
		assertThat(ctCase.getCaseExpression()).isInstanceOf(CtLiteral.class);
		assertThat(ctCase.getCaseExpression().getType()).isEqualTo(sw.getFactory().Type().nullType());
	}

	@Test
	void testCaseNullDefault() {
		// contract: "case null, default" is represented by a null literal and TODO ???
		CtSwitch<?> sw = createFromSwitchStatement("case null");
		CtCase<?> ctCase = sw.getCases().get(0);
		List<? extends CtExpression<?>> caseExpressions = ctCase.getCaseExpressions();
		assertThat(caseExpressions).hasSize(2);
		// TODO
	}
}
