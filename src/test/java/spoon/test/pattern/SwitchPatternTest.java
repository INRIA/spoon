package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class SwitchPatternTest {

	private static CtModel createModelFromString(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(22);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	private static CtSwitch<?> createFromSwitchStatement(String cases) {
		return createFromSwitchStatement(cases, true);
	}

	private static CtSwitch<?> createFromSwitchStatement(String cases, boolean def) {
		return createModelFromString(
			"""
				class Foo {
					void foo(Object arg) {
						switch (arg) {
							%s -> {}
							%s
						}
				}
				""".formatted(cases, def ? "default -> {}" : ""))
			.getElements(new TypeFilter<>(CtSwitch.class)).iterator().next();
	}

	@Test
	void testTypePatternInSwitch() {
		// contract: a simple type pattern is supported
		CtSwitch<?> sw = createFromSwitchStatement("case Integer i");
		CtCase<?> ctCase = sw.getCases().get(0);
		assertThat(ctCase.getIncludesDefault()).isFalse();
		CtExpression<?> caseExpression = ctCase.getCaseExpression();
		assertThat(caseExpression).isInstanceOf(CtCasePattern.class);
		assertThat(ctCase.toString()).containsPattern("case (java\\.lang\\.)?Integer i ->");
	}

	@Test
	void testCasePatternWithGuardInSwitch() {
		// contract: CasePattern holds guard and its inner pattern
		CtSwitch<?> sw = createFromSwitchStatement("case Integer i when i > 0");
		CtCase<?> ctCase = sw.getCases().get(0);
		assertThat(ctCase.getIncludesDefault()).isFalse();
		CtExpression<?> caseExpression = ctCase.getCaseExpression();
		assertThat(caseExpression).isInstanceOf(CtCasePattern.class);
		CtExpression<?> guard = ctCase.getGuard();
		assertThat(guard).isInstanceOf(CtBinaryOperator.class);

		CtPattern pattern = ((CtCasePattern) caseExpression).getPattern();
		assertThat(pattern).isInstanceOf(CtTypePattern.class);
		assertThat(ctCase.toString()).containsPattern("case (java\\.lang\\.)?Integer i when i > 0 ->");
	}

	@Test
	void testCaseNull() {
		// contract: "case null" is represented by a null literal
		CtSwitch<?> sw = createFromSwitchStatement("case null");
		CtCase<?> ctCase = sw.getCases().get(0);
		assertThat(ctCase.getIncludesDefault()).isFalse();
		assertThat(ctCase.getCaseExpression()).isInstanceOf(CtLiteral.class);
		assertThat(ctCase.getCaseExpression().getType()).isEqualTo(sw.getFactory().Type().nullType());
		assertThat(ctCase.toString()).contains("case null ->");
	}

	@Test
	void testCaseNullDefault() {
		// contract: "case null, default" is represented by a null literal and the includesDefault property set to true
		CtSwitch<?> sw = createFromSwitchStatement("case null, default", false);
		CtCase<?> ctCase = sw.getCases().get(0);
		List<? extends CtExpression<?>> caseExpressions = ctCase.getCaseExpressions();
		assertThat(caseExpressions).hasSize(1);
		assertThat(ctCase.getIncludesDefault()).isTrue();
		assertThat(ctCase.toString()).contains("case null, default ->");
	}

	@Test
	void testCaseQualifiedEnumConstant() {
		// contract: fully qualified enum constants in cases are present in the model and printed again
		CtSwitch<?> sw = createFromSwitchStatement("case java.nio.file.StandardCopyOption.ATOMIC_MOVE");
		CtCase<?> ctCase = sw.getCases().get(0);
		assertThat(ctCase.getIncludesDefault()).isFalse();
		List<? extends CtExpression<?>> caseExpressions = ctCase.getCaseExpressions();
		CtFieldRead<Object> fieldRead = sw.getFactory().createFieldRead();
		CtTypeReference<StandardCopyOption> declaringType = sw.getFactory().Type().createReference(StandardCopyOption.class);
		fieldRead.setTarget(sw.getFactory().createTypeAccess(declaringType));
		fieldRead.setVariable(
			sw.getFactory().Core().createFieldReference().setDeclaringType(declaringType)
				.setFinal(true).setStatic(true).setSimpleName("ATOMIC_MOVE")
		).setType(declaringType);
		assertThat(caseExpressions).hasSize(1);
		CtExpression<?> expression = caseExpressions.get(0);
		assertThat(expression).isEqualTo(fieldRead);
		assertThat(((CtFieldRead<?>) expression).getTarget()).matches(Predicate.not(CtExpression::isImplicit));
		assertThat(sw.toString()).contains("case java.nio.file.StandardCopyOption.ATOMIC_MOVE");
	}
}
