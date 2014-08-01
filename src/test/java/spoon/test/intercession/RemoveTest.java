package spoon.test.intercession;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

public class RemoveTest {

	  @Test
	  public void testRemoveAllStatements() {
		Factory factory = TestUtils.createFactory();
	    CtClass<?> clazz = factory
	        .Code()
	        .createCodeSnippetStatement(
	            "" + "class X {" + "public void foo() {" + " int x=0;int y=0;"
	                 + "}};")
	        .compile();
	    CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

	    CtBlock<?> body = foo.getBody();
	    
	    assertEquals(2,body.getStatements().size());
	    
	    for (CtStatement s : body) {
	    	body.removeStatement(s);
	    }
	    
	    assertEquals(0,body.getStatements().size());	    
	  }	  
	  
}
