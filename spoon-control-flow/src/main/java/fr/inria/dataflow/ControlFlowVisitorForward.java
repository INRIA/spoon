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

import fr.inria.controlflow.ControlFlowNode;

/**
 * Created by marodrig on 12/11/2015.
 */
public abstract class ControlFlowVisitorForward extends AbstractControlFlowVisitor {

	/**
	 * Runs the graph backwards starting from a node visiting all nodes until
	 * it reach the node pased as parameters or some node signals an stop
	 *
	 * @param statementNode Statement node to stop
	 */
	public void run(ControlFlowNode statementNode) {

		/*
		Set<ControlFlowNode> visited = new HashSet<>();
		Stack<ControlFlowNode> stack = new Stack<>();

		if (statementNode.getParent() == null) {
		    throw new InvalidArgumentException(new String[]{"The node has no parent"});
		}
		ControlFlowGraph graph = statementNode.getParent();

		stack.push(graph.findNodesOfKind(BranchKind.BEGIN).get(0));
		do {
		    ControlFlowNode n = stack.pop();

		    //Finished when we arrive at the node
		    //and the stack is empty
		    if (n.equals(statementNode) && stack.empty()) return;

		    //Skip this node if we have already visited
		    if (n.getKind().equals(BranchKind.BEGIN)) continue;
		    if (visited.contains(n)) continue;
		    else visited.add(n);

		    //Visit the node

		    //Visit forward
		    for (ControlFlowEdge e : graph.outgoingEdgesOf(n)) {
		        n = e.getTargetNode();
		        if (n.equals(statementNode)) return ;
		        stack.push(n);
		    }
		} while (!stack.empty());*/
	}

}
