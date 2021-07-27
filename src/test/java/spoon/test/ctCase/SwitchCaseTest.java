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
package spoon.test.ctCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static spoon.testing.utils.ModelUtils.build;

import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SwitchCaseTest {

	@Test
	public void insertAfterStatementInSwitchCaseWithoutException() throws Exception {
		String packageName = "spoon.test.ctCase.testclasses";
		String className = "ClassWithSwitchExample";
		Factory factory = factoryFor(packageName, className);
		List<CtCase> elements = elementsOfType(CtCase.class, factory);
		assertEquals(3, elements.size());
		CtCase firstCase = elements.get(0);
		List<CtStatement> statements = firstCase.getStatements();
		assertEquals(2, statements.size());
		CtStatement newStatement = factory.Code().createCodeSnippetStatement("result = 0");
		statements.get(0).insertAfter(newStatement);
		statements = firstCase.getStatements();
		assertEquals(3, statements.size());
		assertSame(statements.get(1), newStatement);
	}

	@Test
	public void insertBeforeStatementInSwitchCaseWithoutException() throws Exception {
		String packageName = "spoon.test.ctCase.testclasses";
		String className = "ClassWithSwitchExample";
		Factory factory = factoryFor(packageName, className);
		List<CtCase> elements = elementsOfType(CtCase.class, factory);
		assertEquals(3, elements.size());
		CtCase firstCase = elements.get(0);
		List<CtStatement> statements = firstCase.getStatements();
		assertEquals(2, statements.size());
		CtStatement newStatement = factory.Code().createCodeSnippetStatement("result = 0");
		statements.get(0).insertBefore(newStatement);
		statements = firstCase.getStatements();
		assertEquals(3, statements.size());
		assertSame(statements.get(0), newStatement);
	}

	private <T extends CtElement> List<T> elementsOfType(Class<T> type, Factory factory) {
		return Query.getElements(factory, new TypeFilter<>(type));
	}

	private Factory factoryFor(String packageName, String className) throws Exception {
		return build(packageName, className).getFactory();
	}
}
