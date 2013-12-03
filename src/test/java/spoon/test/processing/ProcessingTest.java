package spoon.test.processing;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class ProcessingTest {
	
	Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());

	
	@Test
	public void testInsertBegin() throws Exception{
		CtClass type = (CtClass)build ("spoon.test.processing",  "SampleForInsertBefore");
		for (Object m : type.getMethods()) {
			CtMethod meth = (CtMethod) m;
			int i = meth.getBody().getStatements().size();
			meth.getBody().insertBegin(factory.Code().createCodeSnippetStatement("int i = 0;"));
			assertEquals(i+1, meth.getBody().getStatements().size());
		}
	}

}
