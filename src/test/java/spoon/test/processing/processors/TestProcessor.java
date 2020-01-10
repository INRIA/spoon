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
package spoon.test.processing.processors;

import java.util.Date;

import org.apache.logging.log4j.Level;
import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;
import spoon.template.Substitution;
import spoon.test.template.testclasses.constructors.TemplateWithConstructor;

public class TestProcessor extends AbstractProcessor<CtElement> {

	@Override
	public void init() {
		super.init();
		getEnvironment().debugMessage("MAIN METHODS: " + getFactory().Method().getMainMethods());
	}

	public void process(CtElement element) {
		if ((!(element instanceof CtPackage)) && !element.isParentInitialized()) {
			getEnvironment().report(this, Level.ERROR, element,
					"Element's parent is null (" + element + ")");
			throw new RuntimeException("uninitialized parent detected");
		}
		if (element instanceof CtTypedElement) {
			if (((CtTypedElement<?>) element).getType() == null) {
				getEnvironment().report(this, Level.WARN, element,
						"Element's type is null (" + element + ")");
			}
		}
		if (element instanceof CtClass) {
			CtClass<?> c = (CtClass<?>) element;
			if ("Secondary".equals(c.getSimpleName())) {
				@SuppressWarnings("unused")
				CompilationUnit cu = c.getPosition().getCompilationUnit();
			}
			if ("C1".equals(c.getSimpleName())) {
				Substitution.insertAll(c, new TemplateWithConstructor(
						getFactory().Type().createReference(Date.class)));
			}
		}
	}

}
