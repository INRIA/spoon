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
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.Collections;
import java.util.List;

public class CheckModelProcessor extends AbstractProcessor<CtClass<?>> {
	private final FailureStrategy failure;
	private final List<String> excludingClasses = Collections.singletonList("CompilationUnitVirtualImpl");

	public CheckModelProcessor(FailureStrategy failure) {
		this.failure = failure;
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
		final CtType<CtInheritanceScanner> scanner = getFactory().Type().get(CtInheritanceScanner.class);
		final String qualifiedName = element.getQualifiedName().replace(".support.", ".");
		final String interfaceName = qualifiedName.substring(0, qualifiedName.lastIndexOf("Impl"));
		final CtType<Object> theInterface = getFactory().Type().get(interfaceName);

		checkMethod(scanner, theInterface, element.getModifiers().contains(ModifierKind.ABSTRACT));
	}

	private void checkMethod(CtType<CtInheritanceScanner> scanner, CtType<Object> theInterface, boolean isAbstract) {
		int nbScanner = isAbstract ? 1 : 0, nbVisit = isAbstract ? 0 : 1;
		final List<CtMethod<?>> scanners = scanner.getMethodsByName("scan" + theInterface.getSimpleName());
		if (scanners.size() != nbScanner) {
			if (!(scanners.size() > 0 && scanners.get(0).getAnnotation(Deprecated.class) != null)) {
				failure.fail("You should have " + nbScanner + " scanner and " + nbVisit + " visit methods for the element " + theInterface.getSimpleName() + " in the CtInheritanceScanner.");
			}
		}
		final List<CtMethod<?>> visits = scanner.getMethodsByName("visit" + theInterface.getSimpleName());
		if (visits.size() != nbVisit) {
			if (!(visits.size() > 0 && visits.get(0).getAnnotation(Deprecated.class) != null)) {
				failure.fail("You should have " + nbScanner + " scanner and " + nbVisit + " visit methods for the element " + theInterface.getSimpleName() + " in the CtInheritanceScanner.");
			}
		}
	}

	public interface FailureStrategy {
		void fail(String message);
	}
}
