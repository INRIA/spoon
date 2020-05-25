/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.Collection;
import java.util.Iterator;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.SniperJavaPrettyPrinter;

public class CtDeprecatedRefactoring {

	protected void removeDeprecatedMethods(String inputPath, String resultPath) {
		Launcher spoon = new Launcher();
		spoon.addInputResource(inputPath);
		spoon.setSourceOutputDirectory(resultPath);
		doRefactor(spoon);
	}

	protected void removeDeprecatedMethods(String path) {
		removeDeprecatedMethods(path, path);
	}

	private void doRefactor(Launcher spoon) {
		MethodInvocationSearch processor = new MethodInvocationSearch();
		spoon.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(spoon.getEnvironment());
		});
		CtModel model = spoon.buildModel();
		model.getElements(new TypeFilter<>(CtField.class)).forEach(processor::scan);
		model.getElements(new TypeFilter<>(CtExecutable.class)).forEach(processor::scan);

		Collection<MethodCallState> invocationsOfMethod = processor.getInvocationsOfMethod();
		removeUncalledMethods(invocationsOfMethod);

		for (CtExecutable<?> method : model.getElements(new TypeFilter<>(CtExecutable.class))) {
			if (method.hasAnnotation(Deprecated.class)
					&& invocationsOfMethod.stream().noneMatch(v -> v.getMethod().equals(method))) {
				method.delete();
			}
		}
		spoon.prettyprint();
	}

	private void removeUncalledMethods(Collection<MethodCallState> invocationsOfMethod) {
		boolean changed = false;
		do {
			changed = false;
			Iterator<MethodCallState> iterator = invocationsOfMethod.iterator();
			while (iterator.hasNext()) {
				MethodCallState entry = iterator.next();
				if (!entry.getMethod().hasAnnotation(Deprecated.class)) {
					// only look at deprecated Methods
					continue;
				}
				if (entry.checkCallState()) {
					// removes never called and deprecated Methods
					changed = true;
					// remove the method for further lookups in value collections
					invocationsOfMethod.forEach(v -> v.remove(entry.getMethod()));
					// remove the method as key
					iterator.remove();
				}
				if (entry.getCallerMethods().size() == 1 && entry.getCallerMethods().contains(entry.getMethod())
						&& entry.getCallerFields().isEmpty()) {
					// removes deprecated methods, that are only called by itself
					changed = true;
					invocationsOfMethod.forEach(invocation -> invocation.remove(entry.getMethod()));
					iterator.remove();
				}
			}
		} while (changed);
	}
}
