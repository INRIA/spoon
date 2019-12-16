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
package spoon.processing;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.DefaultImportComparator;
import spoon.reflect.visitor.ForceImportProcessor;
import spoon.reflect.visitor.ImportCleaner;
import spoon.reflect.visitor.ImportConflictDetector;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.processing.processors.MyProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class ProcessingTest {

	@Test
	public void testInterruptAProcessor() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/processing/");
		final MyProcessor processor = new MyProcessor();
		launcher.addProcessor(processor);
		try {
			launcher.run();
		} catch (ProcessInterruption e) {
			fail("ProcessInterrupt exception must be catch in the ProcessingManager.");
		}
		assertFalse(processor.isShouldStayAtFalse());
	}

	@Test
	public void testSpoonTagger() {
		final Launcher launcher = new Launcher();
		launcher.addProcessor("spoon.processing.SpoonTagger");
		launcher.run();
		assertTrue(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/Spoon.java").exists());
	}

	private static class SimpleProcessor extends AbstractProcessor<CtType<?>> {
		@Override
		public void process(CtType<?> element) {
			System.out.println(">> Hello: " + element.getSimpleName() + " <<");
		}
	}

	@Test
	public void testStaticImport() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		String[] sourcePath = new String[0];
		e.setNoClasspath(false);
		e.setSourceClasspath(sourcePath);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/compilation3/A.java");
		l.addInputResource("src/test/resources/compilation3/subpackage/B.java");
		l.setSourceOutputDirectory(path.toFile());
		SimpleProcessor simpleProcessor = new SimpleProcessor();
		l.addProcessor(simpleProcessor);
		l.run();
	}

	private static class SimpleProcessor2 extends AbstractProcessor<CtIf> {
		@Override
		public void process(CtIf element) {
			CtExpression condition = element.getCondition();
			CtExpression rightExpression = ((CtBinaryOperator) condition).getRightHandOperand();
			CtExpression leftExpression = ((CtBinaryOperator) condition).getLeftHandOperand();
			System.out.println(rightExpression.toString());
			System.out.println(leftExpression.toString());
		}
	}

	private static class SimpleProcessor3 extends AbstractProcessor<CtLambda> {
		@Override
		public void process(CtLambda element) {
			System.out.println(element.toString());
		}
	}

	@Test
	public void testMissingSourceFragmentContext() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		String[] sourcePath = new String[0];
		e.setNoClasspath(false);
		e.setSourceClasspath(sourcePath);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/compilation4/A.java");
		l.addInputResource("src/test/resources/compilation4/B.java");
		l.setSourceOutputDirectory(path.toFile());
		SimpleProcessor2 simpleProcessor2 = new SimpleProcessor2();
		l.addProcessor(simpleProcessor2);
		SimpleProcessor3 simpleProcessor3 = new SimpleProcessor3();
		l.addProcessor(simpleProcessor3);
		l.run();
	}

	private static class SimpleProcessor4 extends AbstractProcessor<CtMethod> {
		@Override
		public void process(CtMethod element) {
			if(element.getSimpleName().equals("onDraw")) {
				element.getBody().insertEnd(element.getFactory().createCodeSnippetStatement("canvas.clear()"));

			}
		}
	}

	@Test
	public void testBrokenIndentation() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		e.setNoClasspath(true);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/refactoring1/A.java");
		l.setSourceOutputDirectory(path.toFile());
		SimpleProcessor4 simpleProcessor4 = new SimpleProcessor4();
		l.addProcessor(simpleProcessor4);
		l.run();
		String text = new String(Files.readAllBytes(Paths.get(path.toAbsolutePath() + "/refactoring/A.java")), StandardCharsets.UTF_8);
		System.out.println(text);
	}

	private static class SimpleProcessor5 extends AbstractProcessor<CtClass> {

		class Meta {
			CtConstructorCall constructorCall; CtStatement statement; CtClass ctClass; CtVariableReference variableReference;
			public Meta(CtConstructorCall constructorCall, CtStatement statement, CtClass ctClass, CtVariableReference variableReference) {
				this.constructorCall = constructorCall;
				this.statement = statement;
				this.ctClass = ctClass;
				this.variableReference = variableReference;
			}
		}

		List<Meta> metaList = new ArrayList<>();

		public static boolean isClearable(CtTypeReference type) {
			return type.getSimpleName().startsWith("Collection") || type.getSimpleName().startsWith("java.util.List")
					|| type.getSimpleName().startsWith("List") || type.getSimpleName().startsWith("java.util.List")
					|| type.getSimpleName().startsWith("ArrayList") || type.getSimpleName().startsWith("java.util.ArrayList")
					|| type.getSimpleName().startsWith("LinkedList") || type.getSimpleName().startsWith("java.util.LinkedList")
					|| type.getSimpleName().startsWith("Vector") || type.getSimpleName().startsWith("java.util.Vector")
					|| type.getSimpleName().startsWith("Stack") || type.getSimpleName().startsWith("java.util.Stack")
					|| type.getSimpleName().startsWith("Set") || type.getSimpleName().startsWith("java.util.Set")
					|| type.getSimpleName().startsWith("HashSet") || type.getSimpleName().startsWith("java.util.HashSet")
					|| type.getSimpleName().startsWith("LinkedHashSet") || type.getSimpleName().startsWith("java.util.LinkedHashSet")
					|| type.getSimpleName().startsWith("SortedSet") || type.getSimpleName().startsWith("java.util.SortedSet")
					|| type.getSimpleName().startsWith("NavigableSet") || type.getSimpleName().startsWith("java.util.NavigableSet")
					|| type.getSimpleName().startsWith("TreeSet") || type.getSimpleName().startsWith("java.util.TreeSet")
					|| type.getSimpleName().startsWith("EnumSet") || type.getSimpleName().startsWith("java.util.EnumSet")
					|| type.getSimpleName().startsWith("Queue") || type.getSimpleName().startsWith("java.util.Queue")
					|| type.getSimpleName().startsWith("PriorityQueue") || type.getSimpleName().startsWith("java.util.PriorityQueue")
					|| type.getSimpleName().startsWith("Deque") || type.getSimpleName().startsWith("java.util.Deque")
					|| type.getSimpleName().startsWith("ArrayDeque") || type.getSimpleName().startsWith("java.util.ArrayDeque")
					|| type.getSimpleName().startsWith("Map") || type.getSimpleName().startsWith("java.util.Map")
					|| type.getSimpleName().startsWith("HashMap") || type.getSimpleName().startsWith("java.util.HashMap")
					|| type.getSimpleName().startsWith("SortedMap") || type.getSimpleName().startsWith("java.util.SortedMap")
					|| type.getSimpleName().startsWith("NavigableMap") || type.getSimpleName().startsWith("java.util.NavigableMap")
					|| type.getSimpleName().startsWith("TreeMap") || type.getSimpleName().startsWith("java.util.TreeMap");
		}

		private void detectCase(CtStatement statement, CtClass ctClass) {
			CtExpression assignmentExpression;
			CtVariableReference variableReference;
			if(statement instanceof CtVariable) {
				assignmentExpression = ((CtVariable)statement).getDefaultExpression();
				variableReference = ((CtVariable)statement).getReference();
			} else if (statement instanceof CtAssignment) {
				CtAssignment assignment = (CtAssignment)statement;
				assignmentExpression = assignment.getAssignment();
				CtExpression assignedExpression = assignment.getAssigned();
				if(!(assignedExpression instanceof CtVariableWrite)) {
					return;
				}
				variableReference = ((CtVariableWrite) assignedExpression).getVariable();
			} else {
				return;
			}

			CtConstructorCall constructorCall;
			if(assignmentExpression instanceof CtConstructorCall) {
				constructorCall = (CtConstructorCall) assignmentExpression;
			} else {
				return;
			}

			metaList.add(new Meta(constructorCall, statement, ctClass, variableReference));
		}

		private void refactorCase(CtConstructorCall constructorCall, CtStatement statement, CtClass ctClass, CtVariableReference variableReference) {
			// Remove allocation that depend on other variables
			List<CtExpression<?>> expressionList = constructorCall.getArguments();
			for(CtExpression expression : expressionList) {
				if(expression instanceof CtVariableRead) {
					return;
				}
			}

			if(statement instanceof CtVariable) {
				// There is a viewHolder - Check if the field is inside, create it if necessary
				List<CtField<?>> fields = ctClass.getFields();
				Optional<CtField<?>> optionalField = fields.stream().filter(field -> field.getSimpleName()
						.equals(variableReference.getSimpleName())).findFirst();
				if(optionalField.isPresent() && !optionalField.get().getType().getSimpleName()
						.equals(variableReference.getType().getSimpleName())) {
					// If types do not match we ignore for now.
					return;
				}
				if(!optionalField.isPresent()) {
					CtTypeReference typeReference = variableReference.getType();
					CtField field = ctClass.getFactory().createCtField(variableReference.getSimpleName(), typeReference,
							constructorCall.toString(), ModifierKind.PRIVATE);
					ctClass.addField(field);
					if(isClearable(typeReference)) {
						statement.insertBefore(
								ctClass.getFactory().createCodeSnippetStatement(
										variableReference.getSimpleName() + ".clear()"));
					}
					((CtBlock)statement.getParent()).removeStatement(statement);
				}
			}
		}

		private void refactor(CtMethod method, CtClass element) {
			if (!method.getSimpleName().equals("onDraw")) {
				return;
			}
			method.getBody().getStatements().iterator()
					.forEachRemaining(statement -> detectCase(statement, element));
			metaList.iterator()
					.forEachRemaining((Meta meta) -> refactorCase(meta.constructorCall, meta.statement, meta.ctClass, meta.variableReference));
		}

		@Override
		public void process(CtClass element) {
			Set methods = element.getMethods();
			for (Object method : methods) {
				if (method instanceof CtMethod) {
					refactor((CtMethod) method, element);
				}
			}
		}
	}

	@Test
	public void testNoInlinePackage() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		e.setNoClasspath(true);
		e.setAutoImports(false);
		e.setPrettyPrinterCreator(() -> {
			SniperJavaPrettyPrinter sniperJavaPrettyPrinter = new SniperJavaPrettyPrinter(l.getEnvironment());
			return sniperJavaPrettyPrinter;});

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/refactoring1/A.java");
		l.setSourceOutputDirectory(path.toFile());
		SimpleProcessor5 simpleProcessor5 = new SimpleProcessor5();
		l.addProcessor(simpleProcessor5);
		l.run();
		File directory = new File("src/test/resources/refactoring1");
		String text = new String(Files.readAllBytes(Paths.get(path.toAbsolutePath() + "/refactoring/A.java")), StandardCharsets.UTF_8);
		String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/refactoring1/expected1.txt")), StandardCharsets.UTF_8);
		assertEquals(expected, text);
	}
}
