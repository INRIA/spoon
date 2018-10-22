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
package spoon.test.ctBlock;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.test.ctBlock.testclasses.Toto;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 15/03/2017.
 */
public class TestCtBlock {

	@Test
	public void testRemoveStatement() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/ctBlock/testclasses/Toto.java");
		spoon.buildModel();

		List<CtMethod> methods = spoon.getModel().getElements(new NamedElementFilter<>(CtMethod.class, "foo"));

		assertEquals(1, methods.size());

		CtMethod foo = methods.get(0);

		CtBlock block = foo.getBody();
		CtStatement lastStatement = block.getLastStatement();

		assertEquals("i++", lastStatement.toString());

		block.removeStatement(lastStatement);

		CtStatement newLastStatement = block.getLastStatement();

		assertNotSame(newLastStatement, lastStatement);
		assertTrue(newLastStatement instanceof CtIf);
	}

	@Test
	public void testAddStatementInBlock() {
		// contract: we can add a statement at a specific index
		// the statements with a higher index are pushed after

		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/ctBlock/testclasses/Toto.java");
		spoon.buildModel();

		CtClass<?> toto = spoon.getFactory().Class().get(Toto.class);
		CtMethod foo = toto.getMethodsByName("foo").get(0);
		CtBlock block = foo.getBody();
		CtBlock originalBlock = block.clone();

		assertEquals(4, block.getStatements().size());
		block.addStatement(1, (CtStatement) spoon.getFactory().createInvocation().setExecutable(foo.getReference()));

		assertEquals(5, block.getStatements().size());

		assertEquals(originalBlock.getStatement(0), block.getStatement(0));

		for (int i = 1; i < 4; i++) {
			assertEquals(originalBlock.getStatement(i), block.getStatement(i + 1));
		}
	}

	@Test
	public void testAddStatementInCase() {
		// contract: we can add a statement at a specific index
		// the statements with a higher index are pushed after

		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/ctBlock/testclasses/Toto.java");
		spoon.buildModel();

		CtClass<?> toto = spoon.getFactory().Class().get(Toto.class);
		CtMethod bar = toto.getMethodsByName("bar").get(0);
		CtSwitch<?> ctSwitch = (CtSwitch) bar.getBody().getStatement(0);

		CtCase firstCase = ctSwitch.getCases().get(0);
		CtCase originalFirstCase = firstCase.clone();

		CtCase secondCase = ctSwitch.getCases().get(1);
		CtCase originalSecondCase = secondCase.clone();


		assertEquals(3, firstCase.getStatements().size());
		firstCase.addStatement(3, spoon.getFactory().createBreak());
		assertEquals(4, firstCase.getStatements().size());

		for (int i = 0; i < 3; i++) {
			assertEquals(originalFirstCase.getStatements().get(i), firstCase.getStatements().get(i));
		}

		assertEquals(2, secondCase.getStatements().size());
		secondCase.addStatement(1, (CtStatement) spoon.getFactory().createInvocation().setExecutable(bar.getReference()));
		assertEquals(3, secondCase.getStatements().size());

		assertEquals(originalSecondCase.getStatements().get(0), secondCase.getStatements().get(0));
		assertEquals(originalSecondCase.getStatements().get(1), secondCase.getStatements().get(2));
	}
}
