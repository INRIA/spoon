/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.prettyprinter;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.refactoring.Refactoring;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.adaption.TypeAdaptor;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.modelobs.SourceFragmentCreator;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.prettyprinter.testclasses.OneLineMultipleVariableDeclaration;
import spoon.test.prettyprinter.testclasses.RefactorCast;
import spoon.test.prettyprinter.testclasses.Throw;
import spoon.test.prettyprinter.testclasses.InvocationReplacement;
import spoon.test.prettyprinter.testclasses.ToBeChanged;
import spoon.testing.utils.GitHubIssue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestSniperPrinter {

	@Test
	public void testClassRename1(@TempDir File tempDir) throws Exception {
		// contract: one can sniper out of the box after Refactoring.changeTypeName
		testClassRename(tempDir, type -> {
			Refactoring.changeTypeName(type, "Bar");
		});
	}

	@Test
	public void testClassRename2(@TempDir File tempDir) throws Exception {
		// contract: one can sniper after setSimpleName
		// with the necessary tweaks
		testClassRename(tempDir, type -> {
			type.setSimpleName("Bar");
			type.getPosition().getCompilationUnit().getDeclaredTypeReferences().get(0).setSimpleName("Bar");
		});

	}

	public void testClassRename(File tempdir, Consumer<CtType<?>> renameTransfo) throws Exception {
		// contract: sniper supports class rename
		String testClass = ToBeChanged.class.getName();
		Launcher launcher = new Launcher();
		launcher.addInputResource(getResourcePath(testClass));
		launcher.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(launcher.getEnvironment());
		});
		launcher.setBinaryOutputDirectory(tempdir);
		launcher.buildModel();
		Factory f = launcher.getFactory();

		final CtClass<?> type = f.Class().get(testClass);
		String original = type.getPosition().getCompilationUnit().getOriginalSourceCode();

		// performing the type rename
		renameTransfo.accept(type);
		//print the changed model
		launcher.prettyprint();

		String expected = original.replaceAll("\\bToBeChanged\\b", "Bar");

		String contentOfPrettyPrintedClassFromDisk = getContentOfPrettyPrintedClassFromDisk(type);
		assertEquals(expected, contentOfPrettyPrintedClassFromDisk);

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
			context.newField = f.createField(type, Collections.singleton(ModifierKind.PRIVATE), f.Type().dateType(), "dateField");
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
	public void testCalculateCrashesWithInformativeMessageWhenSniperPrinterSetAfterModelBuild() {
		// contract: The sniper printer must be set before building the model, and the error message
		// one gets when this has not been done should say so.

		Launcher launcher = new Launcher();
		launcher.addInputResource(getResourcePath("visibility.YamlRepresenter"));

		// build model, then set sniper
		launcher.buildModel();
		launcher.getEnvironment().setPrettyPrinterCreator(
				() -> new SniperJavaPrettyPrinter(launcher.getEnvironment()));

		CtCompilationUnit cu = launcher.getFactory().CompilationUnit().getMap().values().stream()
				.findFirst().get();

		// crash because sniper was set after model was built, and so the ChangeCollector was not
		// attached to the environment
		try {
			launcher.createPrettyPrinter().calculate(cu, cu.getDeclaredTypes());
		} catch (SpoonException e) {
			assertThat(e.getMessage(), containsString(
					"This typically means that the Sniper printer was set after building the model."));
			assertThat(e.getMessage(), containsString(
					"It must be set before building the model."));
		}
	}

	@Test
	public void testWhitespacePrependedToFieldAddedAtTop() {
		// contract: newline and indentation must be inserted before a field that's added to the top
		// of a class body when the class already has other type members.

		Consumer<CtType<?>> addFieldAtTop = type -> {
			Factory fact = type.getFactory();
			CtField<?> field = fact.createCtField(
					"newFieldAtTop", fact.Type().integerPrimitiveType(), "2");
			type.addFieldAtTop(field);
		};

		final String expectedFieldSource = "int newFieldAtTop = 2;";
		BiConsumer<CtType<?>, String> assertTopAddedFieldOnSeparateLine = (type, result) ->
				assertThat(result, containsString("{\n    " + expectedFieldSource));

		// it doesn't matter which test resource is used, as long as it has a non-empty class
		String nonEmptyClass = "TypeMemberComments";
		testSniper(nonEmptyClass, addFieldAtTop, assertTopAddedFieldOnSeparateLine);
	}

	@Test
	public void testWhitespacePrependedToNestedClassAddedAtTop() {
		// contract: newline and indentation must be inserted before a nested class that's added to
		// the top of a class body when the class already has other type members.

		Consumer<CtType<?>> addNestedClassAtTop = type -> {
			CtClass<?> nestedClass = type.getFactory().createClass("Nested");
			type.addTypeMemberAt(0, nestedClass);
		};

		final String expectedClassSource = "class Nested {}";
		BiConsumer<CtType<?>, String> assertTopAddedClassOnSeparateLine = (type, result) ->
				assertThat(result, containsString("{\n    " + expectedClassSource));

		// it doesn't matter which test resource is used, as long as it has a non-empty class
		String nonEmptyClass = "TypeMemberComments";
		testSniper(nonEmptyClass, addNestedClassAtTop, assertTopAddedClassOnSeparateLine);
	}

	@Test
	public void testWhitespacePrependedToLocalVariableAddAtTopOfNonEmptyMethod() {
		// contract: newline and indentation must be inserted before a local variable that's added
		// to the top of a non-empty statement list.

		Consumer<CtType<?>> addLocalVariableAtTopOfMethod = type -> {
			Factory factory = type.getFactory();
			CtMethod<?> method = type.getMethods().stream()
					.filter(m -> !m.getBody().getStatements().isEmpty())
					.findFirst()
					.get();
			CtLocalVariable<?> localVar = factory.createLocalVariable(
				factory.Type().integerPrimitiveType(), "localVar", factory.createCodeSnippetExpression("2"));
			method.getBody().addStatement(0, localVar);
		};

		final String expectedVariableSource = "int localVar = 2;";
		BiConsumer<CtType<?>, String> assertTopAddedVariableOnSeparateLine = (type, result) ->
				assertThat(result, containsString("{\n        " + expectedVariableSource));

		// the test resource must have a class with a non-empty method
		String classWithNonEmptyMethod = "methodimport.ClassWithStaticMethod";
		testSniper(classWithNonEmptyMethod, addLocalVariableAtTopOfMethod, assertTopAddedVariableOnSeparateLine);
	}

	@Test
	public void testNewlineInsertedBetweenCommentAndTypeMemberWithAddedModifier() {
		// contract: newline must be inserted after comment when a succeeding type member has had a
		// modifier added to it

		Consumer<CtType<?>> addModifiers = type -> {
			type.getField("NON_FINAL_FIELD")
					.addModifier(ModifierKind.FINAL);
			type.getMethod("nonStaticMethod").addModifier(ModifierKind.STATIC);
			type.getNestedType("NonStaticInnerClass").addModifier(ModifierKind.STATIC);
		};
		BiConsumer<CtType<?>, String> assertCommentsCorrectlyPrinted = (type, result) -> {
		    assertThat(result, containsString("// field comment\n"));
			assertThat(result, containsString("// method comment\n"));
			assertThat(result, containsString("// nested type comment\n"));
		};

		testSniper("TypeMemberComments", addModifiers, assertCommentsCorrectlyPrinted);
	}

	@Test
	public void testNewlineInsertedBetweenCommentAndTypeMemberWithRemovedModifier() {
		// contract: newline must be inserted after comment when a succeeding field has had a
		// modifier removed from it

		Consumer<CtType<?>> removeModifier = type -> {
			// we only test removing a modifier from the field in this test, as removing the
			// last modifier leads to a different corner case where the comment disappears
			// altogether
			type.getField("NON_FINAL_FIELD")
					.removeModifier(ModifierKind.PUBLIC);
		};

		BiConsumer<CtType<?>, String> assertCommentCorrectlyPrinted = (type, result) -> {
			assertThat(result, containsString("// field comment\n"));
		};

		testSniper("TypeMemberComments", removeModifier, assertCommentCorrectlyPrinted);
	}

	@Test
	public void testNewlineInsertedBetweenModifiedCommentAndTypeMemberWithAddedModifier() {
		// contract: newline must be inserted after modified comment when a succeeding type member
		// has had its modifier list modified. We test modified comments separately from
		// non-modified comments as they are handled differently in the printer.

		final String commentContent = "modified comment";

		Consumer<CtType<?>> enactModifications = type -> {
			CtField<?> field = type.getField("NON_FINAL_FIELD");
			field.addModifier(ModifierKind.FINAL);
			field.getComments().get(0).setContent(commentContent);
		};

		BiConsumer<CtType<?>, String> assertCommentCorrectlyPrinted = (type, result) -> {
			assertThat(result, containsString("// " + commentContent + "\n"));
		};

		testSniper("TypeMemberComments", enactModifications, assertCommentCorrectlyPrinted);
	}

	@Test
	public void testTypeMemberCommentDoesNotDisappearWhenAllModifiersAreRemoved() {
		// contract: A comment on a field should not disappear when all of its modifiers are removed.

		Consumer<CtType<?>> removeTypeMemberModifiers = type -> {
			type.getField("NON_FINAL_FIELD").setModifiers(Collections.emptySet());
			type.getMethodsByName("nonStaticMethod").get(0).setModifiers(Collections.emptySet());
			type.getNestedType("NonStaticInnerClass").setModifiers(Collections.emptySet());
		};

		BiConsumer<CtType<?>, String> assertFieldCommentPrinted = (type, result) ->
			assertThat(result, allOf(
						containsString("// field comment\n    int NON_FINAL_FIELD"),
						containsString("// method comment\n    void nonStaticMethod"),
						containsString("// nested type comment\n    class NonStaticInnerClass")
					)
			);

		testSniper("TypeMemberComments", removeTypeMemberModifiers, assertFieldCommentPrinted);
	}

	@Test
	public void testAddedImportStatementPlacedOnSeparateLineInFileWithoutPackageStatement() {
		// contract: newline must be inserted between import statements when a new one is added

		Consumer<CtType<?>> addArrayListImport = type -> {
			Factory factory = type.getFactory();
			assertTrue(type.getPackage().isUnnamedPackage(), "there should be no package statement in this test file");
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
			assertFalse(type.getPackage().isUnnamedPackage(), "there should be a package statement in this test file");
			CtCompilationUnit cu = factory.CompilationUnit().getOrCreate(type);
			CtTypeReference<?> arrayListRef = factory.Type().get(java.util.ArrayList.class).getReference();
			cu.getImports().add(factory.createImport(arrayListRef));
		};
		BiConsumer<CtType<?>, String> assertImportsPrintedCorrectly = (type, result) -> {
			assertThat(result, containsString("\nimport java.util.ArrayList;\n"));
		};

		testSniper("visibility.YamlRepresenter", addArrayListImport, assertImportsPrintedCorrectly);
	}

	@Test
	public void testAddedElementsIndentedWithAppropriateIndentationStyle() {
		// contract: added elements in a source file should be indented with the same style of
		// indentation as in the rest of the file

		Consumer<CtType<?>> addElements = type -> {
		    Factory fact = type.getFactory();
		    fact.createField(type, new HashSet<>(), fact.Type().integerPrimitiveType(), "z", fact.createLiteral(3));
		    type.getMethod("sum").getBody()
					.addStatement(0, fact.createCodeSnippetStatement("System.out.println(z);"));
		};
		BiConsumer<CtType<?>, String> assertTabs = (type, result) -> {
			assertThat(result, containsString("\n\tint z = 3;"));
			assertThat(result, containsString("\n\t\tSystem"));
		};
		BiConsumer<CtType<?>, String> assertTwoSpaces = (type, result) -> {
		    assertThat(result, containsString("\n  int z = 3;"));
		    assertThat(result, containsString("\n    System"));
		};
		BiConsumer<CtType<?>, String> assertFourSpaces = (type, result) -> {
			assertThat(result, containsString("\n    int z = 3;"));
			assertThat(result, containsString("\n        System"));
		};

		testSniper("indentation.Tabs", addElements, assertTabs);
		testSniper("indentation.TwoSpaces", addElements, assertTwoSpaces);
		testSniper("indentation.FourSpaces", addElements, assertFourSpaces);
	}

	@Test
	public void testAddedElementsIndentedWithAppropriateIndentationStyleWhenOnlyOneTypeMemberExists() {
		// contract: added elements in a source file should be indented with the same style of
		// indentation as the single type member, when there is only one type member.

		Consumer<CtType<?>> addElement = type -> {
			Factory fact = type.getFactory();
			fact.createField(type, new HashSet<>(), fact.Type().integerPrimitiveType(), "z", fact.createLiteral(2));
		};
		final String newField = "int z = 2;";

		BiConsumer<CtType<?>, String> assertTabs = (type, result) ->
				assertThat(result, containsString("\n\t" + newField));
		BiConsumer<CtType<?>, String> assertTwoSpaces = (type, result) ->
				assertThat(result, containsString("\n  " + newField));
		BiConsumer<CtType<?>, String> assertFourSpaces = (type, result) ->
				assertThat(result, containsString("\n    " + newField));

		testSniper("indentation.singletypemember.Tabs", addElement, assertTabs);
		testSniper("indentation.singletypemember.TwoSpaces", addElement, assertTwoSpaces);
		testSniper("indentation.singletypemember.FourSpaces", addElement, assertFourSpaces);
	}

	@Test
	public void testDefaultsToSingleTabIndentationWhenThereAreNoTypeMembers() {
		// contract: if there are no type members in a compilation unit, the sniper printer defaults
		// to indenting with 1 tab

		Consumer<CtType<?>> addField = type -> {
			Factory fact = type.getFactory();
			fact.createField(type, new HashSet<>(), fact.Type().integerPrimitiveType(), "z", fact.createLiteral(3));
		};
		testSniper("indentation.NoTypeMembers", addField, (type, result) -> {
			assertThat(result, containsString("\n\tint z = 3;"));
		});
	}

	@Test
	public void testOptimizesParenthesesForAddedNestedOperators() {
		// contract: The sniper printer should optimize parentheses for newly inserted elements

		// without parentheses optimization, the expression will be printed as `(1 + 2) + (-(2 + 3))`
		String declaration = "int a = 1 + 2 + -(2 + 3)";
		Launcher launcher = new Launcher();
		CtStatement nestedOps = launcher.getFactory().createCodeSnippetStatement(declaration).compile();

		Consumer<CtType<?>> addNestedOperator = type -> {
			CtMethod<?> method = type.getMethodsByName("main").get(0);
			method.getBody().addStatement(nestedOps);
		};
		BiConsumer<CtType<?>, String> assertCorrectlyPrinted =
				(type, result) -> assertThat(result, containsString(declaration));

		testSniper("methodimport.ClassWithStaticMethod", addNestedOperator, assertCorrectlyPrinted);
	}

	@Test
	public void testPrintTypeWithMethodImportAboveMethodDefinition() {
		// contract: The type references of a method import (e.g. its return type) has source
		// positions in the file the method was imported from. The resolved source end position
		// of the import should not be affected by the placement of the imported method. This
		// test ensures this is the case even when the end position of the imported method is
		// greater than the end position of the import statement.

		Launcher launcher = createLauncherWithSniperPrinter();
		launcher.addInputResource(getResourcePath("methodimport.ClassWithStaticMethod"));
		launcher.addInputResource(getResourcePath("methodimport.MethodImportAboveImportedMethod"));

		CtModel model = launcher.buildModel();
		CtType<?> classWithStaticMethodImport = model.getAllTypes().stream()
				.filter(type -> type.getSimpleName().endsWith("AboveImportedMethod"))
				.findFirst()
				.get();

		List<CtImport> imports = classWithStaticMethodImport.getFactory().CompilationUnit().getOrCreate(classWithStaticMethodImport).getImports();

		String output = launcher
				.getEnvironment()
				.createPrettyPrinter().printTypes(classWithStaticMethodImport);

		assertThat(output, containsString("import static methodimport.ClassWithStaticMethod.staticMethod;"));
	}

	@Test
	public void testPrintTypeWithMethodImportBelowMethodDefinition() {
		// contract: The type references of a method import (e.g. its return type) has source
		// positions in the file the method was imported from. The resolved source start position
		// of the import should not be affected by the placement of the imported method. This
		// test ensures this is the case even when the start position of the imported method is
		// less than the start position of the import statement.

		Launcher launcher = createLauncherWithSniperPrinter();
		launcher.addInputResource(getResourcePath("methodimport.ClassWithStaticMethod"));
		launcher.addInputResource(getResourcePath("methodimport.MethodImportBelowImportedMethod"));

		CtModel model = launcher.buildModel();
		CtType<?> classWithStaticMethodImport = model.getAllTypes().stream()
				.filter(type -> type.getSimpleName().endsWith("BelowImportedMethod"))
				.findFirst()
				.get();

		String output = launcher
				.getEnvironment()
				.createPrettyPrinter().printTypes(classWithStaticMethodImport);

		assertThat(output, containsString("import static methodimport.ClassWithStaticMethod.staticMethod;"));
	}

	@Test
	public void testThrowsWhenTryingToPrintSubsetOfCompilationUnitTypes() {
		// contract: Printing a subset of a compilation unit's types is a hassle to implement at the time of writing
		// this, as a) DJPP will replace the compilation unit with a clone, and b) it makes it more difficult to
		// match source code fragments. For now, we're lazy and simply don't allow it.

		Launcher launcher = createLauncherWithSniperPrinter();
		launcher.addInputResource(getResourcePath("MultipleTopLevelTypes"));

		CtModel model = launcher.buildModel();
		CtType<?> primaryType = model.getAllTypes().stream().filter(CtModifiable::isPublic).findFirst().get();
		CtCompilationUnit cu = primaryType.getFactory().CompilationUnit().getOrCreate(primaryType);
		SniperJavaPrettyPrinter sniper = (SniperJavaPrettyPrinter) launcher.getEnvironment().createPrettyPrinter();

		assertThrows(IllegalArgumentException.class, () -> sniper.calculate(cu, Collections.singletonList(primaryType)));
	}

	@Test
	void testSniperRespectsDeletionInForInit() {
		// contract: The sniper printer should detect deletions in for loop init as modifications
		// and print the model accordingly.

        Consumer<CtType<?>> deleteForUpdate = type -> {
        	CtFor ctFor = type.filterChildren(CtFor.class::isInstance).first();
			ctFor.getForInit().forEach(CtElement::delete);
		};
		BiConsumer<CtType<?>, String> assertNotStaticFindFirstIsEmpty = (type, result) ->
            assertThat(result, containsString("for (; i < 10; i++)"));

		testSniper("ForLoop", deleteForUpdate, assertNotStaticFindFirstIsEmpty);
	}

	@Test
	void testSniperRespectsDeletionInForUpdate() {
		// contract: The sniper printer should detect deletions in for loop update as modifications
		// and print the model accordingly.

		Consumer<CtType<?>> deleteForUpdate = type -> {
			CtFor ctFor = type.filterChildren(CtFor.class::isInstance).first();
			ctFor.getForUpdate().forEach(CtElement::delete);
		};
		BiConsumer<CtType<?>, String> assertNotStaticFindFirstIsEmpty = (type, result) ->
				assertThat(result, containsString("for (int i = 0; i < 10;)"));

		testSniper("ForLoop", deleteForUpdate, assertNotStaticFindFirstIsEmpty);
	}

	@Test
	@GitHubIssue(issueNumber = 4021, fixed = true)
	void testSniperRespectsSuperWithUnaryOperator() {
		// Combining CtSuperAccess and CtUnaryOperator leads to SpoonException with Sniper

		// Noop
		Consumer<CtType<?>> deleteForUpdate = type -> {};

		BiConsumer<CtType<?>, String> assertContainsSuperWithUnaryOperator = (type, result) ->
				assertThat(result, containsString("super.a(-x);"));

		testSniper("superCall.SuperCallSniperTestClass", deleteForUpdate, assertContainsSuperWithUnaryOperator);
	}

	@Test
	@GitHubIssue(issueNumber = 3911, fixed = true)
	void testRoundBracketPrintingInComplexArithmeticExpression() {
		Consumer<CtType<?>> noOpModifyFieldAssignment = type ->
				type.getField("value")
						.getAssignment()
						.descendantIterator()
						.forEachRemaining(TestSniperPrinter::markElementForSniperPrinting);

		BiConsumer<CtType<?>, String> assertPrintsRoundBracketsCorrectly = (type, result) ->
				assertThat(result, containsString("((double) (3 / 2)) / 2"));

		testSniper("ArithmeticExpression", noOpModifyFieldAssignment, assertPrintsRoundBracketsCorrectly);
	}

	@Test
	@GitHubIssue(issueNumber = 3911, fixed = true)
	void testRoundBracketPrintingInComplexArithmeticExpressionWithSpaces() {
		Consumer<CtType<?>> noOpModifyFieldAssignment = type ->
				type.getField("value")
						.getAssignment()
						.descendantIterator()
						.forEachRemaining(TestSniperPrinter::markElementForSniperPrinting);

		// This checks that we retain the ORIGINAL spaces if present in the expression.
		BiConsumer<CtType<?>, String> assertPrintsRoundBracketsCorrectly = (type, result) ->
				assertThat(result, containsString("( (double) (3 / 2)) / 2"));

		testSniper("ArithmeticExpressionWithSpaces", noOpModifyFieldAssignment, assertPrintsRoundBracketsCorrectly);
	}

	@Test
	@GitHubIssue(issueNumber = 4218, fixed = true)
	void testSniperDoesNotPrintTheDeletedAnnotation() {
		Consumer<CtType<?>> deleteAnnotation = type -> {
			type.getAnnotations().forEach(CtAnnotation::delete);
		};

		BiConsumer<CtType<?>, String> assertDoesNotContainAnnotation = (type, result) ->
				assertThat(result, not(containsString("@abc.def.xyz")));

		testSniper("sniperPrinter.DeleteAnnotation", deleteAnnotation, assertDoesNotContainAnnotation);
	}

	@Test
	@GitHubIssue(issueNumber = 4220, fixed = true)
	void testSniperAddsSpaceAfterFinal() {
		Consumer<CtType<?>> modifyField = type -> {
			Factory factory = type.getFactory();
			CtField field = type.filterChildren(CtField.class::isInstance).first();
			field.setType(factory.Type().integerType());
		};

		BiConsumer<CtType<?>, String> assertContainsSpaceAfterFinal = (type, result) ->
				assertThat(result, containsString("private static final java.lang.Integer x;"));

		testSniper("sniperPrinter.SpaceAfterFinal", modifyField, assertContainsSpaceAfterFinal);
	}

	@Test
	void typeAdaptionBodyResetDoesNotBreakSniper() {
		// contract: Resetting the body in the type adaption does not impact the sniper printer.
		testSniper(
			"sniperPrinter.Overriding",
			type -> {
				CtType<?> top = type.getNestedType("Super");
				CtType<?> bottom = type.getNestedType("Sub");

				CtMethod<?> topFoo = top.getMethodsByName("foo").get(0);
				CtMethod<?> bottomFoo = bottom.getMethodsByName("foo").get(0);

				assertTrue(new TypeAdaptor(bottom).isOverriding(bottomFoo, topFoo));
			},
			// Did not reformat body
			(type, result) -> assertThat(result, containsString("System. out. println(1+2\n"))
		);
	}

	@Nested
	class ResourcePrintingInTryWithResourceStatement{
		private CtTryWithResource getTryWithResource(CtType<?> type) {
			return type.getMethodsByName("resourcePrinting").get(0).getBody().getStatement(0);
		}

		@Test
		void test_printSecondResourceExactlyOnce() {
			// contract: sniper should print the second resource exactly once
			Consumer<CtType<?>> noOpModifyTryWithResource = type ->
					TestSniperPrinter.markElementForSniperPrinting(getTryWithResource(type));

			BiConsumer<CtType<?>, String> assertPrintsResourcesCorrectly = (type, result) ->
					assertThat(result, containsString(" try (ZipFile zf = new ZipFile(zipFileName);\n" +
							"             BufferedWriter writer = newBufferedWriter(outputFilePath, charset))"));

			testSniper("sniperPrinter.tryWithResource.PrintOnce", noOpModifyTryWithResource, assertPrintsResourcesCorrectly);
		}

		@Test
		void test_retainSemiColonAfterTheLastResource() {
			// contract: sniper should retain the semi-colon after second resource
			Consumer<CtType<?>> noOpModifyTryWithResource = type ->
					TestSniperPrinter.markElementForSniperPrinting(getTryWithResource(type));

			BiConsumer<CtType<?>, String> assertPrintsResourcesCorrectly = (type, result) ->
					assertThat(result, containsString(" try (ZipFile zf = new ZipFile(zipFileName);\n" +
							"             BufferedWriter writer = newBufferedWriter(outputFilePath, charset);)"));

			testSniper("sniperPrinter.tryWithResource.RetainSemiColon", noOpModifyTryWithResource, assertPrintsResourcesCorrectly);
		}
	}

	@Nested

	class SquareBracketPrintingInArrayInitialisation {
		// contract: square brackets should be printed *only* after the identifier of the field or local variable

		private Consumer<CtType<?>> markFieldForSniperPrinting() {
			return type -> {
				CtField<?> field = type.getField("array");
				TestSniperPrinter.markElementForSniperPrinting(field.getType());
			};
		}

		private BiConsumer<CtType<?>, String> assertPrintsBracketForArrayInitialisation(String arrayDeclaration) {
			return (type, result) ->
					assertThat(result, containsString(arrayDeclaration));
		}

		@Test
		@GitHubIssue(issueNumber = 4315, fixed = true)
		void test_bracketShouldBePrintedWhenArrayIsNull() {
			testSniper(
					"sniperPrinter.arrayInitialisation.ToNull",
					markFieldForSniperPrinting(),
					assertPrintsBracketForArrayInitialisation("int array[];"));
		}

		@Test
		@GitHubIssue(issueNumber = 4315, fixed = true)
		void test_bracketShouldBePrintedWhenArrayIsInitialisedToIntegers() {
			testSniper(
					"sniperPrinter.arrayInitialisation.FiveIntegers",
					markFieldForSniperPrinting(),
					assertPrintsBracketForArrayInitialisation("int array[] = {1, 2, 3, 4, 5};"));
		}

		@Test
		@GitHubIssue(issueNumber = 4315, fixed = true)
		void test_bracketShouldBePrintedWhenArrayIsInitialisedToNullElements() {
			testSniper(
					"sniperPrinter.arrayInitialisation.ToNullElements",
					markFieldForSniperPrinting(),
					assertPrintsBracketForArrayInitialisation("String array[] = new String[42];"));
		}

		@Test
		@GitHubIssue(issueNumber = 4315, fixed = true)
		void test_bracketsShouldBePrintedForMultiDimensionalArray() {
			testSniper(
					"sniperPrinter.arrayInitialisation.MultiDimension",
					markFieldForSniperPrinting(),
					assertPrintsBracketForArrayInitialisation("String array[][][] = new String[1][2][3];"));
		}

		@Test
		@GitHubIssue(issueNumber = 4315, fixed = true)
		void test_bracketsShouldBePrintedForArrayInitialisedInLocalVariable() {
			Consumer<CtType<?>> noOpModifyLocalVariable = type -> {
				CtMethod<?> method = type.getMethod("doNothing");
				TestSniperPrinter.markElementForSniperPrinting(method.getBody().getStatement(0));
			};

			testSniper(
					"sniperPrinter.arrayInitialisation.AsLocalVariable",
					noOpModifyLocalVariable,
					assertPrintsBracketForArrayInitialisation("int array[] = new int[]{ };"));
		}

		@Test
		@GitHubIssue(issueNumber = 4421, fixed = true)
		void test_bracketsShouldBePrintedForGenericTypeOfArray() {
			testSniper(
					"sniperPrinter.arrayInitialisation.GenericTypeArray",
					markFieldForSniperPrinting(),
					assertPrintsBracketForArrayInitialisation("Class<?> array[];"));
		}
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

		final CtType<?> ctType = f.Type().get(testClass);

		//change the model
		transformation.accept(ctType);

		//print the changed model
		launcher.prettyprint();

		//check the printed file
		resultChecker.accept(ctType, getContentOfPrettyPrintedClassFromDisk(ctType));
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

				assertTrue(originalContent.contains(result.replace("\t", "")),
						"ToString() on element (" + el.getClass().getName() + ") =  \"" + el + "\" is not in original content");
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
	@Test
	@GitHubIssue(issueNumber = 3811, fixed = true)
	void noChangeDiffBrackets() throws IOException {
			testNoChangeDiffFailing(
					Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/difftest/Brackets").toFile());
	}
	@Test
	@GitHubIssue(issueNumber = 3811, fixed = true)
	void noChangeDiffConditionalComment() throws IOException {
			testNoChangeDiffFailing(
					Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/difftest/ConditionalComment").toFile());
	}

	@Test
	@GitHubIssue(issueNumber = 3811, fixed = true)
	void noChangeDiffEnumComment() throws IOException {
			testNoChangeDiffFailing(
					Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/difftest/EnumComment").toFile());
	}
	@Test
	@GitHubIssue(issueNumber = 3811, fixed = true)
	void noChangeDiffEnumTest() throws IOException {
			testNoChangeDiffFailing(
					Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/difftest/EnumTest").toFile());
	}
	@Test
	@GitHubIssue(issueNumber = 3811, fixed = true)
	void noChangeDiffExceptionTest() throws IOException {
			testNoChangeDiffFailing(
					Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/difftest/ExceptionTest").toFile());
	}
	@Test
	@GitHubIssue(issueNumber = 3811, fixed = true)
	void noChangeDiffMethodComment() throws IOException {
			testNoChangeDiffFailing(
					Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/difftest/MethodComment").toFile());
	}


	@Test
	@GitHubIssue(issueNumber = 4335, fixed = true)
	public void testCorrectTypeCastParenthesisAfterRefactor() {
		testSniper(RefactorCast.class.getName(), type -> {
			List<CtStatement> blocks = type.getMethodsByName("example").get(0).getBody().getStatements();
			CtLocalVariable<?> localVar = (CtLocalVariable<?>) blocks.get(0);
			CtRenameLocalVariableRefactoring refactor = new CtRenameLocalVariableRefactoring();
			refactor.setTarget(localVar);
			refactor.setNewName("b");
			refactor.refactor();
		}, (type, result) -> assertThat(result, containsString("((Double) b).toString();")));
	}

	@Test
	@GitHubIssue(issueNumber = 5001, fixed = true)
	public void testCorrectPrintingOfUnchangedStringAssignment() throws IOException {
		// We want to make sure that if there are no changes made to the source code, then the output is the same
		// as the input.
		testNoChangeDiffFailing(
				Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/SampleClassIssue5001").toFile());
	}
	/**
	 * Test various syntax by doing an change to every element that should not
	 * result in any change in source. This forces the sniper printer to recreate
	 * the output. Assert that the output is the same as the input.
	 *
	 * Reference: #3811
	 */
	private void testNoChangeDiffFailing(File file) throws IOException {
		String fileName = file.getName();
		Path outputPath = Paths.get("target/test-output");
		File outputFile = outputPath.resolve("spoon/test/prettyprinter/testclasses/difftest")
				.resolve(fileName).toFile();
		final Launcher launcher = new Launcher();
		final Environment e = launcher.getEnvironment();
		e.setLevel("INFO");
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(e));

		launcher.addInputResource(file.toString());
		launcher.setSourceOutputDirectory(outputPath.toString());
		launcher.addProcessor(new AbstractProcessor<>() {
			public void process(CtElement element) {
				markElementForSniperPrinting(element);
			}
		});
		launcher.run();

		assertTrue(FileUtils.contentEquals(file, outputFile),"File " + outputFile.getAbsolutePath() + " is different");
	}

	/**
	 * Modify an element such that the sniper printer detects it as modified, without changing its final content. This
	 * forces it to be sniper-printed "as-is".
	 */
	private static void markElementForSniperPrinting(CtElement element) {
		SourcePosition pos = element.getPosition();
		element.setPosition(SourcePosition.NOPOSITION);
		element.setPosition(pos);
	}
}
