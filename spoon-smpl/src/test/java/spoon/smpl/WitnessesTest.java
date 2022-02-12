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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.*;
import spoon.smpl.metavars.ConstantConstraint;
import spoon.smpl.metavars.IdentifierConstraint;
import static spoon.smpl.TestUtils.*;

import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WitnessesTest {
	private static Formula stmt(String code, Map<String, MetavariableConstraint> metavars) {
		return new Statement(parseStatement(code), metavars);
	}

	@BeforeEach
	public void before() {
		resetControlFlowNodeCounter();
	}

	@Test
	public void testCapture() {

		// contract: witnesses capture environment bindings in nesting order corresponding to ExistsVar formula elements

		CFGModel model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 3; }")));

		CtLocalVariable<?> ctLocalVariable = (CtLocalVariable<?>) model.getCfg().findNodeById(4).getStatement();
		CtVariableReference<?> x = ctLocalVariable.getReference();
		CtLiteral<?> three = (CtLiteral<?>) ctLocalVariable.getAssignment();

		ModelChecker checker = new ModelChecker(model);

		Map<String, MetavariableConstraint> metavars = makeMetavars("v1", new IdentifierConstraint(),
																	"c", new ConstantConstraint());

		Formula phi = new ExistsVar("v1",
									new ExistsVar("c", stmt("int v1 = c;", metavars)));

		phi.accept(checker);
		ModelChecker.ResultSet result = checker.getResult();

		assertEquals(new HashSet<>(asList(witness(4, "v1", x, witness(4, "c", three)))),
					 result.getAllWitnesses());
	}

	@Test
	public void testDifferentValuesInDifferentBranches() {

		// contract: witnesses allow metavariables to bind to different values in different branches

		CFGModel model = new CFGModel(methodCfg(parseMethod("int pos(int n) {  \n" +
															"    int x;        \n" +
															"    if (n > 0) {  \n" +
															"        x = 1;    \n" +
															"    } else {      \n" +
															"        x = 0;    \n" +
															"    }             \n" +
															"}                 \n")));

		ModelChecker checker = new ModelChecker(model);

		CtLocalVariable<?> ctLocalVariable = (CtLocalVariable<?>) model.getCfg().findNodeById(4).getStatement();
		CtVariableReference<?> x = ctLocalVariable.getReference();

		CtLiteral<?> one = (CtLiteral<?>) ((CtAssignment<?, ?>) model.getCfg().findNodeById(8).getStatement()).getAssignment();
		CtLiteral<?> zero = (CtLiteral<?>) ((CtAssignment<?, ?>) model.getCfg().findNodeById(11).getStatement()).getAssignment();

		Map<String, MetavariableConstraint> metavars = makeMetavars("v1", new IdentifierConstraint(),
																	"c", new ConstantConstraint());

		Formula phi = new ExistsVar("v1", new And(stmt("int v1;", metavars),
												  new AllNext(
														  new AllUntil(new True(),
																	   new ExistsVar("c", stmt("v1 = c;", metavars))))));

		phi.accept(checker);

		assertEquals(new HashSet<>(asList(witness(4, "v1", x, witness(8, "c", one), witness(11, "c", zero)))),
					 checker.getResult().getAllWitnesses());

	}
}
