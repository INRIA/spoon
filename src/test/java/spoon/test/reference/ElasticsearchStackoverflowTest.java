package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

public class ElasticsearchStackoverflowTest {

	private class Scanner extends CtScanner {
		@Override
		public <T> void visitCtExecutableReference(
				CtExecutableReference<T> reference) {
			super.visitCtExecutableReference(reference);
			reference.getDeclaration();
		}
	}

	@Test
	public void testStackOverflow() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/elasticsearch-stackoverflow");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtModel model = launcher.getModel();
		Scanner scanner = new Scanner();
		scanner.scan(model.getRootPackage());
	}

}
