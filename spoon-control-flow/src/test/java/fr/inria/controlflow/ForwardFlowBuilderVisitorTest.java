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
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

import java.io.PrintWriter;
import java.util.List;
import java.net.URISyntaxException;

import static fr.inria.controlflow.BranchKind.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by marodrig on 14/10/2015.
 */
public class ForwardFlowBuilderVisitorTest {

	public static ControlFlowGraph buildGraph(String folder, final String methodName, boolean simplify)
			throws Exception {
		final ControlFlowBuilder visitor = new ControlFlowBuilder();

		Factory factory = new SpoonMetaFactory().buildNewFactory(folder, 11);
		ProcessingManager pm = new QueueProcessingManager(factory);
		pm.addProcessor(new AbstractProcessor<CtMethod>() {
			@Override
			public void process(CtMethod element) {
				if (element.getSimpleName().equals(methodName)) {
					visitor.build(element);
				}
			}

		});
		pm.process(factory.getModel().getRootPackage());

		ControlFlowGraph graph = visitor.getResult();
		if (simplify) graph.simplifyConvergenceNodes();

		System.out.println(graph.toGraphVisText());

		return graph;
	}

	public ControlFlowGraph testMethod(final String methodName, boolean simplify,
	                                   Integer branchCount, Integer stmntCount, Integer totalCount) throws Exception {
		ControlFlowGraph graph = buildGraph(
				this.getClass().getResource("/control-flow").toURI().getPath(), methodName, simplify);


		PrintWriter out = new PrintWriter("target/graph.dot");
		out.println(graph.toGraphVisText());
		out.close();

		if (branchCount != null) assertEquals((int) branchCount, graph.branchCount());
		if (stmntCount != null) assertEquals((int) stmntCount, graph.statementCount());
		if (totalCount != null) assertEquals((int) totalCount, graph.vertexSet().size());



/*
        graph.simplifyConvergenceNodes();
        PrintWriter out2 = new PrintWriter("C:\\MarcelStuff\\DATA\\graphsimplified.dot");
        out2.println(graph.toGraphVisText());
        out2.close();*/
		return graph;


	}


	/**
	 * Test some topology properties
	 *
	 * @param graph                   Graph to test
	 * @param edgesBranchesStatement  Number of edges going from branches to statements
	 * @param edgesBranchesBranches   Number of edges going from branches to branches
	 * @param edgesStatementStatement Number of edges going from statements to statements
	 * @param totalEdges              Total number of edges
	 */
	private void testEdges(ControlFlowGraph graph, Integer edgesBranchesStatement, Integer edgesBranchesBranches,
	                       Integer edgesStatementStatement, Integer totalEdges) {

		int bs, bb, ss;
		bs = bb = ss = 0;

		for (ControlFlowEdge e : graph.edgeSet()) {
			if (e.getSourceNode().getKind() == BRANCH && e.getTargetNode().getKind() == STATEMENT) bs++;
			else if (e.getSourceNode().getKind() == BRANCH && e.getTargetNode().getKind() == BRANCH) bb++;
			else if (e.getSourceNode().getKind() == STATEMENT && e.getTargetNode().getKind() == STATEMENT) ss++;
		}

		if (edgesBranchesStatement != null) {
			assertEquals((int) edgesBranchesStatement, bs);
		}

		if (edgesBranchesBranches != null) {
			assertEquals((int) edgesBranchesBranches, bb);
		}

		if (edgesStatementStatement != null) {
			assertEquals((int) edgesStatementStatement, ss);
		}

		if (totalEdges != null) assertEquals((int) totalEdges, graph.edgeSet().size());
	}

    /*
    @Ignore("")
    @Test
    public void testBreakComplex() throws Exception {
        testMethod("continueAndBreak", true, 6,22,39);
    }*/

	@Test
	public void testBreakSimpleLabeled() throws Exception {
		testMethod("simpleBreakLabeled", true, 3, 12, 25);
	}

	@Test
	public void testBreakSimple() throws Exception {
		testMethod("simpleBreak", true, 3, 12, 25);
//        fail();
	}


	@Test
	public void testContinueSimple() throws Exception {
		testMethod("simpleContinueTo", true, 3, 6, 19);
	}

	//Test some mixed conditions
	@Test
	public void testifThenElseBlock() throws Exception {
		testMethod("simple", false, 0, 2, 6);
	}

	//Test some mixed conditions
	@Test
	public void testSwitch() throws Exception {
		testMethod("switchTest", false, 1, 13, 29);
	}

	//Test fall-through of last switch case
	@Test
	public void testSwitchFallThrough() throws Exception {
		testMethod("lastCaseFallThrough", false, 1, 4, 12);
	}

	@Test
	public void testSwitchImplicitDefault() throws Exception {
		ControlFlowGraph graph = testMethod("lastCaseFallThrough", false, 1, 4, 12);
		graph.simplify();
		ControlFlowPathHelper pathHelper = new ControlFlowPathHelper();
		ControlFlowNode entryNode = pathHelper.findNodeByString(graph, "int b = 0");
		ControlFlowNode caseNode = pathHelper.findNodeByString(graph, "b = 1");
		boolean canAvoid = pathHelper.canAvoidNode(entryNode, caseNode);
		assertTrue(canAvoid, "Path for implicit default case missing");
	}

	@Test
	public void testMultipleCaseExpressions() throws Exception {
		ControlFlowGraph graph = testMethod("multipleCaseExpressions", true, 1, 8, 17);
		graph.simplify();
		ControlFlowPathHelper pathHelper = new ControlFlowPathHelper();
		ControlFlowNode startNode = pathHelper.findNodeByString(graph, "int b = 0");
		List<List<ControlFlowNode>> paths = pathHelper.paths(startNode);
		assertTrue(paths.size() > 2, "Not enough paths. Possibly missing different paths from multiple expressions for a case");
	}

	//Test some mixed conditions
	@Test
	public void testSimple() throws Exception {
		ControlFlowGraph graph = testMethod("simple", false, 0, 2, 6);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 1, 5);
	}

	//Test some mixed conditions

	@Test
	public void testMixed() throws Exception {
		//int branchCount, int stmntCount, int totalCount
		ControlFlowGraph graph = testMethod("mixed", false, 2, 5, 17);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 0, null);
	}

	@Test
	public void testMixedSimplified() throws Exception {
		ControlFlowGraph graph = testMethod("mixed", true, 2, 5, 15);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 2, 0, 0, null);
	}

	@Test
	public void testCtFor() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctFor", true, 1, 4, 11);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 0, null);
	}

	@Test
	public void testCtForBlock() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctForBlock", true, 1, 5, 12);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 1, null);
	}

	@Test
	public void testIfThenBlock() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ifThenBlock", true, 1, 3, 10);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 1, null);
	}

	@Test
	public void testIfThenElse() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ifThenElse", true, 1, 3, 12);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 0, null);
	}

	@Test
	public void testIfThen() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ifThen", true, 1, 2, 9);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 0, null);
	}

	@Test
	public void testCtForEachBlock() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctForEachBlock", true, 1, 5, 12);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 2, null);
	}

	@Test
	public void testCtForEach() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctForEach", true, 1, 4, 11);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 1, null);
	}

	@Test
	public void testCtWhileBlock() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctWhileBlock", false, 1, 5, 13);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 2, null);
	}

	@Test
	public void testCtWhileBlockSimplify() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctWhileBlock", true, 1, 5, 12);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 2, null);
	}

	@Test
	public void testCtWhile() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctWhile", true, 1, 4, 11);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 1, null);
	}

	@Test
	public void testCtDoWhileBlock() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctDoWhileBlock", false, 1, 5, 14);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 2, null);
	}

	@Test
	public void testCtDoWhileBlockSimplify() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctDoWhileBlock", true, 1, 5, 12);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 2, null);
	}

	@Test
	public void testCtDoWhile() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("ctDoWhile", false, 1, 4, 13);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 1, null);
	}

	@Test
	public void testConditional() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("conditional", false, 1, 3, 9);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 2, 0, 0, null);
	}

	@Test
	public void testNestedConditional() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("nestedConditional", false, 2, 5, 13);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 3, 1, 0, null);
	}

	@Test
	public void testNestedIf() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("nestedIfs", false, 3, 6, null);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 0, 0, 2, null);
	}

	@Test
	public void testInvocation() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("invocation", true, 1, 2, null);
		//Branches-Statement Branches-Branches sStatement-Statement total
		testEdges(graph, 1, 0, 0, null);
	}

	@Test
	public void testConstructor() throws URISyntaxException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		ControlFlowBuilder visitor = new ControlFlowBuilder();

		Factory factory = new SpoonMetaFactory().buildNewFactory(this.getClass().getResource("/control-flow").toURI().getPath(), 17);
		ProcessingManager pm = new QueueProcessingManager(factory);
		pm.addProcessor(new AbstractProcessor<CtConstructor<?>>() {
			@Override
			public void process(CtConstructor<?> element) {
				if (!element.getBody().getStatements().isEmpty()) {
					visitor.build(element);
				}
			}

		});
		pm.process(factory.getModel().getRootPackage());

		ControlFlowGraph graph = visitor.getResult();
		ControlFlowNode entryNode = graph.findNodesOfKind(BEGIN).get(0);
		ControlFlowNode exitNode = graph.getExitNode();

		assertFalse(graph.containsEdge(entryNode, exitNode), "Graph is missing statements");
	}

	@Test
	public void testConstructorCall() throws Exception {
		testMethod("constructorCall", true, 0, 1, 5);
	}

	@Test
	public void testtestCase() throws Exception {
		//branchCount, stmntCount, totalCount
		ControlFlowGraph graph = testMethod("complex1", true, null, null, null);
		graph.simplifyBlockNodes();
		System.out.println(graph.toGraphVisText());
		//Branches-Statement Branches-Branches sStatement-Statement total
		//testEdges(graph, 2, 0, 2, null);
	}


}
