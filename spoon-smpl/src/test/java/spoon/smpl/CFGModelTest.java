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

import fr.inria.controlflow.BranchKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.*;
import spoon.smpl.formula.*;
import spoon.smpl.label.PropositionLabel;
import spoon.smpl.label.StatementLabel;
import static spoon.smpl.TestUtils.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CFGModelTest {
	@BeforeEach
	public void before() {
		resetControlFlowNodeCounter();
	}

	@Test
	public void testSimple() {

		// contract: CFGModel should produce a checkable model from a given CFG

		CtMethod<?> method = parseMethod("int m() { int x = 1; return x + 1; }");
		CFGModel model = new CFGModel(methodCfg(method));

		Formula phi = new And(
				new Statement(parseStatement("int x = 1;")),
				new ExistsNext(
						new Statement(parseReturnStatement("return x + 1;"))));

		ModelChecker checker = new ModelChecker(model);
		phi.accept(checker);
		//System.out.println(model.getCfg().toGraphVisText());
		assertEquals(res(4, env()), checker.getResult());
	}

	@Test
	public void testBranch() {

		// TODO: split this test into multiple tests with clear contracts

		CtMethod<?> method = parseMethod("int m() { int x = 8; if (x > 0) { return 1; } else { return 0; } }");
		CFGModel model = new CFGModel(methodCfg(method));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);
		Formula phi;

		phi = new Branch(parseStatement("if (x > 0) {}"));
		phi.accept(checker);
		assertEquals(res(5, env()), checker.getResult());

		phi = new And(new Branch(parseStatement("if (x > 0) {}")),
					  new ExistsNext(new And(new Proposition("falseBranch"),
											 new AllNext(new Statement(parseStatement("return 0;"))))));
		phi.accept(checker);
		assertEquals(res(5, env()), checker.getResult());

		phi = new And(new Branch(parseStatement("if (x > 0) {}")),
					  new AllNext(new Statement(parseStatement("return 0;"))));
		phi.accept(checker);
		assertEquals(res(), checker.getResult());

		phi = new Or(new Statement(parseStatement("return 1;")),
					 new Statement(parseStatement("return 0;")));
		phi.accept(checker);
		assertEquals(res(8, env(), 11, env()), checker.getResult());

		phi = new And(new Branch(parseStatement("if (x > 0) {}")),
					  new AllNext(new Or(new And(new Proposition("trueBranch"),
												 new AllNext(new Statement(parseStatement("return 1;")))),
										 new And(new Proposition("falseBranch"),
												 new AllNext(new Statement(parseStatement("return 0;")))))));
		phi.accept(checker);
		assertEquals(res(5, env()), checker.getResult());
	}

	@Test
	public void testBeginNodeBug() {

		// contract: the CFGModel should not include a state for the BEGIN node

		Model model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 1; } ")));
		ModelChecker checker = new ModelChecker(model);

		new AllNext(new AllNext(new Statement(parseStatement("int x = 1;")))).accept(checker);

		assertEquals(res(), checker.getResult());

		// Before bugfix was [(2, {})] where 2 is the ID of the BEGIN node in the CFG
	}

	@Test
	public void testExitNodeHasSelfLoop() {

		// contract: the exit node should have itself as its single successor

		CFGModel model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 1; } ")));

		model.getCfg().findNodesOfKind(BranchKind.EXIT).forEach((node) -> {
			// TODO: this assumes state ids correspond to node ids which isnt being tested
			assertEquals(1, model.getSuccessors(node.getId()).size());
			assertTrue(node.getId() == model.getSuccessors(node.getId()).get(0));
		});
	}

	@Test
	public void testBranchAnnotations() {

		// contract: a CFGModel should annotate branches with proposition labels

		Model model = new CFGModel(methodCfg(parseMethod("int foo(int n) {     \n" +
														 "    if (n > 0) {     \n" +
														 "        return 1;    \n" +
														 "    } else {         \n" +
														 "        return 0;    \n" +
														 "    }                \n" +
														 "}                    \n")));

		for (int state : model.getStates()) {
			for (Label label : model.getLabels(state)) {
				if (label instanceof StatementLabel) {
					StatementLabel stmLabel = (StatementLabel) label;

					if (stmLabel.getCodeElement().toString().equals("return 1")) {
						for (int otherState : model.getStates()) {
							if (model.getSuccessors(otherState).contains(state)) {
								assertTrue(model.getLabels(otherState).contains(new PropositionLabel("trueBranch")));
							}
						}
					} else if (stmLabel.getCodeElement().toString().equals("return 0")) {
						for (int otherState : model.getStates()) {
							if (model.getSuccessors(otherState).contains(state)) {
								assertTrue(model.getLabels(otherState).contains(new PropositionLabel("falseBranch")));
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testToString() {

		// contract: CFGModel should provide a useful string representation

		Model model = new CFGModel(methodCfg(parseMethod("int foo(int n) {     \n" +
														 "    if (n > 0) {     \n" +
														 "        return 1;    \n" +
														 "    } else {         \n" +
														 "        return 0;    \n" +
														 "    }                \n" +
														 "}                    \n")));

		assertEquals("CFGModel(states=[1, 4, 5, 6, 7, 9, 10, 13], successors={1->1, 4->6, 4->9, 5->1, 6->7, 7->1, 9->10, 10->1, 13->4}, labels={1: [end], 4: [if (n > 0)], 5: [after, Metadata(parent:0)], 6: [trueBranch, Metadata(parent:0)], 7: [return 1], 9: [falseBranch, Metadata(parent:0)], 10: [return 0], 13: [methodHeader, int foo(int n)]})",
					 model.toString());
	}
}
