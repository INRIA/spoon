/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
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

		//order problem in map container...
		testCases.remove("src/test/java/spoon/test/annotation/testclasses/Main.java");

		int i = 0;
		for (String path: testCases) {
			System.out.println("(" + i + " / " + testCases.size() + ") Test on " + path);
			testSpoonifierWith(path, i++);
		}
	}

	@Test
	public void testSpoonifier() throws ClassNotFoundException, MalformedURLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		int i = 0;
		//testSpoonifierWith("src/test/java/spoon/test/annotation/testclasses/Main.java", i++);
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

	@Test
	public void testGeneratedSpoonifyCode() {

		CtClass aClass = Launcher.parseClass("public class A {int i = 1+1; static Object m(String toto) { return toto;}}");
		SpoonifyVisitor v = new SpoonifyVisitor();
		aClass.accept(v);

		String expected = "\tCtClass ctClass0 = factory.createClass();\n" +
				"\tctClass0.setSimpleName(\"A\");\n" +
				"\tSet<ModifierKind> ctClass0Modifiers = new HashSet<>();\n" +
				"\tctClass0Modifiers.add(ModifierKind.PUBLIC);\n" +
				"\tctClass0.setModifiers(ctClass0Modifiers);\n" +
				"\t\tCtConstructor ctConstructor0 = factory.createConstructor();\n" +
				"\t\tctConstructor0.setImplicit(true);\n" +
				"\t\tctConstructor0.setSimpleName(\"<init>\");\n" +
				"\t\tSet<ModifierKind> ctConstructor0Modifiers = new HashSet<>();\n" +
				"\t\tctConstructor0Modifiers.add(ModifierKind.PUBLIC);\n" +
				"\t\tctConstructor0.setModifiers(ctConstructor0Modifiers);\n" +
				"\t\tList ctClass0TypeMembers = new ArrayList();\n" +
				"\t\tctClass0TypeMembers.add(ctConstructor0);\n" +
				"\t\t\tCtBlock ctBlock0 = factory.createBlock();\n" +
				"\t\t\tctConstructor0.setValueByRole(CtRole.BODY, ctBlock0);\n" +
				"\t\t\t\tCtInvocation ctInvocation0 = factory.createInvocation();\n" +
				"\t\t\t\tctInvocation0.setImplicit(true);\n" +
				"\t\t\t\tList ctBlock0Statements = new ArrayList();\n" +
				"\t\t\t\tctBlock0Statements.add(ctInvocation0);\n" +
				"\t\t\t\t\tCtExecutableReference ctExecutableReference0 = factory.createExecutableReference();\n" +
				"\t\t\t\t\tctExecutableReference0.setSimpleName(\"<init>\");\n" +
				"\t\t\t\t\tctInvocation0.setValueByRole(CtRole.EXECUTABLE_REF, ctExecutableReference0);\n" +
				"\t\t\t\t\t\tctExecutableReference0.setValueByRole(CtRole.DECLARING_TYPE, factory.Type().createReference(\"java.lang.Object\"));\n" +
				"\t\t\t\t\t\tctExecutableReference0.setValueByRole(CtRole.TYPE, factory.Type().createReference(\"java.lang.Object\"));\n" +
				"\t\t\tctBlock0.setValueByRole(CtRole.STATEMENT, ctBlock0Statements);\n" +
				"\t\tCtField ctField0 = factory.createField();\n" +
				"\t\tctField0.setSimpleName(\"i\");\n" +
				"\t\tctClass0TypeMembers.add(ctField0);\n" +
				"\t\t\tctField0.setValueByRole(CtRole.TYPE, factory.Type().INTEGER_PRIMITIVE);\n" +
				"\t\t\tCtBinaryOperator ctBinaryOperator0 = factory.createBinaryOperator();\n" +
				"\t\t\tctBinaryOperator0.setKind(BinaryOperatorKind.PLUS);\n" +
				"\t\t\tctField0.setValueByRole(CtRole.DEFAULT_EXPRESSION, ctBinaryOperator0);\n" +
				"\t\t\t\tctBinaryOperator0.setValueByRole(CtRole.TYPE, factory.Type().INTEGER_PRIMITIVE);\n" +
				"\t\t\t\tCtLiteral ctLiteral0 = factory.createLiteral();\n" +
				"\t\t\t\tctLiteral0.setValue((int) 1);\n" +
				"\t\t\t\tctLiteral0.setBase(LiteralBase.DECIMAL);\n" +
				"\t\t\t\tctBinaryOperator0.setValueByRole(CtRole.LEFT_OPERAND, ctLiteral0);\n" +
				"\t\t\t\t\tctLiteral0.setValueByRole(CtRole.TYPE, factory.Type().INTEGER_PRIMITIVE);\n" +
				"\t\t\t\tCtLiteral ctLiteral1 = factory.createLiteral();\n" +
				"\t\t\t\tctLiteral1.setValue((int) 1);\n" +
				"\t\t\t\tctLiteral1.setBase(LiteralBase.DECIMAL);\n" +
				"\t\t\t\tctBinaryOperator0.setValueByRole(CtRole.RIGHT_OPERAND, ctLiteral1);\n" +
				"\t\t\t\t\tctLiteral1.setValueByRole(CtRole.TYPE, factory.Type().INTEGER_PRIMITIVE);\n" +
				"\t\tCtMethod ctMethod0 = factory.createMethod();\n" +
				"\t\tctMethod0.setSimpleName(\"m\");\n" +
				"\t\tSet<ModifierKind> ctMethod0Modifiers = new HashSet<>();\n" +
				"\t\tctMethod0Modifiers.add(ModifierKind.STATIC);\n" +
				"\t\tctMethod0.setModifiers(ctMethod0Modifiers);\n" +
				"\t\tctClass0TypeMembers.add(ctMethod0);\n" +
				"\t\t\tctMethod0.setValueByRole(CtRole.TYPE, factory.Type().createSimplyQualifiedReference(\"java.lang.Object\"));\n" +
				"\t\t\tCtParameter ctParameter0 = factory.createParameter();\n" +
				"\t\t\tctParameter0.setSimpleName(\"toto\");\n" +
				"\t\t\tList ctMethod0Parameters = new ArrayList();\n" +
				"\t\t\tctMethod0Parameters.add(ctParameter0);\n" +
				"\t\t\t\tctParameter0.setValueByRole(CtRole.TYPE, factory.Type().createSimplyQualifiedReference(\"java.lang.String\"));\n" +
				"\t\t\tCtBlock ctBlock1 = factory.createBlock();\n" +
				"\t\t\tctMethod0.setValueByRole(CtRole.BODY, ctBlock1);\n" +
				"\t\t\t\tCtReturn ctReturn0 = factory.createReturn();\n" +
				"\t\t\t\tList ctBlock1Statements = new ArrayList();\n" +
				"\t\t\t\tctBlock1Statements.add(ctReturn0);\n" +
				"\t\t\t\t\tCtVariableRead ctVariableRead0 = factory.createVariableRead();\n" +
				"\t\t\t\t\tctReturn0.setValueByRole(CtRole.EXPRESSION, ctVariableRead0);\n" +
				"\t\t\t\t\t\tCtParameterReference ctParameterReference0 = factory.createParameterReference();\n" +
				"\t\t\t\t\t\tctParameterReference0.setSimpleName(\"toto\");\n" +
				"\t\t\t\t\t\tctVariableRead0.setValueByRole(CtRole.VARIABLE, ctParameterReference0);\n" +
				"\t\t\t\t\t\t\tctParameterReference0.setValueByRole(CtRole.TYPE, factory.Type().createReference(\"java.lang.String\"));\n" +
				"\t\t\tctBlock1.setValueByRole(CtRole.STATEMENT, ctBlock1Statements);\n" +
				"\t\tctMethod0.setValueByRole(CtRole.PARAMETER, ctMethod0Parameters);\n" +
				"\tctClass0.setValueByRole(CtRole.TYPE_MEMBER, ctClass0TypeMembers);\n";

		//non regression
		assertEquals(expected, v.getResult());
	}


}
