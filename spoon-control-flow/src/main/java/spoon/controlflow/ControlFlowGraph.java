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

import org.jgrapht.graph.DefaultDirectedGraph;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Representation of a Control Flow Graph
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

	private int countNodes(NodeKind kind) {
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
	 * @return the node if found
	 * @throws NotFoundException if no such node could be found
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
	 * @return the node if found or {@code null} if no such node exists
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
	 * @return a list of all the nodes of the given kind
	 */
	public List<ControlFlowNode> findNodesOfKind(NodeKind kind) {
		ArrayList<ControlFlowNode> result = new ArrayList<>();
		for (ControlFlowNode n : vertexSet()) {
			if (n.getKind().equals(kind)) {
				result.add(n);
			}
		}
		return result;
	}

	/**
	 * Add an edge to this graph
	 * @param source The node the edge originates from
	 * @param target The node the edge ends at
	 * @return the inserted edge
	 */
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
	 * {@return all statements}
	 */
	public List<ControlFlowNode> statements() {
		return findNodesOfKind(NodeKind.STATEMENT);
	}

	/**
	 * {@return all branches}
	 */
	public List<ControlFlowNode> branches() {
		return findNodesOfKind(NodeKind.BRANCH);
	}

	private void simplify(NodeKind kind) {
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
		simplify(NodeKind.BLOCK_BEGIN);
		simplify(NodeKind.BLOCK_END);
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
		simplify(NodeKind.CONVERGE);
	}

	//public void

	public int branchCount() {
		return countNodes(NodeKind.BRANCH);
	}

	public int statementCount() {
		return countNodes(NodeKind.STATEMENT);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ControlFlowNode getExitNode() {
		if (exitNode == null) {
			exitNode = findNodesOfKind(NodeKind.EXIT).get(0);
		}
		return exitNode;
	}
}
