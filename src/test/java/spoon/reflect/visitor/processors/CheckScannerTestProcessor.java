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
package spoon.reflect.visitor.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;

import java.util.Arrays;
import java.util.List;

public class CheckScannerTestProcessor extends AbstractProcessor<CtClass<?>> {
	private final List<String> excludingClasses = Arrays.asList("CompilationUnitVirtualImpl", "CtTypeMemberWildcardImportReferenceImpl", "InvisibleArrayConstructorImpl");

	@Override
	public boolean isToBeProcessed(CtClass<?> candidate) {
		return super.isToBeProcessed(candidate) //
				&& !excludingClasses.contains(candidate.getSimpleName()) //
				&& !candidate.hasModifier(ModifierKind.ABSTRACT)
				&& candidate.getSimpleName().endsWith("Impl") //
				&& candidate.getPackage().getQualifiedName().startsWith("spoon.support.reflect") //
				&& candidate.isTopLevel();
	}

	@Override
	public void process(CtClass<?> element) {
		final CtType<CtVisitor> scanner = getFactory().Type().get(CtScanner.class);
		final String qualifiedName = element.getQualifiedName().replace(".support.", ".");
		final String interfaceName = qualifiedName.substring(0, qualifiedName.lastIndexOf("Impl"));
		final CtType<Object> theInterface = getFactory().Type().get(interfaceName);

		final List<CtMethod<?>> visits = scanner.getMethodsByName("visit" + theInterface.getSimpleName());
		if (visits.size() != 1) {
			throw new AssertionError("You must have only one visitor methods in CtScanner for visit" + theInterface.getSimpleName());
		}

		final CtMethod<?> visit = visits.get(0);
		if (visit.getBody().getStatements().size() < 2) {
			throw new AssertionError("You must have minimum 2 statements in the visit method to call enter and exit in visit" + theInterface.getSimpleName());
		}

		checkInvocation("enter", visit.getBody().getStatement(0));
		checkInvocation("exit", visit.getBody().getLastStatement());
	}

	private void checkInvocation(String expected, CtStatement statement) {
		if (!(statement instanceof CtInvocation)) {
			throw new AssertionError("The statement must be a call to " + expected + " method.");
		}

		if (!expected.equals(((CtInvocation) statement).getExecutable().getSimpleName())) {
			throw new AssertionError("The statement must be a call to " + expected + " method.");
		}
	}
}
