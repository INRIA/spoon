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

import java.util.*;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.*;
import spoon.smpl.label.PropositionLabel;
import spoon.smpl.label.StatementLabel;
import spoon.smpl.metavars.IdentifierConstraint;
import static spoon.smpl.TestUtils.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
public class ModelCheckerTest {
	private static class ModelBuilder implements Model {
		public List<Integer> states;
		public Map<Integer, List<Integer>> successors;
		public Map<Integer, List<Label>> labels;

		public ModelBuilder() {
			states = new ArrayList<Integer>();
			successors = new HashMap<Integer, List<Integer>>();
			labels = new HashMap<Integer, List<Label>>();
		}

		public ModelBuilder addStates(int... n) {
			for (int nn : n) {
				states.add(nn);
				successors.put(nn, new ArrayList<Integer>());
				labels.put(nn, new ArrayList<Label>());
			}

			return this;
		}

		public ModelBuilder addTransition(int from, int to) {
			successors.get(from).add(to);
			return this;
		}

		public ModelBuilder addLabel(int state, Label label) {
			labels.get(state).add(label);
			return this;
		}

		@Override
		public List<Integer> getStates() {
			return states;
		}

		@Override
		public List<Integer> getSuccessors(int state) {
			return successors.get(state);
		}

		@Override
		public List<Label> getLabels(int state) {
			return labels.get(state);
		}
	}

	@Test
	public void testTrue() {

		// contract: SAT(true) should select every state

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(2, 3)
			 .addTransition(3, 3);

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new True().accept(checker);
		assertEquals(res(1, env(), 2, env(), 3, env()), checker.getResult());
	}

	@Test
	public void testAnd() {

		// contract: SAT(phi AND psi) should select every state selected by both SAT(phi) and SAT(psi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(2, 3)
			 .addTransition(3, 3)
			 .addLabel(1, new PropositionLabel("p"))
			 .addLabel(2, new PropositionLabel("q"))
			 .addLabel(3, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("q"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new And(new Proposition("p"), new Proposition("q")).accept(checker);
		assertEquals(res(3, env()), checker.getResult());
	}

	@Test
	public void testOr() {

		// contract: SAT(phi OR psi) should select all states selected by either SAT(phi) or SAT(psi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(2, 3)
			 .addTransition(3, 3)
			 .addLabel(1, new PropositionLabel("p"))
			 .addLabel(2, new PropositionLabel("q"))
			 .addLabel(3, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("q"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new Or(new Proposition("p"), new Proposition("q")).accept(checker);
		assertEquals(res(1, env(), 2, env(), 3, env()), checker.getResult());
	}

	@Test
	public void testNeg() {

		// contract: SAT(not(phi)) should select all states not selected by SAT(phi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(2, 3)
			 .addTransition(3, 3)
			 .addLabel(1, new PropositionLabel("p"))
			 .addLabel(2, new PropositionLabel("q"))
			 .addLabel(3, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("q"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new Not(new Proposition("p")).accept(checker);
		assertEquals(res(2, env()), checker.getResult());

		new Not(new Proposition("q")).accept(checker);
		assertEquals(res(1, env()), checker.getResult());
	}

	@Test
	public void testProposition() {

		// contract: for a proposition p, SAT(p) should select all states s for which p \in Labels(s)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(2, 3)
			 .addTransition(3, 3)
			 .addLabel(1, new PropositionLabel("p"))
			 .addLabel(2, new PropositionLabel("q"))
			 .addLabel(3, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("q"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new Proposition("p").accept(checker);
		assertEquals(res(1, env(), 3, env()), checker.getResult());

		new Proposition("q").accept(checker);
		assertEquals(res(2, env(), 3, env()), checker.getResult());

		new Proposition("r").accept(checker);
		assertEquals(res(), checker.getResult());
	}

	@Test
	public void testExistsNext() {

		// contract: SAT(EX(phi)) should select all states s for which there exists a transition (s -> s') and s' is selected by SAT(phi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3, 4, 5)
			 .addTransition(1, 2)
			 .addTransition(1, 3)
			 .addTransition(2, 2)
			 .addTransition(3, 4)
			 .addTransition(3, 5)
			 .addTransition(4, 3)
			 .addTransition(5, 3)
			 .addLabel(2, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("q"))
			 .addLabel(4, new PropositionLabel("r"))
			 .addLabel(5, new PropositionLabel("r"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new ExistsNext(new Proposition("p")).accept(checker);
		assertEquals(res(1, env(), 2, env()), checker.getResult());

		new ExistsNext(new Proposition("q")).accept(checker);
		assertEquals(res(1, env(), 4, env(), 5, env()), checker.getResult());

		new ExistsNext(new Proposition("r")).accept(checker);
		assertEquals(res(3, env()), checker.getResult());
	}

	@Test
	public void testAllNext() {

		// contract: SAT(AX(phi)) should select all states s where it holds for all transitions (s -> s') that s' is selected by SAT(phi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3, 4, 5)
			 .addTransition(1, 2)
			 .addTransition(1, 3)
			 .addTransition(2, 2)
			 .addTransition(3, 4)
			 .addTransition(3, 5)
			 .addTransition(4, 3)
			 .addTransition(5, 3)
			 .addLabel(2, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("q"))
			 .addLabel(4, new PropositionLabel("r"))
			 .addLabel(5, new PropositionLabel("r"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new AllNext(new Proposition("p")).accept(checker);
		assertEquals(res(2, env()), checker.getResult());

		new AllNext(new Proposition("q")).accept(checker);
		assertEquals(res(4, env(), 5, env()), checker.getResult());

		new AllNext(new Proposition("r")).accept(checker);
		assertEquals(res(3, env()), checker.getResult());
	}

	@Test
	public void testExistsUntil() {

		// contract: SAT(EU(phi, psi)) should select the states s either selected by SAT(psi) or for which there exists a path (s, s1, ..., sn) where sn is selected by SAT(psi) and for each i \in [1, n-1] si is selected by SAT(phi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3, 4, 5)
			 .addTransition(1, 2)
			 .addTransition(1, 5)
			 .addTransition(2, 3)
			 .addTransition(3, 4)
			 .addTransition(4, 4)
			 .addTransition(5, 4)
			 .addLabel(1, new PropositionLabel("p"))
			 .addLabel(2, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("p"))
			 .addLabel(4, new PropositionLabel("q"))
			 .addLabel(5, new PropositionLabel("r"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new ExistsUntil(new Proposition("p"), new Proposition("q")).accept(checker);
		assertEquals(res(1, env(), 2, env(), 3, env(), 4, env()), checker.getResult());
	}

	@Test
	public void testAllUntil() {

		// contract: SAT(EU(phi, psi)) should select the states s either selected by SAT(psi) or for which it holds that over all paths (s, s1, ..., sn) sn is selected by SAT(psi) and for each i \in [1, n-1] si is selected by SAT(phi)

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3, 4, 5)
			 .addTransition(1, 2)
			 .addTransition(1, 5)
			 .addTransition(2, 3)
			 .addTransition(3, 4)
			 .addTransition(4, 4)
			 .addTransition(5, 4)
			 .addLabel(1, new PropositionLabel("p"))
			 .addLabel(2, new PropositionLabel("p"))
			 .addLabel(3, new PropositionLabel("p"))
			 .addLabel(4, new PropositionLabel("q"))
			 .addLabel(5, new PropositionLabel("r"));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new AllUntil(new Proposition("p"), new Proposition("q")).accept(checker);
		assertEquals(res(2, env(), 3, env(), 4, env()), checker.getResult());
	}

	@Test
	public void testPreExists() {

		// contract: preExists(X) should produce the set of states capable of transitioning into X, i.e the set {s | exists (s -> s'). s' in X}

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3, 4, 5, 6)
			 .addTransition(1, 2)
			 .addTransition(1, 4)
			 .addTransition(2, 3)
			 .addTransition(3, 6)
			 .addTransition(4, 5)
			 .addTransition(5, 6)
			 .addTransition(6, 6);

		assertTrue(ModelChecker.isValid(model));

		assertEquals(intSet(), ModelChecker.preExists(model, intSet(1)));
		assertEquals(intSet(1), ModelChecker.preExists(model, intSet(2)));
		assertEquals(intSet(1), ModelChecker.preExists(model, intSet(4)));
		assertEquals(intSet(1), ModelChecker.preExists(model, intSet(2, 4)));
		assertEquals(intSet(1, 2), ModelChecker.preExists(model, intSet(2, 3)));
		assertEquals(intSet(3, 5, 6), ModelChecker.preExists(model, intSet(6)));
	}

	@Test
	public void testPreAll() {

		// contract: preExists(X) should produce the set of states ONLY capable of transitioning into X, i.e the set {s | forall (s -> s'). s' in X}

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3, 4, 5, 6)
			 .addTransition(1, 2)
			 .addTransition(1, 4)
			 .addTransition(2, 3)
			 .addTransition(3, 6)
			 .addTransition(4, 5)
			 .addTransition(5, 6)
			 .addTransition(6, 6);

		assertTrue(ModelChecker.isValid(model));

		assertEquals(intSet(), ModelChecker.preAll(model, intSet(1)));
		assertEquals(intSet(), ModelChecker.preAll(model, intSet(2)));
		assertEquals(intSet(), ModelChecker.preAll(model, intSet(4)));
		assertEquals(intSet(1), ModelChecker.preAll(model, intSet(2, 4)));
		assertEquals(intSet(2), ModelChecker.preAll(model, intSet(2, 3)));
		assertEquals(intSet(3, 5, 6), ModelChecker.preExists(model, intSet(6)));
	}

	@Test
	public void testStatementPattern() {

		// contract: when no metavariables are involved, a statement pattern should essentially match the contained statement literally

		Launcher launcher = new Launcher();
		CtMethod<?> method = parseMethod("void M() { int x = 1; }");
		CtElement stmt = method.getBody().getStatement(0);

		ModelBuilder model = new ModelBuilder();
		model.addStates(1).addTransition(1, 1);
		model.addLabel(1, new StatementLabel(stmt));

		assertTrue(ModelChecker.isValid(model));

		ModelChecker checker = new ModelChecker(model);

		new Statement(stmt).accept(checker);
		assertEquals(res(1, env()), checker.getResult());
	}

	static class VariableUseLabel implements Label {
		public VariableUseLabel(List<String> usedVarNames) {
			this.usedVarNames = usedVarNames;
		}

		public List<String> usedVarNames;
		public Map<String, Object> bindings;

		public boolean matches(Predicate obj) {
			if (obj instanceof VariableUsePredicate) {
				VariableUsePredicate vup = (VariableUsePredicate) obj;

				if (vup.getMetavariables().containsKey(vup.getVariable())) {
					bindings = new HashMap<>();

					if (usedVarNames.size() == 1) {
						bindings.put(vup.getVariable(), usedVarNames.get(0));
					} else {
						bindings.put(vup.getVariable(), usedVarNames);
					}

					return true;
				} else {
					return usedVarNames.contains(vup.getVariable());
				}
			}

			return false;
		}

		@Override
		public List<LabelMatchResult> getMatchResults() {
			return Collections.singletonList(new LabelMatchResultImpl(bindings));
		}

		public void reset() {
		}
	}

	static class AnyConstraint implements MetavariableConstraint {
		public CtElement apply(CtElement value) {
			return value;
		}
	}

	@Test
	public void testVariableUsePredicateExplicitMatch() {

		// TODO: get rid of VariableUseLabel and use real code labels for this test
		// contract: with no metavariables involved, SAT(VariableUsePredicate(x)) should select the states that represent code that makes use of the literal identifier "x"

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(1, 3)
			 .addTransition(2, 1)
			 .addTransition(3, 3);

		assertTrue(ModelChecker.isValid(model));

		model.addLabel(1, new VariableUseLabel(Arrays.asList("x")))
			 .addLabel(2, new VariableUseLabel(Arrays.asList("x", "y")))
			 .addLabel(3, new VariableUseLabel(Arrays.asList("y")));

		ModelChecker checker = new ModelChecker(model);

		new VariableUsePredicate("x", new HashMap<>()).accept(checker);
		assertEquals(res(1, env(), 2, env()), checker.getResult());

		new VariableUsePredicate("y", new HashMap<>()).accept(checker);
		assertEquals(res(2, env(), 3, env()), checker.getResult());
	}

	@Test
	public void testVariableUsePredicateMetavariableBind() {

		// contract: SAT(VariableUsePredicate(mv)) with mv a metavariable should select the states representing code in which some identifier is used that can be bound to the metavariable, recording the binding in the environment and producing multiple results as necessary

		ModelBuilder model = new ModelBuilder();
		model.addStates(1, 2, 3)
			 .addTransition(1, 2)
			 .addTransition(1, 3)
			 .addTransition(2, 1)
			 .addTransition(3, 3);

		assertTrue(ModelChecker.isValid(model));

		model.addLabel(1, new VariableUseLabel(Arrays.asList("x")))
			 .addLabel(2, new VariableUseLabel(Arrays.asList("x", "y")))
			 .addLabel(3, new VariableUseLabel(Arrays.asList("y")));

		ModelChecker checker = new ModelChecker(model);

		new VariableUsePredicate("z", makeMetavars("z", new AnyConstraint())).accept(checker);
		assertEquals(res(1, env("z", "x"), 2, env("z", Arrays.asList("x", "y")), 3, env("z", "y")),
					 checker.getResult());
	}

	@Test
	public void testInnerAnd() {

		// contract: InnerAnd(phi) should merge results of SAT(phi) under environments containing only positive bindings

		CFGModel model = new CFGModel(methodCfg(parseMethod("void m() { a = b; }")));

		Map<String, MetavariableConstraint> meta = makeMetavars("x", new IdentifierConstraint(),
																"y", new IdentifierConstraint());

		SequentialOr phi = new SequentialOr();
		phi.add(new And(new Expression(parseExpression("x;"), meta), new ExistsVar("_v", new SetEnv("_v", "Delete"))));
		phi.add(new And(new Expression(parseExpression("y;"), meta), new ExistsVar("_v", new SetEnv("_v", "Delete"))));

		ModelChecker checker = new ModelChecker(model);
		new InnerAnd(phi).accept(checker);

		ModelChecker.ResultSet results = checker.getResult();

		assertEquals(4, results.size()); // (x,y) = (a,a,) | (a,b) | (b,a) | (b,b)

		for (ModelChecker.Result result : results) {
			for (String key : result.getEnvironment().keySet()) {
				if (result.getEnvironment().get(key) instanceof Environment.NegativeBinding) {
					fail("A negative binding was present");
				}
			}
		}
	}
}
