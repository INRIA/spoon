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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.util.RtHelper;

/**
 * This class represents symbolic values that can be used by
 * {@link spoon.reflect.eval.SymbolicEvaluator}.
 */
public class SymbolicInstance<T> {

	/**
	 * The true literal symbolic instance.
	 */
	public final static SymbolicInstance<Boolean> TRUE = new SymbolicInstance<Boolean>(
			"true");
	/**
	 * The false literal symbolic instance.
	 */
	public final static SymbolicInstance<Boolean> FALSE = new SymbolicInstance<Boolean>(
			"false");
	/**
	 * The null literal symbolic instance.
	 */
	public final static SymbolicInstance<?> NULL = new SymbolicInstance<Object>(
			"null");
	/**
	 * The 0 literal symbolic instance.
	 */
	public final static SymbolicInstance<?> ZERO = new SymbolicInstance<Object>(
			"0");
	/**
	 * The strictly positive domain symbolic instance.
	 */
	public final static SymbolicInstance<?> POS_DOMAIN = new SymbolicInstance<Object>(
			">0");
	/**
	 * The positive domain symbolic instance.
	 */
	public final static SymbolicInstance<?> ZEROPOS_DOMAIN = new SymbolicInstance<Object>(
			">=0");
	/**
	 * The strictly negative domain symbolic instance.
	 */
	public final static SymbolicInstance<?> NEG_DOMAIN = new SymbolicInstance<Object>(
			"<0");
	/**
	 * The positive domain symbolic instance.
	 */
	public final static SymbolicInstance<?> ZERONEG_DOMAIN = new SymbolicInstance<Object>(
			"<=0");

	/**
	 * Creates a literal symbolic instance.
	 */
	public SymbolicInstance(String literal) {
		this.literal = literal;
	}

	String literal = null;

	static long id = 0;

	/**
	 * Gets the next id to be attributed to the created instance.
	 */
	private static long getNextId() {
		return id++;
	}

	/**
	 * Resets the id counter.
	 */
	public static void resetIds() {
		id = 0;
	}

	private String symbolName = null;

	/**
	 * Helper method to get the symbol's unique Id from its type and its name.
	 *
	 * @param concreteType
	 *            the type
	 * @param name
	 *            the name (can be null)
	 * @return a unique Id or the type id if name is null
	 */
	public static String getSymbolId(CtTypeReference<?> concreteType,
			String name) {
		CtTypeReference<?> t = concreteType;
		if (name != null) {
			return t.getQualifiedName() + "$" + name;
		}
		return t.getQualifiedName();
	}

	/**
	 * Gets the unique Id of this abstract instance.
	 */
	public String getId() {
		if (literal != null) {
			return literal;
		}
		return getSymbolId(concreteType, symbolName);
	}

	/**
	 * Tests the equality.
	 */
	@Override
	public boolean equals(Object obj) {
		SymbolicInstance<?> i = (SymbolicInstance<?>) obj;

		boolean b = false;
		if ((concreteType != null) && (i != null)) {
			b = concreteType.equals(i.concreteType) && fields.equals(i.fields)
					&& (isExternal == i.isExternal);
		} else if ((concreteType == null) && (i != null)) {
			b = (i.concreteType == null)
					&& ((literal == null) ? false : literal.equals(i.literal));
		}
		return b;
	}

	/**
	 * Tests the equality by reference.
	 */
	public boolean equalsRef(Object obj) {
		if (this == obj) {
			return true;
		}
		SymbolicInstance<?> i = (SymbolicInstance<?>) obj;
		boolean b = getId().equals(i.getId());
		return b;
	}

	/**
	 * Creates a new abstract instance (logical value).
	 *
	 * @param evaluator
	 *            the evaluator
	 * @param concreteType
	 *            the type of the instance
	 * @param isType
	 *            tells if it is a type instance or a regular instance
	 */
	public SymbolicInstance(SymbolicEvaluator evaluator,
			CtTypeReference<T> concreteType, boolean isType) {
		this.concreteType = concreteType;
		CtSimpleType<T> type = concreteType.getDeclaration();
		// TODO: check that enums are working properly
		if (!concreteType.isPrimitive() && (type != null)
				&& !Enum.class.isAssignableFrom(concreteType.getActualClass())) {
			for (CtFieldReference<?> fr : concreteType.getAllFields()) {
				CtField<?> f = fr.getDeclaration();
				// skip external fields
				if (f == null) {
					continue;
				}

				CtTypeReference<?> fieldType = f.getType();

				// TODO: check this
				if (fieldType == null) {
					fields.put(fr, null);
					continue;
				}

				if (isType && f.hasModifier(ModifierKind.STATIC)) {
					SymbolicInstance<?> r = evaluator.evaluate(f
							.getDefaultExpression());
					fields.put(fr, r == null ? null : r.getId());
				}
				if (!isType && !f.hasModifier(ModifierKind.STATIC)) {
					SymbolicInstance<?> r = evaluator.evaluate(f
							.getDefaultExpression());
					fields.put(fr, r == null ? null : r.getId());
				}
			}
		} else {
			isExternal = true;
			if (!isType) {
				for (CtTypeReference<?> t : evaluator.getStatefullExternals()) {
					if (t.isAssignableFrom(concreteType)) {
						for (Method m : RtHelper.getAllMethods(concreteType
								.getActualClass())) {
							if (m.getName().startsWith("get")
									&& (m.getParameterTypes().length == 0)) {
								CtFieldReference<?> f = concreteType
										.getFactory()
										.Field()
										.createReference(
												concreteType,
												concreteType
														.getFactory()
														.Type()
														.createReference(
																m
																		.getReturnType()),
												m.getName().substring(3));
								fields.put(f, null);
							}
						}
					}
				}
			}
		}
		// evaluator.getHeap().get(
		// evaluator,
		// concreteType.getFactory().Type()
		// .createReference(
		// m.getReturnType()));

		if (isType) {
			this.symbolName = "type";
		} else {
			this.symbolName = "" + getNextId();
		}
	}

	/**
	 * Tells if this logical value is stateful or not.
	 */
	public boolean isStateful() {
		return !fields.isEmpty();
	}

	private boolean isExternal = false;

	private CtTypeReference<T> concreteType;

	private Map<CtVariableReference<?>, String> fields = new TreeMap<CtVariableReference<?>, String>();

	// private Map<String, AbstractInstance> properties;

	/**
	 * Gets the type of the abstract instance.
	 */
	public CtTypeReference<T> getConcreteType() {
		return concreteType;
	}

	/**
	 * Gets the value of a field belonging to this instance, as an abstract
	 * instance id.
	 *
	 * @return null if non-existing field
	 */
	public String getFieldValue(CtVariableReference<?> fref) {
		return fields.get(fref);
	}

	/**
	 * Gets the value of a field belonging to this instance and identified by
	 * its name, as an abstract instance id.
	 *
	 * @return null if non-existing field
	 */
	public String getFieldValue(String fname) {
		for (CtVariableReference<?> v : fields.keySet()) {
			if (v.getSimpleName().equals(fname)) {
				return fields.get(v);
			}
		}
		return null;
	}

	// /**
	// * Sets the value of an assumed property belonging to this instance.
	// */
	// public void setPropertyValue(String propertyName, AbstractInstance value)
	// {
	// if (value == null)
	// return;
	// if (properties == null)
	// properties = new HashMap<String, AbstractInstance>();
	// properties.put(propertyName, value);
	// }
	//
	// /**
	// * Gets the value of an assumed property belonging to this instance.
	// */
	// public AbstractInstance getPropertyValue(String propertyName) {
	// if (properties == null)
	// return null;
	// return properties.get(propertyName);
	// }

	/**
	 * Sets the value of a field belonging to this instance, and stores the
	 * instance on the heap.
	 */
	public void setFieldValue(SymbolicHeap heap, CtVariableReference<?> fref,
			SymbolicInstance<?> value) {
		if (fields.containsKey(fref) || isExternal()) {
			fields.put(fref, value.getId());
			heap.store(value);
		} else {
			// TODO: JJ - recheck this
			throw new RuntimeException("unknown field '" + fref
					+ "' for target " + this);
		}
	}

	/**
	 * Tells if this instance is a wrapper for an instance external from the
	 * evaluator (regular Java object).
	 */
	public boolean isExternal() {
		return isExternal;
	}

	/**
	 * A string representation.
	 */
	@Override
	public String toString() {
		if (literal != null) {
			return "#" + literal + "#";
		}
		return "#" + getId() + fields + "#";
	}

	/**
	 * Gets a copy of this instance (if the instance is stateless, returns
	 * this).
	 */
	public SymbolicInstance<T> getClone() {
		if (!isStateful()) {
			return this;
		}
		return new SymbolicInstance<T>(this);
	}

	/**
	 * Creates a copy of the given instance.
	 */
	public SymbolicInstance(SymbolicInstance<T> i) {
		concreteType = i.concreteType;
		isExternal = i.isExternal;
		symbolName = i.symbolName;
		fields.putAll(i.fields);
	}

	/**
	 * Gets the name of this symbolic instance.
	 */
	public String getSymbolName() {
		return symbolName;
	}

	/**
	 * Gets the fields for this instance.
	 */
	public Map<CtVariableReference<?>, String> getFields() {
		return fields;
	}
}
