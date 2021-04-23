/*
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
import fr.inria.controlflow.ControlFlowNode;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.operation.Operation;
import spoon.smpl.operation.OperationCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transformer contains methods capable to applying a set of transformations as recorded by
 * CTL-VW witnesses to a CFGModel.
 * See paper "A foundation for flow-based program matching: using temporal logic and model checking" (2009)
 * <p>
 * A CTL-VW witness is a 4-tuple of the form (state, metavar, binding, subWitnesses) where
 * subWitnesses is a set of witnesses.
 * <p>
 * Transformations are generally encoded as the following witness structure:
 * <p>
 * {(_, x1, y1, {(_, x2, y2, ... {(S, _, Operation)})})}
 * <p>
 * The transformation in the structure above would be applied by collecting all bindings along
 * the path to the leaf witness node and applying its Operation to state S in the CFGModel
 * supplying the Operation with the bindings collected along the path (x1=y1, x2=y2, ...).
 */
public class Transformer {
	/**
	 * Hide utility class constructor.
	 */
	private Transformer() { }

	/**
	 * Apply a set of transformations to the control flow graph of a method body.
	 *
	 * @param model     CFG of method body
	 * @param witnesses CTL-VW witnesses that encode transformations
	 */
	public static void transform(CFGModel model, Set<ModelChecker.Witness> witnesses) {
		Map<String, Object> bindings = new HashMap<>();
		HashSet<StateElementPair> done = new HashSet<>();

		for (ModelChecker.Witness witness : witnesses) {
			transform(model, bindings, witness, done);
		}
	}

	/**
	 * Copy method additions specified by a matching rule to the parent class of a matching method. Only missing
	 * methods are copied.
	 *
	 * @param model CFG Model of matching method
	 * @param rule  Matching rule
	 */
	public static void copyAddedMethods(CFGModel model, SmPLRule rule) {
		copyAddedMethods(model.getCfg().findNodesOfKind(BranchKind.STATEMENT).get(0).getStatement().getParent(CtClass.class), rule);
	}

	/**
	 * Copy method additions specified by a matching rule to the parent class of a matching method. Only missing
	 * methods are copied.
	 *
	 * @param cls  Parent class of matching method
	 * @param rule Matching rule
	 */
	public static void copyAddedMethods(CtClass<?> cls, SmPLRule rule) {
		List<String> sigs = new ArrayList<>();

		for (CtMethod<?> method : cls.getMethods()) {
			sigs.add(method.getSignature());
		}

		for (CtMethod<?> method : rule.getMethodsAdded()) {
			if (!sigs.contains(method.getSignature())) {
				cls.addMethod(method);
			}
		}
	}

	/**
	 * Apply a set of transformations using a given set of metavariable bindings to the control
	 * flow graph of a method body.
	 *
	 * @param model    CFG of method body
	 * @param bindings Metavariable bindings
	 * @param witness  CTL-VW witness that encodes zero or more transformations
	 */
	private static void transform(CFGModel model, Map<String, Object> bindings, ModelChecker.Witness witness, Set<StateElementPair> done) {
		if (witness.binding instanceof List<?>) {
			// we're at at a leaf of the witness trees
			// The witness binding is a list of operations, apply them

			List<?> objects = (List<?>) witness.binding;
			ControlFlowNode node = model.getCfg().findNodeById(witness.state);
			BranchKind kind = node.getKind();

			CtElement targetElement;

			// naming convention: "_e" means a bound expression between a metavariable and a subexpression of a statement
			if (bindings.containsKey("_e")) {
				targetElement = (CtElement) bindings.get("_e");
			} else {
				if (kind == BranchKind.STATEMENT) {
					targetElement = node.getStatement();
				} else if (kind == BranchKind.BRANCH || kind == BranchKind.BLOCK_BEGIN) {
					targetElement = ((SmPLMethodCFG.NodeTag) node.getTag()).getAnchor();
				} else {
					throw new IllegalArgumentException("unexpected node kind " + kind);
				}
			}

			StateElementPair target = new StateElementPair(witness.state, targetElement);

			if (done.contains(target)) {
				System.out.println("WARNING: already transformed " + target + ": " + model.getCfg().findNodeById(witness.state));
				return;
			}

			done.add(target);

			// Process any prepend operations in the list
			objects.stream().filter((obj) -> obj instanceof Operation).forEachOrdered((obj) -> {
				((Operation) obj).accept(OperationCategory.PREPEND, targetElement, bindings);
			});

			// Process any append operations in the list, in reverse order to preserve correct output order
			objects.stream().filter((obj) -> obj instanceof Operation)
					.collect(Collectors.toCollection(LinkedList::new))
					.descendingIterator().forEachRemaining((obj) -> {
				((Operation) obj).accept(OperationCategory.APPEND, targetElement, bindings);
			});

			// Process any delete operations in the list
			objects.stream().filter((obj) -> obj instanceof Operation).forEachOrdered((obj) -> {
				((Operation) obj).accept(OperationCategory.DELETE, targetElement, bindings);
			});
		} else {
			// The witness binding is an actual metavariable binding, record it and process sub-witnesses
			bindings.put(witness.metavar, witness.binding);

			for (ModelChecker.Witness subWitness : witness.witnesses) {
				transform(model, bindings, subWitness, done);
			}

			bindings.remove(witness.metavar);
		}
	}

	/**
	 * A StateElementPair is a record of a state ID and a code element, used for keeping track of which elements have
	 * been processed as targets of transformation operations.
	 */
	private static class StateElementPair {
		/**
		 * Create a new StateElementPair.
		 *
		 * @param state   State ID
		 * @param element Code element
		 */
		StateElementPair(Integer state, CtElement element) {
			this.state = state;
			this.element = element;
		}

		@Override
		public int hashCode() {
			final int prime = 43;
			int result = 1;
			result = prime * result + 17 * state;

			if (!(element.getPosition() instanceof NoSourcePosition)) {
				result = prime * result + 23 * element.getPosition().getSourceStart();
			} else {
				result = prime * result + 23 * element.getParent().getPosition().getSourceStart();
			}

			return result;
		}

		@Override
		public boolean equals(Object other) {
			return this == other || (other instanceof StateElementPair && this.hashCode() == other.hashCode());
		}

		@Override
		public String toString() {
			return "(" + state.toString() + ", " + element.toString() + ")";
		}

		/**
		 * State ID.
		 */
		public final Integer state;

		/**
		 * Code element.
		 */
		public final CtElement element;
	}
}
