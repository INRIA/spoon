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
package spoon.test.loop;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.loop.testclasses.Condition;
import spoon.test.loop.testclasses.Join;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class LoopTest {
	private static final String nl = System.lineSeparator();

	@Test
	public void testAnnotationInForLoop() {
		CtType<?> aFoo = build(new File("./src/test/resources/spoon/test/loop/testclasses/")).Type().get("spoon.test.loop.testclasses.Foo");

		CtFor aFor = aFoo.getMethod("m").getElements(new TypeFilter<>(CtFor.class)).get(0);
		assertEquals(1, ((CtLocalVariable) aFor.getForInit().get(0)).getType().getAnnotations().size());
		assertEquals(1, ((CtLocalVariable) aFor.getForInit().get(1)).getType().getAnnotations().size());

		CtForEach aForEach = aFoo.getMethod("m").getElements(new TypeFilter<>(CtForEach.class)).get(0);
		assertEquals(1, aForEach.getVariable().getType().getAnnotations().size());
	}

	@Test
	public void testForeachShouldHaveAlwaysABlockInItsBody() throws Exception {
		final CtClass<Join> aType = build(Join.class, Condition.class).Class().get(Join.class);
		final CtConstructor<Join> joinCtConstructor = aType.getConstructors().stream().findFirst().get();
		final CtLoop ctLoop = joinCtConstructor.getBody().getElements(new TypeFilter<>(CtLoop.class)).get(0);
		assertTrue(ctLoop.getBody() instanceof CtBlock);
		// contract: the implicit block is not pretty printed
		String expectedPrettyPrinted = //
				"for (Condition<? super T> condition : conditions)" + nl //
						+ "    this.conditions.add(notNull(condition));" + nl;
		assertEquals(expectedPrettyPrinted, ctLoop.prettyprint());


		// contract: the implicit block is viewable in debug mode
		String expectedDebug = //
				"for (spoon.test.loop.testclasses.Condition<? super T> condition : conditions) {" + nl //
						+ "    this.conditions.add(spoon.test.loop.testclasses.Join.notNull(condition));" + nl + "}";

		assertEquals(expectedDebug, ctLoop.toString());
	}

	@Test
	public void testEmptyForLoopExpression() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/loop/testclasses/EmptyLoops.java");
		CtModel model = launcher.buildModel();
		CtFor ctFor = model.getElements(new TypeFilter<>(CtFor.class)).get(0);
		assertTrue(ctFor.getForInit().isEmpty());
		assertNull(ctFor.getExpression());
		assertTrue(ctFor.getForUpdate().isEmpty());
		assertEquals("x = 5", ((CtBlock)ctFor.getBody()).getStatement(0).toString().trim());
	}
}
