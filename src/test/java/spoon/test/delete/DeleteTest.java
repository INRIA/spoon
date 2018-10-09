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
package spoon.test.delete;

import org.junit.Test;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.delete.testclasses.Adobada;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static spoon.testing.utils.ModelUtils.build;

public class DeleteTest {

	@Test
	public void testDeleteAStatementInAnonymousExecutable() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final List<CtAnonymousExecutable> anonymousExecutables = adobada.getAnonymousExecutables();
		final CtAnonymousExecutable instanceExec = anonymousExecutables.get(0);

		assertEquals(2, instanceExec.getBody().getStatements().size());

		final CtStatement statement = instanceExec.getBody().getStatement(1);
		statement.delete();

		assertEquals(1, instanceExec.getBody().getStatements().size());
		assertFalse(instanceExec.getBody().getStatements().contains(statement));
	}

	@Test
	public void testDeleteAStatementInStaticAnonymousExecutable() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final List<CtAnonymousExecutable> anonymousExecutables = adobada.getAnonymousExecutables();
		final CtAnonymousExecutable staticExec = anonymousExecutables.get(1);

		assertEquals(2, staticExec.getBody().getStatements().size());

		final CtStatement statement = staticExec.getBody().getStatement(1);
		statement.delete();

		assertEquals(1, staticExec.getBody().getStatements().size());
		assertFalse(staticExec.getBody().getStatements().contains(statement));
	}

	@Test
	public void testDeleteAStatementInConstructor() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtConstructor<Adobada> constructor = adobada.getConstructor();

		assertEquals(3, constructor.getBody().getStatements().size());

		final CtStatement statement = constructor.getBody().getStatement(1);
		statement.delete();

		assertEquals(2, constructor.getBody().getStatements().size());
		assertFalse(constructor.getBody().getStatements().contains(statement));
	}

	@Test
	public void testDeleteAStatementInMethod() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m");

		assertEquals(2, method.getBody().getStatements().size());

		final CtStatement statement = method.getBody().getStatement(1);
		statement.delete();

		assertEquals(1, method.getBody().getStatements().size());
		assertFalse(method.getBody().getStatements().contains(statement));
	}

	@Test
	public void testDeleteReturn() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m2");

		assertEquals(1, method.getBody().getStatements().size());

		final CtStatement statement = method.getBody().getStatement(0);
		statement.delete();

		assertEquals(0, method.getBody().getStatements().size());
		assertFalse(method.getBody().getStatements().contains(statement));
	}

	@Test
	public void testDeleteStatementInCase() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m3");
		final CtCase aCase = method.getElements(new TypeFilter<>(CtCase.class)).get(0);

		assertEquals(2, aCase.getStatements().size());

		final CtStatement statement = aCase.getStatements().get(1);
		statement.delete();

		assertEquals(1, aCase.getStatements().size());
		assertFalse(aCase.getStatements().contains(statement));
	}

	@Test
	public void testDeleteACaseOfASwitch() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m3");
		final CtSwitch aSwitch = method.getElements(new TypeFilter<>(CtSwitch.class)).get(0);
		final CtCase aCase = (CtCase) aSwitch.getCases().get(1);

		assertEquals(2, aSwitch.getCases().size());

		aCase.delete();

		assertEquals(1, aSwitch.getCases().size());
		assertFalse(aSwitch.getCases().contains(aCase));
	}

	@Test
	public void testDeleteMethod() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m4", factory.Type().INTEGER_PRIMITIVE, factory.Type().FLOAT_PRIMITIVE, factory.Type().STRING);

		int n = adobada.getMethods().size();

		// deleting m4
		method.delete();

		assertEquals(n - 1, adobada.getMethods().size());
		assertFalse(adobada.getMethods().contains(method));
	}

	@Test
	public void testDeleteParameterOfMethod() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m4", factory.Type().INTEGER_PRIMITIVE, factory.Type().FLOAT_PRIMITIVE, factory.Type().STRING);
		final CtParameter param = (CtParameter) method.getParameters().get(1);

		assertEquals(3, method.getParameters().size());

		param.delete();

		assertEquals(2, method.getParameters().size());
		assertFalse(method.getParameters().contains(param));
	}

	@Test
	public void testDeleteBodyOfAMethod() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m");

		assertNotNull(method.getBody());

		method.getBody().delete();

		assertNull(method.getBody());
	}

	@Test
	public void testDeleteAnnotationOnAClass() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		assertEquals(1, adobada.getAnnotations().size());
		final CtAnnotation<? extends Annotation> annotation = adobada.getAnnotations().get(0);

		annotation.delete();

		assertEquals(0, adobada.getAnnotations().size());
		assertFalse(adobada.getAnnotations().contains(annotation));
	}

	@Test
	public void testDeleteAClassTopLevel() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);
		final CtPackage aPackage = adobada.getParent(CtPackage.class);

		assertEquals(1, aPackage.getTypes().size());

		adobada.delete();

		assertEquals(0, aPackage.getTypes().size());
		assertFalse(aPackage.getTypes().contains(adobada));
	}

	@Test
	public void testDeleteConditionInACondition() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m4", factory.Type().INTEGER_PRIMITIVE, factory.Type().FLOAT_PRIMITIVE, factory.Type().STRING);
		final CtIf anIf = method.getElements(new TypeFilter<>(CtIf.class)).get(0);

		assertNotNull(anIf.getCondition());

		anIf.getCondition().delete();

		assertNull(anIf.getCondition());
	}

	@Test
	public void testDeleteChainOfAssignment() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);

		final CtMethod method = adobada.getMethod("m4", factory.Type().INTEGER_PRIMITIVE, factory.Type().FLOAT_PRIMITIVE, factory.Type().STRING);
		final CtAssignment chainOfAssignment = method.getElements(new TypeFilter<>(CtAssignment.class)).get(0);

		assertNotNull(chainOfAssignment.getAssignment());

		chainOfAssignment.getAssignment().delete();

		assertNull(chainOfAssignment.getAssignment());
	}
}
