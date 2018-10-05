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
package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.LocalVariableReferenceFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.Pozole;
import spoon.test.reference.testclasses.Tortillas;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class VariableAccessTest {

	@Test
	public void testVariableAccessDeclarationInAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.reference.testclasses", "FooBar");
		assertEquals("FooBar", type.getSimpleName());

		final CtParameterReference<?> ref = type.getElements(new AbstractReferenceFilter<CtParameterReference<?>>(CtParameterReference.class) {
			@Override
			public boolean matches(CtParameterReference<?> reference) {
				return "myArg".equals(reference.getSimpleName());
			}
		}).get(0);

		assertNotNull("Parameter can't be null", ref.getDeclaration());
		assertNotNull("Declaring method reference can't be null", ref.getDeclaringExecutable());
		assertNotNull("Declaring type of the method can't be null", ref.getDeclaringExecutable().getDeclaringType());
		assertNotNull("Declaration of declaring type of the method can't be null", ref.getDeclaringExecutable().getDeclaringType().getDeclaration());
		assertNotNull("Declaration of root class can't be null", ref.getDeclaringExecutable().getDeclaringType().getDeclaringType().getDeclaration());
	}

	@Test
	public void testDeclarationArray() throws Exception {
		final CtType<Pozole> aPozole = buildClass(Pozole.class);
		final CtMethod<Object> m2 = aPozole.getMethod("m2");
		final CtArrayWrite<?> ctArrayWrite = m2.getElements(new TypeFilter<CtArrayWrite<?>>(CtArrayWrite.class)).get(0);
		final CtLocalVariable expected = m2.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);

		assertEquals(expected, ((CtVariableAccess) ctArrayWrite.getTarget()).getVariable().getDeclaration());
	}

	@Test
	public void testParameterReferenceInConstructorNoClasspath () {
		final Launcher launcher = new Launcher();
		// throws `NullPointerException` before PR #1098
		launcher.addInputResource("./src/test/resources/noclasspath/org/elasticsearch/indices/analysis/HunspellService.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();
	}

	@Test
	public void testDeclarationOfVariableReference() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Foo2.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtVariableReference>(CtVariableReference.class) {
			@Override
			public boolean matches(CtVariableReference element) {
				try {
					element.clone().getDeclaration();
				} catch (NullPointerException e) {
					fail("Fail with " + element.getSimpleName() + " declared in " + element.getParent().getShortRepresentation());
				}
				return super.matches(element);
			}
		});
	}

	@Test
	public void testDeclaringTypeOfALambdaReferencedByParameterReference() {
		final spoon.Launcher launcher = new spoon.Launcher();
		launcher.addInputResource("src/test/resources/noclasspath/Foo3.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtExecutable<?>>(CtExecutable.class) {
			@Override
			public boolean matches(CtExecutable<?> exec) {
				final List<CtParameterReference<?>> guiParams = exec.getParameters().stream().map(CtParameter::getReference).collect(Collectors.toList());

				if (guiParams.size() != 1) {
					return false;
				}

				final CtParameterReference<?> param = guiParams.get(0);

				exec.getBody().getElements(new TypeFilter<CtParameterReference<?>>(CtParameterReference.class) {
					@Override
					public boolean matches(CtParameterReference<?> p) {
						assertEquals(p.getSimpleName(), param.getSimpleName());
						return super.matches(p);
					}
				});

				return super.matches(exec);
			}
		});
	}

	@Test
	public void testGetDeclarationAfterClone() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/A2.java");
		launcher.buildModel();

		final CtClass<Object> a2 = launcher.getFactory().Class().get("A2");
		final CtClass<Object> a2Cloned = a2.clone();

		assertEquals(a2, a2Cloned);

		final CtMethod<Object> methodA2 = getMethod(launcher, a2);
		final CtMethod<Object> methodA2Cloned = getMethod(launcher, a2Cloned);

		final CtLocalVariable declaration = methodA2.getBody().getStatement(0);
		final CtLocalVariable declarationCloned = methodA2Cloned.getBody().getStatement(0);

		final CtLocalVariableReference localVarRef = getLocalVariableRefF1(methodA2);
		final CtLocalVariableReference localVarRefCloned = getLocalVariableRefF1(methodA2Cloned);

		assertEquals(localVarRef.getDeclaration(), declaration);
		assertSame(localVarRef.getDeclaration(), declaration);
		assertEquals(localVarRefCloned.getDeclaration(), declarationCloned);
		assertSame(localVarRefCloned.getDeclaration(), declarationCloned);
	}

	@Test
	public void testReferences() throws Exception {

		/* test getReference on local variable
		*  getReference().getDeclaration() must be circular
		*/

		final CtType<Tortillas> aTortillas = buildClass(Tortillas.class);
		final CtMethod<Object> make = aTortillas.getMethod("make", aTortillas.getFactory().Type().stringType());

		final CtLocalVariable localVar = make.getBody().getStatement(0);
		final CtLocalVariable localVarCloned = localVar.clone();

		final CtLocalVariableReference localVarRef = localVar.getReference();
		final CtLocalVariableReference localVarRefCloned = localVarCloned.getReference();

		assertEquals(localVarRef.getDeclaration(), localVar);
		assertSame(localVarRef.getDeclaration(), localVar);
		assertEquals(localVar.getReference().getDeclaration(), localVar);
		assertSame(localVar.getReference().getDeclaration(), localVar);

		assertEquals(localVarRefCloned.getDeclaration(), localVarCloned);
		assertSame(localVarRefCloned.getDeclaration(), localVarCloned);
		assertEquals(localVarCloned.getReference().getDeclaration(), localVarCloned);
		assertSame(localVarCloned.getReference().getDeclaration(), localVarCloned);
	}
	@Test
	public void testReferencesInInitExpression() throws Exception {
		/* test getReference on local variable
		*  getReference().getDeclaration() must be circular
		*/

		final CtType<Tortillas> aTortillas = buildClass(Tortillas.class);
		final CtMethod<Object> make = aTortillas.getMethod("make", aTortillas.getFactory().Type().stringType());
		
		final CtLocalVariable localVarNumber = make.getBody().getStatement(1);
		List<CtLocalVariableReference<?>> refs = localVarNumber.map(new LocalVariableReferenceFunction()).list();
		assertEquals(1, refs.size());
		assertSame(localVarNumber, refs.get(0).getParent(CtLocalVariable.class));
	}

	@Test
	public void testReferenceToLocalVariableDeclaredInLoop() {
		final class CtLocalVariableReferenceScanner extends CtScanner {
			@Override
			public <T> void visitCtLocalVariableReference(
					final CtLocalVariableReference<T> reference) {
				assertNotNull(reference.getDeclaration());
				assertEquals(reference.getDeclaration().getSimpleName(),
						reference.getSimpleName());
				assertEquals(reference.getDeclaration().getType(),
						reference.getType());
				super.visitCtLocalVariableReference(reference);
			}
		}

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/reference-test/ChangeScanner.java");
		launcher.buildModel();

		new CtLocalVariableReferenceScanner().scan(launcher.getModel().getRootPackage());
	}

	@Test
	public void testMultipleDeclarationsOfLocalVariable() {
		final class CtLocalVariableReferenceScanner extends CtScanner {
			@Override
			public <T> void visitCtLocalVariableReference(
					final CtLocalVariableReference<T> reference) {
				assertNotNull(reference.getDeclaration());
				final CtLocalVariable decl = reference.getDeclaration();
				assertEquals(7, decl.getPosition().getLine());
				assertTrue(decl.getDefaultExpression() instanceof CtLiteral);
				final CtLiteral literal = (CtLiteral) decl.getDefaultExpression();
				assertEquals(42, literal.getValue());
				super.visitCtLocalVariableReference(reference);
			}
		}

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/reference-test/MultipleDeclarationsOfLocalVariable.java");
		launcher.buildModel();

		new CtLocalVariableReferenceScanner().scan(launcher.getModel().getRootPackage());
	}

	private CtMethod<Object> getMethod(Launcher launcher, CtClass<Object> a2) {
		return a2.getMethod("b", launcher.getFactory().Type().integerPrimitiveType());
	}

	private CtLocalVariableReference getLocalVariableRefF1(CtMethod<Object> method) {
		return method.getElements(new TypeFilter<CtLocalVariableReference>(CtLocalVariableReference.class) {
			@Override
			public boolean matches(CtLocalVariableReference element) {
				return "f1".equals(element.getSimpleName()) && super.matches(element);
			}
		}).get(0);
	}
	
	@Test
	public void testSuperAccess() throws Exception {
		// contract: the type of "super" variable is set and correct		
		CtClass<?> type = build("spoon.test.reference.testclasses", "SuperAccess");
		CtMethod<?> method = type.getMethodsByName("method").get(0);
		CtInvocation<?> invocation = method.getBody().getStatement(0);
		CtSuperAccess<?> superAccess = (CtSuperAccess<?>) invocation.getTarget();
		assertNotNull(superAccess.getType());
		assertEquals("spoon.test.reference.testclasses.Parent", superAccess.getType().getQualifiedName());
	}
}
