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
package fr.inria.dataflow;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowNode;
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
 * Find initialized variables in a control flow graph.
 * <p>
 * The algorithms trust that the code from which the graph was created was successfully compiled. That's why
 * not only assigned variables are considered initialized, but also used variables.
 * </p>
 * Created by marodrig on 13/11/2015.
 */
public class InitializedVariables {

	private int depth = 0;
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

	private class InitFactors {
		//DO NOT INITIALIZE!!!!! Initial Null value needed by the algorithm
		Set<CtVariableReference> defined = null;
		//Initialize this one
		Set<CtVariableReference> used = new HashSet<>();
	}

	public Set<CtVariableReference> getInitialized() {
		return initialized;
	}

	Set<CtVariableReference> initialized = new HashSet<>();

	public void run(ControlFlowNode node) {
		//Already calculated factors
		HashMap<ControlFlowNode, InitFactors> factors = new HashMap<>();

		if (node.getParent() != null) {
			if (node.getParent().findNodesOfKind(BranchKind.BLOCK_END).size() > 0
					|| node.getParent().findNodesOfKind(BranchKind.BLOCK_BEGIN).size() > 0
					|| node.getParent().findNodesOfKind(BranchKind.CONVERGE).size() > 0) {
				throw new RuntimeException("Invalid node types. Simplify the graph with the simplify() method.");
			}
		}

		InitFactors fp = initialized(node, factors, includeDefinedInNode);
		initialized = fp.defined;
		initialized.addAll(fp.used);
	}

	private Set<CtVariableReference> defined(ControlFlowNode n) {
		//Obtain the variables defined in this node
		HashSet<CtVariableReference> def = new HashSet<>();
		if (n.getStatement() != null) {
			if (n.getStatement() instanceof CtLocalVariable) {
				CtLocalVariable lv = ((CtLocalVariable) n.getStatement());
				if (lv.getDefaultExpression() != null) {
					def.add(lv.getReference());
				}
			} else if (n.getStatement() instanceof CtAssignment) {
				CtExpression e = ((CtAssignment) n.getStatement()).getAssigned();
				if (e instanceof CtVariableAccess) {
					def.add(((CtVariableAccess) e).getVariable());
				} else if (e instanceof CtArrayAccess) {
					CtExpression exp = ((CtArrayAccess) e).getTarget();
					if (exp instanceof CtVariableAccess) {
						CtVariableReference a = ((CtVariableAccess) exp).getVariable();
						def.add(a);
					} else {
						System.out.println("Could not obtain variable from expression");
					}
				}
			}
		}
		return def;
	}

	private Set<CtVariableReference> used(ControlFlowNode n) {
		if (n.getStatement() == null) {
			return new HashSet<>();
		}
		//Obtain variables used in this node
		HashSet<CtVariableReference> used = new HashSet<>();
		for (CtVariableAccess a: n.getStatement().getElements(new TypeFilter<CtVariableAccess>(CtVariableAccess.class))) {
			used.add(a.getVariable());
		}
		return used;
	}

	/**
	 * Finds the initialized variables at a given point in the control flow
	 *
	 * @param n                    Node to find initialized variables
	 * @param factors              already calculated factors for all nodes
	 * @param includeDefinedInNode
	 * @return
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


		Set<CtVariableReference> defN = includeDefinedInNode ? defined(n) : new HashSet<CtVariableReference>();
		//[Used_n - Def_n]
		Set<CtVariableReference> usedN = includeDefinedInNode ? used(n) : new HashSet<CtVariableReference>();
		usedN.removeAll(defN);

		InitFactors result = new InitFactors();

		for (ControlFlowEdge e : n.getParent().incomingEdgesOf(n)) {
			if (e.isBackEdge()) {
				continue;
			}
			ControlFlowNode p = e.getSourceNode();
			depth++;
			InitFactors fp;
			if (factors.containsKey(p)) {
				fp = factors.get(p);
			} else {
				fp = initialized(p, factors, true);
			}
			depth--;

			//[Def_P for each P ]
			if (result.defined == null) {
				result.defined = new HashSet<>();
				result.defined.addAll(fp.defined);
			} else {
				result.defined.retainAll(fp.defined);
			}

			fp.used.removeAll(fp.defined);
			result.used.addAll(fp.used);
		}

		if (result.defined == null) {
			result.defined = defN;
		} else {
			result.defined.addAll(defN);
		}
		result.used.addAll(usedN);
		result.used.removeAll(result.defined);

		factors.put(n, result);
		return result;
	}
}
