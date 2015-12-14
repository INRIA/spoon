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

package spoon.reflect.visitor.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.CtVisitor;

import java.util.Collections;
import java.util.List;

/**
 * Used to check if a visitor (or a sub class) have all scanner and visitor methods necessary.
 *
 * @param <T>
 * 		Visitor to analyse.
 */
public class CheckVisitorProcessor<T extends CtVisitor> extends AbstractProcessor<CtClass<?>> {
	private Class<T> visitor;
	private final List<String> excludingClasses = Collections.singletonList("CompilationUnitVirtualImpl");
	private boolean hasScanners;
	private boolean hasVisitors;

	public CheckVisitorProcessor(Class<T> visitor) {
		this.visitor = visitor;
	}

	@Override
	public boolean isToBeProcessed(CtClass<?> candidate) {
		return super.isToBeProcessed(candidate) //
				&& !excludingClasses.contains(candidate.getSimpleName()) //
				&& candidate.getSimpleName().endsWith("Impl") //
				&& candidate.getPackage().getQualifiedName().startsWith("spoon.support.reflect") //
				&& candidate.isTopLevel();
	}

	@Override
	public void process(CtClass<?> element) {
		final CtType<CtVisitor> visitor = getFactory().Type().get(this.visitor);
		final String qualifiedName = element.getQualifiedName().replace(".support.", ".");
		final String interfaceName = qualifiedName.substring(0, qualifiedName.lastIndexOf("Impl"));
		final CtType<Object> theInterface = getFactory().Type().get(interfaceName);

		if (hasScanners) {
			checkPresenceScanMethods(visitor, theInterface, element.getModifiers().contains(ModifierKind.ABSTRACT));
		}
		if (hasVisitors) {
			checkPresenceVisitMethods(visitor, theInterface, element.getModifiers().contains(ModifierKind.ABSTRACT));
		}
	}

	public CheckVisitorProcessor withScanners() {
		hasScanners = true;
		return this;
	}

	public CheckVisitorProcessor withVisitors() {
		hasVisitors = true;
		return this;
	}

	private void checkPresenceScanMethods(CtType<CtVisitor> visitorType, CtType<Object> element, boolean isAbstract) {
		int nbScanner = isAbstract ? 1 : 0;
		final List<CtMethod<?>> scanners = visitorType.getMethodsByName("scan" + element.getSimpleName());
		if (scanners.size() != nbScanner) {
			if (!(scanners.size() > 0 && scanners.get(0).getAnnotation(Deprecated.class) != null)) {
				throw new AssertionError("You should have " + nbScanner + " scanner methods for the element " + element.getSimpleName() + " in the CtInheritanceScanner.");
			}
		}
	}

	private void checkPresenceVisitMethods(CtType<CtVisitor> visitorType, CtType<Object> element, boolean isAbstract) {
		int nbVisit = isAbstract ? 0 : 1;
		final List<CtMethod<?>> visits = visitorType.getMethodsByName("visit" + element.getSimpleName());
		if (visits.size() != nbVisit) {
			if (!(visits.size() > 0 && visits.get(0).getAnnotation(Deprecated.class) != null)) {
				throw new AssertionError("You should have " + nbVisit + " visit methods for the element " + element.getSimpleName() + " in the CtInheritanceScanner.");
			}
		}
	}
}
