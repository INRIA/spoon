package fr.inria.controlflow;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExceptionFlowTests {
    @Test
    public void testBasicSingleLevel() {
        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "      b();\n" +
                                                 "      c();\n" +
                                                 "    } catch (Exception e) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        Set<ControlFlowNode> nodes = new HashSet<>();

        Iterator<CtElement> it = method.descendantIterator();

        ControlFlowNode catchNode = cfg.findNodesOfKind(BranchKind.CATCH).get(0);

        while (it.hasNext()) {
            CtElement element = it.next();

            if (!(element instanceof CtInvocation)) {
                continue;
            }

            try {
                if (element.toString().equals("a()") || element.toString().equals("b()") || element.toString().equals("c()")) {
                    nodes.add(cfg.findNode(element));
                }
            }
            catch (Exception e) {
                fail(e.toString());
            }
        }

        assertEquals(3, nodes.size());

        for (ControlFlowNode node : nodes) {
            assertEquals(2, node.next().size());
            assertEquals(true, node.next().contains(catchNode));
        }
    }
}
