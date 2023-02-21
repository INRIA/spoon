package spoon.test.textBlocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.testing.utils.LineSeparatorExtension;

/** Test for the new Java 15 text block feature with */
public class TextBlockTest{
	private Launcher setUpTest() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/textBlock/TextBlockTestClass.java");
		launcher.run();
		return launcher;
	}

	private Factory getSpoonFactory() {
		final Launcher launcher = new Launcher();
		launcher.run();
		return launcher.getFactory();
	}

	@Test
	public void testTextBlock1(){
		//contract: Test Text Block usage introduced in Java 15
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("textBlock.TextBlockTestClass");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		
		CtStatement stmt1 = m1.getBody().getStatement(0);
		assertTrue(stmt1.getValueByRole(CtRole.ASSIGNMENT) instanceof CtTextBlock);
		CtTextBlock l1 = (CtTextBlock) stmt1.getValueByRole(CtRole.ASSIGNMENT);
		assertEquals("<html>\n    <body>\n        <p>Hello, कसौटी 🥲</p>\n    </body>\n</html>\n", l1.getValue());
	}

	@Test
	public void testTextBlockqoute(){
		//contract: Test Text Block containing "
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("textBlock.TextBlockTestClass");
		CtMethod<?> m2 =  allstmt.getMethod("m2");
		
		CtStatement stmt2 = m2.getBody().getStatement(0);
		assertTrue(stmt2.getValueByRole(CtRole.ASSIGNMENT) instanceof CtTextBlock);
		CtTextBlock l2 = (CtTextBlock) stmt2.getValueByRole(CtRole.ASSIGNMENT);
		assertEquals("SELECT \"EMP_ID\", \"LAST_NAME\" FROM \"EMPLOYEE_TB\"\n"
				+ "WHERE \"CITY\" = 'INDIANAPOLIS'\n"
				+ "ORDER BY \"EMP_ID\", \"LAST_NAME\";\n",
				l2.getValue());
	}

	@Test
	public void testTextBlockQouteWithinQoute(){
		//contract: Test Text Block usage containing nested " within '
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("textBlock.TextBlockTestClass");
		CtMethod<?> m3 =  allstmt.getMethod("m3");
		
		CtTry stmt4 = (CtTry) m3.getBody().getStatement(1);
		CtStatement stmt5 = stmt4.getBody().getStatement(0);
		CtInvocation inv = (CtInvocation) stmt5.getDirectChildren().get(1);
		assertTrue(inv.getArguments().get(0) instanceof CtTextBlock);
		CtTextBlock l3 = (CtTextBlock) inv.getArguments().get(0);
		assertEquals("function hello() {\n"
				+ "    print('\"Hello, world\"');\n"
				+ "}\n"
				+ "\n"
				+ "hello();\n"
				+ "",
				l3.getValue());
	}

	@Test
	public void testTextBlockEmpty(){
		//contract: Test Text Block containing empty string ""
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("textBlock.TextBlockTestClass");
		CtMethod<?> m4 =  allstmt.getMethod("m4");
		
		CtStatement stmt1 = m4.getBody().getStatement(0);
		assertTrue(stmt1.getValueByRole(CtRole.ASSIGNMENT) instanceof CtTextBlock);
		CtTextBlock l1 = (CtTextBlock) stmt1.getValueByRole(CtRole.ASSIGNMENT);
		assertEquals("", l1.getValue());
	}

	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testTextBlockCreation(){
		// contract: Test creation of TextBlock and prettyprinting
		Factory factory = getSpoonFactory();
		CtClass<?> c = Launcher.parseClass("class Test{public String m1(){String s = \"\";}}");
		CtBlock<?> body = c.getMethod("m1").getBody();
		CtReturn<?> ret = factory.createReturn();
		ret.setValueByRole(CtRole.EXPRESSION, factory.createTextBlock("Hello, \"World\"!\nTesting\n\tTabs"));
		body.insertEnd(ret);
		assertEquals(
				"class Test {\n"
				+ "    public String m1() {\n"
				+ "        String s = \"\";\n"
				+ "        return \"\"\"\n"
				+ "        Hello, \"World\"!\n"
				+ "        Testing\n"
				+ "        	Tabs\"\"\";\n"
				+ "    }\n"
				+ "}",
				c.toString()
		);
	}

	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testTextBlockEscapes(){
		//contract: text-blocks should retain escape sequences in code
		Launcher launcher = setUpTest();
		launcher.getEnvironment().setAutoImports(true);
		
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("textBlock.TextBlockTestClass");
		CtMethod<?> m5 =  allstmt.getMethod("m5");

		CtStatement stmt5 = m5.getBody().getStatement(0);
		assertTrue(stmt5.getValueByRole(CtRole.ASSIGNMENT) instanceof CtTextBlock);

		// test text block value
		CtTextBlock l1 = (CtTextBlock) stmt5.getValueByRole(CtRole.ASSIGNMENT);
		assertEquals("no-break space: \\00a0\n" +
						"newline:        \\n\n" +
						"tab:            \\t ('\t')\n",
				l1.getValue());

		// escape sequences should be retained in code
		String m5Text = m5.toString();
		assertEquals("void m5() {\n" +
						"    String escape = \"\"\"\n" +
						"    no-break space: \\\\00a0\n" +
						"    newline:        \\\\n\n" +
						"    tab:            \\\\t ('\t')\n" +
						"    \"\"\";\n" +
						"}",
				m5Text);
	}

}
