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
package spoon.test.path;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtElementPathBuilder;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathBuilder;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.path.CtPathStringBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by nicolas on 10/06/2015.
 */
public class PathTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/path/testclasses/Foo.java"))
				.build();
	}

	private void equals(CtPath path, CtElement... elements) {
		Collection<CtElement> result = path.evaluateOn(factory.Package().getRootPackage());
		assertEquals(elements.length, result.size());
		assertArrayEquals(elements, result.toArray(new CtElement[0]));
	}

	private void equalsSet(CtPath path, Set<? extends CtElement> elements) {
		Collection<CtElement> result = path.evaluateOn(factory.Package().getRootPackage());
		assertEquals(elements.size(), result.size());
		assertTrue(result.containsAll(elements));
	}

	@Test
	public void testBuilderMethod() {
		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").type(CtMethod.class).build(),

				factory.Type().get("spoon.test.path.testclasses.Foo").getMethods()
		);

		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").name("bar").build(),

				new HashSet<>(factory.Type().get("spoon.test.path.testclasses.Foo").getMethodsByName("bar"))
		);

		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").name("bar\\(int,.*?\\)").build(),

				new HashSet<>(factory.Type().get("spoon.test.path.testclasses.Foo")
						.filterChildren((CtMethod m) -> "bar(int,int)".equals(m.getSignature()))
						.list())
		);

		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").name("(java.lang.String)").build(),

				new HashSet<>(factory.Type().get("spoon.test.path.testclasses.Foo")
						.filterChildren((CtConstructor c) -> "spoon.test.path.testclasses.Foo(java.lang.String)".equals(c.getSignature()))
						.list())
		);

		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").name("()").build(),

				new HashSet<>(factory.Type().get("spoon.test.path.testclasses.Foo")
						.filterChildren((CtConstructor c) -> "spoon.test.path.testclasses.Foo()".equals(c.getSignature()))
						.list())
		);

		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").role(CtRole.CONSTRUCTOR, new String[]{"signature", "()"}).build(),

				new HashSet<>(factory.Type().get("spoon.test.path.testclasses.Foo")
						.filterChildren((CtConstructor c) -> "spoon.test.path.testclasses.Foo()".equals(c.getSignature()))
						.list())
		);

		equalsSet(
				new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo/CtMethod"),

				factory.Type().get("spoon.test.path.testclasses.Foo").getMethods()
		);

	}

	@Test
	public void testBuilder() {
		equals(
				new CtPathBuilder().recursiveWildcard().name("toto").role(
						CtRole.DEFAULT_EXPRESSION).build(),

				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getField("toto").getDefaultExpression()
		);
	}

	@Test
	public void testPathFromString() {
		// match the first statement of Foo.foo() method
		equals(
				new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]"),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("foo").getBody()
						.getStatement(0));

		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar/CtParameter"),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo")
					.filterChildren(new NamedElementFilter<>(CtMethod.class, "bar"))
					.filterChildren(new TypeFilter<>(CtParameter.class))
					.list().toArray(new CtElement[0])
		);
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar(int,int)/CtParameter"),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class),
						factory.Type().createReference(int.class))
						.getParameters().toArray(new CtElement[0])
		);
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar(int)/CtParameter"),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class))
						.getParameters().toArray(new CtElement[0])
		);

		CtLiteral<String> literal = factory.Core().createLiteral();
		literal.setValue("salut");
		literal.setType(literal.getFactory().Type().STRING);
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.toto#defaultExpression"), literal);
	}

	@Test
	public void testMultiPathFromString() {
		// When role match a list but no index is provided, all of them must be returned
		Collection<CtElement> results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(3, results.size());
		// When role match a set but no name is provided, all of them must be returned
		results = new CtPathStringBuilder().fromString("#subPackage")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(1, results.size());
		// When role match a map but no key is provided, all of them must be returned
		results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar#annotation[index=0]#value")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(1, results.size());

	}

	@Test
	public void testIncorrectPathFromString() {
		// match the else part of the if in Foo.bar() method which does not exist (Test non existing unique element)
		Collection<CtElement> results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar#body#statement[index=2]#else")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
		// match the third statement of Foo.foo() method which does not exist (Test non existing element of a list)
		results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=3]")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
		// match an non existing package (Test non existing element of a set)
		results = new CtPathStringBuilder().fromString("#subPackage[name=nonExistingPackage]")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
		//match a non existing field of an annotation (Test non existing element of a map)
		results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar#annotation[index=0]#value[key=misspelled]")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
	}

	@Test
	public void testGetPathFromNonParent() {
		//contract: CtElementPathBuilder fails when there is no path from element to parent element
		CtMethod fooMethod = (CtMethod) new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo")
				.evaluateOn(factory.getModel().getRootPackage()).get(0);
		CtMethod barMethod = (CtMethod) new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar")
				.evaluateOn(factory.getModel().getRootPackage()).get(0);
		try {
			new CtElementPathBuilder().fromElement(fooMethod,barMethod);
			fail("No path should be found to .spoon.test.path.testclasses.Foo.foo from .spoon.test.path.testclasses.Foo.bar");
		} catch (CtPathException e) {

		}
	}

	@Test
	public void testWildcards() {
		// get the first statements of all Foo methods
		List<CtElement> list = new LinkedList<>();
		list.add(factory.getModel().getRootPackage());
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.*#body#statement[index=0]"),
				((CtClass) factory.Package().get("spoon.test.path.testclasses").getType("Foo")).getConstructor().getBody()
						.getStatement(0),
				((CtClass) factory.Package().get("spoon.test.path.testclasses").getType("Foo")).getConstructor(factory.Type().createReference(String.class)).getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("foo").getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class), factory.Type().createReference(int.class)).getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethodsByName("methodWithArgs").get(0).getBody()
						.getStatement(0)
		);
	}

	@Test
	public void testFastPathWithIndex() {
		// contract: the path can still use index
		CtType<?> fooClass = factory.Package().get("spoon.test.path.testclasses").getType("Foo");
		CtMethod<Object> method = fooClass.getMethod("foo");
		CtStatement ifStmt = ((CtIf) method.getBody()
				.getStatement(2)).getElseStatement();

		//check that path with index 
		CtPath absPath = new CtElementPathBuilder().setUseNamesInPath(false).fromElement(ifStmt);
		assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#typeMember[index=3]#body#statement[index=2]#else", absPath.toString());
	}

	@Test
	public void testRoles() {
		// get the then statement
		CtType<?> fooClass = factory.Package().get("spoon.test.path.testclasses").getType("Foo");
		CtPath path = new CtPathStringBuilder().fromString(".**/CtIf#else");
		CtMethod<Object> method = fooClass.getMethod("foo");
		CtStatement expected = ((CtIf) method.getBody()
				.getStatement(2)).getElseStatement();
		equals(path,
				expected
		);

		// now we get the absolute path
		CtPath absPath = path.evaluateOn(factory.getModel().getRootPackage()).get(0).getPath();
		assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#method[signature=foo()]#body#statement[index=2]#else", absPath.toString());
		

		// contract: subpath enables to have relative path
		CtPath subPath = absPath.relativePath(fooClass);
		assertEquals("#method[signature=foo()]#body#statement[index=2]#else", subPath.toString());
		assertSame(expected, subPath.evaluateOn(fooClass).get(0));

		CtPath subPath2 = absPath.relativePath(method);
		assertEquals("#body#statement[index=2]#else", subPath2.toString());
		assertSame(expected, subPath2.evaluateOn(method).get(0));

		equals(new CtPathStringBuilder().fromString(".**#else"),
				expected
		);
	}
	
	@Test
	public void testAmbiguousTypeMembers() {
		CtType<?> type = factory.Type().get("spoon.test.path.testclasses.Foo");
		
		for (CtTypeMember typeMember : type.getTypeMembers()) {
			CtPath path = typeMember.getPath();
			List<CtElement> elements = path.evaluateOn(factory.getModel().getRootPackage());
			assertEquals("ambiguous path " + path + " on element " + typeMember.toString(), 1, elements.size());
			assertSame(typeMember, elements.get(0));
		}
	}

	@Test
	public void toStringTest() {
		comparePath(".spoon.test.path.testclasses.Foo/CtMethod");
		comparePath(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]");
		comparePath(".spoon.test.path.testclasses.Foo.bar/CtParameter");
		comparePath(".spoon.test.path.testclasses.Foo.toto#defaultExpression");
		comparePath(".spoon.test.path.testclasses.Foo.*#body#statement[index=0]");
		comparePath(".**/CtIf#else");
		comparePath(".**#else");
	}

	private void comparePath(String path) throws CtPathException {
		assertEquals(path, new CtPathStringBuilder().fromString(path).toString());
	}

	@Test
	public void exceptionTest() {
		try {
			new CtPathStringBuilder().fromString("/CtClassss");
			fail();
		} catch (CtPathException e) {
			assertEquals("Unable to locate element with type CtClassss in Spoon model", e.getMessage());
		}
	}

	@Test
	public void testGenericTypeReferenceInSuperType() {
		//contract: path works for param type reference in super type of class
		CtTypeParameterReference typeParamRef = (CtTypeParameterReference) factory.Type().get("spoon.test.path.testclasses.Foo").getSuperclass().getActualTypeArguments().get(0);
		CtPath path = typeParamRef.getPath();
		assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#superType#typeArgument[name=T]", path.toString());
		List<CtElement> result = path.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(1, result.size());
		assertSame(typeParamRef, result.get(0));
	}

	@Test
	public void testSignatureOfVarargMethod() {
		//contract: path works for methods with varargs too
		List<CtMethod<?>> methods = factory.Type().get("spoon.test.path.testclasses.Foo").getMethodsByName("processors");
		{
			CtMethod<?> method = methods.get(0);
			CtPath path = method.getPath();
			assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#method[signature=processors(java.lang.String,java.lang.String)]", path.toString());
			CtPath pathFromString = new CtPathStringBuilder().fromString(path.toString());
			assertEquals(path.toString(), pathFromString.toString());
			List<CtElement> result = pathFromString.evaluateOn(factory.getModel().getRootPackage());
			assertEquals(1, result.size());
			assertSame(method, result.get(0));
		}
		{
			CtMethod<?> method = methods.get(1);
			CtPath path = method.getPath();
			assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#method[signature=processors(java.lang.String[])]", path.toString());
			CtPath pathFromString = new CtPathStringBuilder().fromString(path.toString());
			assertEquals(path.toString(), pathFromString.toString());
			List<CtElement> result = pathFromString.evaluateOn(factory.getModel().getRootPackage());
			assertEquals(1, result.size());
			assertSame(method, result.get(0));
		}
	}
	@Test
	public void testAmbiguousName() {
		//contract: path fallbacks to index if name is ambiguous
		CtInvocation<?> inv = factory.Type().get("spoon.test.path.testclasses.Foo").getMethodsByName("methodWithArgs").get(0).getBody().getStatement(1);
		{
			CtTypeReference<?> argType = inv.getExecutable().getParameters().get(0);
			CtPath path = argType.getPath();
			assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#method[signature=methodWithArgs(java.lang.String[])]#body#statement[index=1]#executableRef#argumentType[index=0;name=String]", path.toString());
			CtPath pathFromString = new CtPathStringBuilder().fromString(path.toString());
			assertEquals(path.toString(), pathFromString.toString());
			List<CtElement> result = pathFromString.evaluateOn(factory.getModel().getRootPackage());
			assertEquals(1, result.size());
			assertSame(argType, result.get(0));
		}
		{
			CtTypeReference<?> argType = inv.getExecutable().getParameters().get(1);
			CtPath path = argType.getPath();
			assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#method[signature=methodWithArgs(java.lang.String[])]#body#statement[index=1]#executableRef#argumentType[index=1;name=String]", path.toString());
			CtPath pathFromString = new CtPathStringBuilder().fromString(path.toString());
			assertEquals(path.toString(), pathFromString.toString());
			List<CtElement> result = pathFromString.evaluateOn(factory.getModel().getRootPackage());
			assertEquals(1, result.size());
			assertSame(argType, result.get(0));
		}
	}
	@Test
	public void testFieldOfArrayType() {
		//contract: path works for fields with type String[]
		CtInvocation<?> inv = factory.Type().get("spoon.test.path.testclasses.Foo").getMethodsByName("methodWithArgs").get(0).getBody().getStatement(0);
		CtTypeReference<?> argType = inv.getExecutable().getParameters().get(0);
		CtPath path = argType.getPath();
		assertEquals("#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#method[signature=methodWithArgs(java.lang.String[])]#body#statement[index=0]#executableRef#argumentType[name=String[]]", path.toString());
		CtPath pathFromString = new CtPathStringBuilder().fromString(path.toString());
		assertEquals(path.toString(), pathFromString.toString());
		List<CtElement> result = pathFromString.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(1, result.size());
		assertSame(argType, result.get(0));
	}
}