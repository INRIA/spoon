/**
 * The MIT License
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.inria.controlflow;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtIfImpl;
import static fr.inria.controlflow.BranchKind.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Control flow graph tests
 *
 * Created by marodrig on 14/10/2015.
 */
public class ControlFlowGraphTests {

	@Test
	public void testFindNodeNotFound() throws NotFoundException{
		assertThrows(NotFoundException.class, () -> {
			ControlFlowGraph graph = new ControlFlowGraph();
			CtStatement s = new CtIfImpl();
			ControlFlowNode branch1 = new ControlFlowNode(null, graph, BRANCH);
			ControlFlowNode branch2 = new ControlFlowNode(null, graph, BRANCH);
			graph.addEdge(branch1, branch2);
			ControlFlowNode n = graph.findNode(s);
		});
	} 

	@Test
	public void testFindNode() throws NotFoundException {
		ControlFlowGraph graph = new ControlFlowGraph();

		CtStatement s = new CtIfImpl();
		ControlFlowNode branch1 = new ControlFlowNode(s, graph, BRANCH);
		ControlFlowNode branch2 = new ControlFlowNode(null, graph, BRANCH);
		graph.addEdge(branch1, branch2);

		assertEquals(graph.findNode(s), branch1);
	}

	/**
	 *     Build this graph (* means fictitious nodes)
	 *     X1 -X2 - O1
	 *     |    |   |
	 *     \ __*1 _/
	 *          |
	 *          O2
	 *
	 *     Simplify onto this
	 *     X1 -X2 - O1
	 *     |    |   |
	 *     \__ O2 __/
	 */
	@Test
	public void testSimplify() {
		ControlFlowGraph graph = new ControlFlowGraph();

		CtStatement s = new CtIfImpl();

		ControlFlowNode branch1 = new ControlFlowNode(s, graph, BRANCH);
		ControlFlowNode branch2 = new ControlFlowNode(null, graph, BRANCH);
		ControlFlowNode node1 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode node2 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode fictitious = new ControlFlowNode(null, graph, CONVERGE);

		graph.addEdge(branch1, branch2);
		graph.addEdge(branch1, fictitious);
		graph.addEdge(branch2, node1);
		graph.addEdge(branch2, fictitious);
		graph.addEdge(node1, fictitious);
		graph.addEdge(fictitious, node2);

		graph.simplifyConvergenceNodes();

		assertTrue(graph.containsEdge(branch1, node2));
		assertTrue(graph.containsEdge(branch2, node2));
		assertTrue(graph.containsEdge(node1, node2));
		assertFalse(graph.containsVertex(fictitious));
	}

	/**
	 *     Build this graph (* means fictitious nodes)
	 *     X1 -X2 - O1
	 *     |    |   |
	 *     \ __*1 _/
	 *          |
	 *         *2
	 *          |
	 *          O2
	 *
	 *     Simplify onto this
	 *     X1 -X2 - O1
	 *     |    |   |
	 *     \__ O2 __/
	 */
	@Test
	public void testSimplify2() {
		ControlFlowGraph graph = new ControlFlowGraph();

		ControlFlowNode branch1 = new ControlFlowNode(null, graph, BRANCH);
		ControlFlowNode branch2 = new ControlFlowNode(null, graph, BRANCH);
		ControlFlowNode node1 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode node2 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode fictitious = new ControlFlowNode(null, graph, CONVERGE);
		ControlFlowNode fictitious2 = new ControlFlowNode(null, graph, CONVERGE);


		graph.addEdge(branch1, branch2);
		graph.addEdge(branch1, fictitious);
		graph.addEdge(branch2, node1);
		graph.addEdge(branch2, fictitious);
		graph.addEdge(node1, fictitious);
		graph.addEdge(fictitious, fictitious2);
		graph.addEdge(fictitious2, node2);

		graph.simplifyConvergenceNodes();

		assertTrue(graph.containsEdge(branch1, node2));
		assertTrue(graph.containsEdge(branch2, node2));
		assertTrue(graph.containsEdge(node1, node2));
		assertFalse(graph.containsVertex(fictitious));
		assertFalse(graph.containsVertex(fictitious2));
	}

	@Test
	public void testCounting() {
		ControlFlowGraph graph = new ControlFlowGraph();

		ControlFlowNode branch1 = new ControlFlowNode(null, graph, BRANCH);
		ControlFlowNode branch2 = new ControlFlowNode(null, graph, BRANCH);
		ControlFlowNode node1 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode node2 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode node3 = new ControlFlowNode(null, graph, STATEMENT);
		ControlFlowNode fictitious = new ControlFlowNode(null, graph, CONVERGE);

		graph.addEdge(branch1, branch2);
		graph.addEdge(branch1, fictitious);
		graph.addEdge(branch2, node1);
		graph.addEdge(branch2, fictitious);
		graph.addEdge(node1, fictitious);
		graph.addEdge(fictitious, node2);
		graph.addEdge(node2, node3);

		assertEquals(2, graph.branchCount());
		assertEquals(3, graph.statementCount());
	}

	@Test
	void issue4803() {
		// contract: analyzing while(true); should not throw a NPE. See issue 4803
		ControlFlowBuilder builder = new ControlFlowBuilder();
		Launcher launcher = new Launcher();
		CtElement element = launcher.getFactory().createCodeSnippetStatement("while(true)").compile();
		assertDoesNotThrow(() -> builder.build(element));
	}
}
