package spoon.test.staticFieldAccess.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.code.CtBlockImpl;

/**
 * Created by nicolas on 08/09/2014.
 */
public class InsertBlockProcessor extends AbstractProcessor<CtMethod<?>> {

	@Override
	public boolean isToBeProcessed(CtMethod<?> candidate) {
		return super.isToBeProcessed(candidate) && candidate.getBody() != null;
	}

	@Override
	public void process(CtMethod<?> element) {
		CtBlock block = new CtBlockImpl();
		// we clone the body so that there is no two elements with the same parent 
		block.addStatement(element.getBody().clone());
		element.setBody(block);
	}
}
