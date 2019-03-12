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
package fr.inria.controlflow;

import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;

import java.util.List;
import java.util.Set;

/**
 * An algorithm that takes a CtElement, builds a graph for it and checks that all branches return
 * something or that there is no branch returns anything
 *
 * Created by marodrig on 04/01/2016.
 */
public class AllBranchesReturn {

	/**
	 * Finds if all branches returns
	 *
	 * @param element   starting point
	 * @return True if all branches return or none return
	 */
	public boolean execute(CtElement element) {
		ControlFlowBuilder builder = new ControlFlowBuilder();
		ControlFlowGraph graph = builder.build(element);
		graph.simplify();
		//System.out.println(graph.toGraphVisText());
		//System.out.println(graph.toGraphVisText());


		List<ControlFlowNode> exits = graph.findNodesOfKind(BranchKind.EXIT);

		int returnCount = 0;
		int incomingCount = -1;
		for (ControlFlowNode n : exits) {
			Set<ControlFlowEdge> edges = graph.incomingEdgesOf(n);
			incomingCount = edges.size();
			for (ControlFlowEdge in : edges) {
				if (in.getSourceNode().getStatement() != null
						&& in.getSourceNode().getStatement() instanceof CtReturn) {
					returnCount++;
				}
			}
		}
		return returnCount == incomingCount || returnCount == 0;
	}

}
