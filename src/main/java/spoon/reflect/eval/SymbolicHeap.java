/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import spoon.reflect.reference.CtTypeReference;

/**
 * This class defines the heap for {@link spoon.reflect.eval.SymbolicEvaluator}.
 */
public class SymbolicHeap {

	/**
	 * Creates an empty heap.
	 */
	public SymbolicHeap() {
	}

	/**
	 * Copies the given heap.
	 */
	@SuppressWarnings("unchecked")
	public SymbolicHeap(SymbolicHeap heap) {
		statelessAbstractInstances.putAll(heap.statelessAbstractInstances);
		for (Entry<String, SymbolicInstance<?>> e : heap.statefullAbstractInstances
				.entrySet()) {
			statefullAbstractInstances.put(e.getKey(), new SymbolicInstance(e
					.getValue()));
		}
	}

	/**
	 * A string representation.
	 */
	@Override
	public String toString() {
		return "stateful=" + statefullAbstractInstances + " stateless="
				+ statelessAbstractInstances;
	}

	/**
	 * Dumps the heap on the screen.
	 */
	public void dump() {
		System.out.println("\tHeap:");
		System.out.println("\t - stateful: " + statefullAbstractInstances);
		System.out.println("\t - stateless: " + statelessAbstractInstances);
	}

	private Map<String, SymbolicInstance<?>> statefullAbstractInstances = new HashMap<String, SymbolicInstance<?>>();

	private Map<String, SymbolicInstance<?>> statelessAbstractInstances = new HashMap<String, SymbolicInstance<?>>();

	/**
	 * Gets/creates a symbolic value of a given type (automatically stored in
	 * the heap).
	 * 
	 * @param <T>
	 *            the actual type if known
	 * @param concreteType
	 *            the type reference
	 * @return the symbolic value for the type
	 */
	public <T> SymbolicInstance<T> getType(SymbolicEvaluator evaluator,
			CtTypeReference<T> concreteType) {
		SymbolicInstance<T> type = get(SymbolicInstance.getSymbolId(
				concreteType, "type"));
		if (type == null) {
			type = new SymbolicInstance<T>(evaluator, concreteType, true);
			store(type);
		}
		return type;
	}

	/**
	 * Stores the given symbolic instance in the heap.
	 */
	public void store(SymbolicInstance<?> instance) {
		if (instance.isStateful()) {
			statefullAbstractInstances.put(instance.getId(), instance);
		} else {
			statelessAbstractInstances.put(instance.getId(), instance);
		}
	}

	/**
	 * Gets an existing symbolic instance from its id.
	 */
	@SuppressWarnings("unchecked")
	public <T> SymbolicInstance<T> get(String id) {
		SymbolicInstance<T> i = (SymbolicInstance<T>) statelessAbstractInstances
				.get(id);
		if (i == null) {
			i = (SymbolicInstance<T>) statefullAbstractInstances.get(id);
		}
		return i;
	}

	/**
	 * Clears this heap (only the stateful instances).
	 */
	public void clear() {
		statefullAbstractInstances.clear();
	}

}
