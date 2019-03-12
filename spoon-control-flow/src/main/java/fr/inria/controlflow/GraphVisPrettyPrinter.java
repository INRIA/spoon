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

import java.util.HashMap;
/**
 * Prints the control flow in .Dot for GraphVis to visualize
 *
 * Created by marodrig on 14/10/2015.
 */
public class GraphVisPrettyPrinter {

	private final ControlFlowGraph graph;

	public GraphVisPrettyPrinter(ControlFlowGraph graph) {
		this.graph = graph;
	}

	public String print() {
		StringBuilder sb = new StringBuilder("digraph ").append(graph.getName()).append(" { \n");
		//sb.append("exit [shape=doublecircle];\n");
		sb.append("node [fontsize = 8];\n");


		int i = 0;
		HashMap<ControlFlowNode, Integer> nodeIds = new HashMap<ControlFlowNode, Integer>();
		for (ControlFlowNode n : graph.vertexSet()) {
			printNode(++i, n, sb);
			nodeIds.put(n, i);
		}

		for (ControlFlowEdge e : graph.edgeSet()) {
			if (e.isBackEdge()) {
				sb.append(nodeIds.get(e.getSourceNode())).append(" -> ").
						append(nodeIds.get(e.getTargetNode())).append("[style=dashed];\n ");
			} else {
				sb.append(nodeIds.get(e.getSourceNode())).append(" -> ").
						append(nodeIds.get(e.getTargetNode())).append(" ;\n ");
			}
		}

		sb.append("\n }");
		return sb.toString();
	}


	private String printNode(int i, ControlFlowNode n, StringBuilder sb) {
		String labelStr = " [shape=rectangle, label=\"";
		if (n.getKind() == BranchKind.BRANCH) {
			labelStr = " [shape=diamond, label=\"";
		} else if (n.getKind() == BranchKind.BEGIN) {
			labelStr = " [shape=Mdiamond, label=\"";
		} else if (n.getKind() == BranchKind.BLOCK_BEGIN || n.getKind() == BranchKind.BLOCK_END) {
			labelStr = " [shape=rectangle, style=filled, fillcolor=gray, label=\"";
		} else if (n.getKind() == BranchKind.EXIT) {
			labelStr = " [shape=doublecircle, label=\"";
		} else if (n.getKind() == BranchKind.CONVERGE) {
			labelStr = " [shape=point label=\"";
		}

		sb.append(i).append(labelStr).append(n.toString().replace("\"", "quot ")).append(" \"]").append(";\n");
		return sb.toString();
	}

}
