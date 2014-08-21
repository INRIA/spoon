package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

public class SwitchCaseTest {

	  @Test
	  public void testIterationStatements() {
		Factory factory = TestUtils.createFactory();
	    CtClass<?> clazz = factory
	        .Code()
	        .createCodeSnippetStatement(
	            "" + "class X {" + "public void foo() {" + " int x=0;" +
	        "switch(x) {"
	        + "case 0: x=x+1;break;"
	        + "case 1: x=0;"
	        + "default: x=-1;"
	        + "}"
	                 + "}};")
	        .compile();
	    CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

	    CtSwitch<?> sw = foo.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

	    assertEquals(3, sw.getCases().size());

	    CtCase<?> c = (CtCase<?>) sw.getCases().get(0);
	    
	    assertEquals(0, ((CtLiteral<?>)c.getCaseExpression()).getValue());
	    assertEquals(2, c.getStatements().size());

	    List<CtStatement> l = new ArrayList<CtStatement>();
	    
	    // this compiles (thanks to the new CtCase extends CtStatementList)
	    for (CtStatement s : c) {
	    	l.add(s);
	    }
	    assertTrue(c.getStatements().equals(l));	    
	  }
	  
}
