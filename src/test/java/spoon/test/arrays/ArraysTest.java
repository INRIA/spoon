package spoon.test.arrays;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class ArraysTest {

	@Test
	public void testArrayReferences() throws Exception {
		CtType<?> type = build("spoon.test.arrays", "ArrayClass");
		assertEquals("ArrayClass", type.getSimpleName());
		assertEquals("int[][][]", type.getField("i").getType().toString());
		assertEquals(3, ((CtArrayTypeReference<?>) type.getField("i").getType()).getDimensionCount());
		final CtArrayTypeReference<?> arrayTypeReference = (CtArrayTypeReference<?>) type.getField("i").getDefaultExpression().getType();
		assertEquals(1, arrayTypeReference.getArrayType().getAnnotations().size());
		assertEquals("@spoon.test.arrays.ArrayClass.TypeAnnotation(integer = 1)" + System.lineSeparator(), arrayTypeReference.getArrayType().getAnnotations().get(0).toString());

		CtField<?> x = type.getField("x");
		assertTrue(x.getType() instanceof CtArrayTypeReference);
		assertEquals("Array", x.getType().getSimpleName());
		assertEquals("java.lang.reflect.Array", x.getType().getQualifiedName());
		assertEquals("int", ((CtArrayTypeReference<?>) x.getType()).getComponentType().getSimpleName());
		assertTrue(((CtArrayTypeReference<?>) x.getType()).getComponentType().getActualClass().equals(int.class));
	}

	@Test
	public void testInitializeWithNewArray() throws Exception {
		Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/resources/noclasspath/Foo.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		CtType<Object> aType = launcher.getFactory().Type().get("com.example.Foo");

		final List<CtNewArray> elements = aType.getElements(new TypeFilter<>(CtNewArray.class));
		assertEquals(2, elements.size());

		final CtNewArray attribute = elements.get(0);
		assertEquals(1, attribute.getDimensionExpressions().size());
		assertEquals(0, ((CtLiteral) attribute.getDimensionExpressions().get(0)).getValue());
		assertTrue(attribute.getType() instanceof CtArrayTypeReference);
		assertEquals("new java.lang.String[0]", attribute.toString());

		final CtNewArray local = elements.get(1);
		assertEquals(1, local.getDimensionExpressions().size());
		assertTrue(local.getDimensionExpressions().get(0) instanceof CtInvocation);
		assertTrue(local.getType() instanceof CtArrayTypeReference);
		assertEquals("new Type[list.size()]", local.toString());
	}

	@Test
	public void testCtNewArrayInnerCtNewArray() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/arrays/testclasses/Foo.java");
		launcher.setSourceOutputDirectory("target/foo");
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCtNewArrayWitComments() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/arrays/testclasses/NewArrayWithComment.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.setSourceOutputDirectory("target/foo2");
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
