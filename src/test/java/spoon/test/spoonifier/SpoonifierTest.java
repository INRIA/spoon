/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.spoonifier;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.experimental.SpoonifyVisitor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


import static org.junit.Assert.assertEquals;

public class SpoonifierTest {

	/**
	 * This test is too long for CI, but it checks that SpoonifierVisitor does produce equivalent Type for all
	 * files used for testing purposes.
	 */
	@Ignore
	@Test
	public void testOnAllTestClasses() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		//Get all testclasses files
		List<String> testCases = Files.walk(Paths.get("src/test/java/spoon/test"))
				.filter(Files::isRegularFile)
				.filter(f -> f.toString().endsWith(".java"))
				.filter(f -> !f.toString().endsWith("package-info.java"))
				.filter(f -> f.getParent().toString().endsWith("testclasses"))
				.map(f -> f.toString())
				.collect(Collectors.toList());

		// The code of method get(Factory) is exceeding the 65535 bytes limit
		// Generated code is too long.
		testCases.remove("src/test/java/spoon/test/refactoring/testclasses/CtRenameLocalVariableRefactoringTestSubject.java");
		testCases.remove("src/test/java/spoon/test/query_function/testclasses/VariableReferencesModelTest.java");

		//Carpet debugging, but this case of comment is not completely handled in spoon currently anyway...
		testCases.remove("src/test/java/spoon/test/prettyprinter/testclasses/ToBeChanged.java");
		testCases.remove("src/test/java/spoon/test/position/testclasses/PositionParameterTypeWithReference.java");
		testCases.remove("src/test/java/spoon/test/position/testclasses/Expressions.java");

		int i = 0;
		for (String path: testCases) {
			System.out.println("(" + i + " / " + testCases.size() + ") Test on " + path);
			testSpoonifierWith(path, i++);
		}
	}

	@Test
	public void testSpoonifier() throws ClassNotFoundException, MalformedURLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		int i = 0;
		testSpoonifierWith("src/test/java/spoon/test/annotation/testclasses/Main.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/labels/testclasses/ManyLabels.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/api/testclasses/Bar.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/refactoring/parameter/testclasses/TestHierarchy.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/arrays/testclasses/VaragParam.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/arrays/testclasses/NewArrayWithComment.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/model/testclasses/AnonymousExecutableClass.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/fieldaccesses/testclasses/Panini.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/prettyprinter/testclasses/A.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/prettyprinter/testclasses/AClass.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/spoonifier/testclasses/ArrayRealVector.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/prettyprinter/testclasses/FooCasper.java", i++);
		testSpoonifierWith("src/test/java/spoon/test/prettyprinter/testclasses/Rule.java", i++);

	}

	/**
	 * This test is too long for CI, but it checks that SpoonifierVisitor does produce equivalent CtElement for all
	 * CtElement in Rule
	 */
	@Ignore
	@Test
	public void testSpoonifierElement() throws ClassNotFoundException, MalformedURLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		int i = 0;

		//Build the model of the given class
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/prettyprinter/testclasses/Rule.java");
		CtModel model = launcher.buildModel();
		List<CtType> l = model.getElements(new TypeFilter<CtType>(CtType.class));
		if (l.isEmpty()) return;
		CtType targetType = l.get(0);
		Iterator<CtElement> iterator =  targetType.descendantIterator();
		while (iterator.hasNext()) {
			testSpoonifierWith(iterator.next(),i++);
		}


	}

	/**
	 * @param pathToClass path to the class that will be Spoonified
	 * @param i an integer to avoid duplicated classes
	 *
	 * This method verifies that SpoonifyVisitor can generate code that replicates a class
	 */
	public void testSpoonifierWith(String pathToClass, int i) throws ClassNotFoundException, MalformedURLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		//Build the model of the given class
		Launcher launcher = new Launcher();
		launcher.addInputResource(pathToClass);
		CtModel model = launcher.buildModel();
		List<CtType> l = model.getElements(new TypeFilter<CtType>(CtType.class));
		if (l.isEmpty()) return;
		CtType targetType = l.get(0);
		testSpoonifierWith(targetType, i);
	}

	/**
	 * @param targetElement that will be Spoonified
	 * @param i an integer to avoid duplicated classes
	 *
	 * This method verifies that SpoonifyVisitor can generate code that replicates a class
	 */
	public void testSpoonifierWith(CtElement targetElement, int i)
			throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {


		//Spoonify
		String wrapper = generateSpoonifiyWrapper(targetElement, i);

		//System.out.println(wrapper);

		//Output launcher containing a class wrapping the generated code
		Launcher oLauncher = new Launcher();
		File outputBinDir = new File("./spooned-classes/");
		oLauncher.setBinaryOutputDirectory(outputBinDir);

		CtClass wrapperClass = oLauncher.parseClass(wrapper);
		CtModel oModel = oLauncher.buildModel();
		oLauncher.getEnvironment().disableConsistencyChecks();
		oModel.getRootPackage().addType(wrapperClass);
		oLauncher.getModelBuilder().compile(SpoonModelBuilder.InputType.CTTYPES);

		//Invoke the code generated
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{outputBinDir.toURI().toURL()});
		Class rtWrapper = urlClassLoader.loadClass("SpoonifierWrapper" + i);
		Method get = rtWrapper.getMethod("get", Factory.class);
		CtElement generatedElement = (CtElement) get.invoke(null, targetElement.getFactory());

		//contract: The element created by the code generated by SpoonifyVisitor is equivalent to the visited element
		assertEquals(targetElement, generatedElement);

		//contract: The original element and the sponified element leads to the same code once pretty printed
		//assertEquals(targetType.prettyprint(), generatedElement.prettyprint());
	}

	/*
	 * Generates a class with a single static method:
	 * public static CtElement get(Factory factory);
	 * This method calls the code generated by SpoonifyVisitor to
	 * recreate a CtElement equaled to the one visited.
	 */
	public String generateSpoonifiyWrapper(CtElement element, int i) {
		String elementClass = element.getClass().getSimpleName();
		if (elementClass.endsWith("Impl")) {
			elementClass = elementClass.replace("Impl","");
		}
		String variableName = elementClass.substring(0, 1).toLowerCase() + elementClass.substring(1) + "0";

		SpoonifyVisitor spoonifier = new SpoonifyVisitor();
		element.accept(spoonifier);
		StringBuffer buf = new StringBuffer();
		buf.append("import java.util.*;\n");
		buf.append("import spoon.reflect.code.*;\n");
		buf.append("import spoon.reflect.declaration.*;\n");
		buf.append("import spoon.reflect.factory.Factory;\n");
		buf.append("import spoon.reflect.path.CtRole;\n");
		buf.append("import spoon.reflect.reference.*;\n");
		buf.append("public class SpoonifierWrapper" + i + "{\n");
		buf.append("\tpublic static CtElement get(Factory factory) {\n");
		buf.append(spoonifier.getResult());
		buf.append("\treturn " + variableName + ";\n");
		buf.append("\t}\n");
		buf.append("}\n");

		return buf.toString();
	}


}
