package spoon.test.arrays;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.arrays.testclasses.VaragParam;
import spoon.testing.utils.ModelUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class ArraysTest {

	@Test
	public void testArrayReferences() throws Exception {
		CtType<?> type = build("spoon.test.arrays.testclasses", "ArrayClass");
		assertEquals("ArrayClass", type.getSimpleName());
		assertEquals("int[][][]", type.getField("i").getType().getSimpleName());
		assertEquals(3, ((CtArrayTypeReference<?>) type.getField("i").getType()).getDimensionCount());
		final CtArrayTypeReference<?> arrayTypeReference = (CtArrayTypeReference<?>) type.getField("i").getDefaultExpression().getType();
		assertEquals(1, arrayTypeReference.getArrayType().getAnnotations().size());
		assertEquals("@spoon.test.arrays.testclasses.ArrayClass.TypeAnnotation(integer = 1)", arrayTypeReference.getArrayType().getAnnotations().get(0).toString());

		CtField<?> x = type.getField("x");
		assertTrue(x.getType() instanceof CtArrayTypeReference);
		assertEquals("int[]", x.getType().getSimpleName());
		assertEquals("int[]", x.getType().getQualifiedName());
		assertEquals("int", ((CtArrayTypeReference<?>) x.getType()).getComponentType().getSimpleName());
		assertTrue(((CtArrayTypeReference<?>) x.getType()).getComponentType().getActualClass().equals(int.class));
	}

	@Test
	public void testInitializeWithNewArray() {
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
		assertEquals("new com.example.Type[list.size()]", local.toString());
	}

	@Test
	public void testCtNewArrayInnerCtNewArray() {
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
	public void testCtNewArrayWitComments() {
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

	@Test
	public void testParameterizedVarargReference() throws Exception {
		//contract: check actual type arguments of parameter type: List<?>...
		CtType<?> ctClass = ModelUtils.buildClass(VaragParam.class);
		CtParameter<?> param1 = ctClass.getMethodsByName("m1").get(0).getParameters().get(0);
		CtArrayTypeReference<?> varArg1TypeRef = (CtArrayTypeReference<?>) param1.getType();
		assertEquals("java.util.List<?>[]", varArg1TypeRef.toString());
		assertEquals("java.util.List<?>", varArg1TypeRef.getComponentType().toString());
		assertEquals(1, varArg1TypeRef.getComponentType().getActualTypeArguments().size());
		assertEquals(0, varArg1TypeRef.getActualTypeArguments().size());
	}

	@Test
	public void testParameterizedArrayReference() throws Exception {
		//contract: check actual type arguments of parameter type: List<?>[]
		CtType<?> ctClass = ModelUtils.buildClass(VaragParam.class);
		CtParameter<?> param1 = ctClass.getMethodsByName("m2").get(0).getParameters().get(0);
		CtArrayTypeReference<?> varArg1TypeRef = (CtArrayTypeReference<?>) param1.getType();
		assertEquals("java.util.List<?>[]", varArg1TypeRef.toString());
		assertEquals("java.util.List<?>", varArg1TypeRef.getComponentType().toString());
		assertEquals(1, varArg1TypeRef.getComponentType().getActualTypeArguments().size());
		assertEquals(0, varArg1TypeRef.getActualTypeArguments().size());
	}

	@Test
	public void testParameterizedArrayVarargReference() throws Exception {
		//contract: check actual type arguments of parameter type: List<?>[]...
		CtType<?> ctClass = ModelUtils.buildClass(VaragParam.class);
		CtParameter<?> param1 = ctClass.getMethodsByName("m3").get(0).getParameters().get(0);
		CtArrayTypeReference<?> varArg1TypeRef = (CtArrayTypeReference<?>) param1.getType();
		assertEquals("java.util.List<?>[][]", varArg1TypeRef.toString());
		assertEquals("java.util.List<?>[]", varArg1TypeRef.getComponentType().toString());
		assertEquals("java.util.List<?>", ((CtArrayTypeReference<?>) varArg1TypeRef.getComponentType()).getComponentType().toString());
		assertEquals(1, ((CtArrayTypeReference<?>) varArg1TypeRef.getComponentType()).getComponentType().getActualTypeArguments().size());
		assertEquals(0, varArg1TypeRef.getComponentType().getActualTypeArguments().size());
		assertEquals(0, varArg1TypeRef.getActualTypeArguments().size());
	}

	@Test
	public void testParameterizedTypeReference() throws Exception {
		//contract: check actual type arguments of parameter type: List<?>
		CtType<?> ctClass = ModelUtils.buildClass(VaragParam.class);
		CtParameter<?> param1 = ctClass.getMethodsByName("m4").get(0).getParameters().get(0);
		CtTypeReference<?> typeRef = param1.getType();
		assertEquals("java.util.List<?>", typeRef.toString());
		assertEquals(1, typeRef.getActualTypeArguments().size());
	}
}
