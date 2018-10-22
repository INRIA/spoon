/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertEquals;

public class CtIteratorTest {

	@Test
	public void testCtElementIteration() {
		// contract: CtIterator must go over all nodes in dfs order
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{"--output-type", "nooutput"});
		launcher.getEnvironment().setNoClasspath(true);
		// resources to iterate
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.buildModel();

		// get the first Type
		CtElement root = launcher.getFactory().getModel().getAllTypes().iterator().next();

		testCtIterator(root);
		testAsIterable(root);
		testDescendantIterator(root);
	}

	public void testCtIterator(CtElement root) {
		Deque<CtElement> ctElements = getDescendantsInDFS(root);

		CtIterator iterator = new CtIterator(root);
		while (iterator.hasNext()) {
			assertEquals(ctElements.pollFirst(), iterator.next());
		}
	}

	public void testAsIterable(CtElement root) {
		Deque<CtElement> ctElements = getDescendantsInDFS(root);

		for (CtElement elem: root.asIterable()) {
			assertEquals(elem, ctElements.pollFirst());
		}
	}

	public void testDescendantIterator(CtElement root) {
		Deque<CtElement> ctElements = getDescendantsInDFS(root);

		root.descendantIterator().forEachRemaining((CtElement elem) ->
				assertEquals(ctElements.pollFirst(), elem)
		);
	}

	Deque<CtElement> getDescendantsInDFS(CtElement root) {
		CtScannerList counter = new CtScannerList();
		root.accept(counter);

		return counter.nodes;
	}

	/**
	 * Class that saves a deque with all the nodes the {@link CtScanner} visits,
	 * in DFS order, for the {@link CtIterator} test
	 */
	class CtScannerList extends CtScanner {
		public Deque<CtElement> nodes = new ArrayDeque<>();

		@Override
		protected void enter(CtElement e) {
			nodes.addLast(e);
		}
	}
}
