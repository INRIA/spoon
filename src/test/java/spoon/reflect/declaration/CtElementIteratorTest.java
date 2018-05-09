package spoon.reflect.declaration;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.visitor.CtIterator;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CtElementIteratorTest {
    @Test
    public void testCtElementForEach() throws Exception {
        // contract: CtIterator must go over all nodes in dfs order
        final Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.getEnvironment().setNoClasspath(true);
        // resources to iterate
        launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
        launcher.buildModel();

        // get the first Type
        CtElement root = launcher.getFactory().getModel().getAllTypes().iterator().next();

        List<CtElement> ctElements = getDescendantsInDFS(root);

        testAsIterable(ctElements, root);
        testDescendantIterator(ctElements, root);
    }

    public void testAsIterable(List<CtElement> ctElements, CtElement root) {
        int i = 0;
        for (CtElement elem : CtElement.asIterable(root)) {
            assertEquals(elem, ctElements.get(i));
            i++;
        }
    }

    public void testDescendantIterator(List<CtElement> ctElements, CtElement root) {
        root.descendantIterator().forEachRemaining((CtElement elem) ->
                assertEquals(ctElements.remove(0), elem)
        );
    }

    public List<CtElement> getDescendantsInDFS(CtElement root) {
        CtScannerList counter = new CtScannerList();
        root.accept(counter);

        List<CtElement> ctElements = new ArrayList<>();
        while (! counter.nodes.isEmpty()) {
            ctElements.add(counter.nodes.pollFirst());
        }

        return ctElements;
    }

    /**
     * Class that saves a deque with all the nodes the {@link CtScanner} visits,
     * in DFS order, for the {@link CtIterator} test
     */
    class CtScannerList extends CtScanner {
        public ArrayDeque<CtElement> nodes = new ArrayDeque<>();

        @Override
        protected void enter(CtElement e) {
            nodes.addLast(e);
        }
    }
}
