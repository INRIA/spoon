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

/**
 * The kind of a {@link ControlFlowNode}
 */
public enum NodeKind {
	/**
	 * Represents the start of a try block
	 */
	TRY,
	/**
	 * Represents the start of a catch block
	 */
	CATCH,
	/**
	 * Represents the start of a finally block
	 */
	FINALLY,
	/**
	 * Represents a branch
	 */
	BRANCH,
	/**
	 * Represents an statement
	 */
	STATEMENT,
	/**
	 * Represents the beginning of a block
	 */
	BLOCK_BEGIN,
	/**
	 * Represents the end of a block
	 */
	BLOCK_END,
	/**
	 * The exit node of all branches. Depending on the analysis it may be convenient to leave them
	 */
	CONVERGE,
	/**
	 * The node, where all return statements point to
	 */
	EXIT,
	/**
	 * Entry point for the control flow graph
	 */
	BEGIN
}
