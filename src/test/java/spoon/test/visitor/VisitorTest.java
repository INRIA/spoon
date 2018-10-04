package spoon.test.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

import static org.junit.Assert.assertTrue;

/**
 * Created by marcel on 16.03.16.
 */
public class VisitorTest {

	static class MyVisitor extends CtScanner {
		private int expected;
		private int actual;
		public boolean equals;

		MyVisitor(int expected) {
			this.expected = expected;
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			actual = 0;
			super.visitCtMethod(m);
			equals = expected == actual;
		}

		@Override
		public void visitCtIf(CtIf ifElement) {
			actual++;
			super.visitCtIf(ifElement);
		}
	}

	@Test
	public void testRecursiveDescent() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/visitor/Foo.java");
		launcher.buildModel();

		final MyVisitor visitor = new MyVisitor(2);
		visitor.scan(launcher.getFactory().Package().getRootPackage());
		assertTrue(visitor.equals);
	}
}
