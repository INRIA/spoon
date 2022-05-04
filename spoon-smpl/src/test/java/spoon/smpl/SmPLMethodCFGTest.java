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

package spoon.smpl;

import fr.inria.controlflow.*;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import static org.junit.jupiter.api.Assertions.*;

public class SmPLMethodCFGTest {
	@Test
	public void testOutermostBlockBeginNodeRemoved() {

		// contract: SmPLCFGAdapter should remove the outermost BLOCK_BEGIN node

		CtClass<?> myclass = SpoonJavaParser.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }", "A");

		SmPLMethodCFG cfg = new SmPLMethodCFG(myclass.getMethods().iterator().next());

		assertEquals(1, cfg.findNodesOfKind(BranchKind.BEGIN).size());
		assertEquals(1, cfg.findNodesOfKind(BranchKind.BEGIN).get(0).next().size());
		assertNotEquals(BranchKind.BLOCK_BEGIN, cfg.findNodesOfKind(BranchKind.BEGIN).get(0).next().get(0).getKind());
	}

	@Test
	public void testBlockEndNodesRemoved() {

		// contract: SmPLCFGAdapter should remove all BLOCK_END nodes

		CtClass<?> myclass = SpoonJavaParser.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }", "A");

		SmPLMethodCFG cfg = new SmPLMethodCFG(myclass.getMethods().iterator().next());

		assertEquals(0, cfg.findNodesOfKind(BranchKind.BLOCK_END).size());
	}

	@Test
	public void testBlockBeginNodesTagged() {

		// contract: SmPLCFGAdapter should tag BLOCK_BEGIN nodes with String "trueBranch" or "falseBranch"

		CtClass<?> myclass = SpoonJavaParser.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }", "A");

		SmPLMethodCFG cfg = new SmPLMethodCFG(myclass.getMethods().iterator().next());

		int branchesFound = 0;

		for (ControlFlowNode node : cfg.findNodesOfKind(BranchKind.STATEMENT)) {
			if (node.getStatement().toString().equals("int y = 1")) {
				branchesFound += 1;
				assertEquals(1, cfg.incomingEdgesOf(node).size());
				ControlFlowNode ancestor = cfg.incomingEdgesOf(node).iterator().next().getSourceNode();
				assertEquals(BranchKind.BLOCK_BEGIN, ancestor.getKind());
				assertTrue(ancestor.getTag() instanceof SmPLMethodCFG.NodeTag);
				assertEquals("trueBranch", ((SmPLMethodCFG.NodeTag) ancestor.getTag()).getLabel());
			}

			if (node.getStatement().toString().equals("int z = 2")) {
				branchesFound += 1;
				assertEquals(1, cfg.incomingEdgesOf(node).size());
				ControlFlowNode ancestor = cfg.incomingEdgesOf(node).iterator().next().getSourceNode();
				assertEquals(BranchKind.BLOCK_BEGIN, ancestor.getKind());
				assertTrue(ancestor.getTag() instanceof SmPLMethodCFG.NodeTag);
				assertEquals("falseBranch", ((SmPLMethodCFG.NodeTag) ancestor.getTag()).getLabel());
			}
		}

		assertEquals(2, branchesFound);
	}

	@Test
	public void testUnsupportedElementInAnnotationBug() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.addInputResource(new VirtualFile("class A {\n" +
												  "  public static @interface MyAnnotation {\n" +
												  "    public String[] value() default {};\n" +
												  "  }\n" +
												  "\n" +
												  "  @MyAnnotation({\"a\", \"b\"})\n" +
												  "  public void m() {\n" +
												  "    int[] xs = new int[]{1, 2, 3};\n" +
												  "  }\n" +
												  "}\n"));
		launcher.buildModel();
		CtMethod<?> method = launcher.getModel().getRootPackage().getType("A").getMethodsByName("m").get(0);

		SmPLMethodCFG.UnsupportedElementSwapper ues = new SmPLMethodCFG.UnsupportedElementSwapper(method);
		ues.restore();

		assertFalse(method.getParent().toString().contains("__SmPLUnsupported__"));
	}

	@Test
	public void testNestedInvocationRestoreBug() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.addInputResource(new VirtualFile("class A {\n" +
												  "  public void foo(int[] xs) { }\n" +
												  "  public void m() {\n" +
												  "    foo(new int[]{1, 2, 3});\n" +
												  "  }\n" +
												  "}\n"));
		launcher.buildModel();
		CtMethod<?> method = launcher.getModel().getRootPackage().getType("A").getMethodsByName("m").get(0);

		SmPLMethodCFG.UnsupportedElementSwapper ues = new SmPLMethodCFG.UnsupportedElementSwapper(method);
		ues.restore();

		assertFalse(method.toString().contains("__SmPLUnsupported__"));
	}

	@Test
	public void testForLoopUnsupportedSwap() {

		// contract: the UnsupportedElementSwapper should replace CtFor elements

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.addInputResource(new VirtualFile("class A {\n" +
												  "  private void forLoop() {\n" +
												  "    for (int i = 0; i < 10; ++i) {\n" +
												  "      System.out.println(i);\n" +
												  "    }\n" +
												  "  }\n" +
												  "}\n"));
		launcher.buildModel();
		CtMethod<?> method = launcher.getModel().getRootPackage().getType("A").getMethodsByName("forLoop").get(0);

		assertTrue(method.filterChildren(new TypeFilter<>(CtFor.class)).list().size() == 1);

		SmPLMethodCFG.UnsupportedElementSwapper ues = new SmPLMethodCFG.UnsupportedElementSwapper(method);

		assertTrue(method.filterChildren(new TypeFilter<>(CtFor.class)).list().size() == 0);
	}

	@Test
	public void testForEachLoopUnsupportedSwap() {

		// contract: the UnsupportedElementSwapper should replace CtForEach elements

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.addInputResource(new VirtualFile("class A {\n" +
												  "  private void forEachLoop() {\n" +
												  "    for (int i : Arrays.asList(1, 2, 3, 4)) {\n" +
												  "      System.out.println(i);\n" +
												  "    }\n" +
												  "  }\n" +
												  "}\n"));
		launcher.buildModel();
		CtMethod<?> method = launcher.getModel().getRootPackage().getType("A").getMethodsByName("forEachLoop").get(0);

		assertTrue(method.filterChildren(new TypeFilter<>(CtForEach.class)).list().size() == 1);

		SmPLMethodCFG.UnsupportedElementSwapper ues = new SmPLMethodCFG.UnsupportedElementSwapper(method);

		assertTrue(method.filterChildren(new TypeFilter<>(CtForEach.class)).list().size() == 0);
	}
}
