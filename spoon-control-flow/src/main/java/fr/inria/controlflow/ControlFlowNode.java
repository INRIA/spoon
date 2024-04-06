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

import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A node of the control flow
 * <p>
 * Created by marodrig on 13/10/2015.
 */
public class ControlFlowNode {

	public static int count = 0;

	private int id;

	/**
	 * Çontrol flow graph containing this node
	 */
	ControlFlowGraph parent;

	public BranchKind getKind() {
		return kind;
	}

	public void setKind(BranchKind kind) {
		this.kind = kind;
	}

	private BranchKind kind;

	/**
	 * Statement that is going to be pointed to by this node
	 */
	CtElement statement;
	/**
	 * Whether this Node finishes the statement. See conditionals or switch expressions for an example where it might not.
	 */
	private boolean isStatementEnd;

	List<Value> input;

	List<Value> output;

	//An object you can tag to the node
	Object tag;

	/**
	 * Visitor containing the transfer functions for each node
	 */
	TransferFunctionVisitor visitor;

	public ControlFlowNode(CtElement statement, ControlFlowGraph parent, BranchKind kind) {
		this.kind = kind;
		this.parent = parent;
		this.statement = statement;
		this.isStatementEnd = true;
		++count;
		id = count;
	}


	public ControlFlowNode(CtElement statement, ControlFlowGraph parent) {
		this.parent = parent;
		this.statement = statement;
		this.isStatementEnd = true;
		++count;
		id = count;
	}

	/**
	 * Performs the transfer using a given visitor
	 */
	public void transfer(TransferFunctionVisitor visitor) {
		this.visitor = visitor;
		transfer();
	}

	/**
	 * Perform the transfer function
	 */
	public void transfer() {
		if (statement != null && visitor != null) {
			output = visitor.transfer(statement);
		} else {
			throw new RuntimeException("Unable to perform the transfer function. Statement or visitor are null.");
		}

	}

	public int getId() {
		return id;
	}

	/**
	 * Obtains the siblings of a control node. Siblings are the nodes in parallel branches
	 */
	public List<ControlFlowNode> siblings() {
		ArrayList<ControlFlowNode> result = new ArrayList<ControlFlowNode>();
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
		ArrayList<ControlFlowNode> result = new ArrayList<ControlFlowNode>();
		for (ControlFlowEdge e : parent.outgoingEdgesOf(this)) {
			result.add(e.getTargetNode());
		}
		return result;
	}

	/**
	 * List of nodes that could be executed just before this one
	 */
	public List<ControlFlowNode> prev() {
		ArrayList<ControlFlowNode> result = new ArrayList<ControlFlowNode>();
		for (ControlFlowEdge e : parent.incomingEdgesOf(this)) {
			result.add(e.getSourceNode());
		}
		return result;
	}

	public List<Value> getOutput() {
		if (output == null) {
			transfer();
		}
		return output;
	}

	public CtElement getStatement() {
		return statement;
	}

	public void setStatement(CtElement statement) {
		this.statement = statement;
	}

	public boolean getIsStatementEnd() {
		return isStatementEnd;
	}

	public void setEndTo(ControlFlowNode end) {
		this.isStatementEnd = false;
		setTag(end);
		end.setTag(this);
		end.isStatementEnd = true;
	}

	public List<Value> getInput() {
		return input;
	}

	public void setInput(List<Value> input) {
		this.input = input;
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
		if (statement == null) {
			return kind + "_" + id;
		} else if (kind == BranchKind.STATEMENT_END || !isStatementEnd) {
			String prefix;
			if (isStatementEnd && tag instanceof ControlFlowNode start) {
				prefix = "END of " + start.id + ": " + id + " - ";
			} else {
				prefix = id + " - ";
			}
			if (statement instanceof CtAbstractSwitch<?> switchStatement) {
				return prefix + "switch (" + switchStatement.getSelector() + ")";
			} else if (statement instanceof CtReturn<?> returnStatement && returnStatement.getReturnedExpression() instanceof CtAbstractSwitch<?> switchExpression) {
				return prefix + "return switch (" + switchExpression.getSelector() + ")";
			} else if (statement instanceof CtLocalVariable<?> localVariable && localVariable.getDefaultExpression() instanceof CtAbstractSwitch<?> switchExpression) {
				CtLocalVariable<?> renderVariable = localVariable.clone();
				renderVariable.setDefaultExpression(null);
				return prefix + renderVariable + " = switch (" + switchExpression.getSelector() + ")";
			} else if (statement instanceof CtConditional<?> conditional) {
				return prefix + conditional.getCondition() + " ?";
			} else if (statement instanceof CtIf ifStatement) {
				CtIf renderStatement = ifStatement.clone();
				renderStatement.setThenStatement(null);
				renderStatement.setElseStatement(null);
				return prefix + "if (" + ifStatement.getCondition() + ")";
			} else if (statement instanceof CtDo && !isStatementEnd) {
				return prefix + "do";
			} else if (statement instanceof CtDo doWhileLoop) {
				return prefix + "while (" + doWhileLoop.getLoopingExpression() + ")";
			} else if (statement instanceof CtWhile whileLoop) {
				CtWhile renderLoop = whileLoop.clone();
				renderLoop.setBody(null);
				return prefix + renderLoop;
			} else if (statement instanceof CtFor forLoop) {
				CtFor renderLoop = forLoop.clone();
				renderLoop.setBody(null);
				return prefix + renderLoop;
			} else {
				return prefix + statement;
			}
		} else {
			return id + " - " + statement;
		}
	}

}
