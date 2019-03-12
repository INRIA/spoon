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

import org.jgrapht.graph.DefaultDirectedGraph;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by marodrig on 13/10/2015.
 */
public class ControlFlowGraph extends DefaultDirectedGraph<ControlFlowNode, ControlFlowEdge> {

	/**
	 * Description of the graph
	 */
	private String name;

	private ControlFlowNode exitNode;

	public ControlFlowGraph(Class<? extends ControlFlowEdge> edgeClass) {
		super(edgeClass);
	}

	public ControlFlowGraph() {
		super(ControlFlowEdge.class);
	}

	private int countNodes(BranchKind kind) {
		int result = 0;
		for (ControlFlowNode v : vertexSet()) {
			if (v.getKind().equals(kind)) {
				result++;
			}
		}
		return result;
	}

	public String toGraphVisText() {
		GraphVisPrettyPrinter p = new GraphVisPrettyPrinter(this);
		return p.print();
	}

	/**
	 * Find the node holding and element
	 *
	 * @param e node to find
	 * @return
	 */
	public ControlFlowNode findNode(CtElement e) throws NotFoundException {
		if (e != null) {
			for (ControlFlowNode n : vertexSet()) {
				if (e == n.getStatement()) {
					return n;
				}
			}
		}
		throw new NotFoundException("Element's node not found ");
	}

	/**
	 * Find nodes by a given id
	 * @param id of the node to find
	 * @return
	 */
	public ControlFlowNode findNodeById(int id) {
		for (ControlFlowNode n : vertexSet()) {
			if (n.getId() == id) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Find all nodes of a given kind
	 *
	 * @param kind of node to find
	 * @return list of nodes
	 */
	public List<ControlFlowNode> findNodesOfKind(BranchKind kind) {
		ArrayList<ControlFlowNode> result = new ArrayList<ControlFlowNode>();
		for (ControlFlowNode n : vertexSet()) {
			if (n.getKind().equals(kind)) {
				result.add(n);
			}
		}
		return result;
	}

	@Override
	public ControlFlowEdge addEdge(ControlFlowNode source, ControlFlowNode target) {
		if (!containsVertex(source)) {
			addVertex(source);
		}
		if (!containsVertex(target)) {
			addVertex(target);
		}
		return super.addEdge(source, target);
	}

	/**
	 * Returns all statements
	 */
	public List<ControlFlowNode> statements() {
		return findNodesOfKind(BranchKind.STATEMENT);
	}

	/**
	 * Returns all branches
	 */
	public List<ControlFlowNode> branches() {
		return findNodesOfKind(BranchKind.BRANCH);
	}

	private void simplify(BranchKind kind) {
		try {
			List<ControlFlowNode> convergence = findNodesOfKind(kind);
			for (ControlFlowNode n : convergence) {
				Set<ControlFlowEdge> incoming = incomingEdgesOf(n);
				Set<ControlFlowEdge> outgoing = outgoingEdgesOf(n);
				if (incoming != null && outgoing != null) {
					for (ControlFlowEdge in : incoming) {
						for (ControlFlowEdge out : outgoing) {
							ControlFlowEdge ed = addEdge(in.getSourceNode(), out.getTargetNode());
							if (ed != null) {
								ed.setBackEdge(out.isBackEdge() || in.isBackEdge());
							}
						}
					}
				}

				for (ControlFlowEdge e : edgesOf(n)) {
					removeEdge(e);
				}
				removeVertex(n);
			}
		} catch (Exception e) {
			System.out.println(toGraphVisText());
			throw e;
		}
		//Clean the exit node
		exitNode = null;
	}

	/**
	 * Removes all blocks
	 */
	public void simplifyBlockNodes() {
		simplify(BranchKind.BLOCK_BEGIN);
		simplify(BranchKind.BLOCK_END);
	}

	/**
	 * Removes all non statements or branches
	 */
	public void simplify() {
		simplifyConvergenceNodes();
		simplifyBlockNodes();
	}

	/**
	 * Removes all convergence nodes
	 */
	public void simplifyConvergenceNodes() {
		simplify(BranchKind.CONVERGE);
	}

	//public void

	public int branchCount() {
		return countNodes(BranchKind.BRANCH);
	}

	public int statementCount() {
		return countNodes(BranchKind.STATEMENT);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ControlFlowNode getExitNode() {
		if (exitNode == null) {
			exitNode = findNodesOfKind(BranchKind.EXIT).get(0);
		}
		return exitNode;
	}
}
