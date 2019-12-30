/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtLambdaImpl;
import spoon.support.sniper.SniperJavaPrettyPrinter;

public class CtDeprecatedRefactoring {

	public void removeDeprecatedMethods(String inputPath, String resultPath) {
		Launcher spoon = new Launcher();
		spoon.addInputResource(inputPath);
		spoon.setSourceOutputDirectory(resultPath);
		doRefactor(spoon);
	}

	public void removeDeprecatedMethods(String path) {
		Launcher spoon = new Launcher();
		spoon.addInputResource(path);
		spoon.setSourceOutputDirectory(path);
		doRefactor(spoon);
	}

	private void doRefactor(Launcher spoon) {
		MethodInvocationSearch processor = new MethodInvocationSearch();
		spoon.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(spoon.getEnvironment());
		});

		spoon.addProcessor(processor);
		CtModel model = spoon.buildModel();
		spoon.process();

		Collection<CtExecutable<?>> invocationsFromFields = getInvocationsFromFields(model);
		model.getElements(new TypeFilter<>(CtExecutable.class)).forEach(method -> processor.process(method));
		removeUncalledMethods(processor.invocationsOfMethod);

		model.getElements(new TypeFilter<>(CtExecutable.class)).forEach(method -> {
			if (method.hasAnnotation(Deprecated.class) && !checkInvocationState(processor, invocationsFromFields, method)) {
				method.delete();
			}
		});
		spoon.prettyprint();
	}

	private boolean checkInvocationState(MethodInvocationSearch processor,
			Collection<CtExecutable<?>> invocationsFromFields, CtExecutable<?> method) {
		return processor.matches(method) || invocationsFromFields.contains(method);
	}

	private Set<CtExecutable<?>> getInvocationsFromFields(CtModel model) {
		Set<CtExecutable<?>> result = new HashSet<>();
		// search all method invocations from fields
		model.getElements(new TypeFilter<>(CtField.class)).stream()
				.map(field -> field.getElements(new TypeFilter<>(CtInvocation.class))).flatMap(List::stream)
				.map(call -> call.getExecutable().getExecutableDeclaration()).forEach(result::add);

		// search all constructor invocations from fields
		model.getElements(new TypeFilter<>(CtField.class)).stream()
				.map(field -> field.getElements(new TypeFilter<>(CtConstructorCall.class))).flatMap(List::stream)
				.map(call -> call.getExecutable().getExecutableDeclaration()).forEach(result::add);
		return result;
	}

	public void removeUncalledMethods(Map<CtExecutable<?>, Collection<CtExecutable<?>>> invocationsOfMethod) {
		boolean changed = false;
		// Typefilter returns the method itself so we need to remove it.
		invocationsOfMethod.entrySet().iterator().forEachRemaining(v -> v.getValue().remove(v.getKey()));
		do {

			changed = false;
			Iterator<Entry<CtExecutable<?>, Collection<CtExecutable<?>>>> mapIterator = invocationsOfMethod.entrySet()
					.iterator();
			while (mapIterator.hasNext()) {
				Entry<CtExecutable<?>, Collection<CtExecutable<?>>> entry = mapIterator.next();
				if (!entry.getKey().hasAnnotation(Deprecated.class)) {
					// only look at deprecated Methods
					continue;
				}
				if (entry.getValue().isEmpty()) {
					// removes never called and deprecated Methods
					changed = true;
					// remove the method for further lookups in value collections
					invocationsOfMethod.values().forEach(v -> v.remove(entry.getKey()));
					// remove the method as key
					mapIterator.remove();
				}
				if (entry.getValue().size() == 1 && entry.getValue().contains(entry.getKey())) {
					// removes deprecated methods, that are only called by itself
					changed = true;
					invocationsOfMethod.values().forEach(invocation -> invocation.remove(entry.getKey()));
					mapIterator.remove();
				}
			}
		} while (changed);
	}

	private class MethodInvocationSearch extends AbstractProcessor<CtExecutable<?>> implements Filter<CtExecutable<?>> {
		private Map<CtExecutable<?>, Collection<CtExecutable<?>>> invocationsOfMethod = new HashMap<>();

		@Override
		public void process(CtExecutable<?> method) {
			if (!method.getPosition().isValidPosition()) {
				return;
			}
			final CtExecutable<?> transformedMethod;
			if (method instanceof CtLambda) {
				// because lambdas are difficult we transform them
				// in a method public void foo(){ List.of("a").stream().forEach($method)}
				// we need the ref to foo() and not the ref to the lambda expression
				transformedMethod = method.getParent(CtExecutable.class);
			} else {
				transformedMethod = method;
			}
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
			List<CtConstructorCall<?>> constructors = method.getElements(new TypeFilter<>(CtConstructorCall.class));
			if (!invocationsOfMethod.containsKey(method) && !method.isImplicit() && !(method instanceof CtLambdaImpl)) {
				// now every method should be key
				invocationsOfMethod.put(method, Collections.emptyList());
			}
			invocations.stream().filter(v -> !v.isImplicit()).map(v -> v.getExecutable().getExecutableDeclaration())
					.filter(Objects::nonNull).filter(v -> v.getPosition().isValidPosition())
					.forEach(v -> invocationsOfMethod.merge(v, new ArrayList<>(Arrays.asList(transformedMethod)),
							(o1, o2) -> Stream.concat(o1.stream(), o2.stream()).collect(Collectors.toCollection(HashSet::new))));
			constructors.stream().filter(v -> !v.isImplicit()).map(v -> v.getExecutable().getExecutableDeclaration())
					.filter(Objects::nonNull)
					.forEach(v -> invocationsOfMethod.merge(v, new ArrayList<>(Arrays.asList(transformedMethod)),
							(o1, o2) -> Stream.concat(o1.stream(), o2.stream()).collect(Collectors.toCollection(HashSet::new))));
		}

		@Override
		public boolean matches(CtExecutable<?> element) {
			return invocationsOfMethod.keySet().contains(element);
		}
	}

}
