package spoon.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ControlTest {

	@Test
	public void testModelBuildingFor() throws Exception {
		CtType<?> type = build("spoon.test.control", "Fors");
		assertEquals("Fors", type.getSimpleName());

		List<CtFor> fors = type.getElements(new TypeFilter<CtFor>(CtFor.class));

		assertEquals(4, fors.size());

		CtMethod<?> normalFor = type.getElements(
				new NameFilter<CtMethod<?>>("normalFor")).get(0);
		CtFor firstFor = (CtFor) normalFor.getBody().getStatements().get(0);
		assertEquals("int i = 0", firstFor.getForInit().get(0).toString());
		assertEquals("i < 2", firstFor.getExpression().toString());
		assertEquals("i++", firstFor.getForUpdate().get(0).toString());

		CtMethod<?> empty1 = type.getElements(
				new NameFilter<CtMethod<?>>("empty1")).get(0);
		CtFor empty1For = (CtFor) empty1.getBody().getStatements().get(1);
		assertEquals("i = 0", empty1For.getForInit().get(0).toString());
		// TODO: is it good to return null??
		// I'm not sure I want to specify this
		// I would prefer to add a fake null object that is printed as empty in
		// the output
		assertNull(empty1For.getExpression());
		assertEquals("i++", empty1For.getForUpdate().get(0).toString());

	}

	@Test
	public void testModelBuildingDoWhile() throws Exception {
		CtType<?> type = build("spoon.test.control", "DoWhile");
		assertEquals("DoWhile", type.getSimpleName());
		CtMethod<?> meth = type.getElements(
				new NameFilter<CtMethod<?>>("methode")).get(0);
		List<CtStatement> stmts = meth.getBody().getStatements();
		assertEquals(2, stmts.size());
		assertTrue(stmts.get(1) instanceof CtDo);
		assertEquals("i++", ((CtDo) stmts.get(1)).getBody().toString());
	}
}
