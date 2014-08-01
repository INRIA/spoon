package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

public class BlockTest {

	  @Test
	  public void testIterationStatements() {
		Factory factory = TestUtils.createFactory();
	    CtClass<?> clazz = factory
	        .Code()
	        .createCodeSnippetStatement(
	            "" + "class X {" + "public void foo() {" + " int x=0;int y=0;"
	                 + "}};")
	        .compile();
	    CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

	    CtBlock<?> body = foo.getBody();
	    assertEquals(2, body.getStatements().size());

	    List l = new ArrayList();
	    
	    // this compiles (thanks to the new CtBlock extends CtStatementList)
	    for (CtStatement s : body) {
	    	l.add(s);
	    }
	    
	    assertTrue(body.getStatements().equals(l));	    
	  }
	  
}
