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
package spoon.controlflow;

import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Find initialized variables in access control flow graph.
 * <previous>
 * The algorithms trust that the code from which the graph was created was successfully compiled. That's why
 * not only assigned variables are considered initialized, but also used variables.
 */
public class InitializedVariables {

	//See the algorithm description in the adjoint pdf

	public boolean getIncludeDefinedInNode() {
		return includeDefinedInNode;
	}

	public void setIncludeDefinedInNode(boolean includeDefinedInNode) {
		this.includeDefinedInNode = includeDefinedInNode;
	}

	/**
	 * Indicates if in the result those variables defined in the node must be included.
	 */
	private boolean includeDefinedInNode = false;

	private record InitFactors(Set<CtVariableReference<?>> defined, Set<CtVariableReference<?>> used) {
		InitFactors() {
			this(new HashSet<>(), new HashSet<>());
		}
	}

	public Set<CtVariableReference<?>> getInitialized() {
		return initialized;
	}

	Set<CtVariableReference<?>> initialized = new HashSet<>();

	public void run(ControlFlowNode node) {
		//Already calculated factors
		HashMap<ControlFlowNode, InitFactors> factors = new HashMap<>();

		if (node.getParent() != null) {
			if (!node.getParent().findNodesOfKind(NodeKind.BLOCK_END).isEmpty()
					|| !node.getParent().findNodesOfKind(NodeKind.BLOCK_BEGIN).isEmpty()
					|| !node.getParent().findNodesOfKind(NodeKind.CONVERGE).isEmpty()) {
				throw new RuntimeException("Invalid node types. Simplify the graph with the simplify() method.");
			}
		}

		InitFactors fp = initialized(node, factors, includeDefinedInNode);
		initialized = fp.defined;
		initialized.addAll(fp.used);
	}

	private Set<CtVariableReference<?>> defined(ControlFlowNode n) {
		//Obtain the variables defined in this node
		HashSet<CtVariableReference<?>> def = new HashSet<>();
		if (n.getStatement() != null) {
			if (n.getStatement() instanceof CtLocalVariable<?> localVariable) {
				if (localVariable.getDefaultExpression() != null) {
					def.add(localVariable.getReference());
				}
			} else if (n.getStatement() instanceof CtAssignment<?, ?> assignment) {
				CtExpression<?> assignedExpression = assignment.getAssigned();
				if (assignedExpression instanceof CtVariableAccess<?> variableAccess) {
					def.add(variableAccess.getVariable());
				} else if (assignedExpression instanceof CtArrayAccess<?, ?> arrayAccess) {
					CtExpression<?> arrayExpression = arrayAccess.getTarget();
					if (arrayExpression instanceof CtVariableAccess<?> variableAccess) {
						CtVariableReference<?> array = variableAccess.getVariable();
						def.add(array);
					} else {
						System.out.println("Could not obtain variable from expression");
					}
				}
			}
		}
		return def;
	}

	private Set<CtVariableReference<?>> used(ControlFlowNode n) {
		if (n.getStatement() == null) {
			return new HashSet<>();
		}
		//Obtain variables used in this node
		HashSet<CtVariableReference<?>> used = new HashSet<>();
		for (CtVariableAccess<?> access : n.getStatement().getElements(new TypeFilter<>(CtVariableAccess.class))) {
			used.add(access.getVariable());
		}
		return used;
	}

	/**
	 * Finds the initialized variables at access given point in the control flow
	 *
	 * @param n                    Node to find initialized variables
	 * @param factors              already calculated factors for all nodes
	 * @param includeDefinedInNode Whether to include initializations and usages in the node n
	 * @return An init factors object holding initialized and used variables for the given node
	 */
	private InitFactors initialized(ControlFlowNode n, HashMap<ControlFlowNode, InitFactors> factors, boolean includeDefinedInNode) {
		//+ -> Union
		//* -> Intersection
		//Def_Result_n = [Def_P for each P ] + Def_n
		//Used_Result_n = [[Used_P - Def_p] for each P] + [[Used_n - Def_n] - Def_Result_n]
		//Initialized = Def_Result_n + Used_Result_n

		if (n.getParent() == null) {
			throw new RuntimeException("The node has no parent");
		}


		Set<CtVariableReference<?>> defN = includeDefinedInNode ? defined(n) : new HashSet<>();
		//[Used_n - Def_n]
		Set<CtVariableReference<?>> usedN = includeDefinedInNode ? used(n) : new HashSet<>();
		usedN.removeAll(defN);

		InitFactors result = new InitFactors();

		boolean initialEdge = true;
		for (ControlFlowEdge edge : n.getParent().incomingEdgesOf(n)) {
			if (edge.isBackEdge()) {
				continue;
			}
			ControlFlowNode previous = edge.getSourceNode();
			InitFactors previousFactors;
			if (factors.containsKey(previous)) {
				previousFactors = factors.get(previous);
			} else {
				previousFactors = initialized(previous, factors, true);
			}

			//[Def_P for each P ]
			if (initialEdge) {
				initialEdge = false;
				result.defined.addAll(previousFactors.defined);
			} else {
				result.defined.retainAll(previousFactors.defined);
			}

			result.used.addAll(previousFactors.used);
		}

		result.defined.addAll(defN);
		result.used.addAll(usedN);
		result.used.removeAll(result.defined);

		factors.put(n, result);
		return result;
	}
}
