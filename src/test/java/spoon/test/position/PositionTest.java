package spoon.test.position;

import org.junit.Test;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;
import spoon.test.position.testclasses.FooClazz;
import spoon.test.position.testclasses.FooField;
import spoon.test.position.testclasses.FooMethod;
import spoon.test.position.testclasses.FooStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class PositionTest {

	@Test
	public void testPositionClass() throws Exception {
		final Factory build = TestUtils.build(FooClazz.class);
		final CtType<FooClazz> foo = build.Type().get(FooClazz.class);
		String classContent = getClassContent(foo);

		SourcePosition position = foo.getPosition();

		assertEquals(4, position.getLine());
		assertEquals(6, position.getEndLine());

		assertEquals(42, position.getSourceStart());
		assertEquals(79, position.getSourceEnd());
		assertEquals("@Deprecated\n"
				+ "public class FooClazz {\n"
				+ "\n"
				+ "}", contentAtPosition(classContent, position));
	}

	@Test
	public void testPositionField() throws Exception {
		final Factory build = TestUtils.build(FooField.class);
		final CtType<FooField> foo = build.Type().get(FooField.class);
		String classContent = getClassContent(foo);

		SourcePosition position1 = foo.getField("field1").getPosition();

		assertEquals(5, position1.getLine());
		assertEquals(5, position1.getEndLine());

		assertEquals(68, position1.getSourceStart());
		assertEquals(95, position1.getSourceEnd());

		assertEquals("public final int field1 = 0;", contentAtPosition(classContent, position1));

		SourcePosition position2 = foo.getField("field2").getPosition();

		assertEquals(7, position2.getLine());
		assertEquals(8, position2.getEndLine());

		assertEquals(99, position2.getSourceStart());
		assertEquals(116, position2.getSourceEnd());

		assertEquals("int field2 =\n"
				+ "\t\t\t0;", contentAtPosition(classContent, position2));
	}

	@Test
	public void testPositionMethod() throws Exception {
		final Factory build = TestUtils.build(FooMethod.class);
		final CtType<FooMethod> foo = build.Type().get(FooMethod.class);
		String classContent = getClassContent(foo);

		CtMethod<?> method1 = foo.getMethodsByName("m").get(0);
		SourcePosition position1 = method1.getPosition();

		assertEquals(5, position1.getLine());
		assertEquals(7, position1.getEndLine());

		assertEquals(69, position1.getSourceStart());
		assertEquals(114, position1.getSourceEnd());

		assertEquals("public static void m(int parm1) {\n"
				+ "\t\treturn;\n"
				+ "\t}", contentAtPosition(classContent, position1));

		SourcePosition positionParam1 = method1.getParameters().get(0).getPosition();

		assertEquals(5, positionParam1.getLine());
		assertEquals(5, positionParam1.getEndLine());

		assertEquals(90, positionParam1.getSourceStart());
		assertEquals(98, positionParam1.getSourceEnd());

		assertEquals("int parm1", contentAtPosition(classContent, positionParam1));


		CtMethod<?> method2 = foo.getMethodsByName("mWithDoc").get(0);
		SourcePosition position2 = method2.getPosition();

		assertEquals(13, position2.getLine());
		assertEquals(15, position2.getEndLine());

		assertEquals("/**\n"
				+ "\t * Mathod with javadoc\n"
				+ "\t * @param parm1 the parameter\n"
				+ "\t */\n"
				+ "\tint mWithDoc(int parm1) {\n"
				+ "\t\treturn parm1;\n"
				+ "\t}", contentAtPosition(classContent, position2));
	}

	@Test
	public void testPositionStatement() throws Exception {
		final Factory build = TestUtils.build(FooStatement.class);
		final CtType<FooStatement> foo = build.Type().get(FooStatement.class);
		String classContent = getClassContent(foo);

		CtMethod<?> method1 = foo.getMethodsByName("m").get(0);
		CtBlock<?> body = method1.getBody();
		SourcePosition positionBody = body.getPosition();

		assertEquals(7, positionBody.getLine());
		assertEquals(23, positionBody.getEndLine());

		assertEquals("{\n"
				+ "\t\tint field2 = m2(parm1);\n"
				+ "\t\tthis.field = m2(parm1);\n"
				+ "\t\tif(parm1 > 2 && true) {\n"
				+ "\t\t\tswitch (parm1) {\n"
				+ "\t\t\tcase 1:\n"
				+ "\t\t\t\treturn;\n"
				+ "\t\t\tdefault:\n"
				+ "\t\t\t\tparm1++;\n"
				+ "\t\t\t}\n"
				+ "\t\t\tint count = 0;\n"
				+ "\t\t\tfor (int i =0; i< parm1; i++) {\n"
				+ "\t\t\t\tcount ++;\n"
				+ "\t\t\t}\n"
				+ "\t\t}\n"
				+ "\t\treturn;\n"
				+ "\t}", contentAtPosition(classContent, positionBody));

		SourcePosition positionLocalVariable = body.getStatement(0).getPosition();

		assertEquals(8, positionLocalVariable.getLine());
		assertEquals(8, positionLocalVariable.getEndLine());

		assertEquals("int field2 = m2(parm1);", contentAtPosition(classContent, positionLocalVariable));

		SourcePosition positionFieldWrite = body.getStatement(1).getPosition();

		assertEquals(9, positionFieldWrite.getLine());
		assertEquals(9, positionFieldWrite.getEndLine());

		assertEquals("this.field = m2(parm1);", contentAtPosition(classContent, positionFieldWrite));

		CtIf ctIf = body.getStatement(2);
		SourcePosition positionIf = ctIf.getPosition();

		assertEquals(10, positionIf.getLine());
		assertEquals(21, positionIf.getEndLine());

		assertEquals("if(parm1 > 2 && true) {\n"
				+ "\t\t\tswitch (parm1) {\n"
				+ "\t\t\tcase 1:\n"
				+ "\t\t\t\treturn;\n"
				+ "\t\t\tdefault:\n"
				+ "\t\t\t\tparm1++;\n"
				+ "\t\t\t}\n"
				+ "\t\t\tint count = 0;\n"
				+ "\t\t\tfor (int i =0; i< parm1; i++) {\n"
				+ "\t\t\t\tcount ++;\n"
				+ "\t\t\t}\n"
				+ "\t\t}", contentAtPosition(classContent, positionIf));

		SourcePosition positionSwitch = ((CtBlock)ctIf.getThenStatement()).getStatement(0).getPosition();

		assertEquals(11, positionSwitch.getLine());
		assertEquals(16, positionSwitch.getEndLine());

		assertEquals("switch (parm1) {\n"
				+ "\t\t\tcase 1:\n"
				+ "\t\t\t\treturn;\n"
				+ "\t\t\tdefault:\n"
				+ "\t\t\t\tparm1++;\n"
				+ "\t\t\t}", contentAtPosition(classContent, positionSwitch));

		positionLocalVariable = ((CtBlock)ctIf.getThenStatement()).getStatement(1).getPosition();

		assertEquals(17, positionLocalVariable.getLine());
		assertEquals(17, positionLocalVariable.getEndLine());

		assertEquals("int count = 0;", contentAtPosition(classContent, positionLocalVariable));

		SourcePosition positionFor = ((CtBlock)ctIf.getThenStatement()).getStatement(2).getPosition();

		assertEquals(18, positionFor.getLine());
		assertEquals(20, positionFor.getEndLine());

		assertEquals("for (int i =0; i< parm1; i++) {\n"
				+ "\t\t\t\tcount ++;\n"
				+ "\t\t\t}", contentAtPosition(classContent, positionFor));

		SourcePosition positionReturn = method1.getBody().getStatement(3).getPosition();

		assertEquals(22, positionReturn.getLine());
		assertEquals(22, positionReturn.getEndLine());

		assertEquals("return;", contentAtPosition(classContent, positionReturn));
	}


	private String getClassContent(CtType type) {
		File file = type.getPosition().getFile();
		String content = "";
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(
				Paths.get(file.getPath()), charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				content+=line + "\n";
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		return content;
	}

	private String contentAtPosition(String content, SourcePosition position) {
		return content.substring(position.getSourceStart(),  position.getSourceEnd() + 1);
	}
}
