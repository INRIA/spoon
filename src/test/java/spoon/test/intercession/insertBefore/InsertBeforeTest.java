package spoon.test.intercession.insertBefore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class InsertBeforeTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/intercession/insertBefore/InsertBeforeExample.java"))
				.build();
	}

    @Test
    public void testInsertBefore() {
        CtClass<?> clazz = factory
                .Code()
                .createCodeSnippetStatement(
                        "" + "class X {" + "public void foo() {" + " int x=0;"
                                + " int y=0;" + " int z=x+y;" + "}" + "};")
                .compile();
        CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

        CtBlock<?> body = foo.getBody();
        assertEquals(3, body.getStatements().size());

        CtStatement s = body.getStatements().get(2);
        assertEquals("int z = x + y", s.toString());

        // adding a new statement;
        CtCodeSnippetStatement stmt = factory.Core()
                .createCodeSnippetStatement();
        stmt.setValue("System.out.println(x);");
        s.insertBefore(stmt);
        assertEquals(4, body.getStatements().size());
        assertSame(stmt, body.getStatements().get(2));
    }

    // test for a bug found by Maxime Clement
    @Test
    public void testInsertBeforeBrace() throws Exception {
        CtClass<?> foo = factory.Package().get("spoon.test.intercession.insertBefore")
                .getType("InsertBeforeExample");

        {
        CtMethod<?> ifWithoutBraces_m = foo.getElements(
                new NameFilter<CtMethod<?>>("ifWithoutBraces")).get(0);

        // replace the return
        CtCodeSnippetStatement s = factory.Code().createCodeSnippetStatement("return 2");

        CtIf ifWithoutBraces = ifWithoutBraces_m.getElements(
                new TypeFilter<CtIf>(CtIf.class)).get(0);

        // Inserts a s before the then statement
        ifWithoutBraces.getThenStatement().insertBefore(s);

        assertTrue(ifWithoutBraces.getThenStatement() instanceof CtBlock);
        assertEquals(s, ((CtBlock<?>) ifWithoutBraces.getThenStatement()).getStatement(0));
        //System.out.println("end 1");

        }

        {
        CtMethod<?> ifWithBraces_m = foo.getElements(
                new NameFilter<CtMethod<?>>("ifWithBraces")).get(0);

        // replace the return
        CtCodeSnippetStatement s = factory.Code().createCodeSnippetStatement("return 2");

        CtIf ifWithBraces = ifWithBraces_m.getElements(
                new TypeFilter<CtIf>(CtIf.class)).get(0);

        // Inserts a s before the then statement
        ifWithBraces.getThenStatement().insertBefore(s);
        assertTrue(ifWithBraces.getThenStatement() instanceof CtBlock);
        assertEquals(s, ((CtBlock<?>) ifWithBraces.getThenStatement()).getStatement(0));
        }
    }

    @Test
    public void testInsertBeforeSwitchCase() throws Exception {
        CtClass<?> foo = factory.Package().get("spoon.test.intercession.insertBefore")
                .getType("InsertBeforeExample");

        {
            CtMethod<?> sm = foo.getElements(
                    new NameFilter<CtMethod<?>>("switchMethod")).get(0);

            CtCase caseElem = factory.Core().createCase();
            CtLiteral<Object> literal = factory.Core().createLiteral();
            literal.setValue(1);
            caseElem.setCaseExpression(literal);

            CtSwitch<?> sw = sm.getElements(
                    new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

            CtCase<?> ctCase1 = (CtCase<?>) sw.getCases().get(2);
            CtCase<?> ctCase2= (CtCase<?>) sw.getCases().get(3);
            CtCodeSnippetStatement snippet = factory.Code().createCodeSnippetStatement("System.out.println(\"foo\")");
            ctCase1.getStatements().get(0).insertBefore(snippet);
            ctCase2.getStatements().get(1).insertBefore(snippet);
            assertEquals(snippet, ctCase1.getStatements().get(0));
            assertEquals(snippet, ctCase2.getStatements().get(1));

            CtCase<?> ctCase = (CtCase<?>) sw.getCases().get(1);
            
            // here we may call either insertBefore(CtStatement) or insertBefore(CtStatementList)
            // ctCase.insertBefore(caseElem);
            // so force the correct insert            
            CtStatement stmt = caseElem;
            ctCase.insertBefore(stmt);

            assertEquals(5, sw.getCases().size());
            assertEquals(caseElem, sw.getCases().get(1));
            assertEquals(ctCase, sw.getCases().get(2));
            
        }
    }

}
