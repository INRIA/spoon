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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
import spoon.reflect.code.CtLiteral;
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
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.AccessibleVariablesFinder;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.OperatorHelper;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.reflect.eval.EvalHelper;
import spoon.support.reflect.eval.InlinePartialEvaluator;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.test.eval.testclasses.Foo;
import spoon.testing.utils.GitHubIssue;

import static org.junit.jupiter.api.Assertions.*;
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

	private static <T> CtTypeReference<?> inferType(CtBinaryOperator<T> ctBinaryOperator) {
		switch (ctBinaryOperator.getKind()) {
			case AND:
			case OR:
			case INSTANCEOF:
			case EQ:
			case NE:
			case LT:
			case LE:
			case GT:
			case GE:
				return ctBinaryOperator.getFactory().Type().booleanPrimitiveType();
			case SL:
			case SR:
			case USR:
			case MUL:
			case DIV:
			case MOD:
			case MINUS:
			case PLUS:
			case BITAND:
			case BITXOR:
			case BITOR:
				return OperatorHelper.getPromotedType(
					ctBinaryOperator.getKind(),
					ctBinaryOperator.getLeftHandOperand(),
					ctBinaryOperator.getRightHandOperand()
				).orElseThrow();
			default:
				throw new IllegalArgumentException("Unknown operator: " + ctBinaryOperator.getKind());
		}
	}

	private CtBinaryOperator<?> createBinaryOperatorOnLiterals(Factory factory, Object leftLiteral, Object rightLiteral, BinaryOperatorKind opKind) {
		CtBinaryOperator<?> result = factory.createBinaryOperator(factory.createLiteral(leftLiteral), factory.createLiteral(rightLiteral), opKind);
		if (result.getType() == null) {
			result.setType(inferType(result));
		}
		return result;
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

	private static final Map<Class<?>, Function<Factory, CtLiteral<?>>> LITERAL_PROVIDER = Map.ofEntries(
		Map.entry(byte.class, factory -> factory.createLiteral((byte) 1)),
		Map.entry(short.class, factory -> factory.createLiteral((short) 1)),
		Map.entry(int.class, factory -> factory.createLiteral((int) 1)),
		Map.entry(long.class, factory -> factory.createLiteral(1L)),
		Map.entry(float.class, factory -> factory.createLiteral(1.0f)),
		Map.entry(double.class, factory -> factory.createLiteral(1.0d)),
		Map.entry(boolean.class, factory -> factory.createLiteral(true)),
		Map.entry(char.class, factory -> factory.createLiteral('a')),
		Map.entry(String.class, factory -> factory.createLiteral("a")),
		// null can be any type, so use Object.class
		Map.entry(Object.class, factory -> factory.createLiteral(null))
	);

	// Returns a stream of all ordered pairs. For example, cartesianProduct([1, 2], [a, b])
	// returns [(1, a), (1, b), (2, a), (2, b)]
	private static <A, B> Stream<Map.Entry<A, B>> cartesianProduct(Collection<? extends A> left, Collection<? extends B> right) {
		return left.stream().flatMap(l -> right.stream().map(r -> Map.entry(l, r)));
	}

	private static Stream<Arguments> provideBinaryOperatorsForAllLiterals() {
		// This generates all combinations of binary operators and literals:
		//
		// There are 10 types, so 10 * 10 = 100 pairs
		// For each pair, all operators are tested: 100 * 19 = 1900 tests
		return cartesianProduct(LITERAL_PROVIDER.entrySet(), LITERAL_PROVIDER.entrySet())
			.flatMap(tuple -> Arrays.stream(BinaryOperatorKind.values())
				// not yet implemented and does not make sense on literals
				.filter(operator -> operator != BinaryOperatorKind.INSTANCEOF)
				.map(operator -> Arguments.of(operator, tuple.getKey().getKey(), tuple.getKey().getValue(), tuple.getValue().getKey(), tuple.getValue().getValue())));
	}

	@ParameterizedTest(name = "{0}({1}, {3})")
	@MethodSource("provideBinaryOperatorsForAllLiterals")
	void testVisitCtBinaryOperatorLiteralType(
		BinaryOperatorKind operator,
		Class<?> leftType,
		Function<Factory, CtLiteral<?>> leftLiteralProvider,
		Class<?> rightType,
		Function<Factory, CtLiteral<?>> rightLiteralProvider
	) {
		// contract: the type is preserved during partial evaluation

		Launcher launcher = new Launcher();

		CtLiteral<?> leftLiteral = leftLiteralProvider.apply(launcher.getFactory());
		CtLiteral<?> rightLiteral = rightLiteralProvider.apply(launcher.getFactory());

		Optional<CtTypeReference<?>> expectedType = OperatorHelper.getPromotedType(operator, leftLiteral, rightLiteral);

		if (expectedType.isEmpty()) {
			return;
		}
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(%s);\n"
			+ "	}\n"
			+ "}\n";

		launcher.addInputResource(new VirtualFile(String.format(
			code,
			String.format("(%s) %s (%s)", leftLiteral, OperatorHelper.getOperatorText(operator), rightLiteral)
		)));

		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);

		CtClass<?> ctClass = (CtClass<?>) launcher.buildModel().getAllTypes().stream().findFirst().get();
		CtBinaryOperator<?> ctBinaryOperator = ctClass
			.getElements(new TypeFilter<>(CtBinaryOperator.class))
			.get(0);

		CtType<?> currentType = ctBinaryOperator.getType().getTypeDeclaration().clone();
		CtExpression<?> evaluated = ctBinaryOperator.partiallyEvaluate();
		assertNotNull(
			evaluated.getType(),
			String.format("type of '%s' is null after evaluation", ctBinaryOperator)
		);
		assertEquals(currentType, evaluated.getType().getTypeDeclaration());
	}

	@Test
	void testEvaluateLiteralTypeCasts() {
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println((byte) 400 + 20);\n"
			+ "	}\n"
			+ "}\n";
		CtBinaryOperator<?> ctBinaryOperator =  Launcher.parseClass(code)
			.getElements(new TypeFilter<>(CtBinaryOperator.class))
			.get(0);
		CtLiteral<?> evaluated = ctBinaryOperator.partiallyEvaluate();
		assertNotNull(
			evaluated.getType(),
			String.format("type of '%s' is null after evaluation", ctBinaryOperator)
		);
		assertEquals(
			ctBinaryOperator.getFactory().Type().integerPrimitiveType(),
			evaluated.getType()
		);
		assertEquals(
			-92,
			evaluated.getValue()
		);
	}

	private static Stream<Arguments> provideUnaryOperatorsForAllLiterals() {
		// This generates all combinations of unary operators and literals:
		return LITERAL_PROVIDER.entrySet()
			.stream()
			// String cannot be used with unary operators
			.filter(entry -> !entry.getKey().equals(String.class))
			.flatMap(entry -> Arrays.stream(UnaryOperatorKind.values())
				.map(operator -> Arguments.of(operator, entry.getKey(), entry.getValue()))
			);
	}

	@ParameterizedTest(name = "{0}({1})")
	@MethodSource("provideUnaryOperatorsForAllLiterals")
	void testVisitCtUnaryOperatorLiteralType(UnaryOperatorKind operator, Class<?> type, Function<Factory, CtLiteral<?>> provider) {
		// contract: the type is preserved during partial evaluation
		Launcher launcher = new Launcher();

		CtLiteral<?> literal = provider.apply(launcher.getFactory());

		Optional<CtTypeReference<?>> expectedType = OperatorHelper.getPromotedType(operator, literal);

		if (expectedType.isEmpty()) {
			return;
		}

		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(%s);\n"
			+ "	}\n"
			+ "}\n";

		launcher.addInputResource(new VirtualFile(String.format(
			code,
			String.format("%s(%s)", OperatorHelper.getOperatorText(operator), literal)
		)));

		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);

		CtClass<?> ctClass = (CtClass<?>) launcher.buildModel().getAllTypes().stream().findFirst().get();
		CtUnaryOperator<?> ctUnaryOperator =  ctClass
			.getElements(new TypeFilter<>(CtUnaryOperator.class))
			.get(0);
		CtType<?> currentType = ctUnaryOperator.getType().getTypeDeclaration().clone();
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

	@Test
	void testVisitCtBinaryOperatorIntegerDivision() {
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(1 / 0);\n"
			+ "	}\n"
			+ "}\n";
		CtBinaryOperator<?> ctBinaryOperator =  Launcher.parseClass(code)
			.getElements(new TypeFilter<>(CtBinaryOperator.class))
			.get(0);
		SpoonException exception = assertThrows(
			SpoonException.class,
			ctBinaryOperator::partiallyEvaluate
		);

		assertEquals(
			"Expression '1 / 0' evaluates to '1 / 0' which can not be evaluated",
			exception.getMessage()
		);
	}

	@Test
	void testVisitCtBinaryOperatorFloatingDivision() {
		String code = "public class Test {\n"
			+ "	void test() {\n"
			+ "		System.out.println(1.0 / 0);\n"
			+ "	}\n"
			+ "}\n";
		CtBinaryOperator<?> ctBinaryOperator =  Launcher.parseClass(code)
			.getElements(new TypeFilter<>(CtBinaryOperator.class))
			.get(0);
		CtLiteral<?> ctLiteral = ctBinaryOperator.partiallyEvaluate();

		assertEquals(
			ctBinaryOperator.getFactory().createLiteral(Double.POSITIVE_INFINITY),
			ctLiteral
		);
	}

	@Test
	@GitHubIssue(issueNumber = 5001, fixed = true)
	public void testVisitCtLiteralWithLongStringValue() throws Exception {
		CtClass<?> ctClass = Launcher.parseClass(Files.readString(Paths.get("src/test/java/spoon/test/prettyprinter/testclasses/SampleClassIssue5001.java")));
		CtExpression<?> sql = ctClass.getField("sql").getAssignment();
		StringBuilder result = new StringBuilder();
		sql.accept(new CtScanner() {
			@Override
			public <T> void visitCtLiteral(CtLiteral<T> literal) {
				result.append(literal.getValue());
			}
		});

		String expectedValue = "Select distinct t.NETWORK_IP, t.NETWORK_IP1, t.NETWORK_IP2, " +
				"t.NETWORK_IP3, t.NETWORK_IP4 from (SELECT DISTINCT t1.ipv4digit1 || '.' || t1.ipv4digit2 || '.' || " +
				"t1.ipv4digit3 || '.0' network_ip, TO_NUMBER (t1.ipv4digit1) network_ip1, TO_NUMBER (t1.ipv4digit2) " +
				"network_ip2, TO_NUMBER (t1.ipv4digit3) network_ip3, TO_NUMBER ('0') network_ip4, t1.t2_team_id, " +
				"t1.system_owner_id, t1.system_owner_team_id FROM ip_info t1 where t1.binary_ip >= '' and t1.binary_ip " +
				"<= '' ORDER BY network_ip1, network_ip2, network_ip3 ) t order by t.NETWORK_IP1,t.NETWORK_IP2,t.NETWORK_IP3,t.NETWORK_IP4";

		String actualValue = result.toString().strip().replaceAll("\\s{2,}", " ");
		assertEquals(expectedValue, actualValue);
	}
}
