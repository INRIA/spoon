package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;

import static org.junit.Assert.assertEquals;

public class CtIteratorTest {
    @Test
    public void testMethodsInIterator() throws Exception {
        // contract: CtIterator must go over all nodes in dfs order
        final Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.getEnvironment().setNoClasspath(true);
        // resources to iterate
        launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
        launcher.buildModel();

        // get the first Type
        CtElement root = launcher.getFactory().getModel().getAllTypes().iterator().next();

        // use custom CtScanner to assert the proper behaviour
        CtScannerList counter = new CtScannerList();

        // scan the root to get the elements in DFS order
        root.accept(counter);

        // test the iterator by testing that it matches the DFS order as expected
        CtIterator iterator = new CtIterator(root);
        while (iterator.hasNext()) {
            assertEquals(counter.nodes.pollFirst(), iterator.next());
        }
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
