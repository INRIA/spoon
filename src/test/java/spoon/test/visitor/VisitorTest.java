package spoon.test.visitor;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;
import spoon.testing.utils.ModelTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@ModelTest("./src/test/resources/spoon/test/visitor/Foo.java")
	public void testRecursiveDescent(Factory factory) {
		final MyVisitor visitor = new MyVisitor(2);
		visitor.scan(factory.Package().getRootPackage());
		assertTrue(visitor.equals);
	}
}
