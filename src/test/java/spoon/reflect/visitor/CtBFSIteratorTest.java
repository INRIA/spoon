package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

public class CtBFSIteratorTest {

	@Test
	public void testCtElementIteration() {
		// contract: CtIterator must go over all nodes in bfs order
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{"--output-type", "nooutput"});
		launcher.getEnvironment().setNoClasspath(true);
		// resources to iterate
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.buildModel();

		// get the first Type
		CtElement root = launcher.getFactory().getModel().getAllTypes().iterator().next();

		testCtIterator(root);
	}

	public void testCtIterator(CtElement root) {
		CtBFSIteratorTest.BFS it = new CtBFSIteratorTest.BFS(root);

		CtBFSIterator iterator = new CtBFSIterator(root);
		while (iterator.hasNext()) {
			assertTrue(it.hasNext());
			assertEquals(it.next(), iterator.next());
		}
	}

	/**
	 * Class that implement an alternative BFS iterator,
	 * for the {@link CtBFSIterator} test
	 */
	class BFS implements Iterator<CtElement> {
		Queue<CtElement> queue = new ArrayDeque<>();

		public BFS(CtElement root) {
			queue.add(root);
		}

		@Override
		public boolean hasNext() {
			return !queue.isEmpty();
		}

		@Override
		public CtElement next() {
			CtElement cur = queue.poll();

			//Get all direct children of cur
			List<CtElement> toAdd = cur.getElements(el -> (el.getParent() == cur));

			//Queue them
			queue.addAll(toAdd);
			return cur;
		}
	}
}
