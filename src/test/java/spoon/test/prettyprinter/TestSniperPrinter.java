/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.prettyprinter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.refactoring.Refactoring;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.modelobs.SourceFragmentCreator;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.prettyprinter.testclasses.OneLineMultipleVariableDeclaration;
import spoon.test.prettyprinter.testclasses.Throw;
import spoon.test.prettyprinter.testclasses.InvocationReplacement;
import spoon.test.prettyprinter.testclasses.ToBeChanged;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSniperPrinter {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testClassRename1() throws Exception {
		// contract: one can sniper out of the box after Refactoring.changeTypeName
		testClassRename(type -> {
			Refactoring.changeTypeName(type, "Bar");
		});
	}

	@Test
	public void testClassRename2() throws Exception {
		// contract: one can sniper after setSimpleName
		// with the necessary tweaks
		testClassRename(type -> {
			type.setSimpleName("Bar");
			type.getFactory().CompilationUnit().addType(type);
		});

	}

	public void testClassRename(Consumer<CtType<?>> renameTransfo) throws Exception {
		// contract: sniper supports class rename
		String testClass = ToBeChanged.class.getName();
		Launcher launcher = new Launcher();
		launcher.addInputResource(getResourcePath(testClass));
		launcher.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(launcher.getEnvironment());
		});
		launcher.setBinaryOutputDirectory(folder.newFolder());
		launcher.buildModel();
		Factory f = launcher.getFactory();

		final CtClass<?> type = f.Class().get(testClass);

		// performing the type rename
		renameTransfo.accept(type);
		//print the changed model
		launcher.prettyprint();


		String contentOfPrettyPrintedClassFromDisk = getContentOfPrettyPrintedClassFromDisk(type);
		assertTrue(contentOfPrettyPrintedClassFromDisk, contentOfPrettyPrintedClassFromDisk.contains("EOLs*/ Bar<T, K>"));

	}


	@Test
	public void testPrintInsertedThrow() {
		testSniper(Throw.class.getName(), type -> {
			CtConstructorCall<?> ctConstructorCall = (CtConstructorCall<?>) type.getMethodsByName("foo").get(0).getBody().getStatements().get(0);
			CtThrow ctThrow = type.getFactory().createCtThrow(ctConstructorCall.toString());
			ctConstructorCall.replace(ctThrow);
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed,
					"\\Qvoid foo(int x) {\n"
					+ "\t\tnew IllegalArgumentException(\"x must be nonnegative\");\n"
					+ "\t}",
					"void foo(int x) {\n"
					+ "\t\tthrow new java.lang.IllegalArgumentException(\"x must be nonnegative\");\n"
					+ "\t}");
		});
	}

	@Test
	public void testPrintReplacementOfInvocation() {
		testSniper(InvocationReplacement.class.getName(), type -> {
			CtLocalVariable<?> localVariable = (CtLocalVariable<?>) type.getMethodsByName("main").get(0).getBody().getStatements().get(0);
			CtInvocation<?> invocation = (CtInvocation<?>) localVariable.getAssignment();
			CtExpression<?> prevTarget = invocation.getTarget();
			CtCodeSnippetExpression<?> newTarget = type.getFactory().Code().createCodeSnippetExpression("Arrays");
			CtType<?> arraysClass = type.getFactory().Class().get(Arrays.class);
			CtMethod<?> method = (CtMethod<?>) arraysClass.getMethodsByName("toString").get(0);
			CtExecutableReference<?> refToMethod = type.getFactory().Executable().createReference(method);
			CtInvocation<?> newInvocation = type.getFactory().Code().createInvocation(newTarget, refToMethod, prevTarget);
			invocation.replace(newInvocation);
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\QString argStr = args.toString();", "String argStr = Arrays.toString(args);");
		});
	}

	@Test
	public void testPrintLocalVariableDeclaration() {
		// contract: joint local declarations can be sniper-printed in whole unmodified method
		testSniper(OneLineMultipleVariableDeclaration.class.getName(), type -> {
			type.getFields().stream().forEach(x -> { x.delete(); });
		}, (type, printed) -> {
			assertEquals("package spoon.test.prettyprinter.testclasses;\n"
					+	"\n"
					+	"public class OneLineMultipleVariableDeclaration {\n"
					+	"\n"
					+	"\tvoid foo(int a) {\n"
					+ "\t\tint b = 0, e = 1;\n"
					+ "\t\ta = a;\n"
					+	"\t}\n"
					+	"}", printed);
		});
	}

	@Test
	public void testPrintLocalVariableDeclaration2() {
		// contract: joint local declarations can be sniper-printed
		testSniper(OneLineMultipleVariableDeclaration.class.getName(), type -> {
			type.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0).delete();
		}, (type, printed) -> {
			assertEquals("package spoon.test.prettyprinter.testclasses;\n"
					+	"\n"
					+	"public class OneLineMultipleVariableDeclaration {int a;\n"
					+ "\n"
					+	"\tint c;\n"
					+	"\n"
					+ "\tvoid foo(int a) {int e = 1;\n"
					+ "\t\ta = a;\n"
					+ "\t}\n"
					+	"}", printed);
		});
	}

	@Test
	public void testPrintOneLineMultipleVariableDeclaration() {
		// contract: files with joint field declarations can be recompiled after sniper
		testSniper(OneLineMultipleVariableDeclaration.class.getName(), type -> {
			// we change something (anything would work)
			type.getMethodsByName("foo").get(0).delete();
		}, (type, printed) -> {
			assertEquals("package spoon.test.prettyprinter.testclasses;\n"
					+	"\n"
					+	"public class OneLineMultipleVariableDeclaration {int a;\n"
					+	"\n"
					+ "\tint c;\n"
					+ "}", printed);
		});
	}

	@Test
	public void testPrintUnchaged() {
		//contract: sniper printing of unchanged compilation unit returns origin sources
		testSniper(ToBeChanged.class.getName(), type -> {
			//do not change the model
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed);
		});
	}

	@Test
	public void testPrintAfterRenameOfField() {
		//contract: sniper printing after rename of field
		testSniper(ToBeChanged.class.getName(), type -> {
			//change the model
			type.getField("string").setSimpleName("modified");
		}, (type, printed) -> {
			// everything is the same but the field name
			assertIsPrintedWithExpectedChanges(type, printed, "\\bstring\\b", "modified");
		});
	}

	@Test
	public void testPrintChangedComplex() {
		//contract: sniper printing after remove of statement from nested complex `if else if ...`
		testSniper("spoon.test.prettyprinter.testclasses.ComplexClass", type -> {
			//find to be removed statement "bounds = false"
			CtStatement toBeRemoved = type.filterChildren((CtStatement stmt) -> stmt.getPosition().isValidPosition() && stmt.getPosition().getLine() == 231).first();

			// check that we have picked the right statement
			ChangeCollector.runWithoutChangeListener(type.getFactory().getEnvironment(), () -> {
				assertEquals("bounds = false", toBeRemoved.toStringDebug());
			});
			//change the model
			toBeRemoved.delete();
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\QNO_SUPERINTERFACES) {\n\\E\\s*bounds\\s*=\\s*false;\n", "NO_SUPERINTERFACES) {\n");
		});
	}

	@Test
	public void testPrintAfterRemoveOfFirstParameter() {
		//contract: sniper print after remove of first parameter
		testSniper(ToBeChanged.class.getName(), type -> {
			//delete first parameter of method `andSomeOtherMethod`
			type.getMethodsByName("andSomeOtherMethod").get(0).getParameters().get(0).delete();
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\s*int\\s*param1,", "");
		});
	}

	@Test
	public void testSimple() {
		//contract: sniper print after remove of last statement
		testSniper(spoon.test.prettyprinter.testclasses.Simple.class.getName(), type -> {
			//delete first parameter of method `andSomeOtherMethod`
			type.getMethodsByName("andSomeOtherMethod").get(0).getBody().getStatements().get(1).delete();
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\s*System.out.println\\(\"bbb\"\\);", "");
		});
	}

	@Test
	public void testPrintAfterRemoveOfMiddleParameter() {
		//contract: sniper print after remove of middle (not first and not last) parameter
		testSniper(ToBeChanged.class.getName(), type -> {
			//delete second parameter of method `andSomeOtherMethod`
			type.getMethodsByName("andSomeOtherMethod").get(0).getParameters().get(1).delete();
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\s*String\\s*param2\\s*,", "");
		});
	}

	@Test
	public void testPrintAfterRemoveOfLastParameter() {
		//contract: sniper print after remove of last parameter
		testSniper(ToBeChanged.class.getName(), type -> {
			//delete last parameter of method `andSomeOtherMethod`
			type.getMethodsByName("andSomeOtherMethod").get(0).getParameters().get(2).delete();
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\s*, \\QList<?>[][]... twoDArrayOfLists\\E", "");
		});
	}

	@Test
	public void testPrintAfterRemoveOfLastTypeMember() {
		//contract: sniper print after remove of last type member - check that suffix spaces are printed correctly
		testSniper(ToBeChanged.class.getName(), type -> {
			//delete first parameter of method `andSomeOtherMethod`
			type.getField("twoDArrayOfLists").delete();
		}, (type, printed) -> {
			assertIsPrintedWithExpectedChanges(type, printed, "\\Q\tList<?>[][] twoDArrayOfLists = new List<?>[7][];\n\\E", "");
		});
	}

	@Test
	public void testPrintAfterAddOfLastTypeMember() {
		//contract: sniper print after add of last type member - check that suffix spaces are printed correctly
		class Context {
			CtField<?> newField;
		}
		Context context = new Context();

		testSniper(ToBeChanged.class.getName(), type -> {
			Factory f = type.getFactory();
			//create new type member
			context.newField = f.createField(type, Collections.singleton(ModifierKind.PRIVATE), f.Type().DATE, "dateField");
			type.addTypeMember(context.newField);
		}, (type, printed) -> {
			String lastMemberString = "new List<?>[7][];";
			assertIsPrintedWithExpectedChanges(type, printed, "\\Q" + lastMemberString + "\\E", lastMemberString + "\n\n\t" + context.newField.toStringDebug());
		});
	}

	@Test
	public void testPrintAfterRemoveOfFormalTypeParamsAndChangeOfReturnType() {
		//contract: sniper printing after remove of formal type parameters and change of return type
		testSniper(ToBeChanged.class.getName(), type -> {
			//change the model
			CtMethod<?> m = type.getMethodsByName("andSomeOtherMethod").get(0);
			m.setFormalCtTypeParameters(Collections.emptyList());
			m.setType((CtTypeReference) m.getFactory().Type().stringType());
		}, (type, printed) -> {
			// everything is the same but method formal type params and return type
			assertIsPrintedWithExpectedChanges(type, printed, "\\Qpublic <T, K> void andSomeOtherMethod\\E", "public java.lang.String andSomeOtherMethod");
		});
	}

	@Test
	public void testPrintTypesProducesFullOutputForSingleTypeCompilationUnit() {
		// contract: printTypes() should produce the same output as launcher.prettyprint() for a
		// single-type compilation unit

		// there is no particular reason for using the YamlRepresenter resource, it simply already
		// existed and filled the role it needed to
		String resourceName = "visibility.YamlRepresenter";
		String inputPath = getResourcePath(resourceName);

		Launcher printTypesLauncher = createLauncherWithSniperPrinter();
		printTypesLauncher.addInputResource(inputPath);
		printTypesLauncher.buildModel();
		String printTypesString = printTypesLauncher.createPrettyPrinter()
				.printTypes(printTypesLauncher.getModel().getAllTypes().toArray(new CtType[0]));

		testSniper(resourceName, ctType -> {}, (type, prettyPrint) -> {
			assertEquals(prettyPrint, printTypesString);
		});
	}

	@Test
	public void testPrintTypesThrowsWhenPassedTypesFromMultipleCompilationUnits() {
		// contract: printTypes() should raise an IllegalArgumentException if it is passed types
		// from multiple CUs

		Launcher launcher = createLauncherWithSniperPrinter();
		// there is no particular reason for the choice of these two resources, other than that
		// they are different from each other and existed at the time of writing this test
		launcher.addInputResource(getResourcePath("visibility.YamlRepresenter"));
		launcher.addInputResource(getResourcePath("spoon.test.variable.Tacos"));
		CtType<?>[] types = launcher.buildModel().getAllTypes().toArray(new CtType<?>[0]);

		try {
			launcher.getEnvironment().createPrettyPrinter().printTypes(types);
			fail("Expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		    // pass
		}
	}

	@Test
	public void testAddedImportStatementPlacedOnSeparateLineInFileWithoutPackageStatement() {
		// contract: newline must be inserted between import statements when a new one is added

		Consumer<CtType<?>> addArrayListImport = type -> {
			Factory factory = type.getFactory();
			assertTrue("there should be no package statement in this test file", type.getPackage().isUnnamedPackage());
			CtCompilationUnit cu = factory.CompilationUnit().getOrCreate(type);
			CtTypeReference<?> arrayListRef = factory.Type().get(java.util.ArrayList.class).getReference();
			cu.getImports().add(factory.createImport(arrayListRef));
		};
		BiConsumer<CtType<?>, String> assertImportsPrintedCorrectly = (type, result) -> {
			assertThat(result, anyOf(
					containsString("import java.util.Set;\nimport java.util.ArrayList;\n"),
					containsString("import java.util.ArrayList;\nimport java.util.Set;\n")));
		};

		testSniper("ClassWithSingleImport", addArrayListImport, assertImportsPrintedCorrectly);
	}

	@Test
	public void testAddedImportStatementPlacedOnSeparateLineInFileWithPackageStatement() {
		// contract: newline must be inserted both before and after a new import statement if ther
		// is a package statement in the file

		Consumer<CtType<?>> addArrayListImport = type -> {
			Factory factory = type.getFactory();
			assertFalse("there should be a package statement in this test file", type.getPackage().isUnnamedPackage());
			CtCompilationUnit cu = factory.CompilationUnit().getOrCreate(type);
			CtTypeReference<?> arrayListRef = factory.Type().get(java.util.ArrayList.class).getReference();
			cu.getImports().add(factory.createImport(arrayListRef));
		};
		BiConsumer<CtType<?>, String> assertImportsPrintedCorrectly = (type, result) -> {
			assertThat(result, containsString("\nimport java.util.ArrayList;\n"));
		};

		testSniper("visibility.YamlRepresenter", addArrayListImport, assertImportsPrintedCorrectly);
	}

	/**
	 * 1) Runs spoon using sniper mode,
	 * 2) runs `typeChanger` to modify the code,
	 * 3) runs `resultChecker` to check if sources printed by sniper printer are as expected
	 * @param testClass a file system path to test class
	 * @param transformation a code which changes the Spoon model
	 * @param resultChecker a code which checks that printed sources are as expected
	 */
	private void testSniper(String testClass, Consumer<CtType<?>> transformation, BiConsumer<CtType<?>, String> resultChecker) {
		Launcher launcher = createLauncherWithSniperPrinter();
		launcher.addInputResource(getResourcePath(testClass));
		launcher.buildModel();
		Factory f = launcher.getFactory();

		final CtClass<?> ctClass = f.Class().get(testClass);

		//change the model
		transformation.accept(ctClass);

		//print the changed model
		launcher.prettyprint();

		//check the printed file
		resultChecker.accept(ctClass, getContentOfPrettyPrintedClassFromDisk(ctClass));
	}

	private static Launcher createLauncherWithSniperPrinter() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(launcher.getEnvironment());
		});
		return launcher;
	}

	private String getContentOfPrettyPrintedClassFromDisk(CtType<?> type) {
		File outputFile = getFileForType(type);

		byte[] content = new byte[(int) outputFile.length()];
		try (InputStream is = new FileInputStream(outputFile)) {
			is.read(content);
		} catch (IOException e) {
			throw new RuntimeException("Reading of " + outputFile.getAbsolutePath() + " failed", e);
		}
		try {
			return new String(content, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private File getFileForType(CtType<?> type) {
		File outputDir = type.getFactory().getEnvironment().getSourceOutputDirectory();
		return new File(outputDir, type.getQualifiedName().replace('.', '/') + ".java");
	}

	private static String getResourcePath(String className) {
		String r = "./src/test/java/" + className.replaceAll("\\.", "/") + ".java";
		if (new File(r).exists()) {
			return r;
		}
		r = "./src/test/resources/" + className.replaceAll("\\.", "/") + ".java";
		if (new File(r).exists()) {
			return r;
		}
		throw new RuntimeException("Resource of class " + className + " doesn't exist");
	}

	/**
	 * checks that printed code contains only expected changes
	 */
	private void assertIsPrintedWithExpectedChanges(CtType<?> ctClass, String printedSource, String... regExpReplacements) {
		assertEquals(0, regExpReplacements.length % 2);
		String originSource = ctClass.getPosition().getCompilationUnit().getOriginalSourceCode();
		//apply all expected replacements using Regular expressions
		int nrChanges = regExpReplacements.length / 2;
		for (int i = 0; i < nrChanges; i++) {
			String str = regExpReplacements[i];
			String replacement = regExpReplacements[i * 2 + 1];
			originSource = originSource.replaceAll(str, replacement);
		}
		//check that origin sources which expected changes are equal to printed sources
		assertEquals(originSource, printedSource);
	}


	private static String fileAsString(String path, Charset encoding)
			throws IOException	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public void testToStringWithSniperPrinter(String inputSourcePath) throws Exception {

		final Launcher launcher = new Launcher();
		launcher.addInputResource(inputSourcePath);
		String originalContent = fileAsString(inputSourcePath, StandardCharsets.UTF_8).replace("\t", "");
		CtModel model = launcher.buildModel();

		new SourceFragmentCreator().attachTo(launcher.getFactory().getEnvironment());

		final SniperJavaPrettyPrinter sp = new SniperJavaPrettyPrinter(launcher.getEnvironment());

		launcher.getEnvironment().setPrettyPrinterCreator(
				() -> {
					return sp;
				}
		);
		List<CtElement> ops = model.getElements(new TypeFilter<>(CtElement.class));


		ops.stream()
				.filter(el -> !(el instanceof spoon.reflect.CtModelImpl.CtRootPackage)
				&& !(el instanceof spoon.reflect.factory.ModuleFactory.CtUnnamedModule)
				).forEach(el -> {
			try {
				sp.reset();
				sp.printElementSniper(el);
				//Contract, calling toString on unmodified AST elements should draw only from original.
				String result = sp.getResult();

				if (!SniperJavaPrettyPrinter.hasImplicitAncestor(el) && !(el instanceof CtPackage) && !(el instanceof CtReference)) {
					assertTrue(result.length() > 0);
				}

				assertTrue("ToString() on element (" + el.getClass().getName() + ") =  \"" + el + "\" is not in original content",
						originalContent.contains(result.replace("\t", "")));
			} catch (UnsupportedOperationException | SpoonException e) {
				//Printer should not throw exception on printable element. (Unless there is a bug in the printer...)
				fail("ToString() on Element (" + el.getClass().getName() + "): at " + el.getPath() + " lead to an exception: " + e);
			}
		});
	}

	@Test
	public void testToStringWithSniperOnElementScan() throws Exception {
		testToStringWithSniperPrinter("src/test/java/spoon/test/prettyprinter/testclasses/ElementScan.java");
	}

}
