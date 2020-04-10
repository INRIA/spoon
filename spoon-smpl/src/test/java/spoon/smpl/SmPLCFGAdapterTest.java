package spoon.smpl;

import fr.inria.controlflow.*;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

import static org.junit.jupiter.api.Assertions.*;

public class SmPLCFGAdapterTest {
    @Test
    public void testSimplifiedRejected() {

        // contract: SmPLCFGAdapter should reject a simplified cfg

        CtClass<?> myclass = Launcher.parseClass("class A { void m() { int x = 0; } }");

        ControlFlowBuilder builder = new ControlFlowBuilder();
        ControlFlowGraph cfg = builder.build(myclass.getMethods().iterator().next());
        cfg.simplify();

        try {
            new SmPLCFGAdapter(cfg);
            fail();
        } catch (IllegalArgumentException e) { }
    }

    @Test
    public void testOutermostBlockBeginNodeRemoved() {

        // contract: SmPLCFGAdapter should remove the outermost BLOCK_BEGIN node

        CtClass<?> myclass = Launcher.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }");

        ControlFlowBuilder builder = new ControlFlowBuilder();
        SmPLCFGAdapter cfg = new SmPLCFGAdapter(builder.build(myclass.getMethods().iterator().next()));

        assertEquals(1, cfg.findNodesOfKind(BranchKind.BEGIN).size());
        assertEquals(1, cfg.findNodesOfKind(BranchKind.BEGIN).get(0).next().size());
        assertNotEquals(BranchKind.BLOCK_BEGIN, cfg.findNodesOfKind(BranchKind.BEGIN).get(0).next().get(0).getKind());
    }

    @Test
    public void testBlockEndNodesRemoved() {

        // contract: SmPLCFGAdapter should remove all BLOCK_END nodes

        CtClass<?> myclass = Launcher.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }");

        ControlFlowBuilder builder = new ControlFlowBuilder();
        SmPLCFGAdapter cfg = new SmPLCFGAdapter(builder.build(myclass.getMethods().iterator().next()));

        assertEquals(0, cfg.findNodesOfKind(BranchKind.BLOCK_END).size());
    }

    @Test
    public void testRedundantConvergenceNodesRemoved() {

        // contract: SmPLCFGAdapter should remove all CONVERGE nodes with single successor being another CONVERGE node

        CtClass<?> myclass = Launcher.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }");

        ControlFlowBuilder builder = new ControlFlowBuilder();
        SmPLCFGAdapter cfg = new SmPLCFGAdapter(builder.build(myclass.getMethods().iterator().next()));

        for (ControlFlowNode node : cfg.findNodesOfKind(BranchKind.CONVERGE)) {
            if (node.next().size() == 1) {
                assertNotEquals(BranchKind.CONVERGE, node.next().get(0).getKind());
            }
        }
    }

    @Test
    public void testBlockBeginNodesTagged() {

        // contract: SmPLCFGAdapter should tag BLOCK_BEGIN nodes with String "trueBranch" or "falseBranch"

        CtClass<?> myclass = Launcher.parseClass("class A { void m() { int x = 0; if (true) { int y = 1; } else { int z = 2; } } }");

        ControlFlowBuilder builder = new ControlFlowBuilder();
        SmPLCFGAdapter cfg = new SmPLCFGAdapter(builder.build(myclass.getMethods().iterator().next()));

        int branchesFound = 0;

        for (ControlFlowNode node : cfg.findNodesOfKind(BranchKind.STATEMENT)) {
            if (node.getStatement().toString().equals("int y = 1")) {
                branchesFound += 1;
                assertEquals(1, cfg.incomingEdgesOf(node).size());
                ControlFlowNode ancestor = cfg.incomingEdgesOf(node).iterator().next().getSourceNode();
                assertEquals(BranchKind.BLOCK_BEGIN, ancestor.getKind());
                assertTrue(ancestor.getTag() instanceof SmPLCFGAdapter.NodeTag);
                assertEquals("trueBranch", ((SmPLCFGAdapter.NodeTag) ancestor.getTag()).getLabel());
            }

            if (node.getStatement().toString().equals("int z = 2")) {
                branchesFound += 1;
                assertEquals(1, cfg.incomingEdgesOf(node).size());
                ControlFlowNode ancestor = cfg.incomingEdgesOf(node).iterator().next().getSourceNode();
                assertEquals(BranchKind.BLOCK_BEGIN, ancestor.getKind());
                assertTrue(ancestor.getTag() instanceof SmPLCFGAdapter.NodeTag);
                assertEquals("falseBranch", ((SmPLCFGAdapter.NodeTag) ancestor.getTag()).getLabel());
            }
        }

        assertEquals(2, branchesFound);
    }
}
