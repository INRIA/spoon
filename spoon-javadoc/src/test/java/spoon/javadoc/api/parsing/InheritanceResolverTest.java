package spoon.javadoc.api.parsing;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocBlockTag;
import spoon.javadoc.api.elements.JavadocCommentView;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocReference;
import spoon.javadoc.api.elements.JavadocText;
import spoon.reflect.declaration.CtMethod;
import spoon.support.compiler.VirtualFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InheritanceResolverTest {

	@Test
	void testInheritAll() {
		// contract: If no javadoc is specified, everything is inherited
		var view = inheritTestFromSuper("");
		assertEquals(view.actual().getElements(), view.ref().getElements());
	}

	@Test
	void testInheritDocBody() {
		// contract: An inheritDoc tag is inherited (as is the rest implicitly)
		var view = inheritTestFromSuper("""
			/**
			  * {@inheritDoc}
			  */""");
		assertEquals(view.actual().getElements(), view.ref().getElements());
	}

	@Test
	void testInheritDocParam() {
		// contract: An inheritDoc tag is inherited (as is the rest implicitly)
		var view = inheritTestFromSuper("""
			/**
			  * @param a {@inheritDoc}
			  */""");
		assertEquals(view.actual().getElements(), view.ref().getElements());
	}

	@Test
	void testInheritDocThrows() {
		// contract: An inheritDoc tag is inherited (as is the rest implicitly)
		var view = inheritTestFromSuper("""
			/**
			  * @throws IOException {@inheritDoc}
			  */""");
		assertEquals(view.actual().getElements(), view.ref().getElements());
	}

	@Test
	void testInheritDocException() {
		// contract: An inheritDoc tag is inherited for throws/exception (as is the rest implicitly)
		var view = inheritTestFromSuper("""
			/**
			  * @exception IOException {@inheritDoc}
			  */""");
		assertEquals(view.actual().getElements(), view.ref().getElements());
	}

	@Test
	void testInheritMultiple() {
		// contract: Inheritance walks the super interfaces and extends in-order
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.addInputResource("src/test/java/");
		launcher.buildModel();

		CtMethod<?> method = launcher.getFactory().Type().get(SubMultiSuper.class).getMethodsByName("test").get(0);
		JavadocCommentView view = new JavadocCommentView(new InheritanceResolver().completeJavadocWithInheritedTags(
			method, new JavadocCommentView(JavadocParser.forElement(method))
		));

		List<JavadocElement> expected = List.of(
			new JavadocText("A test."),
			new JavadocBlockTag(
				List.of(new JavadocText("a"), new JavadocText("the a param")),
				StandardJavadocTagType.PARAM
			),
			new JavadocBlockTag(
				List.of(new JavadocText("foo")),
				StandardJavadocTagType.RETURN
			),
			new JavadocBlockTag(
				List.of(
					new JavadocReference(launcher.getFactory().createCtTypeReference(IOException.class)),
					new JavadocText("never")
				),
				StandardJavadocTagType.THROWS
			)
		);

		assertEquals(expected, view.getElements());
	}

	@Test
	void testInheritMultiple22() {
		// contract: Inheritance walks the super interfaces and extends in-order (but now for java 22)
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.addInputResource("src/test/java/");
		launcher.buildModel();

		CtMethod<?> method = launcher.getFactory().Type().get(SubMultiSuper.class).getMethodsByName("test").get(0);
		method.getFactory().getEnvironment().setComplianceLevel(22);

		JavadocCommentView view = new JavadocCommentView(new InheritanceResolver().completeJavadocWithInheritedTags(
			method, new JavadocCommentView(JavadocParser.forElement(method))
		));

		List<JavadocElement> expected = List.of(
			new JavadocText("Never used in <22, shadowed by the interface."),
			new JavadocBlockTag(
				List.of(new JavadocText("a"), new JavadocText("the a param")),
				StandardJavadocTagType.PARAM
			),
			new JavadocBlockTag(
				List.of(new JavadocText("foo")),
				StandardJavadocTagType.RETURN
			),
			new JavadocBlockTag(
				List.of(
					new JavadocReference(launcher.getFactory().createCtTypeReference(IOException.class)),
					new JavadocText("never")
				),
				StandardJavadocTagType.THROWS
			)
		);

		assertEquals(expected, view.getElements());
	}

	private static CompletedDoc inheritTestFromSuper(String javadoc) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.addInputResource("src/test/java/");

		String subclassName = InheritanceResolverTest.class.getPackageName() + ".Sub";
		String packageName = InheritanceResolverTest.class.getPackageName();
		launcher.addInputResource(new VirtualFile(
			"""
				package %s;
				import java.io.IOException;
				import %s;
				public static class Sub extends Super {
				%s
				@Override
				public <T> int test(int a) throws IOException { return a; }
				}
				""".formatted(packageName, InheritanceResolverTest.class.getName() + ".Super", javadoc),
			subclassName.replace(".", "/")
		));

		launcher.buildModel();
		CtMethod<?> method = launcher.getFactory().Type().get(subclassName).getMethodsByName("test").get(0);

		JavadocCommentView actual = new JavadocCommentView(
			new InheritanceResolver().completeJavadocWithInheritedTags(
				method, new JavadocCommentView(JavadocParser.forElement(method))
			)
		);
		JavadocCommentView ref = new JavadocCommentView(
			JavadocParser.forElement(
				launcher.getFactory().Type().get(Super.class).getMethodsByName("test").get(0)
			)
		);
		return new CompletedDoc(actual, ref);
	}

	static class Super {
		/**
		 * This is a foo method.
		 *
		 * @param a the a param
		 * @param <T> a type param
		 * @return foo
		 * @throws IOException never.
		 */
		public <T> int test(int a) throws IOException {
			return a;
		}
	}

	private interface SuperInt1 {
		/**
		 * A test.
		 */
		<T> int test(int a) throws IOException;
	}

	private interface SuperInt2 extends SuperInt1 {
		/**
		 * @param a the a param
		 */
		@Override
		<T> int test(int a) throws IOException;
	}

	private interface SuperInt3 {
		/**
		 * Never used, shadowed by body in super int 1.
		 *
		 * @throws IOException never
		 */
		<T> int test(int a) throws IOException;
	}

	private interface SuperInt4 {
		/**
		 * @return foo
		 */
		<T> int test(int a) throws IOException;
	}

	private static class SubMultiSuperParent {
		/**
		 * Never used in <22, shadowed by the interface.
		 */
		public <T> int test(int a) throws IOException {
			return 0;
		}
	}

	private static class SubMultiSuper extends SubMultiSuperParent
		implements SuperInt2, SuperInt3, SuperInt4 {

		@Override
		public int test(int a) throws IOException {
			return 0;
		}
	}

	private record CompletedDoc(JavadocCommentView actual, JavadocCommentView ref) {

	}
}
