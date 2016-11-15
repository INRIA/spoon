package spoon.test.ctBodyHolder;

import static org.junit.Assert.*;
import static spoon.testing.utils.ModelUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.test.ctBodyHolder.testclasses.CWBStatementTemplate;
import spoon.test.ctBodyHolder.testclasses.ClassWithBodies;

public class CtBodyHolderTest
{
    @Test
    public void testConstructor() throws Exception {
        Factory factory = build(ClassWithBodies.class, CWBStatementTemplate.class);
        CtClass<?> cwbClass = (CtClass<?>) factory.Type().get(ClassWithBodies.class);
        assertEquals(1, cwbClass.getConstructors().size());
        CtConstructor<?> constructor =  cwbClass.getConstructor();
        checkCtBody(constructor, "constructor_body", 1);
    }

    @Test
    public void testMethod() throws Exception {
        Factory factory = build(ClassWithBodies.class, CWBStatementTemplate.class);
        CtClass<?> cwbClass = (CtClass<?>) factory.Type().get(ClassWithBodies.class);
        assertEquals(2, cwbClass.getMethods().size());
        CtMethod<?> method =  cwbClass.getMethod("method");
        checkCtBody(method, "method_body", 0);
    }

    @Test
    public void testTryCatch() throws Exception {
        Factory factory = build(ClassWithBodies.class, CWBStatementTemplate.class);
        CtClass<?> cwbClass = (CtClass<?>) factory.Type().get(ClassWithBodies.class);
        assertEquals(2, cwbClass.getMethods().size());
        CtMethod<?> method =  cwbClass.getMethod("method2");
        CtBlock<?> methodBody = method.getBody();
        assertTrue(methodBody.getStatement(0) instanceof CtTry);
        CtTry tryStmnt = (CtTry)methodBody.getStatement(0);
        checkCtBody(tryStmnt, "try_body", 0);
        assertEquals(1, tryStmnt.getCatchers().size());
        assertTrue(tryStmnt.getCatchers().get(0) instanceof CtCatch);
        checkCtBody(tryStmnt.getCatchers().get(0), "catch_body", 0);
    }

    @Test
    public void testForWithStatement() throws Exception {
        Factory factory = build(ClassWithBodies.class, CWBStatementTemplate.class);
        CtClass<?> cwbClass = (CtClass<?>) factory.Type().get(ClassWithBodies.class);
        assertEquals(2, cwbClass.getMethods().size());
        CtMethod<?> method =  cwbClass.getMethod("method2");
        CtBlock<?> methodBody = method.getBody();
        assertTrue(methodBody.getStatement(1) instanceof CtFor);
        CtFor forStmnt = (CtFor)methodBody.getStatement(1);
        checkCtBody(forStmnt, "for_statemnt", 0);
    }

    @Test
    public void testForWithBlock() throws Exception {
        Factory factory = build(ClassWithBodies.class, CWBStatementTemplate.class);
        CtClass<?> cwbClass = (CtClass<?>) factory.Type().get(ClassWithBodies.class);
        assertEquals(2, cwbClass.getMethods().size());
        CtMethod<?> method =  cwbClass.getMethod("method2");
        CtBlock<?> methodBody = method.getBody();
        assertTrue(methodBody.getStatement(2) instanceof CtFor);
        CtFor forStmnt = (CtFor)methodBody.getStatement(2);
        checkCtBody(forStmnt, "for_block", 0);
    }

    @Test
    public void testWhileWithBlock() throws Exception {
        Factory factory = build(ClassWithBodies.class, CWBStatementTemplate.class);
        CtClass<?> cwbClass = (CtClass<?>) factory.Type().get(ClassWithBodies.class);
        assertEquals(2, cwbClass.getMethods().size());
        CtMethod<?> method =  cwbClass.getMethod("method2");
        CtBlock<?> methodBody = method.getBody();
        assertTrue(methodBody.getStatement(3) instanceof CtWhile);
        CtWhile whileStmnt = (CtWhile)methodBody.getStatement(3);
        checkCtBody(whileStmnt, "while_block", 0);
    }

    private void checkCtBody(CtBodyHolder p_bodyHolder, String p_constant, int off)
	{
		CtStatement body = p_bodyHolder.getBody();
		assertTrue(body instanceof CtBlock<?>);
		
		CtBlock<?> block = (CtBlock)body;
		assertEquals(1+off, block.getStatements().size());
		
		assertTrue(block.getStatement(off) instanceof CtAssignment);
		
		CtAssignment assignment = block.getStatement(off);
		assertEquals(p_constant, ((CtLiteral<String>)assignment.getAssignment().partiallyEvaluate()).getValue());
		
		Factory f = body.getFactory();
		
		CtStatement newStat = new CWBStatementTemplate("xx").apply(body.getParent(CtType.class));
		try {
			newStat.getParent();
			fail();
		} catch(ParentNotInitializedException e) {
			//expected exception
		}
		//try to set statement and get CtBlock
		p_bodyHolder.setBody(newStat);
		CtBlock newBlock = (CtBlock)p_bodyHolder.getBody();
		assertSame(p_bodyHolder, newBlock.getParent());
		assertSame(newBlock, newStat.getParent());

		//try to set CtBlock and get the same CtBlock
		CtStatement newStat2 = newStat.clone();
		try {
			newStat2.getParent();
			fail();
		} catch(ParentNotInitializedException e) {
			//expected exception
		}
		CtBlock newBlock2 = f.Code().createCtBlock(newStat2);
		assertSame(newBlock2, newStat2.getParent());
		try {
			newBlock2.getParent();
			fail();
		} catch(ParentNotInitializedException e) {
			//expected exception
		}

		p_bodyHolder.setBody(newBlock2);
		assertSame(newBlock2, p_bodyHolder.getBody());
		assertSame(p_bodyHolder, newBlock2.getParent());
		assertSame(newBlock2, newStat2.getParent());
	}
}
