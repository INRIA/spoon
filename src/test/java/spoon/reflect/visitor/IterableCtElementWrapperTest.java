package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;

import static org.junit.Assert.assertEquals;

public class IterableCtElementWrapperTest {
    @Test
    public void testIterableOrder() throws Exception {
        // contract: CtIterator must go over all nodes in dfs order
        final Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.getEnvironment().setNoClasspath(true);
        // resources to iterate
        launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
        launcher.buildModel();

        // get the first Type
        CtElement root = launcher.getFactory().getModel().getAllTypes().iterator().next();

        // use custom CtScanner to assert the proper ordering
        CtScannerList counter = new CtScannerList();
        root.accept(counter);

        IterableCtElementWrapper iterable = new IterableCtElementWrapper(root);
        for (CtElement elem : iterable) {
            assertEquals(counter.nodes.pollFirst(), elem);
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
