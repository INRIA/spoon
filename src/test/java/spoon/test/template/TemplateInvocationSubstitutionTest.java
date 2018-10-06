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
package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.InvocationSubstitutionByExpressionTemplate;
import spoon.test.template.testclasses.InvocationSubstitutionByStatementTemplate;
import spoon.test.template.testclasses.SubstitutionByExpressionTemplate;

public class TemplateInvocationSubstitutionTest {

	@Test
	public void testInvocationSubstitutionByStatement() {
		//contract: the template engine supports substitution of any method invocation
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InvocationSubstitutionByStatementTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtBlock<?> templateArg = factory.Class().get(InvocationSubstitutionByStatementTemplate.class).getMethod("sample").getBody();
		
		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new InvocationSubstitutionByStatementTemplate(templateArg).apply(resultKlass);
		assertEquals("throw new java.lang.RuntimeException(\"Failed\")", result.toString());
	}

	@Test
	public void testInvocationSubstitutionByExpression() {
		//contract: the template engine supports substitution of any method invocation
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InvocationSubstitutionByExpressionTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtBlock<?> result = new InvocationSubstitutionByExpressionTemplate(factory.createLiteral("abc")).apply(resultKlass);

		assertEquals("java.lang.System.out.println(\"abc\".substring(1))", result.getStatement(0).toString());
		assertEquals("java.lang.System.out.println(\"abc\".substring(1))", result.getStatement(1).toString());

		// contract: the result of the template has no parent, and can be put anywhere in an AST
		assertFalse(result.isParentInitialized());
	}

	@Test
	public void testSubstitutionByExpression() {
		//contract: the template engine understands fields whose type extends from TemplateParameter as template parameter automatically. No need for extra annotation
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstitutionByExpressionTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtBlock<?> result = new SubstitutionByExpressionTemplate(factory.createLiteral("abc")).apply(resultKlass);
		assertEquals("java.lang.System.out.println(\"abc\".substring(1))", result.getStatement(0).toString());
	}
}
