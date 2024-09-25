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
package spoon.test.prettyprinter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.reflect.code.CtFieldAccessImpl;
import spoon.support.reflect.code.CtTypeAccessImpl;
import spoon.test.delete.testclasses.Adobada;
import spoon.test.prettyprinter.testclasses.QualifiedThisRef;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class QualifiedThisRefTest {

	Factory factory;

	@BeforeEach
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/prettyprinter/testclasses/QualifiedThisRef.java"))
				.build();
		factory.getEnvironment().setAutoImports(true);
	}

	@Test
	public void testQualifiedThisRef() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		CtType<?> ctClass = factory.Type().get(QualifiedThisRef.class);
		final List<CtType<?>> ctTypes = new ArrayList<>();
		ctTypes.add(ctClass);
		printer.scan(ctClass);
		assertTrue(printer.getResult().contains("Object o = this"));
	}

	@Test
	public void testCloneThisAccess() throws Exception {
		// contract: the target of "this" is correct and can be cloned
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);
		final CtMethod<?> m2 = adobada.getMethod("methodUsingjlObjectMethods");

		CtThisAccess th = (CtThisAccess) m2.getElements(new TypeFilter(CtThisAccess.class)).get(0);
		assertTrue(th.isImplicit());
		assertEquals("notify()",th.getParent().toString());
		CtInvocation<?> clone = m2.clone().getBody().getStatement(0);
		// clone preserves implicitness
		assertTrue(clone.getTarget().isImplicit());
		assertEquals("notify()", clone.toString()); // the original bug

		// note that this behavior means that you can only keep cloned "this" in the same class,
		// and you cannot "transplant" a cloned "this" to another class
		// it makes perfectly sense about the meaning of this.
		// to "transplant" a this, you have to first set the target to null
	}

	@Test
	public void testPrintCtFieldAccessWorkEvenWhenParentNotInitialized() {
		CtClass zeclass = factory.Class().get(QualifiedThisRef.class);

		List<CtMethod> methods = zeclass.getMethodsByName("bla");

		assertEquals(1, methods.size());

		CtStatement invocation = methods.get(0).getBody().getStatement(0);

		assertTrue(invocation instanceof CtInvocation);
		CtInvocation<?> arg0 = (CtInvocation) invocation;

		CtExpression param = arg0.getArguments().get(0);
		CtExecutableReference execref = factory.Core().createExecutableReference();
		execref.setDeclaringType(factory.Type().createReference("java.util.Map"));
		execref.setSimpleName("exorcise");
		execref.setStatic(true);

		CtTypeReference tmp = param.getType();

		CtFieldReference ctfe = factory.createFieldReference();
		ctfe.setSimpleName("class");
		ctfe.setDeclaringType(tmp.box());
		CtExpression arg = factory.Core().createFieldRead();
		((CtFieldAccessImpl) arg).setVariable(ctfe);


		CtLiteral location = factory.Core().createLiteral();
		location.setType(factory.Type().createReference(String.class));

		CtTypeReference tmpref = factory.Core().clone(tmp);

		CtInvocation invoc = factory.Core().createInvocation();
		invoc.setExecutable(execref);
		invoc.setArguments(Arrays.asList(new CtExpression[]{param,arg,location}));
		execref.setActualTypeArguments(Arrays.asList(new CtTypeReference<?>[]{tmpref}));

		// succeeds
		arg0.getArguments().set(0, invoc);

		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		printer.visitCtClass(zeclass);

		assertFalse(printer.getResult().isEmpty());

	}

	@Test
	public void testGuavaBug() {
		// contract: does not model buf as a typeaccess, does not write ByteArrayOutputStream.buf
		// this bug was found by spooning Guava in Jenkins
		final String code = "class ExposedByteArrayOutputStream extends java.io.ByteArrayOutputStream {\n" +
				"        ExposedByteArrayOutputStream(int expectedInputSize) {\n" +
				"            super(expectedInputSize);\n" +
				"        }\n" +
				"\n" +
				"        void write(java.nio.ByteBuffer input) {\n" +
				"            int remaining = input.remaining();\n" +
				"            if ((count + remaining) > buf.length) {\n" +
				"                buf = java.util.Arrays.copyOf(buf, count + remaining);\n" +
				"            }\n" +
				"            input.get(buf, count, remaining);\n" +
				"            count += remaining;\n" +
				"        }\n" +
				"\n" +
				"        byte[] byteArray() {\n" +
				"            return buf;\n" +
				"        }\n" +
				"\n" +
				"        int length() {\n" +
				"            return count;\n" +
				"        }\n" +
				"    }";

		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile(code));
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setAutoImports(true);

		CtClass<?> c = (CtClass<?>) launcher.buildModel().getAllTypes().iterator().next();
		assertEquals(c.getSimpleName().toString(), "ExposedByteArrayOutputStream");

		final List<Object> list = c.filterChildren(new TypeFilter<>(CtBinaryOperator.class)).list();
		CtBinaryOperator<?> binaryOperator = (CtBinaryOperator<?>) list.get(0);
		assertTrue(CtFieldRead.class.isAssignableFrom(binaryOperator.getRightHandOperand().getClass()));
		assertEquals("(count + remaining) > buf.length", binaryOperator.toString());
		assertFalse(c.toString().contains("ByteArrayOutputStream.buf"), "that will not compile for sure");

	}
}
