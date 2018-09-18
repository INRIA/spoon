package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.InvocationSubstitutionByExpressionTemplate;
import spoon.test.template.testclasses.InvocationSubstitutionByStatementTemplate;
import spoon.test.template.testclasses.SubstitutionByExpressionTemplate;

public class TemplateInvocationSubstitutionTest {

	@Test
	public void testInvocationSubstitutionByStatement() {
		//contract: the template engine supports substitution of any method invocation
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InvocationSubstitutionByStatementTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtBlock<?> templateArg = factory.Class().get(InvocationSubstitutionByStatementTemplate.class).getMethod("sample").getBody();
		
		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new InvocationSubstitutionByStatementTemplate(templateArg).apply(resultKlass);
		assertEquals("throw new java.lang.RuntimeException(\"Failed\")", result.toString());
	}

	@Test
	public void testInvocationSubstitutionByExpression() {
		//contract: the template engine supports substitution of any method invocation
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InvocationSubstitutionByExpressionTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtBlock<?> result = new InvocationSubstitutionByExpressionTemplate(factory.createLiteral("abc")).apply(resultKlass);

		assertEquals("java.lang.System.out.println(\"abc\".substring(1))", result.getStatement(0).toString());
		assertEquals("java.lang.System.out.println(\"abc\".substring(1))", result.getStatement(1).toString());

		// contract: the result of the template has no parent, and can be put anywhere in an AST
		assertFalse(result.isParentInitialized());
	}

	@Test
	public void testSubstitutionByExpression() {
		//contract: the template engine understands fields whose type extends from TemplateParameter as template parameter automatically. No need for extra annotation
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstitutionByExpressionTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtBlock<?> result = new SubstitutionByExpressionTemplate(factory.createLiteral("abc")).apply(resultKlass);
		assertEquals("java.lang.System.out.println(\"abc\".substring(1))", result.getStatement(0).toString());
	}
}
