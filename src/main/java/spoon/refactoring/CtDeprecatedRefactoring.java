/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtExecutable;
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
		spoon.addProcessor(new AbstractProcessor<CtExecutable<?>>() {
			@Override
			public void process(CtExecutable<?> method) {

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
		private Map<CtExecutable<?>, Collection<CtExecutable<?>>> invocationsOfMethod = new HashMap<>();

		@Override
		public void process(CtExecutable<?> method) {
			if (!method.hasAnnotation(Deprecated.class)) {
				List<CtInvocation<?>> var = method.getElements(new TypeFilter<>(CtInvocation.class));
				var.stream().filter(Objects::nonNull)
						.forEach(v -> invocationsOfMethod.merge(v.getExecutable().getExecutableDeclaration(), Arrays.asList(method),
								(o1, o2) -> Stream.concat(o1.stream(), o2.stream()).collect(Collectors.toSet())));
			}
		}

		@Override
		public boolean matches(CtExecutable<?> element) {
			return invocationsOfMethod.keySet().contains(element);
			// return calledMethods.contains(element);
		}

		@Override
		public void processingDone() {
			boolean changed = false;
			do {
				Iterator<Entry<CtExecutable<?>, Collection<CtExecutable<?>>>> mapIterator = invocationsOfMethod.entrySet()
						.iterator();
				while (mapIterator.hasNext()) {
					Entry<CtExecutable<?>, Collection<CtExecutable<?>>> entry = mapIterator.next();
					if (entry.getValue().isEmpty() && entry.getKey().hasAnnotation(Deprecated.class)) {
						changed = true;
						invocationsOfMethod.values().forEach(v -> v.remove(entry.getKey()));
						mapIterator.remove();
					}
				}
			} while (changed);
			super.processingDone();
		}
	}
}
