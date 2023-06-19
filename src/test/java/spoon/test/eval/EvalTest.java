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
package spoon.test.eval;


import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.AccessibleVariablesFinder;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.eval.EvalHelper;
import spoon.support.reflect.eval.InlinePartialEvaluator;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.test.eval.testclasses.Foo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.utils.ModelUtils.build;

public class EvalTest {

	@Test
	public void testStringConcatenation() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testStrings").get(0).getBody();
		assertEquals(4, b.getStatements().size());
		b = b.partiallyEvaluate();
		b = type.getMethodsByName("testInts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("// if removed", b.getStatements().get(0).toString());
	}

	@Test
	public void testArrayLength() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testArray").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("// if removed", b.getStatements().get(0).toString());
	}

	@Test
	public void testDoNotSimplify() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testDoNotSimplify").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("java.lang.System.out.println(((\"enter: \" + className) + \" - \") + methodName)", b.getStatements().get(0).toString());
	}

	@Test
	public void testDoNotSimplifyCasts() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testDoNotSimplifyCasts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("return ((U) (java.lang.Object) (spoon.test.eval.testclasses.ToEvaluate.castTarget(element).getClass()))", b.getStatements().get(0).toString());
	}

	@Test
	public void testScanAPartiallyEvaluatedElement() throws Exception {
		// contract: once partially evaluated a code element should be still visitable to find variables
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testDoNotSimplifyCasts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();

		AccessibleVariablesFinder avf = new AccessibleVariablesFinder(b);
		List<CtVariable> ctVariables = avf.find();
		assertEquals(1, ctVariables.size());
	}

	@Test
	public void testTryCatchAndStatement() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("tryCatchAndStatement").get(0).getBody();
		assertEquals(2, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals(2, b.getStatements().size());
	}

	@Test
	public void testDoNotSimplifyToExpressionWhenStatementIsExpected() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("simplifyOnlyWhenPossible").get(0).getBody();
		assertEquals(3, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("spoon.test.eval.testclasses.ToEvaluate.class.getName()", b.getStatements().get(0).toString());
		assertEquals("java.lang.System.out.println(spoon.test.eval.testclasses.ToEvaluate.getClassLoader())", b.getStatements().get(1).toString());
		assertEquals("return \"spoon.test.eval.testclasses.ToEvaluate\"", b.getStatements().get(2).toString());
	}

	@Test
	public void testIsKnownAtCompileTime() throws Exception {
		// contract: one can ask whether an expression is known at compile time
		Launcher launcher = new Launcher();
		CtExpression el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3").compile();
		assertTrue(EvalHelper.isKnownAtCompileTime(el));

		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());
		CtExpression<?> foo = ((CtReturn)type.getMethodsByName("foo").get(0).getBody().getStatement(0)).getReturnedExpression();
		assertFalse(EvalHelper.isKnownAtCompileTime(foo));

		CtExpression<?> foo2 = ((CtReturn)type.getMethodsByName("foo2").get(0).getBody().getStatement(0)).getReturnedExpression();
		assertTrue(EvalHelper.isKnownAtCompileTime(foo2));

		CtExpression<?> foo3 = ((CtReturn)type.getMethodsByName("foo3").get(0).getBody().getStatement(0)).getReturnedExpression();
		assertTrue(EvalHelper.isKnownAtCompileTime(foo3));

	}

	@Test
	public void testVisitorPartialEvaluator_binary() {
		Launcher launcher = new Launcher();

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("0+1").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("1", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("3", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3>0").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("true", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+3-1)*3<=0").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("false", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = createBinaryOperatorOnLiterals(launcher.getFactory(), (byte) 2, 2, BinaryOperatorKind.SL);
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("8", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = createBinaryOperatorOnLiterals(launcher.getFactory(), (short) 2, 2, BinaryOperatorKind.SL);
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("8", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("2<<2").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("8", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(1L<<53)-1").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("9007199254740991L", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = createBinaryOperatorOnLiterals(launcher.getFactory(), (byte) 8, 2, BinaryOperatorKind.SR);
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("2", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = createBinaryOperatorOnLiterals(launcher.getFactory(), (short) 8, 2, BinaryOperatorKind.SR);
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("2", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("8>>2").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("2", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(9007199254740991L>>53)+1").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("1L", elnew.toString());
		}
	}

	@Test
	public void testVisitorPartialEvaluator_unary() {

    	{ // NEG urnary operator
      		Launcher launcher = new Launcher();
      		CtCodeElement el =
          		launcher.getFactory().Code().createCodeSnippetExpression("-(100+1)").compile();
      		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
      		CtElement element = eval.evaluate(el);
      		assertEquals("-101", element.toString());
    	}
	}

	@Test
	public void testVisitorPartialEvaluator_if() {
		Launcher launcher = new Launcher();
		{ // the untaken branch is removed
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetStatement("if (false) {System.out.println(\"foo\");} else {System.out.println(\"bar\");} ").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("{" + System.lineSeparator()
					+ "    java.lang.System.out.println(\"bar\");" + System.lineSeparator()
					+ "}", elnew.toString());
		}
	}

	@Test
	public void testVisitorPartialEvaluatorScanner() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/eval/testclasses/Foo.java");
		launcher.buildModel();
		PartialEvaluator eval = launcher.getFactory().Eval().createPartialEvaluator();
		CtType<?> foo = launcher.getFactory().Type().get((Class<?>) Foo.class);
		foo.accept(new InlinePartialEvaluator(eval));
		assertEquals("false", foo.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0).getDefaultExpression().toString());
		// the if has been removed
		assertEquals(0, foo.getElements(new TypeFilter<>(CtIf.class)).size());
	}

	@Test
	public void testconvertElementToRuntimeObject() {
		// contract: getCorrespondingRuntimeObject works well for all kinds of expression
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/eval/testclasses/Foo.java");
		launcher.buildModel();
		CtType<?> foo = launcher.getFactory().Type().get((Class<?>) Foo.class);

		// also works for non-enum fields with partial evaluation embedded in convertElementToRuntimeObject
		assertEquals(false, EvalHelper.convertElementToRuntimeObject(foo.getField("b6").getDefaultExpression()));

		// impossible with no partial evaluation
		try {
			assertEquals(false, EvalHelper.getCorrespondingRuntimeObject(foo.getField("b6").getDefaultExpression()));
			fail();
		} catch (SpoonException expected) {}

		// also works for static runtime fields
		assertEquals(Integer.MAX_VALUE, EvalHelper.convertElementToRuntimeObject(foo.getField("i1").getDefaultExpression()));
		assertEquals(Integer.MAX_VALUE, EvalHelper.getCorrespondingRuntimeObject(foo.getField("i1").getDefaultExpression()));
		assertEquals(File.pathSeparator, EvalHelper.convertElementToRuntimeObject(foo.getField("str1").getDefaultExpression()));
		assertEquals(File.pathSeparator, EvalHelper.getCorrespondingRuntimeObject(foo.getField("str1").getDefaultExpression()));

	}

	private CtBinaryOperator<?> createBinaryOperatorOnLiterals(Factory factory, Object leftLiteral, Object rightLiteral, BinaryOperatorKind opKind) {
		return factory.createBinaryOperator(factory.createLiteral(leftLiteral), factory.createLiteral(rightLiteral), opKind);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		useHeadersInDisplayName = true,
		value = {
			" Literal  | Expected  ",
			"-1.234567 | -1.234567 ",
			"-2.345F   | -2.345F   ",
			"-3        | -3        ",
			"-4L       | -4L       "
		}
	)
	void testDoublePrecisionLost(String literal, String expected) {
		// contract: the partial evaluation of a binary operator on literals does not lose precision for double and float
		String code = "public class Test {\n"
		+ "	void test() {\n"
		+ "		System.out.println(%s);\n"
		+ "	}\n"
		+ "}\n";
		CtMethod<?> method =  Launcher.parseClass(String.format(code, literal)).getElements(new TypeFilter<>(CtMethod.class)).get(0);
		CtInvocation<?> parameter = method.getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		method.setBody(method.getBody().partiallyEvaluate());
		assertEquals(expected, parameter.getArguments().get(0).toString());
	}

	private static <T> Set<T> concat(Set<? extends T> left, Set<? extends T> right) {
		Set<T> result = new HashSet<>(left);
		result.addAll(right);
		return result;
	}

	private static <T> Set<T> concat(Set<? extends T> left) {
		return new HashSet<>(left);
	}

	private static Stream<Arguments> provideBinaryOperatorsForAllLiterals() {
		Set<Class<?>> wholeNumbers = Set.of(
			byte.class,
			short.class,
			int.class,
			long.class
		);
		// all primitive types where the boxed type implements Number
		Set<Class<?>> numericalTypes = concat(wholeNumbers, Set.of(double.class, float.class));
		// ^ here boolean.class and char.class are missing

		Set<Class<?>> truePrimitives = concat(numericalTypes, Set.of(boolean.class /*, char.class */));

		// additionally, there exist: String literals, class literals, null literals, and arrays

		Map<BinaryOperatorKind, Set<Class<?>>> supportedTypes = Map.ofEntries(
			// arithmetic operators:
			Map.entry(BinaryOperatorKind.MUL, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.DIV, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.MOD, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.PLUS, concat(numericalTypes, Set.of(/*char.class,*/ String.class))),
			Map.entry(BinaryOperatorKind.MINUS, concat(numericalTypes /*, Set.of(char.class) */)),
			// relational operators:
			Map.entry(BinaryOperatorKind.EQ, concat(truePrimitives, Set.of(String.class))),
			Map.entry(BinaryOperatorKind.NE, concat(truePrimitives, Set.of(String.class))),
			Map.entry(BinaryOperatorKind.LE, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.LT, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.GE, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.GT, concat(numericalTypes /*, Set.of(char.class) */)),
			// logical operators:
			Map.entry(BinaryOperatorKind.AND, Set.of(boolean.class)),
			Map.entry(BinaryOperatorKind.OR, Set.of(boolean.class)),
			// bitwise operators:
			Map.entry(BinaryOperatorKind.BITAND, concat(wholeNumbers, Set.of(boolean.class /*, Set.of(char.class) */))),
			Map.entry(BinaryOperatorKind.BITOR, concat(wholeNumbers, Set.of(boolean.class /*, Set.of(char.class) */))),
			Map.entry(BinaryOperatorKind.BITXOR, concat(wholeNumbers, Set.of(boolean.class /*, Set.of(char.class) */))),
			// boolean is not supported by the following operators:
			Map.entry(BinaryOperatorKind.SL, concat(wholeNumbers /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.SR, concat(wholeNumbers /*, Set.of(char.class) */)),
			Map.entry(BinaryOperatorKind.USR, concat(wholeNumbers /*, Set.of(char.class) */)),
			// other operators:
			// TODO: what kind does this support? Object.class?
			Map.entry(BinaryOperatorKind.INSTANCEOF, Set.of())
		);

		return Stream.of(
			Map.entry(byte.class, List.of("((byte) 1)", "((byte) 2)")),
			Map.entry(short.class, List.of("((short) 1)", "((short) 2)")),
			Map.entry(int.class, List.of("1", "2")),
			Map.entry(long.class, List.of("1l", "2l")),
			Map.entry(float.class, List.of("1.0f", "2.0f")),
			Map.entry(double.class, List.of("1.0d", "2.0d")),
			Map.entry(boolean.class, List.of("true", "false")),
			Map.entry(char.class, List.of("1.0d", "2.0d")),
			Map.entry(String.class, List.of("\"a\"", "\"b\""))
		).flatMap(entry -> {
			Class<?> type = entry.getKey();
			String left = entry.getValue().get(0);
			String right = entry.getValue().get(1);

			return supportedTypes.entrySet()
				.stream()
				.filter(e -> e.getValue().contains(type))
				.map(Map.Entry::getKey)
				.map(operator -> Arguments.of(operator, left, right));
		});
	}

	// TODO: those are from spoon.reflect.visitor.OperatorHelper, make this public or is this copy okay?
	private static String getOperatorText(BinaryOperatorKind o) {
		switch (o) {
			case OR:
				return "||";
			case AND:
				return "&&";
			case BITOR:
				return "|";
			case BITXOR:
				return "^";
			case BITAND:
				return "&";
			case EQ:
				return "==";
			case NE:
				return "!=";
			case LT:
				return "<";
			case GT:
				return ">";
			case LE:
				return "<=";
			case GE:
				return ">=";
			case SL:
				return "<<";
			case SR:
				return ">>";
			case USR:
				return ">>>";
			case PLUS:
				return "+";
			case MINUS:
				return "-";
			case MUL:
				return "*";
			case DIV:
				return "/";
			case MOD:
				return "%";
			case INSTANCEOF:
				return "instanceof";
			default:
				throw new SpoonException("Unsupported operator " + o.name());
		}
	}
	private static String getOperatorText(UnaryOperatorKind o) {
		switch (o) {
			case POS:
				return "+";
			case NEG:
				return "-";
			case NOT:
				return "!";
			case COMPL:
				return "~";
			case PREINC:
				return "++";
			case PREDEC:
				return "--";
			case POSTINC:
				return "++";
			case POSTDEC:
				return "--";
			default:
				throw new SpoonException("Unsupported operator " + o.name());
		}
	}


	@ParameterizedTest
	@MethodSource("provideBinaryOperatorsForAllLiterals")
	void testVisitCtBinaryOperatorLiteralType(BinaryOperatorKind operator, String left, String right) {
		// TODO: what happens with assignment operators?
		// contract: the type is preserved during partial evaluation
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(%s);\n"
			+ "	}\n"
			+ "}\n";
		CtBinaryOperator<?> ctBinaryOperator =  Launcher.parseClass(String.format(
				code,
				String.format("(%s) %s (%s)", left, getOperatorText(operator), right)
			))
			.getElements(new TypeFilter<>(CtBinaryOperator.class))
			.get(0);
		CtType<?> currentType = ctBinaryOperator.getType().getTypeDeclaration();
		CtExpression<?> evaluated = ctBinaryOperator.partiallyEvaluate();
		assertNotNull(
			evaluated.getType(),
			String.format("type of '%s' is null after evaluation", ctBinaryOperator)
		);
		assertEquals(currentType, evaluated.getType().getTypeDeclaration());
	}

	private static Stream<Arguments> provideUnaryOperatorsForAllLiterals() {
		Set<Class<?>> wholeNumbers = Set.of(
			byte.class,
			short.class,
			int.class,
			long.class
		);
		// all primitive types where the boxed type implements Number
		Set<Class<?>> numericalTypes = concat(wholeNumbers, Set.of(double.class, float.class));
		// ^ here boolean.class and char.class are missing

		Map<UnaryOperatorKind, Set<Class<?>>> supportedTypes = Map.ofEntries(
			// Map.entry(UnaryOperatorKind.COMPL, concat(numericalTypes, Set.of(char.class))),
			Map.entry(UnaryOperatorKind.NEG, concat(numericalTypes /*, Set.of(char.class) */)),
			Map.entry(UnaryOperatorKind.NOT, Set.of(boolean.class))
			// Map.entry(UnaryOperatorKind.POS, concat(numericalTypes, Set.of(char.class))),
		);

		return Stream.of(
			Map.entry(byte.class, "((byte) 1)"),
			Map.entry(short.class, "((short) 1)"),
			Map.entry(int.class, "1"),
			Map.entry(long.class, "1l"),
			Map.entry(float.class, "1.0f"),
			Map.entry(double.class, "1.0d"),
			Map.entry(boolean.class, "true"),
			Map.entry(char.class, "1.0d")
		).flatMap(entry -> {
			Class<?> type = entry.getKey();
			String value = entry.getValue();

			return supportedTypes.entrySet()
				.stream()
				.filter(e -> e.getValue().contains(type))
				.map(Map.Entry::getKey)
				.map(operator -> Arguments.of(operator, value));
		});
	}

	@ParameterizedTest
	@MethodSource("provideUnaryOperatorsForAllLiterals")
	void testVisitCtUnaryOperatorLiteralType(UnaryOperatorKind operator, String value) {
		// contract: the type is preserved during partial evaluation
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(%s);\n"
			+ "	}\n"
			+ "}\n";
		CtUnaryOperator<?> ctUnaryOperator =  Launcher.parseClass(String.format(
				code,
				String.format("%s(%s)", getOperatorText(operator), value)
			))
			.getElements(new TypeFilter<>(CtUnaryOperator.class))
			.get(0);
		CtType<?> currentType = ctUnaryOperator.getType().getTypeDeclaration();
		CtExpression<?> evaluated = ctUnaryOperator.partiallyEvaluate();
		assertNotNull(
			evaluated.getType(),
				String.format("type of '%s' is null after evaluation", ctUnaryOperator)
		);
		assertEquals(currentType, evaluated.getType().getTypeDeclaration());
	}

	@Test
	void testVisitCtFieldAccessLiteralType() {
		// contract: the type is preserved during partial evaluation
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(String.class);\n"
			+ "	}\n"
			+ "}\n";
		CtFieldAccess<?> ctFieldAccess =  Launcher.parseClass(code)
			.getElements(new TypeFilter<>(CtFieldAccess.class))
			.get(0);
		CtType<?> currentType = ctFieldAccess.getType().getTypeDeclaration();
		CtExpression<?> evaluated = ctFieldAccess.partiallyEvaluate();
		assertNotNull(
			evaluated.getType(),
			String.format("type of '%s' is null after evaluation", ctFieldAccess)
		);
		assertEquals(currentType, evaluated.getType().getTypeDeclaration());
	}
}
