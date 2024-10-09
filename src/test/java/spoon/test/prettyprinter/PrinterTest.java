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


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.DefaultTokenWriter;
import spoon.reflect.visitor.ElementPrinterHelper;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.TokenWriter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.StandardEnvironment;
import spoon.test.prettyprinter.testclasses.MissingVariableDeclaration;
import spoon.testing.utils.ModelUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class PrinterTest {

	@Test
	public void testPrettyPrinter() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/annotation/testclasses/PersistenceProperty.java",
								"./src/test/java/spoon/test/prettyprinter/testclasses/Validation.java"))
				.build();
		for (CtType<?> t : factory.Type().getAll()) {
			t.toString();
		}
		assertEquals(0, factory.getEnvironment().getWarningCount());
		assertEquals(0, factory.getEnvironment().getErrorCount());

	}

	@Test
	public void testChangeAutoImportModeWorks() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(false);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/AClass.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(!result.contains("import java.util.List;"), "The result should not contain imports: " + result);

		// recreating an auto-immport  printer
		spoon.getEnvironment().setAutoImports(true);
		printer = spoon.createPrettyPrinter();

		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		result = printer.getResult();
		assertTrue(result.contains("import java.util.List;"), "The result should now contain imports: " + result);
	}

	@Test
	public void testFQNModeWriteFQNConstructorInCtVisitor() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(false);
		spoon.addInputResource("./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(result.contains("new spoon.support.visitor.replace.ReplacementVisitor("), "The result should contain FQN for constructor: " + result);
		assertTrue(!result.contains("new ReplacementVisitor("), "The result should not contain reduced constructors: " + result);
	}

	@Test
	public void testAutoimportModeDontImportUselessStatic() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(true);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/ImportStatic.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(!result.contains("import static spoon.test.prettyprinter.testclasses.sub.Constants.READY"), "The result should not contain import static: ");
		assertTrue(result.contains("import spoon.test.prettyprinter.testclasses.sub.Constants"), "The result should contain import type: ");
		assertTrue(result.contains("import static org.junit.jupiter.api.Assertions.assertTrue;"), "The result should contain import static assertTrue: ");
		assertTrue(result.contains("assertTrue(\"blabla\".equals(\"toto\"));"), "The result should contain assertTrue(...): ");
		assertTrue(result.contains("System.out.println(Constants.READY);"), "The result should use System.out.println(Constants.READY): " + result);
	}

	@Test
	public void testAutoimportModeDontImportUselessStaticNoClassPath() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/resources/unresolved/UnresolvedExtend.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(result.contains("import java.util.ArrayList;"), "The result should contain import java.util.ArrayList: ");
		assertTrue(result.contains("import java.util.List;"), "The result should contain import java.util.List: ");
		assertTrue(result.contains("import static org.Bar.m;"), "The result should contain import static org.Bar.m: ");
	}

	@Test
	public void testUnresolvedImportStaticNoClassPath() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/resources/unresolved/StaticImportUnresolved.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(result.contains("import java.util.ArrayList;"), "The result should contain import java.util.ArrayList: ");
		assertTrue(result.contains("import java.util.List;"), "The result should contain import java.util.List: ");
		assertTrue(result.contains("import static org.Bar.*;"), "The result should contain import static org.Bar.m: ");
	}

	@Test
	public void testUnresolvedNoClassPath() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/resources/unresolved/Unresolved.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(result.contains("import org.Bar;"), "The result should contain import org.Bar: ");
		assertTrue(result.contains("import org.foo.*;"), "The result should contain import org.foo.*: ");
	}

	@Test
	public void testRuleCanBeBuild() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(true);
		String output = "./target/spoon-rule/";
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/Rule.java");
		spoon.setSourceOutputDirectory(output);
		spoon.run();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(!result.contains("Rule.Phoneme.this.phonemeText"), "The result should contain direct this accessor for field: " + result);
		canBeBuilt(output, StandardEnvironment.DEFAULT_CODE_COMPLIANCE_LEVEL);
	}


	@Test
	public void testLambdaCanBeBuild() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(true);
		String output = "./target/spoon-lambda/";
		spoon.addInputResource("./src/test/java/spoon/test/lambda/testclasses/Intersection.java");
		spoon.setSourceOutputDirectory(output);
		spoon.run();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);

		canBeBuilt(output, 8);
	}

	@Test
	public void testJDTBatchCompilerCanBeBuild() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(false);
		String output = "./target/spoon-jdtbatchcompiler/";
		spoon.addInputResource("./src/main/java/spoon/support/compiler/jdt/JDTBatchCompiler.java");
		spoon.setSourceOutputDirectory(output);
		spoon.run();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue(!result.contains("Rule.Phoneme.this.phonemeText"), "The result should contain direct this accessor for field: " + result);
		canBeBuilt(output, StandardEnvironment.DEFAULT_CODE_COMPLIANCE_LEVEL);
	}

	@Test
	public void testPrintingOfOrphanFieldReference() throws Exception {
		CtType<?> type = ModelUtils.buildClass(MissingVariableDeclaration.class);
		//delete the field, so the model is broken.
		//It may happen during substitution operations and then it is helpful to display descriptive error message
		type.getField("testedField").delete();
		//contract: printer doesn't fail, but prints the field reference even if there is no declaration visible
		assertEquals("testedField = 1", type.getMethodsByName("failingMethod").get(0).getBody().getStatement(0).toString());
	}

	private final Set<String> separators = new HashSet<>(Arrays.asList("->","::","..."));
	{
		"(){}[];,.:@=<>?&|".chars().forEach(c->separators.add(new String(Character.toChars(c))));
	}
	private final Set<String> operators = new HashSet<>(Arrays.asList(
			"=",
			">",
			"<",
			"!",
			"~",
			"?",
			":",
			"==",
			"<=",
			">=",
			"!=",
			"&&",
			"||",
			"++",
			"--",
			"+",
			"-",
			"*",
			"/",
			"&",
			"|",
			"^",
			"%",
			"<<",">>",">>>",

			"+=",
			"-=",
			"*=",
			"/=",
			"&=",
			"|=",
			"^=",
			"%=",
			"<<=",
			">>=",
			">>>=",
			"instanceof"
	));

	private final String[] javaKeywordsJoined = {
			"abstract continue for new switch",
			"assert default goto package synchronized",
			"boolean do if private this",
			"break double implements protected throw",
			"byte else import public throws",
			"case enum instanceof return transient",
			"catch extends int short try",
			"char final interface static void",
			"class finally long strictfp volatile",
			"const float native super while"
	};

	private final Set<String> javaKeywords = new HashSet<>();
	{
		for (String str : javaKeywordsJoined) {
			StringTokenizer st = new StringTokenizer(str, " ");
			while (st.hasMoreTokens()) {
				javaKeywords.add(st.nextToken());
			}
		}
	}

	@Test
	public void testPrinterTokenListener() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setCommentEnabled(false);
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
				.resources(
						"./src/test/java/spoon/test/annotation/testclasses/",
						"./src/test/java/spoon/test/prettyprinter/"))
//this case needs longer, but checks contract on all spoon java sources
//				.resources("./src/main/java/"))
				.build();

		assertFalse(factory.Type().getAll().isEmpty());
		for (CtType<?> t : factory.Type().getAll()) {
			//create DefaultJavaPrettyPrinter with standard DefaultTokenWriter
			DefaultJavaPrettyPrinter pp = new DefaultJavaPrettyPrinter(factory.getEnvironment());
			pp.calculate(t.getPosition().getCompilationUnit(), Collections.singletonList(t));
			//result of printing using standard DefaultTokenWriter
			String standardPrintedResult = pp.getResult();
			
			StringBuilder allTokens = new StringBuilder();
			//print type with custom listener
			//1) register custom TokenWriter which checks the TokenWriter contract
			pp.setPrinterTokenWriter(new TokenWriter() {
				String lastToken;
				PrinterHelper printerHelper = new PrinterHelper(factory.getEnvironment());

				@Override
				public TokenWriter writeSeparator(String separator) {
					checkRepeatingOfTokens("writeSeparator");
					checkTokenWhitespace(separator, false);					
					//one of the separators
					assertTrue(separators.contains(separator), "Unexpected separator: " + separator);
					handleTabs();
					allTokens.append(separator);
					return this;
				}
				
				@Override
				public TokenWriter writeOperator(String operator) {
					checkRepeatingOfTokens("writeOperator");
					checkTokenWhitespace(operator, false);					
					assertTrue(operators.contains(operator), "Unexpected operator: " + operator);
					handleTabs();
					allTokens.append(operator);
					return this;
				}
				
				@Override
				public TokenWriter writeLiteral(String literal) {
					checkRepeatingOfTokens("writeLiteral");
					assertFalse(literal.isEmpty());
					handleTabs();
					allTokens.append(literal);
					return this;
				}
				
				@Override
				public TokenWriter writeKeyword(String keyword) {
					checkRepeatingOfTokens("writeKeyword");
					checkTokenWhitespace(keyword, false);					
					assertTrue(javaKeywords.contains(keyword), "Unexpected java keyword: " + keyword);
					handleTabs();
					allTokens.append(keyword);
					return this;
				}
				
				@Override
				public TokenWriter writeIdentifier(String identifier) {
					checkRepeatingOfTokens("writeIdentifier");
					checkTokenWhitespace(identifier, false);
					for (int i = 0; i < identifier.length(); i++) {
						char c = identifier.charAt(i);
						if ('*' == c) {
							continue;
						}
						if(i==0) {
							assertTrue(Character.isJavaIdentifierStart(c));
						} else {
							assertTrue(Character.isJavaIdentifierPart(c));
						}
					}
					assertEquals(false, javaKeywords.contains(identifier), "Keyword found in Identifier: " + identifier);
					handleTabs();
					allTokens.append(identifier);
					return this;
				}
				
				@Override
				public TokenWriter writeComment(CtComment comment) {
					checkRepeatingOfTokens("writeComment");
					DefaultTokenWriter sptw = new DefaultTokenWriter(new PrinterHelper(factory.getEnvironment()));
					PrinterHelper ph = sptw.getPrinterHelper();
					ph.setLineSeparator(getPrinterHelper().getLineSeparator());
					ph.setTabCount(getPrinterHelper().getTabCount());
					sptw.writeComment(comment);
					handleTabs();
					allTokens.append(sptw.getPrinterHelper().toString());
					return this;
				}
				
				@Override
				public TokenWriter writeln() {
					checkRepeatingOfTokens("writeln");
					allTokens.append(getPrinterHelper().getLineSeparator());
					lastTokenWasEOL = true;
					return this;
				}
				
				private boolean lastTokenWasEOL = true;
				private int tabCount = 0;
				
				public TokenWriter handleTabs() {
					if(lastTokenWasEOL) {
						lastTokenWasEOL = false;
						for (int i = 0; i < tabCount; i++) {
							if (factory.getEnvironment().isUsingTabulations()) {
								allTokens.append('\t');
							} else {
								for (int j = 0; j < factory.getEnvironment().getTabulationSize(); j++) {
									allTokens.append(' ');
								}
							}
						}
						
					}
					return this;
				}

				@Override
				public TokenWriter writeCodeSnippet(String token) {
					checkRepeatingOfTokens("writeCodeSnippet");
					assertFalse(token.isEmpty());
					handleTabs();
					allTokens.append(token);
					return this;
				}

				@Override
				public TokenWriter incTab() {
					tabCount++;
					return this;
				}

				@Override
				public TokenWriter decTab() {
					tabCount--;
					return this;
				}

				@Override
				public PrinterHelper getPrinterHelper() {
					return printerHelper;
				}

				@Override
				public void reset() {
					printerHelper.reset();
				}

				@Override
				public TokenWriter writeSpace() {
					checkRepeatingOfTokens("writeWhitespace");
					allTokens.append(' ');
					return this;
				}

				//checks that token types are changing. There must be no two tokens of the same type in queue
				private void checkRepeatingOfTokens(String tokenType) {
					if("writeln".equals(tokenType)
							|| "writeIdentifier".equals(tokenType)
							|| "writeSeparator".equals(tokenType)
							|| "writeWhitespace".equals(tokenType)) {
						// nothing
					} else {
						//check only other tokens then writeln, which is the only one which can repeat
						assertEquals(false, tokenType.equals(this.lastToken), "Two tokens of same type current:" + tokenType + " " + allTokens.toString());
					}
					this.lastToken = tokenType;
				}
			});
			
			//2) print type using PrettyPrinter with listener
			pp.calculate(t.getPosition().getCompilationUnit(), Collections.singletonList(t));
			String withEmptyListenerResult = pp.getResult();
			//contract: each printed character is handled by listener. PrinterHelper is not called directly
			//and because PrinterTokenListener above does not use PrinterHelper, the result must be empty
			assertEquals(0, withEmptyListenerResult.length());
			
			//contract: result built manually from tokens is same like the one made by DefaultTokenWriter
			assertEquals(standardPrintedResult, allTokens.toString());
		}
	}

	private void checkTokenWhitespace(String stringToken, boolean isWhitespace) {
		//contract: there is no empty token
		assertFalse(stringToken.isEmpty());
		//contract: only whitespace token contains whitespace
		for (int i = 0; i < stringToken.length(); i++) {
			char c = stringToken.charAt(i);
			if (isWhitespace) {
				//a whitespace
				assertEquals(true, Character.isWhitespace(c));
			} else {
				//not a whitespace
				assertEquals(false, Character.isWhitespace(c));
			}
		}
	}

	@Test
	public void testListPrinter() {

		Launcher spoon = new Launcher();
		DefaultJavaPrettyPrinter pp = (DefaultJavaPrettyPrinter) spoon.createPrettyPrinter();

		PrinterHelper ph = new PrinterHelper(spoon.getEnvironment());
		TokenWriter tw = new DefaultTokenWriter(ph);
		pp.setPrinterTokenWriter(tw);

		ElementPrinterHelper elementPrinterHelper = pp.getElementPrinterHelper();

		String[] listString = {"un", "deux", "trois"};

		elementPrinterHelper.printList(Arrays.asList(listString), null, true, "start", true, true, "next", true, true, "end", s -> tw.writeIdentifier(s));

		String expectedResult = " start un next deux next trois end";
		assertEquals(expectedResult, pp.toString());
	}

		@Test
		public void testMethodParentheses() {
			//contract: there should not be any redundant parentheses
			//https://github.com/INRIA/spoon/issues/2330
			CtClass c1 = Launcher.parseClass("class C1 { int count ; void m() { logger.info(\"Value declared in if:\" + c); }");
			assertEquals("\"Value declared in if:\" + c", c1.getElements(new TypeFilter<>(CtBinaryOperator.class)).get(0).toString());

			CtClass c2 = Launcher.parseClass("class C2 { int count ; void m() { (i++).toString(); (a+b).toString(); }");
			List<CtInvocation> invocations = c2.getElements(new TypeFilter<>(CtInvocation.class));
			assertEquals("super()", invocations.get(0).toString());
			assertEquals("(i++).toString()", invocations.get(1).toString());
			assertEquals("(a + b).toString()", invocations.get(2).toString());
		}

	@Test
	public void testCustomPrettyPrinter() throws Exception {
		// contract: one can use Spoon to write a custom pretty-printer
		// here the pretty-printer writes two spaces before each keyword of the language
		Launcher spoon = new Launcher();
		// Java file to be pretty-printed, can be a folder as well
		spoon.addInputResource("src/test/resources/JavaCode.java");
		spoon.getFactory().getEnvironment().setPrettyPrinterCreator(() -> {
				DefaultJavaPrettyPrinter defaultJavaPrettyPrinter = new DefaultJavaPrettyPrinter(spoon.getFactory().getEnvironment());
				// here we create the custom version of the token writer
				defaultJavaPrettyPrinter.setPrinterTokenWriter(new DefaultTokenWriter() {
					@Override
					public DefaultTokenWriter writeKeyword(String token) {
						// write two spaces and then the keyword
						getPrinterHelper().write("   " + token);
						return this;
					}
				});
				return defaultJavaPrettyPrinter;
		});
		spoon.run();
		// the pretty-printed code is in folder ./spooned (default value that can be set with setOutputDirectory)

		assertTrue(FileUtils.readFileToString(new File("spooned/HelloWorld.java"), "UTF-8").contains("  class"));
	}

	
	@Test
	public void testTypeLostPrintingStringClassReference() {
		// contract: when a class reference is printed, the type is not lost
		String expected = "class A {\n" +
			"    void m() {\n" +
			"        Stream.empty().map(v -> String.class).close();\n" +
			"    }\n" +
			"}";
		CtType<?> type = Launcher.parseClass("class A { void m() { Stream.empty().map(v-> String.class).close(); } }");
		assertEquals(expected, normalizeLineEnds(type.toString()));
	}

	@Test
	public void testTypeLostPrintingListClassReference() {
		// contract: when a class reference is printed, the type is not lost
		String expected = "class A {\n" +
			"    void m() {\n" +
			"        Stream.empty().map(v -> List.class).close();\n" +
			"    }\n" +
			"}";
		CtType<?> type = Launcher.parseClass("class A { void m() { Stream.empty().map(v-> List.class).close(); } }");
		assertEquals(expected, normalizeLineEnds(type.toString()));
	}

	@Test
	public void testTypeLostPrintingFQListClassReference() {
		// contract: when a class reference is printed, the type is not lost
		String expected = "class A {\n" +
			"    void m() {\n" +
				"        Stream.empty().map(v -> List.class).close();\n" +
			"    }\n" +
			"}";
		CtType<?> type = Launcher.parseClass("class A { void m() { Stream.empty().map(v-> java.util.List.class).close(); } }");
		assertEquals(expected, normalizeLineEnds(type.toString()));
	}

	private static String normalizeLineEnds(String s) {
		return s.lines().collect(Collectors.joining("\n"));
	}
}
