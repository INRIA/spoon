/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.ArrayDeque;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
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
		MethodInvocationSearch processor = new MethodInvocationSearch();
		spoon.getEnvironment().setPrettyPrinterCreator(() -> {
			return new SniperJavaPrettyPrinter(spoon.getEnvironment());
		});
		spoon.addProcessor(processor);
		CtModel model = spoon.buildModel();
		spoon.process();
		model.getElements(new TypeFilter<>(CtExecutable.class)).forEach(method -> processor.process(method));
		removeUncalledMethods(processor.invocationsOfMethod);
		model.getElements(new TypeFilter<>(CtExecutable.class)).forEach(method -> {
			if (method.hasAnnotation(Deprecated.class) && !processor.matches(method)) {
				method.delete();
			}
		});
		spoon.prettyprint();
		// does not work, see https://github.com/INRIA/spoon/issues/3183
		// spoon.addProcessor(new AbstractProcessor<CtType>() {
		// @Override
		// public void process(CtType type) {
		// if (type.hasAnnotation(Deprecated.class)) {
		// type.delete();
		// }
		// }
		// });

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
					// removes deprecated methods, that are only called by themself
					changed = true;
					invocationsOfMethod.values().forEach(v -> v.remove(entry.getKey()));
					mapIterator.remove();
				}
			}
			// now search Loops
			LoopFinder finder = new LoopFinder();
			Collection<Collection<CtExecutable<?>>> result = finder.tarjanStrongConnected(invocationsOfMethod);
			changed |= result.stream().filter(v -> v.size() > 1)
					.map(col -> invocationsOfMethod.keySet().removeIf(col::contains)).anyMatch(v -> v == Boolean.TRUE);

		} while (changed);
	}

	private class MethodInvocationSearch extends AbstractProcessor<CtExecutable<?>> implements Filter<CtExecutable<?>> {
		private Map<CtExecutable<?>, Collection<CtExecutable<?>>> invocationsOfMethod = new HashMap<>();

		@Override
		public void process(CtExecutable<?> method) {
			List<CtInvocation<?>> var = method.getElements(new TypeFilter<>(CtInvocation.class));
			if (!invocationsOfMethod.containsKey(method) && !method.isImplicit()) {
				// now every method should be key
				invocationsOfMethod.put(method, Collections.emptyList());
			}
			var.stream().filter(v -> !v.isImplicit())
					.forEach(v -> invocationsOfMethod.merge(v.getExecutable().getExecutableDeclaration(),
							new ArrayList<>(Arrays.asList(method)),
							(o1, o2) -> Stream.concat(o1.stream(), o2.stream()).collect(Collectors.toCollection(HashSet::new))));
		}

		@Override
		public boolean matches(CtExecutable<?> element) {
			return invocationsOfMethod.keySet().contains(element);
			// return calledMethods.contains(element);
		}

	}

	private class LoopFinder {
		private ArrayDeque<Node> stack = new ArrayDeque<>();
		private ArrayDeque<Node> visited = new ArrayDeque<>();
		private Collection<Collection<CtExecutable<?>>> result = new HashSet<>();
		private Collection<Node> graph;
		private HashMap<CtExecutable<?>, Node> lookUp = new HashMap<>();

		private Collection<Collection<CtExecutable<?>>> tarjanStrongConnected(
				Map<CtExecutable<?>, Collection<CtExecutable<?>>> invocationsOfMethod) {

			graph = invocationsOfMethod.entrySet().stream().filter(v -> v.getKey().hasAnnotation(Deprecated.class))
					.map(v -> new Node(v.getKey(), v.getValue())).collect(Collectors.toSet());
			for (Node node : graph) {
				lookUp.put(node.getVertex(), node);
			}
			int index = 0;
			for (Node node : graph) {
				if (node.getIndex() == -1) {
					strongConnect(node, index);
				}
			}
			return result;
		}

		private void strongConnect(Node node, int index) {
			node.setIndex(index);
			node.setLowLink(index);
			index++;
			stack.push(node);
			visited.push(node);
			for (CtExecutable<?> edge : node.getEdges()) {
				Node neighbor = lookUp.get(edge);
				if (neighbor == null) {
					continue;
				}
				if (neighbor.index == -1) {
					strongConnect(neighbor, index);
					node.setLowLink(Math.min(node.getLowLink(), neighbor.getLowLink()));
				} else {
					if (stack.contains(neighbor)) {
						node.setLowLink(Math.min(node.getLowLink(), neighbor.getIndex()));
					}
				}
			}
			if (node.getIndex() == node.getLowLink()) {
				Collection<CtExecutable<?>> cycle = new HashSet<>();
				while (true) {
					Node newNode = stack.pop();
					cycle.add(newNode.getVertex());
					if (newNode.equals(node)) {
						break;
					}
				}
				if (!cycle.isEmpty()) {
					result.add(cycle);
				}
			}
		}
	}

	private class Node {
		private CtExecutable<?> vertex;
		private Collection<CtExecutable<?>> edges;
		private int index = -1;
		private int lowLink = -1;

		/**
		 * @param vertex
		 * @param edges
		 */
		Node(CtExecutable<?> vertex, Collection<CtExecutable<?>> edges) {
			this.vertex = vertex;
			this.edges = edges;
		}

		/**
		 * @return the edges
		 */
		public Collection<CtExecutable<?>> getEdges() {
			return edges;
		}

		/**
		 * @return the vertex
		 */
		public CtExecutable<?> getVertex() {
			return vertex;
		}

		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * @param index the index to set
		 */
		public void setIndex(int index) {
			this.index = index;
		}

		/**
		 * @return the lowLink
		 */
		public int getLowLink() {
			return lowLink;
		}

		/**
		 * @param lowLink the lowLink to set
		 */
		public void setLowLink(int lowLink) {
			this.lowLink = lowLink;
		}
	}
}
