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
package spoon.test.factory;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.test.SpoonTestHelpers;
import spoon.test.factory.testclasses.Foo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.parent.ContractOnSettersParametrizedTest.createCompatibleObject;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class FactoryTest {

	@Test
	public void testClone() throws Exception {
		CtClass<?> type = build("spoon.test.testclasses", "SampleClass");
		CtMethod<?> m = type.getMethodsByName("method3").get(0);
		int i = m.getBody().getStatements().size();
		m.putMetadata("metadata", 1);
		int metadata = m.getAllMetadata().size();

		m = m.clone();

		assertEquals(i, m.getBody().getStatements().size());
		assertEquals(metadata, m.getAllMetadata().size());
		// cloned elements must not have an initialized parent
		assertFalse(m.isParentInitialized());
	}

	@Test
	public void testFactoryOverriding()  throws Exception {

		@SuppressWarnings("serial")
		class MyCtMethod<T> extends CtMethodImpl<T> { }

		@SuppressWarnings("serial")
		final CoreFactory specialCoreFactory = new DefaultCoreFactory() {
			@Override
			public <T> CtMethod<T> createMethod() {
				MyCtMethod<T> m = new MyCtMethod<>();
				m.setFactory(getMainFactory());
				return m;
			}
		};

		Launcher launcher = new Launcher() {
			@Override
			public Factory createFactory() {
				return new FactoryImpl(specialCoreFactory, new StandardEnvironment());
			}
		};

		CtClass<?> type = build("spoon.test.testclasses", "SampleClass", launcher.getFactory());

		CtMethod<?> m = type.getMethodsByName("method3").get(0);

		assertTrue(m instanceof MyCtMethod);
	}

	@Test
	public void testClassAccessCreatedFromFactories() throws Exception {
		final CtType<Foo> foo = buildClass(Foo.class);

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(0, foo.getAnnotations().get(0).getValues().size());

		foo.getFactory().Annotation().annotate(foo, Foo.Bar.class, "clazz", Foo.class);

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(1, foo.getAnnotations().get(0).getValues().size());
		assertTrue(foo.getAnnotations().get(0).getValues().get("clazz") instanceof CtFieldRead);
		assertEquals("spoon.test.factory.testclasses.Foo.class", foo.getAnnotations().get(0).getValues().get("clazz").toString());

		foo.getFactory().Annotation().annotate(foo, Foo.Bar.class, "classes", new Class[] { Foo.class });

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(2, foo.getAnnotations().get(0).getValues().size());
		assertTrue(foo.getAnnotations().get(0).getValues().get("classes") instanceof CtNewArray);
		assertEquals(1, ((CtNewArray) foo.getAnnotations().get(0).getValues().get("classes")).getElements().size());
		assertEquals("spoon.test.factory.testclasses.Foo.class", ((CtNewArray) foo.getAnnotations().get(0).getValues().get("classes")).getElements().get(0).toString());
	}

	@Test
	public void testCtModel() {
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/test/java/spoon/test/factory/testclasses");
		spoon.buildModel();

		CtModel model = spoon.getModel();

		// contains Foo and Foo.@Bar
		assertEquals(1, model.getAllTypes().size());

		// [, spoon, spoon.test, spoon.test.factory, spoon.test.factory.testclasses]
		assertEquals(5, model.getAllPackages().size());

		// add to itself is fine
		model.getRootPackage().addPackage(model.getRootPackage());
		assertEquals(1, model.getAllTypes().size());
		assertEquals(5, model.getAllPackages().size());

		model.getRootPackage().getPackage("spoon").addPackage(model.getRootPackage().getPackage("spoon"));
		assertEquals(1, model.getAllTypes().size());
		assertEquals(5, model.getAllPackages().size());

		model.getRootPackage().addPackage(model.getRootPackage().getPackage("spoon"));
		assertEquals(1, model.getAllTypes().size());
		assertEquals(5, model.getAllPackages().size());


		CtPackage p = model.getElements(new NamedElementFilter<>(CtPackage.class, "spoon")).get(0).clone();
		// if we change the implem, merge is impossible
		CtField f = spoon.getFactory().Core().createField();
		f.setSimpleName("foo");
		f.setType(spoon.getFactory().Type().BYTE);
		p.getElements(new NamedElementFilter<>(CtPackage.class, "testclasses")).get(0).getType("Foo").addField(f);
		try {
			model.getRootPackage().addPackage(p);
			fail("no exception thrown");
		} catch (IllegalStateException success) { }

		model.processWith(new AbstractProcessor<CtType>() {
			@Override
			public void process(CtType element) {
				element.delete();
			}
		});
		assertEquals(0, model.getAllTypes().size());
	}

	public void testIncrementalModel() {

		// contract: one can merge two models together
		// May 2018: we realize that the merge is incomplete see https://github.com/INRIA/spoon/issues/2001
		// * the produced target model contains elements whose getFactory() returns different values
		// * the source model becomes inconsistent, because that model still points to a children elements, but getParent of these elements points to different model.
		// so we remove that test in order to proceed with other key features with strong contracts (incl. wrt to parents)
		// we keep the test here for keeping a trace

//		// Feed some inputResources to a spoon compiler
//		SpoonAPI spoon = new Launcher();
//		spoon.addInputResource("src/test/java/spoon/test/factory/testclasses");
//
//		// Build model
//		spoon.buildModel();
//		assertEquals(1, spoon.getModel().getAllTypes().size());
//
//		// Do something with that model..
//		CtModel model = spoon.getModel();
//		model.processWith(new AbstractProcessor<CtMethod>() {
//			@Override
//			public void process(CtMethod element) {
//				element.setDefaultMethod(false);
//			}
//		});
//
//		// Feed some new inputResources
//		SpoonAPI spoon2 = new Launcher();
//		spoon2.addInputResource("src/test/java/spoon/test/factory/testclasses2");
//
//		// Build models of newly added classes/packages
//		spoon2.buildModel();
//		assertEquals(1, spoon2.getModel().getAllTypes().size());
//
//		// attach them to the existing model.
//		model.getRootPackage().addPackage(spoon2.getModel().getRootPackage());
//
//		// checking the results
//		assertEquals(6, model.getAllPackages().size());
//		assertEquals(2, model.getAllTypes().size());
//		assertEquals(1, model.getElements(new AbstractFilter<CtPackage>() {
//			@Override
//			public boolean matches(CtPackage element) {
//				return "spoon.test.factory.testclasses2".equals(element.getQualifiedName());
//			}
//		}).size());
	}

	@Test
	public void specificationCoreFactoryCreate() {
		// contract: all concrete metamodel classes must be instantiable by CoreFactory.create
		for (CtType<? extends CtElement> itf : SpoonTestHelpers.getAllInstantiableMetamodelInterfaces()) {
			CtElement o = itf.getFactory().Core().create(itf.getActualClass());
			assertNotNull(o);
			assertTrue(itf.getActualClass().isInstance(o));
		}
	}

	@Test
	public void factoryTest() throws Exception {
		// contract: all methods of Factory can be called without exception, returning a correct object
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/main/java/spoon/reflect/factory/Factory.java");
		spoon.buildModel();
		for (CtMethod<?> m : spoon.getFactory().Type().get("spoon.reflect.factory.Factory").getMethods()) {
			if (!m.getSimpleName().startsWith("create")
				|| "createSourcePosition".equals(m.getSimpleName()) // method with implicit contracts on int parameters
				|| "createBodyHolderSourcePosition".equals(m.getSimpleName()) // method with implicit contracts on int parameters
				|| "createDeclarationSourcePosition".equals(m.getSimpleName()) // method with implicit contracts on int parameters
				|| "createNewClass".equals(m.getSimpleName()) // method with implicit contract between the two parameters
			) {
				continue;
			}


			// collecting arguments and creating parameters
			Object[] args = new Object[m.getParameters().size()];
			Class[] argsClass = new Class[m.getParameters().size()];
			for (int i =0; i<args.length; i++) {
				CtTypeReference<?> type = m.getParameters().get(i).getType();
				args[i] = createCompatibleObject(type);
				argsClass[i] = type.getActualClass();
				if (!type.isPrimitive()) {
					// post-condition to be sure that createCompatibleObject works well
					assertTrue(args[i].getClass().toString() + " != " + argsClass[i].toString(), argsClass[i].isAssignableFrom(args[i].getClass()));
				}
			}

			// calling the method
			Method rm;
			rm = m.getReference().getActualMethod();
			//rm = spoon.getFactory().getClass().getDeclaredMethod(m.getSimpleName(), argsClass); // works also
			//System.out.println(rm);
			Object res = rm.invoke(spoon.getFactory(), args);
			assertNotNull(res);

		}
	}

}
