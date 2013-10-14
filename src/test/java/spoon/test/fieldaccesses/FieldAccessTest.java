package spoon.test.fieldaccesses;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class FieldAccessTest {

	@Test 
	public void testModelBuildingFieldAccesses() throws Exception {
		CtSimpleType type = build ("spoon.test.fieldaccesses",  "Mouse");
		assertEquals("Mouse", type.getSimpleName());

		CtMethod meth1 = (CtMethod) type.getElements(new NameFilter("meth1")).get(0);
		CtMethod meth1b = (CtMethod) type.getElements(new NameFilter("meth1b")).get(0);

		assertEquals(3, meth1.getElements(new TypeFilter(CtFieldAccess.class)).size());
		
		assertEquals(2, meth1b.getElements(new TypeFilter(CtFieldAccess.class)).size());

		CtMethod meth2 = (CtMethod) type.getElements(new NameFilter("meth2")).get(0);
		assertEquals(2, meth2.getElements(new TypeFilter(CtFieldAccess.class)).size());
		
		CtMethod meth3 = (CtMethod) type.getElements(new NameFilter("meth3")).get(0);
		assertEquals(3, meth3.getElements(new TypeFilter(CtFieldAccess.class)).size());

		CtMethod meth4 = (CtMethod) type.getElements(new NameFilter("meth4")).get(0);
		assertEquals(1, meth4.getElements(new TypeFilter(CtFieldAccess.class)).size());

	}
	
	@Test 
	public void testModelBuildingOuterThisAccesses() throws Exception {
		CtSimpleType type = build ("spoon.test.fieldaccesses",  "InnerClassThisAccess");
		assertEquals("InnerClassThisAccess", type.getSimpleName());

		CtMethod meth1 = (CtMethod) type.getElements(new NameFilter("methode")).get(0);
		assertEquals("spoon.test.fieldaccesses.InnerClassThisAccess.this.method()", meth1.getBody().getStatements().get(0).toString());
	}

}
