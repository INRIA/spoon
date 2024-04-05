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
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllBranchesReturnTest {

	public void testSegment(AbstractProcessor<?> processor) throws Exception {

		Factory factory = new SpoonMetaFactory().buildNewFactory(
				this.getClass().getResource("/control-flow").toURI().getPath(), 7);
		ProcessingManager pm = new QueueProcessingManager(factory);
		pm.addProcessor(processor);
		pm.process(factory.getModel().getRootPackage());
	}

	@Test
	public void nestedIfSomeNotReturning() throws Exception {
		testSegment(new AbstractProcessor<CtIf>() {
			@Override
			public void process(CtIf element) {
				CtMethod<?> m = element.getParent().getParent(CtMethod.class);
				if (m != null && m.getSimpleName().equals("nestedIfSomeNotReturning"))
					if (element.getCondition().toString().contains("b < 1")) {
						AllBranchesReturn alg = new AllBranchesReturn();
						assertFalse(alg.execute(element));
					}
			}
		});
	}

	@Test
	public void testNestedIfAllReturning() throws Exception {
		testSegment(new AbstractProcessor<CtIf>() {
			@Override
			public void process(CtIf element) {
				CtMethod<?> m = element.getParent().getParent(CtMethod.class);
				if (m != null && m.getSimpleName().equals("nestedIfAllReturning"))
					if (element.getCondition().toString().contains("a > 0")) {
						AllBranchesReturn alg = new AllBranchesReturn();
						assertTrue(alg.execute(element));
					}
			}
		});
	}

}
