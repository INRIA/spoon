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

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.processing.TraversalStrategy;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.ImportCleaner;
import spoon.reflect.visitor.ImportConflictDetector;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.modelobs.SourceFragmentCreator;
import spoon.support.sniper.SniperJavaPrettyPrinter;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSniperPrinter {

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
			assertIsPrintedWithExpectedChanges(type, printed, "\\s*, \\QList<?>[][] ... twoDArrayOfLists\\E", "");
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

	/**
	 * 1) Runs spoon using sniper mode,
	 * 2) runs `typeChanger` to modify the code,
	 * 3) runs `resultChecker` to check if sources printed by sniper printer are as expected
	 * @param testClass a file system path to test class
	 * @param transformation a code which changes the Spoon model
	 * @param resultChecker a code which checks that printed sources are as expected
	 */
	private void testSniper(String testClass, Consumer<CtType<?>> transformation, BiConsumer<CtType<?>, String> resultChecker) {
		Launcher launcher = new Launcher();
		launcher.addInputResource(getResourcePath(testClass));
		launcher.getEnvironment().setPrettyPrinterCreator(() -> {
			SniperJavaPrettyPrinter printer = new SniperJavaPrettyPrinter(launcher.getEnvironment());
			printer.setPreprocessors(Collections.unmodifiableList(Arrays.<Processor<CtElement>>asList(
					//remove unused imports first. Do not add new imports at time when conflicts are not resolved
					new ImportCleaner().setCanAddImports(false),
					//solve conflicts, the current imports are relevant too
					new ImportConflictDetector(),
					//compute final imports
					new ImportCleaner()
				)));
			return printer;
		});
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

	private String getContentOfPrettyPrintedClassFromDisk(CtType<?> type) {
		Factory f = type.getFactory();
		File outputDir = f.getEnvironment().getSourceOutputDirectory();
		File outputFile = new File(outputDir, type.getQualifiedName().replace('.', '/') + ".java");

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

	Pattern importRE = Pattern.compile("^(?:import|package)\\s.*;\\s*$", Pattern.MULTILINE);

	private String sourceWithoutImports(String source) {
		Matcher m = importRE.matcher(source);
		int lastImportEnd = 0;
		while (m.find()) {
			lastImportEnd = m.end();
		}
		return source.substring(lastImportEnd).trim();
	}

	private static String fileAsString(String path, Charset encoding)
			throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public void testToStringWithSniperPrinter(String inputSourcePath) throws Exception {

		final Launcher launcher = new Launcher();
		launcher.addInputResource(inputSourcePath);
		String originalContent = fileAsString(inputSourcePath, StandardCharsets.UTF_8).replace("\t","");
		CtModel model = launcher.buildModel();

		new SourceFragmentCreator().attachTo(launcher.getFactory().getEnvironment());

		launcher.getEnvironment().setPrettyPrinterCreator(
				() -> {
					SniperJavaPrettyPrinter sp = new SniperJavaPrettyPrinter(launcher.getEnvironment());
					sp.setIgnoreImplicit(true);
					return sp;
				}
		);
		List<CtElement> ops = model.getElements(new TypeFilter<>(CtElement.class));


		ops.stream()
				.filter(el -> !(el instanceof spoon.reflect.CtModelImpl.CtRootPackage) &&
						!(el instanceof spoon.reflect.factory.ModuleFactory.CtUnnamedModule)
				).forEach(el -> {
			try {
				//Contract, calling toString on unmodified AST elements should draw only from original.
				assertTrue("ToString() on element (" + el.getClass().getName() + ") =  \"" + el + "\" is not in original content",
						originalContent.contains(el.toString().replace("\t","")));
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
