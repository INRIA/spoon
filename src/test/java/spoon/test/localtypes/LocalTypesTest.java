package spoon.test.localtypes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalTypesTest {

	private static CtModel createModelFromString(String code, int complianceLevel) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(complianceLevel);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	@Test
	void testLocalClassExists() {
		String code = "class X {\n" +
				"	public void myMethod() {\n" +
				"		class MyClass {\n" +
				"			private int field = 2;\n" +
				"			public void doNothing() { }\n" +
				"		}\n" +
				"	}\n" +
				"}";
		CtModel model = createModelFromString(code, 3);
		CtBlock<?> block = model.getElements(new TypeFilter<>(CtBlock.class))
				.stream()
				.filter(b -> b.getParent() instanceof CtMethod<?> && !b.getParent().isImplicit())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("code does not contain explicit method"));

		assertThat("The local class does not exist in the model", block.getStatements().size(), is(1));
		CtStatement statement = block.getStatement(0);
		assertTrue(statement instanceof CtClass<?>);
		CtClass<?> clazz = (CtClass<?>) statement;
		assertThat(clazz.isLocalType(), is(true));
		assertThat(clazz.getSimpleName(), is("MyClass"));
		assertThat(clazz.getFields().size(), is(1));
		assertThat(clazz.getMethods().size(), is(1));
	}

	@Test
	void testLocalEnumExists() {
		String code = "class X {\n" +
				"	public void myMethod() {\n" +
				"		enum MyEnum {\n" +
				"			A,\n" +
				"			B;\n" +
				"			public void doNothing() { }\n" +
				"		}\n" +
				"	}\n" +
				"}";
		CtModel model = createModelFromString(code, 16);
		CtBlock<?> block = model.getElements(new TypeFilter<>(CtBlock.class))
				.stream()
				.filter(b -> b.getParent() instanceof CtMethod<?> && !b.getParent().isImplicit())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("code does not contain explicit method"));

		assertThat("The local enum does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		assertTrue(statement instanceof CtEnum<?>);
		CtEnum<?> enumType = (CtEnum<?>) statement;

		assertThat(enumType.isLocalType(), is(true));
		assertThat(enumType.getSimpleName(), is("MyEnum"));
		assertThat(enumType.getEnumValues().size(), is(2));
		assertThat(enumType.getMethods().size(), is(1));
	}

	@Test
	void testLocalInterfaceExists() {
		String code = "class X {\n" +
				"	public void myMethod() {\n" +
				"		interface MyInterface {\n" +
				"			static final int A = 1;\n" +
				"			void doNothing();\n" +
				"		}\n" +
				"	}\n" +
				"}";
		CtModel model = createModelFromString(code, 16);
		CtBlock<?> block = model.getElements(new TypeFilter<>(CtBlock.class))
				.stream()
				.filter(b -> b.getParent() instanceof CtMethod<?> && !b.getParent().isImplicit())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("code does not contain explicit method"));

		assertThat("The local interface does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		assertTrue(statement instanceof CtInterface<?>);
		CtInterface<?> interfaceType = (CtInterface<?>) statement;

		assertThat(interfaceType.isLocalType(), is(true));
		assertThat(interfaceType.getSimpleName(), is("MyInterface"));
		assertThat(interfaceType.getFields().size(), is(1));
		assertThat(interfaceType.getMethods().size(), is(1));
	}

	@Disabled
	@Test
	void testLocalRecordExists() {
		String code = "class X {\n" +
				"	public void myMethod() {\n" +
				"		record MyRecord(int a) {\n" +
				"			public void doNothing() { }\n" +
				"		}\n" +
				"	}\n" +
				"}";
		CtModel model = createModelFromString(code, 16);
		CtBlock<?> block = model.getElements(new TypeFilter<>(CtBlock.class))
				.stream()
				.filter(b -> b.getParent() instanceof CtMethod<?> && !b.getParent().isImplicit())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("code does not contain explicit method"));

		assertThat("The local record does not exist in the model", block.getStatements().size(), is(1));
		CtStatement statement = block.getStatement(0);
		// TODO record-specific assertions
	}
}
