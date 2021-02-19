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
package spoon.test.ctElement;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.CtAnnotationImpl;
import spoon.support.reflect.declaration.CtMethodImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static spoon.testing.utils.ModelUtils.createFactory;

/**
 * Created by urli on 28/06/2017.
 */
public class ElementTest {

	@Test
	public void testGetFactory() {
		// contract: getFactory should always return an object
		// even if an element is created via its constructor
		// and not through the factory

		Launcher spoon = new Launcher();

		CtElement element = spoon.getFactory().createAnnotation();
		assertNotNull(element.getFactory());

		CtElement otherElement = new CtAnnotationImpl<>();
		assertNotNull(otherElement.getFactory());

		CtElement yetAnotherOne = new CtMethodImpl<>();
		assertNotNull(yetAnotherOne.getFactory());

		// contract: a singleton is used for the default factory
		assertSame(otherElement.getFactory(), yetAnotherOne.getFactory());
	}

	@Test
	public void testGetChildren() {
		Launcher spoon = new Launcher();

		//contract: a freshly created element has no children
		CtElement el = spoon.getFactory().createIf();
		assertEquals(el.getDirectChildren().size(),0);

		//contract: children contains all direct descendants but nothing else
		CtClass cl = Launcher.parseClass("class A {int f; int g; public void m(int k){}}");
		assertEquals(cl.getDirectChildren().size(),4);
	}

	@Test
	public void testGetParentOverloadsInNoParentElements() {
		CtStatement statement = createFactory().Code().createCodeSnippetStatement("String hello = \"t1\";").compile();

		assertThrows(ParentNotInitializedException.class, () -> statement.getParent());
		assertNull(statement.getParent(CtBlock.class));
		assertNull(statement.getParent(new TypeFilter<>(CtBlock.class)));
	}

	@Test
	public void testGetParentOverloadsWithNoMatchingElements() {
		CtStatement statement = createFactory().Code().createCodeSnippetStatement("String hello = \"t1\";").compile();
		CtExpression<?> expression = ((CtLocalVariable<?>) statement).getAssignment();

		assertNull(expression.getParent(CtBlock.class));
		assertNull(expression.getParent(new TypeFilter<>(CtBlock.class)));
	}
}
