package spoon.reflect.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.compiler.VirtualFile;

class AstParentConsistencyCheckerTest {
	private static final int DEEP_BINARY_OPERATOR_COUNT = 32_000;

	@Test
	void deeplyNestedBinaryOperatorsDoNotConsumeOneStackFrameEach() {
		// contract: parent validation supports legal, deeply nested binary-expression trees
		// (#6804)
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile(deepBinarySource("DeepBinary"), "DeepBinary.java"));

		// when / then
		assertThatCode(launcher::buildModel).doesNotThrowAnyException();
	}

	@Test
	void iterativeBinaryTraversalStillRejectsAnInconsistentParent() {
		// contract: iterative traversal retains the parent-consistency invariant
		// (#6804)
		// given
		CtClass<?> type = Launcher.parseClass("class BrokenBinary { int value = 1 + 2 + 3; }");
		CtBinaryOperator<?> root = (CtBinaryOperator<?>) type.getField("value").getDefaultExpression();
		root.getLeftHandOperand().setParent(root.getRightHandOperand());

		// when / then
		assertThatThrownBy(() -> new AstParentConsistencyChecker().scan(root))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("however it is visited as a child of");
	}

	@Test
	void deeplyNestedInconsistentBinaryOperatorProducesTheParentDiagnostic() {
		// contract: reporting an invalid deep tree is bounded as well as validating a valid one
		// (#6804)
		// given
		Launcher launcher = new Launcher();
		launcher.getEnvironment().disableConsistencyChecks();
		launcher.addInputResource(new VirtualFile(
				deepBinarySource("BrokenDeepBinary"), "BrokenDeepBinary.java"));
		launcher.buildModel();
		CtClass<?> type = launcher.getFactory().Class().get("BrokenDeepBinary");
		CtBinaryOperator<?> root = (CtBinaryOperator<?>) type.getField("value").getDefaultExpression();
		root.getLeftHandOperand().setParent(type.getField("value"));

		// when / then
		assertThatThrownBy(() -> new AstParentConsistencyChecker().scan(root))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("PLUS binary operator")
				.hasMessageContaining("however it is visited as a child of");
	}

	@Test
	void subclassesRetainRecursiveScannerDispatchHooks() {
		// contract: subclasses retain CtScanner's visit, role, enter, and exit ordering
		// (#6804)
		// given
		CtClass<?> type = Launcher.parseClass("class BinaryHooks { int value = 1 + 2 + 3; }");
		CtBinaryOperator<?> root = (CtBinaryOperator<?>) type.getField("value").getDefaultExpression();
		DispatchCountingChecker checker = new DispatchCountingChecker();

		// when
		checker.scan(root);

		// then
		assertThat(checker.events).containsExactly(
				"visit PLUS",
				"enter PLUS",
				"role leftOperand",
				"visit PLUS",
				"enter PLUS",
				"exit PLUS",
				"exit PLUS");
	}

	@Test
	void directAcceptDoesNotLeakParentStateIntoTheNextScan() {
		// contract: direct visitor dispatch leaves the reusable checker's root state unchanged
		// (#6804)
		// given
		CtClass<?> firstType = Launcher.parseClass("class FirstBinary { int value = 1 + 2; }");
		CtBinaryOperator<?> firstRoot =
				(CtBinaryOperator<?>) firstType.getField("value").getDefaultExpression();
		CtClass<?> secondType = Launcher.parseClass("class SecondBinary { int value = 3 + 4; }");
		CtBinaryOperator<?> secondRoot =
				(CtBinaryOperator<?>) secondType.getField("value").getDefaultExpression();
		AstParentConsistencyChecker checker = new AstParentConsistencyChecker();

		// when
		firstRoot.accept(checker);

		// then
		assertThatCode(() -> checker.scan(secondRoot)).doesNotThrowAnyException();
	}

	private static String deepBinarySource(String typeName) {
		StringBuilder source = new StringBuilder("class ")
				.append(typeName)
				.append(" { String value = \"a\"");
		for (int index = 0; index < DEEP_BINARY_OPERATOR_COUNT; index++) {
			source.append(" + \"a\"");
		}
		return source.append("; }").toString();
	}

	private static final class DispatchCountingChecker extends AstParentConsistencyChecker {
		private final java.util.List<String> events = new java.util.ArrayList<>();

		@Override
		public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
			events.add("visit " + operator.getKind());
			super.visitCtBinaryOperator(operator);
		}

		@Override
		public void scan(CtRole role, CtElement element) {
			if (role == CtRole.LEFT_OPERAND && element instanceof CtBinaryOperator<?>) {
				events.add("role " + role);
			}
			super.scan(role, element);
		}

		@Override
		protected void enter(CtElement element) {
			if (element instanceof CtBinaryOperator<?> operator) {
				events.add("enter " + operator.getKind());
			}
		}

		@Override
		protected void exit(CtElement element) {
			if (element instanceof CtBinaryOperator<?> operator) {
				events.add("exit " + operator.getKind());
			}
		}
	}
}
