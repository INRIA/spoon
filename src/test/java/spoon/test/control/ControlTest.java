/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.control;

import org.junit.Test;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class ControlTest {

	@Test
	public void testModelBuildingFor() throws Exception {
		CtType<?> type = build("spoon.test.control.testclasses", "Fors");
		assertEquals("Fors", type.getSimpleName());

		List<CtFor> fors = type.getElements(new TypeFilter<>(CtFor.class));

		assertEquals(4, fors.size());

		CtMethod<?> normalFor = type.getElements(new NamedElementFilter<>(CtMethod.class, "normalFor")).get(0);
		CtFor firstFor = (CtFor) normalFor.getBody().getStatements().get(0);
		assertEquals("int i = 0", firstFor.getForInit().get(0).toString());
		assertEquals("i < 2", firstFor.getExpression().toString());
		assertEquals("i++", firstFor.getForUpdate().get(0).toString());

		CtMethod<?> empty1 = type.getElements(new NamedElementFilter<>(CtMethod.class, "empty1")).get(0);
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
		CtType<?> type = build("spoon.test.control.testclasses", "DoWhile");
		assertEquals("DoWhile", type.getSimpleName());
		CtMethod<?> meth = type.getElements(new NamedElementFilter<>(CtMethod.class, "methode")).get(0);
		List<CtStatement> stmts = meth.getBody().getStatements();
		assertEquals(2, stmts.size());
		assertTrue(stmts.get(1) instanceof CtDo);
		assertEquals("i++", ((CtBlock) ((CtDo) stmts.get(1)).getBody()).getStatement(0).toString());
	}
}
