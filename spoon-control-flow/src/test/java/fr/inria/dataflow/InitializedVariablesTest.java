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

package fr.inria.dataflow;

import fr.inria.controlflow.*;
import org.junit.Ignore;
import org.junit.Test;
import spoon.reflect.reference.CtVariableReference;

import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

public class InitializedVariablesTest {

	@Test
	public void testRunSimple() throws Exception {
		ControlFlowGraph graph = ForwardFlowBuilderVisitorTest.buildGraph(
				this.getClass().getResource("/initialized").toURI().getPath(), "simpleflow", true);
		InitializedVariables vars = new InitializedVariables();
		graph.simplify();
		ControlFlowNode n = graph.findNodesOfKind(BranchKind.EXIT).get(0);
		vars.run(n);

		for (CtVariableReference v : vars.getInitialized()) {
			assertFalse(v.getSimpleName().contains("notInitialized"));
		}
		assertEquals(vars.getInitialized().size(), 6);
	}

	@Test
	public void testRunComplex() throws Exception {
		ControlFlowGraph graph = ForwardFlowBuilderVisitorTest.buildGraph(
				this.getClass().getResource("/initialized").toURI().getPath(), "makewt", true);

		System.out.println("Compiled OK");

		InitializedVariables vars = new InitializedVariables();
		graph.simplify();
		System.out.println(graph.toGraphVisText());
		ControlFlowNode n = ControlFlowBuilder.firstNode(graph, graph.branches().get(7).getStatement().getParent());
		vars.run(n);

		HashSet<String> names = new HashSet<>();
		for (CtVariableReference v : vars.getInitialized()) names.add(v.getSimpleName());

		assertEquals(vars.getInitialized().size(), 11);
		assertTrue(names.contains("wn4r"));
		assertTrue(names.contains("wk1r"));
		assertTrue(names.contains("wk3r"));
		assertTrue(names.contains("ip"));
		assertTrue(names.contains("w"));
		assertTrue(names.contains("delta"));
		assertTrue(names.contains("nw1"));
		assertTrue(names.contains("nw"));
		assertTrue(names.contains("nw0"));
		assertTrue(names.contains("delta2"));
		assertTrue(names.contains("nwh"));
	}

	@Test
	public void testRunComplex2() throws Exception {

		ControlFlowNode.count = 0;

		ControlFlowGraph graph = ForwardFlowBuilderVisitorTest.buildGraph(
				this.getClass().getResource("/initialized").toURI().getPath(), "complexFlow1", true);

		System.out.println("Compiled OK");

		InitializedVariables vars = new InitializedVariables();
		graph.simplify();
		System.out.println(graph.toGraphVisText());
		ControlFlowNode n = ControlFlowBuilder.firstNode(graph, graph.findNodeById(15).getStatement().getParent());
		vars.run(n);

		HashSet<String> names = new HashSet<>();
		for (CtVariableReference v : vars.getInitialized()) names.add(v.getSimpleName());

		assertEquals(vars.getInitialized().size(), 5);
		assertTrue(names.contains("pp"));
		assertTrue(names.contains("p"));
		assertTrue(names.contains("iter"));
		assertTrue(names.contains("eps"));
	}

	@Test
	public void testRunComplexSwitch() throws Exception {
		ControlFlowGraph graph = ForwardFlowBuilderVisitorTest.buildGraph(
				this.getClass().getResource("/initialized").toURI().getPath(), "realForwardFull", true);

		System.out.println("Compiled OK");

		InitializedVariables vars = new InitializedVariables();
		graph.simplify();
		System.out.print(graph.toGraphVisText());
		ControlFlowNode n = ControlFlowBuilder.firstNode(graph, graph.branches().get(3).getStatement().getParent());
		vars.run(n);

		//boolean cwk1r = false;
		//boolean cwk3r = false;
		HashSet<String> names = new HashSet<>();
		for (CtVariableReference v : vars.getInitialized()) names.add(v.getSimpleName());

		assertEquals(vars.getInitialized().size(), 6);
		assertTrue(names.contains("a"));
		assertTrue(names.contains("twon"));
		assertTrue(names.contains("offa"));
		assertTrue(names.contains("m"));
		assertTrue(names.contains("plan"));
		assertTrue(names.contains("n"));
	}

	@Test
	public void testIndex54_Bug() throws Exception {
		ControlFlowGraph graph = ForwardFlowBuilderVisitorTest.buildGraph(
				this.getClass().getResource("/initialized").toURI().getPath(), "isPositiveSemiDefinite", true);

		System.out.println("Compiled OK");

		InitializedVariables vars = new InitializedVariables();
		graph.simplify();
		System.out.print(graph.toGraphVisText());
		ControlFlowNode n = ControlFlowBuilder.firstNode(graph, graph.branches().get(0).getStatement().getParent());
		vars.run(n);

		//boolean cwk1r = false;
		//boolean cwk3r = false;
		HashSet<String> names = new HashSet<>();
		for (CtVariableReference v : vars.getInitialized()) names.add(v.getSimpleName());
		assertEquals(vars.getInitialized().size(), 2);
		assertTrue(names.contains("eigenValues"));
		assertTrue(names.contains("e"));
	}


	@Ignore("Known bug")
	@Test
	public void testTwoConsecutiveLoops() throws Exception {
		ControlFlowGraph graph = ForwardFlowBuilderVisitorTest.buildGraph(
				this.getClass().getResource("/initialized").toURI().getPath(), "twoLoops", true);
		graph.simplify();
		InitializedVariables vars = new InitializedVariables();
		//graph.simplify();
		System.out.print(graph.toGraphVisText());
		ControlFlowNode n = ControlFlowBuilder.firstNode(graph, graph.branches().get(1).getStatement().getParent());
		vars.run(n);

		//boolean cwk1r = false;
		//boolean cwk3r = false;
		HashSet<String> names = new HashSet<>();
		for (CtVariableReference v : vars.getInitialized()) names.add(v.getSimpleName());
		assertEquals(2, vars.getInitialized().size());
		assertFalse(names.contains("i"));
	}


}