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

import fr.inria.controlflow.ControlFlowBuilder;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import fr.inria.controlflow.NotFoundException;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Find useless assignments. A useless assignment is the one assigning to a dead variable.
 * (https://en.wikipedia.org/wiki/Live_variable_analysis)
 * <p>
 * Return the list of useless assignments
 * </p>
 * Created by marodrig on 03/02/2016.
 */
public class UselessAssignmentAnalysis {

	Set<CtAssignment> useless;

	/**
	 * The method calculates the control flow for 'container' and then returns the useless assignments
	 *
	 * @param container to run
	 * @throws NotFoundException if element 'begin' does not belong to 'container'
	 */
	public void run(CtElement container) throws NotFoundException {
		ControlFlowGraph graph = new ControlFlowBuilder().build(container);
		run(graph);
	}

	/**
	 * The method calculates the control flow for 'container' and then returns the useless assignments
	 *
	 * @param graph to be processed
	 */
	public void run(ControlFlowGraph graph) {
		//This is a backward analysis. So start from the end
		ControlFlowNode exitNode = graph.getExitNode();
		useless = new HashSet<>();
		doRun(exitNode, graph);
	}

	public void doRun(ControlFlowNode n, ControlFlowGraph graph) {
		for (ControlFlowEdge e : graph.incomingEdgesOf(n)) {

			HashSet<CtVariable> kill = new HashSet<>();
			HashSet<CtVariable> gen = new HashSet<>();

			if (n.getStatement() instanceof CtAssignment) {
				CtExpression left = ((CtAssignment) n.getStatement()).getAssigned();
				CtExpression right = ((CtAssignment) n.getStatement()).getAssigned();
				if (right instanceof CtVariableAccess) {
					kill.add(((CtVariableAccess) right).getVariable().getDeclaration());
					//else if ( ex instanceof CtArrayAccess) kill.add(((CtArrayAccess)ex).getTarget());
				} else {
					throw new UnsupportedOperationException();
				}

				gen.retainAll(getVariableAccess(right));

			} else {
				gen.retainAll(getVariableAccess(n.getStatement()));
			}


			doRun(e.getSourceNode(), graph);
		}
	}

	private List<CtVariableAccess> getVariableAccess(CtElement statement) {
		return statement.getElements(new TypeFilter<CtVariableAccess>(CtVariableAccess.class));
	}

}
