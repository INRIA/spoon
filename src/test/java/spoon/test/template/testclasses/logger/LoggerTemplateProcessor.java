/*
 * Copyright (C) 2006-2015 INRIA and contributors
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

package spoon.test.template.testclasses.logger;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;
import spoon.template.Template;

public class LoggerTemplateProcessor<T> extends AbstractProcessor<CtMethod<T>> {
	@Override
	public boolean isToBeProcessed(CtMethod<T> candidate) {
		return candidate.getBody() != null && !isSubOfTemplate(candidate);
	}

	private boolean isSubOfTemplate(CtMethod<T> candidate) {
		return candidate.getDeclaringType().isSubtypeOf(getFactory().Type().createReference(Template.class));
	}

	@Override
	public void process(CtMethod<T> element) {
		final CtBlock log = new LoggerTemplate(element.getDeclaringType().getSimpleName(), element.getSimpleName(), element.getBody()).apply(element.getDeclaringType());
		element.setBody(log);
	}
}
