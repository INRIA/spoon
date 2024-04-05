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
package fr.inria.controlflow;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A node of the control flow
 */
public class ControlFlowNode {

	public static int count = 0;

	private final int id;

	/**
	 * Control flow graph containing this node
	 */
	ControlFlowGraph parent;

	public NodeKind getKind() {
		return kind;
	}

	public void setKind(NodeKind kind) {
		this.kind = kind;
	}

	private NodeKind kind;

	/**
	 * Statement that is going to be pointed to by this node
	 */
	private CtElement statement;

	//An object you can tag to the node
	Object tag;

	public ControlFlowNode(CtElement statement, ControlFlowGraph parent, NodeKind kind) {
		this.kind = kind;
		this.parent = parent;
		this.statement = statement;
		++count;
		id = count;
	}


	public ControlFlowNode(CtElement statement, ControlFlowGraph parent) {
		this.statement = statement;
		this.parent = parent;
		++count;
		id = count;
	}

	public int getId() {
		return id;
	}

	/**
	 * Obtains the siblings of a control node. Siblings are the nodes in parallel branches
	 */
	public List<ControlFlowNode> siblings() {
		ArrayList<ControlFlowNode> result = new ArrayList<>();
		for (ControlFlowNode n : prev()) {
			for (ControlFlowNode nn : n.next()) {
				if (!nn.equals(this)) {
					result.add(nn);
				}
			}
		}
		return result;
	}

	/**
	 * List of nodes that can be executed just after this one
	 */
	public List<ControlFlowNode> next() {
		ArrayList<ControlFlowNode> result = new ArrayList<>();
		for (ControlFlowEdge e : parent.outgoingEdgesOf(this)) {
			result.add(e.getTargetNode());
		}
		return result;
	}

	/**
	 * List of nodes that could be executed just before this one
	 */
	public List<ControlFlowNode> prev() {
		ArrayList<ControlFlowNode> result = new ArrayList<>();
		for (ControlFlowEdge e : parent.incomingEdgesOf(this)) {
			result.add(e.getSourceNode());
		}
		return result;
	}

	public CtElement getStatement() {
		return statement;
	}

	public void setStatement(CtElement statement) {
		this.statement = statement;
	}

	public ControlFlowGraph getParent() {
		return parent;
	}

	public void setParent(ControlFlowGraph parent) {
		this.parent = parent;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		if (statement != null) {
			return id + " - " + statement;
		} else {
			return kind + "_" + id;
		}
	}

}
