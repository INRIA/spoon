/**
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package spoon.smpl;

import org.junit.jupiter.api.Test;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import static spoon.smpl.TestUtils.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
public class EndToEndTests {
	private CtClass<?> getTargetClass(String code) {
		CtModel model = SpoonJavaParser.parse(code);
		return (CtClass<?>) model.getRootPackage()
								 .getTypes()
								 .stream()
								 .filter(ctType -> ctType.getComments()
														 .stream()
														 .noneMatch(comment -> comment.getContent()
																					  .contains("skip")))
								 .findFirst().get();
	}

	private void runSingleTest(String smpl, String inputCode, String expectedCode) {
		SmPLRule rule = SmPLParser.parse(smpl);
		CtClass<?> input = getTargetClass(inputCode);
		CtClass<?> expected = getTargetClass(expectedCode);

		input.getMethods().forEach(method -> {
			if (method.getComments().stream().anyMatch(x -> x.getContent().toLowerCase().equals("skip"))) {
				return;
			}

			if (!rule.isPotentialMatch(method)) {
				return;
			}

			CFGModel model = new CFGModel(methodCfg(method));
			ModelChecker checker = new ModelChecker(model);
			rule.getFormula().accept(checker);

			ModelChecker.ResultSet results = checker.getResult();

			for (ModelChecker.Result result : results) {
				if (!result.getEnvironment().isEmpty()) {
					fail("nonempty environment");
				}
			}

			Transformer.transform(model, results.getAllWitnesses());

			if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {
				Transformer.copyAddedMethods(model, rule);
			}
			model.getCfg().restoreUnsupportedElements();
		});

		assertEquals(expected.toString(), input.toString());
	}

	@Test
	public void test_src_test_resources_endtoend_AnchorAfterDotsBug() {
		// contract: an addition must not be anchored to an element on the opposite side of an intermediate dots operator
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/AnchorAfterDotsBug.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_AppendContextBranch() {
		// contract: a patch should be able to append elements below a context branch
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/AppendContextBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_AppendToContext() {
		// contract: a patch should be able to append elements to a context statement
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/AppendToContext.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_BasicDots() {
		// contract: dots are able to match any number of arbitrary paths
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/BasicDots.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_BasicPatternDisjunction() {
		// contract: matching of pattern disjunction including clause-order priority
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/BasicPatternDisjunction.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Bugs_ArgDotsPatternMatchMismatchedExecutableBug() {
		// contract: pattern matching the expression "f(...)" should not fail to find a mismatch in the executable of the invocation
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Bugs/ArgDotsPatternMatchMismatchedExecutableBug.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Bugs_DotsLeavingScopeBug01() {
		// contract: dots should be prevented from traversing out of the enclosing scope (when forall version)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Bugs/DotsLeavingScopeBug01.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Bugs_DotsLeavingScopeBug02() {
		// contract: dots should be prevented from traversing out of the enclosing scope (when exists version)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Bugs/DotsLeavingScopeBug02.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Bugs_DotsWhenExistsBug() {
		// contract: the when-exists dots modifier should successfully match in the following example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Bugs/DotsWhenExistsBug.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Bugs_EnvironmentNegationBug() {
		// contract: the bug where the environments (Tv1=int, v1=x) and (Tv1=(int), v1=(y)) could not be joined should be fixed
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Bugs/EnvironmentNegationBug.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Bugs_MissingSubstitutionInAddedLocalVarDeclBug() {
		// contract: bound metavariable should be substituted in addition of local variable declaration
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Bugs/MissingSubstitutionInAddedLocalVarDeclBug.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteBranch() {
		// contract: a patch should be able to delete a complete branch statement
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteBranchInBranch() {
		// contract: a patch should be able to delete a complete branch statement nested inside another branch
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteBranchInBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteEnclosingBranch() {
		// contract: a patch should be able to delete an enclosing branch statement while keeping inner context
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteEnclosingBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteEnclosingBranchDots() {
		// contract: a patch should be able to delete an enclosing branch statement while keeping inner context
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteEnclosingBranchDots.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteStmAfterBranch() {
		// contract: only the statement below the branch should be removed
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteStmAfterBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteStmBeforeBranch() {
		// contract: only the statement above the branch should be removed
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteStmBeforeBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DeleteStmInBranch() {
		// contract: only the statement inside the branch should be removed
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DeleteStmInBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsShortestPath() {
		// contract: dots by default should only match the shortest path between enclosing anchors (if any)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsShortestPath.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenAny() {
		// contract: dots shortest path restriction is lifted by using when any
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenAny.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenNeqExpression_DotsWhenNeqExpression01() {
		// contract: due to the constraint on the dots operator the patch should not match m1, but should match and transform m2
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenNeqExpression/DotsWhenNeqExpression01.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenNeqExpression_DotsWhenNeqExpression02() {
		// contract: due to the constraint on the dots operator the patch should not match m2, but should match and transform m1
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenNeqExpression/DotsWhenNeqExpression02.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenNeqExpression_DotsWhenNeqExpression03() {
		// contract: dots for arguments should be supported in expressions of when != expr, both m1 and m2 should be rejected
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenNeqExpression/DotsWhenNeqExpression03.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenNeqExpression_DotsWhenNeqExpression04() {
		// contract: constructor calls should be supported in when != expr, m1 should match but m2 should not
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenNeqExpression/DotsWhenNeqExpression04.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenNeqExpression_DotsWhenNeqExpression05() {
		// contract: dots for arguments in constructor calls should be supported in when != expr, m1 and m2 should match but not m3
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenNeqExpression/DotsWhenNeqExpression05.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWhenNeqExpression_DotsWhenNeqExpression06() {
		// contract: dots for arguments in constructor calls should be supported in when != expr, none of m1,m2,m3 should match
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWhenNeqExpression/DotsWhenNeqExpression06.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWithOptionalMatch_DotsWithOptionalMatchShortestPath() {
		// contract: optdots should match the shortest path between surrounding context elements
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWithOptionalMatch/DotsWithOptionalMatchShortestPath.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_DotsWithOptionalMatch_DotsWithOptionalMatchSingle() {
		// contract: using the <... P ...> dots alternative to include an optional matching of P
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/DotsWithOptionalMatch/DotsWithOptionalMatchSingle.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_EncloseInBranch() {
		// contract: a patch should be able to add a branch statement enclosing context
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/EncloseInBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Exceptions_DotsEnteringTryBlock() {
		// contract: dots should be able to traverse into try blocks
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Exceptions/DotsEnteringTryBlock.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Exceptions_DotsTraversingTryCatch() {
		// contract: dots should be able to traverse over a try-catch
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Exceptions/DotsTraversingTryCatch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Exceptions_ExistsDotsPatchingCatchBlock() {
		// contract: dots in exists mode should patch a statement only found in the catch block
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Exceptions/ExistsDotsPatchingCatchBlock.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_Exceptions_ForallDotsNotPatchingCatchBlock() {
		// contract: dots in forall mode should not patch a statement only found in the catch block
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/Exceptions/ForallDotsNotPatchingCatchBlock.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ExpressionMatch_BasicExpressionMatchAndTransform() {
		// contract: correct application of patch specifying transformation on sub-element expression part of statement
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ExpressionMatch/BasicExpressionMatchAndTransform.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_FieldReads() {
		// contract: correct matching of field reads
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/FieldReads.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_HelloWorld() {
		// contract: hello world template test
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/HelloWorld.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MatchAnyType() {
		// contract: a 'type' metavariable should match any type
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MatchAnyType.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MatchSpecificType() {
		// contract: a concretely given type in SmPL should match that precise type
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MatchSpecificType.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MatchingMethodHeader_MethodHeaderBinding() {
		// contract: binding metavariables on the method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MatchingMethodHeader/MethodHeaderBinding.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MatchingMethodHeader_MethodHeaderDots() {
		// contract: using dots to match arbitrary sequences of parameters in method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MatchingMethodHeader/MethodHeaderDots.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MatchingMethodHeader_MethodHeaderLiteralMatch() {
		// contract: literal matching on the method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MatchingMethodHeader/MethodHeaderLiteralMatch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodBodyAdditions_AddToEmptyMethod() {
		// contract: a patch should be able to add statements to an empty method body
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodBodyAdditions/AddToEmptyMethod.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodBodyAdditions_AddToMethodBottom() {
		// contract: a patch should be able to add statements at the bottom of methods
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodBodyAdditions/AddToMethodBottom.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodBodyAdditions_AddToMethodTop() {
		// contract: a patch should be able to add statements at the top of methods
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodBodyAdditions/AddToMethodTop.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsMatchAny() {
		// contract: the expression 'f(...)' should match any method call to 'f' regardless of argument list
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsMatchAny.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsMatchSingle() {
		// contract: the expression 'f(..., E, ...)' should match any method call to 'f' where the expression E appears anywhere in the argument list
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsMatchSingle.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsMatchSingleAtEnd() {
		// contract: the expression 'f(..., E)' should match any method call to 'f' where the expression E appears as the last argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsMatchSingleAtEnd.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsMatchSingleAtStart() {
		// contract: the expression 'f(E, ...)' should match any method call to 'f' where the expression E appears as the first argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsMatchSingleAtStart.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested1() {
		// contract: the expression 'f(..., g(...), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs (with any argument list for 'g')
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested1.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested2() {
		// contract: the expression 'f(..., g(..., 1, ...), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs containing the argument 1 in its argument list
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested2.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested3() {
		// contract: the expression 'f(..., g(1, ...), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs containing the argument 1 as its first argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested3.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested4() {
		// contract: the expression 'f(..., g(..., 1), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs containing the argument 1 as its last argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested4.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested5() {
		// contract: the expression 'f(..., g(..., 1))' should match any method call to 'f' with last argument a call to 'g' containing the argument 1 as its last argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested5.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested6() {
		// contract: the expression 'f(..., g(1, ...))' should match any method call to 'f' with last argument a call to 'g' containing the argument 1 as its first argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested6.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodCallArgDots_MethodCallArgDotsNested7() {
		// contract: the expression 'f(1, ..., g(..., 3))' should match any method call to 'f' with first argument 1 and last argument a call to 'g' containing the argument 3 as its last argument
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodCallArgDots/MethodCallArgDotsNested7.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodHeaderTransformation_MethodHeaderTransformationAlsoAddingMethods() {
		// contract: a patch should be able to modify the matched header while also adding new methods
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodHeaderTransformation/MethodHeaderTransformationAlsoAddingMethods.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodHeaderTransformation_MethodHeaderTransformationChangeAll() {
		// contract: a patch should be able to specify multiple modifications to a matched method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodHeaderTransformation/MethodHeaderTransformationChangeAll.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodHeaderTransformation_MethodHeaderTransformationChangeName() {
		// contract: a patch should be able to specify a change of return type on a matched method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodHeaderTransformation/MethodHeaderTransformationChangeName.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodHeaderTransformation_MethodHeaderTransformationChangeParams() {
		// contract: a patch should be able to specify modifications to formal parameters on a matched method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodHeaderTransformation/MethodHeaderTransformationChangeParams.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodHeaderTransformation_MethodHeaderTransformationChangeType() {
		// contract: a patch should be able to specify a change of return type on a matched method header
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodHeaderTransformation/MethodHeaderTransformationChangeType.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_MethodsAddedToClass_MethodsAddedToClass() {
		// contract: a patch should be able to add entire methods to the parent class of a patch-context-matching method
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/MethodsAddedToClass/MethodsAddedToClass.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_PrependContextBranch() {
		// contract: a patch should be able to prepend elements above a context branch
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/PrependContextBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_PrependToContext() {
		// contract: a patch should be able to prepend elements to a context statement
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/PrependToContext.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstants001() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstants001.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstantsBranch() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstantsBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstantsBranchMultiple() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstantsBranchMultiple.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstantsBranchMultipleWhenExists() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstantsBranchMultipleWhenExists.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstantsElselessBranch() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstantsElselessBranch.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstantsExpressionlessReturnBug() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstantsExpressionlessReturnBug.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_RemoveLocalsReturningConstants_RemoveLocalsReturningConstantsRejectUsageInBranchCondition() {
		// contract: correct application of remove-locals-returning-constants patch example
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/RemoveLocalsReturningConstants/RemoveLocalsReturningConstantsRejectUsageInBranchCondition.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ReplacedTypeAccesses_ReplacedTypeAccessesMatchExternal() {
		// contract: the statement "setTextSize(WebSettings.TextSize.LARGER);" should be removed (external class version)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ReplacedTypeAccesses/ReplacedTypeAccessesMatchExternal.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ReplacedTypeAccesses_ReplacedTypeAccessesMatchInner() {
		// contract: the statement "setTextSize(WebSettings.TextSize.LARGER);" should be removed (inner class version)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ReplacedTypeAccesses/ReplacedTypeAccessesMatchInner.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ReplacedTypeAccesses_ReplacedTypeAccessesMatchMissing() {
		// contract: the statement "setTextSize(WebSettings.TextSize.LARGER);" should be removed (missing information version). validate-e2e: purposefully-invalid
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ReplacedTypeAccesses/ReplacedTypeAccessesMatchMissing.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ReplacedTypeAccesses_ReplacedTypeAccessesRejectExternal() {
		// contract: the sub-expression "LARGER" in the patch should NOT match the expression "WebSettings.TextSize.LARGER" in the code, no transformation should be applied (external class version)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ReplacedTypeAccesses/ReplacedTypeAccessesRejectExternal.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ReplacedTypeAccesses_ReplacedTypeAccessesRejectInner() {
		// contract: the sub-expression "LARGER" in the patch should NOT match the expression "WebSettings.TextSize.LARGER" in the code, no transformation should be applied (inner class version)
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ReplacedTypeAccesses/ReplacedTypeAccessesRejectInner.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_ReplacedTypeAccesses_ReplacedTypeAccessesRejectMissing() {
		// contract: the sub-expression "LARGER" in the patch should NOT match the expression "WebSettings.TextSize.LARGER" in the code, no transformation should be applied (missing information version). validate-e2e: purposefully-invalid
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/ReplacedTypeAccesses/ReplacedTypeAccessesRejectMissing.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_TernaryExpression() {
		// contract: patches should be able to match on ternary expressions
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/TernaryExpression.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_TypedIdentifierMetavariables1() {
		// contract: correct bindings of explicitly typed identifier metavariables
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/TypedIdentifierMetavariables1.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_TypedIdentifierMetavariables2() {
		// contract: correct bindings of explicitly typed identifier metavariables
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/TypedIdentifierMetavariables2.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_UnsupportedElements_UnsupportedElementsDotsWhenExists() {
		// contract: dots in "when exists" mode should be allowed to traverse over unsupported elements when there exists a path that avoids them
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/UnsupportedElements/UnsupportedElementsDotsWhenExists.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_UnsupportedElements_UnsupportedElementsMatchAfter() {
		// contract: should be able to match and transform things surrounding an unsupported element
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/UnsupportedElements/UnsupportedElementsMatchAfter.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_UnsupportedElements_UnsupportedElementsMatchBefore() {
		// contract: should be able to match and transform things surrounding an unsupported element
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/UnsupportedElements/UnsupportedElementsMatchBefore.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_UnsupportedElements_UnsupportedElementsMatchSurrounding() {
		// contract: should be able to match and transform things surrounding an unsupported element
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/UnsupportedElements/UnsupportedElementsMatchSurrounding.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_UnsupportedElements_UnsupportedElementsRejectDots() {
		// contract: dots with post-context should not be allowed to traverse across unsupported elements
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/UnsupportedElements/UnsupportedElementsRejectDots.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}

	@Test
	public void test_src_test_resources_endtoend_UnsupportedElements_UnsupportedElementsRejectDotsWhenNotEquals() {
		// contract: dots constrained by "when != x" should not be allowed to traverse across unsupported elements even if there is no post-context
		Map<String, String> test = EndToEndTestReader.validate(EndToEndTestReader.readFileOrDefault("src/test/resources/endtoend/UnsupportedElements/UnsupportedElementsRejectDotsWhenNotEquals.txt", ""));
		runSingleTest(test.get("patch"), test.get("input"), test.get("expected"));
	}
}
