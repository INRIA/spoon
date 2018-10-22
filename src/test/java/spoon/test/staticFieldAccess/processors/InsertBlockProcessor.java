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
