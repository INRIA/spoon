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
import spoon.reflect.declaration.CtType;
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

	private static CtBlock<?> getBlock(CtModel model) {
		return model.getElements(new TypeFilter<>(CtBlock.class))
				.stream()
				.filter(b -> b.getParent() instanceof CtMethod<?> && !b.getParent().isImplicit())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("code does not contain explicit method"));
	}

	private static String wrapLocal(String localDeclarationSnippet) {
		return  "class X {\n" +
				"	public void myMethod() {\n" +
				localDeclarationSnippet +
				"	}\n" +
				"}";
	}

	private static void checkCommon(CtType<?> type, String expectedName) {
		assertThat(type.isLocalType(), is(true));
		assertThat(type.getSimpleName(), is(expectedName));
	}

	@Test
	void testLocalClassExists() {
		// contract: local classes and their members are part of the model
		String code = wrapLocal(
				"		class MyClass {\n" +
				"			private int field = 2;\n" +
				"			public void doNothing() { }\n" +
				"		}\n"
		);
		CtModel model = createModelFromString(code, 3);
		CtBlock<?> block = getBlock(model);

		assertThat("The local class does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		assertTrue(statement instanceof CtClass<?>);
		CtClass<?> clazz = (CtClass<?>) statement;

		checkCommon(clazz, "MyClass");
		assertThat(clazz.getFields().size(), is(1));
		assertThat(clazz.getMethods().size(), is(1));
	}

	@Test
	void testLocalEnumExists() {
		// contract: local enums and their members are part of the model
		String code = wrapLocal(
				"		enum MyEnum {\n" +
				"			A,\n" +
				"			B;\n" +
				"			public void doNothing() { }\n" +
				"		}\n"
		);
		CtModel model = createModelFromString(code, 16);
		CtBlock<?> block = getBlock(model);

		assertThat("The local enum does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		assertTrue(statement instanceof CtEnum<?>);
		CtEnum<?> enumType = (CtEnum<?>) statement;

		checkCommon(enumType, "MyEnum");
		assertThat(enumType.getEnumValues().size(), is(2));
		assertThat(enumType.getMethods().size(), is(1));
	}

	@Test
	void testLocalInterfaceExists() {
		// contract: local interfaces and their members are part of the model
		String code = wrapLocal(
				"		interface MyInterface {\n" +
				"			static final int A = 1;\n" +
				"			void doNothing();\n" +
				"		}\n"
		);
		CtModel model = createModelFromString(code, 16);
		CtBlock<?> block = getBlock(model);

		assertThat("The local interface does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		assertTrue(statement instanceof CtInterface<?>);
		CtInterface<?> interfaceType = (CtInterface<?>) statement;

		checkCommon(interfaceType, "MyInterface");
		assertThat(interfaceType.prettyprint(), is("MyInterface"));
		assertThat(interfaceType.getFields().size(), is(1));
		assertThat(interfaceType.getMethods().size(), is(1));
	}

	@Disabled
	@Test
	void testLocalRecordExists() {
		// contract: local records and their members are part of the model
		String code = wrapLocal(
				"		record MyRecord(int a) {\n" +
				"			public void doNothing() { }\n" +
				"		}\n"
		);
		CtModel model = createModelFromString(code, 16);
		CtBlock<?> block = getBlock(model);

		assertThat("The local record does not exist in the model", block.getStatements().size(), is(1));
		CtStatement statement = block.getStatement(0);
		// TODO record-specific assertions
	}
}
