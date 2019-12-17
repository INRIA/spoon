/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.SniperJavaPrettyPrinter;

public class CtDeprecatedRefactoring {

	public CtDeprecatedRefactoring() {
		super();
	}

	public void removeDeprecatedMethods(String path) {
		Launcher spoon = new Launcher();
		spoon.addInputResource(path);
		spoon.setSourceOutputDirectory(path);
		CalledMethodProcessor processor = new CalledMethodProcessor();
		spoon.addProcessor(processor);
		spoon.addProcessor(new AbstractProcessor<CtMethod<?>>() {
			@Override
			public void process(CtMethod<?> method) {
				if (method.hasAnnotation(Deprecated.class) && !processor.matches(method)) {
					method.delete();
				}
			}
		});

		// does not work, see https://github.com/INRIA/spoon/issues/3183
		// spoon.addProcessor(new AbstractProcessor<CtType>() {
		// @Override
		// public void process(CtType type) {
		// if (type.hasAnnotation(Deprecated.class)) {
		// type.delete();
		// }
		// }
		// });

		spoon.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(spoon.getEnvironment());
		});
		spoon.run();
	}

	private class CalledMethodProcessor extends AbstractProcessor<CtExecutable<?>> implements Filter<CtExecutable<?>> {
		private Collection<CtExecutable<?>> calledMethods = new HashSet<>();

		@Override
		public void process(CtExecutable<?> method) {
			if (!method.hasAnnotation(Deprecated.class)) {
				List<CtInvocation<?>> var = method.getElements(new TypeFilter<>(CtInvocation.class));
				var.stream().filter(Objects::nonNull)
						.forEach(v -> calledMethods.add(v.getExecutable().getExecutableDeclaration()));
			}
		}

		@Override
		public boolean matches(CtExecutable<?> element) {
			return calledMethods.contains(element);
		}
	}
}
