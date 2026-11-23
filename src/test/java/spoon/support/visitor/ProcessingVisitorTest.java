package spoon.support.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.processing.TraversalStrategy;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.support.compiler.VirtualFile;

class ProcessingVisitorTest {
	private static final int DEEP_BINARY_OPERATOR_COUNT = 32_000;

	@ParameterizedTest
	@EnumSource(TraversalStrategy.class)
	void deeplyNestedBinaryOperatorsAreProcessedWithoutRecursiveTraversal(TraversalStrategy traversalStrategy) {
		// contract: both processor traversal strategies support legal, deeply nested binary trees
		// (#6804)
		// given
		StringBuilder source = new StringBuilder("class DeepBinaryProcessing { String value = \"a\"");
		for (int index = 0; index < DEEP_BINARY_OPERATOR_COUNT; index++) {
			source.append(" + \"a\"");
		}
		source.append("; }");
		Launcher launcher = new Launcher();
		launcher.getEnvironment().disableConsistencyChecks();
		launcher.addInputResource(new VirtualFile(source.toString(), "DeepBinaryProcessing.java"));
		launcher.buildModel();
		CtClass<?> type = launcher.getFactory().Class().get("DeepBinaryProcessing");
		CtBinaryOperator<?> root = (CtBinaryOperator<?>) type.getField("value").getDefaultExpression();
		CountingBinaryProcessor processor = new CountingBinaryProcessor(traversalStrategy);
		processor.setFactory(launcher.getFactory());
		ProcessingVisitor visitor = new ProcessingVisitor(launcher.getFactory());
		visitor.setProcessor(processor);

		// when / then
		assertThatCode(() -> visitor.scan(root)).doesNotThrowAnyException();
		assertThat(processor.processedOperators).isEqualTo(DEEP_BINARY_OPERATOR_COUNT);
	}

	private static final class CountingBinaryProcessor extends AbstractProcessor<CtBinaryOperator<?>> {
		private final TraversalStrategy traversalStrategy;
		private int processedOperators;

		private CountingBinaryProcessor(TraversalStrategy traversalStrategy) {
			this.traversalStrategy = traversalStrategy;
		}

		@Override
		public TraversalStrategy getTraversalStrategy() {
			return traversalStrategy;
		}

		@Override
		public void process(CtBinaryOperator<?> operator) {
			processedOperators++;
		}
	}
}
