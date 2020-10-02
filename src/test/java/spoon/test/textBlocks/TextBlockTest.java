package spoon.test.textBlocks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class TextBlockTest{
	private Launcher setUpTest() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/textBlock/TextBlockTestClass.java");
		launcher.run();
		return launcher;
	}
	
	@Test
	public void testTextBlock(){
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("textBlock.TextBlockTestClass");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		CtStatement stmt1 = m1.getBody().getStatement(0);
		CtLiteral l1 = (CtLiteral) stmt1.getDirectChildren().get(1);
		assertEquals(l1.getValue(), "<html>\n    <body>\n        <p>Hello, world</p>\n    </body>\n</html>\n");
		
		CtStatement stmt2 = m1.getBody().getStatement(1);
		CtLiteral l2 = (CtLiteral) stmt2.getDirectChildren().get(1);
		assertEquals(l2.getValue(), "SELECT \"EMP_ID\", \"LAST_NAME\" FROM \"EMPLOYEE_TB\"\n"
				+ "WHERE \"CITY\" = 'INDIANAPOLIS'\n"
				+ "ORDER BY \"EMP_ID\", \"LAST_NAME\";\n");
		
		CtTry stmt4 = (CtTry) m1.getBody().getStatement(3);
		CtStatement stmt5 = stmt4.getBody().getStatement(0);
		CtInvocation inv = (CtInvocation) stmt5.getDirectChildren().get(1);
		CtLiteral l3 = (CtLiteral) inv.getArguments().get(0);
		assertEquals(l3.getValue(), "function hello() {\n"
				+ "    print('\"Hello, world\"');\n"
				+ "}\n"
				+ "\n"
				+ "hello();\n"
				+ "");
	}
}