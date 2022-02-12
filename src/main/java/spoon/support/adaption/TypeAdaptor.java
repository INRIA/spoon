/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.MethodTypingContext;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Determines subtyping relationships and adapts generics from a super- to a subclass.
 */
public class TypeAdaptor {

	private final CtType<?> hierarchyStart;
	private final CtTypeReference<?> hierarchyStartReference;
	private CtMethod<?> startMethod;
	private final boolean initializedWithReference;

	/**
	 * Creates a new type adaptor using the given type as the start of its hierarchy.
	 *
	 * @param hierarchyStart the start of the hierarchy
	 */
	public TypeAdaptor(CtType<?> hierarchyStart) {
		this.hierarchyStart = hierarchyStart;
		this.hierarchyStartReference = hierarchyStart.getReference();
		this.initializedWithReference = false;
	}

	/**
	 * Creates a new type adaptor using the given reference as the start of its hierarchy.
	 *
	 * @param hierarchyStart the start of the hierarchy
	 */
	public TypeAdaptor(CtTypeReference<?> hierarchyStart) {
		CtTypeReference<?> usedHierarchyStart = hierarchyStart;
		if (hierarchyStart instanceof CtArrayTypeReference) {
			usedHierarchyStart = ((CtArrayTypeReference<?>) hierarchyStart).getArrayType();
		}
		this.hierarchyStartReference = usedHierarchyStart;
		this.hierarchyStart = hierarchyStartReference.getTypeDeclaration();
		this.initializedWithReference = true;
	}

	/**
	 * Creates a new type adaptor using the given method as the start of its hierarchy.
	 *
	 * @param hierarchyStart the start of the hierarchy
	 */
	public TypeAdaptor(CtMethod<?> hierarchyStart) {
		this(hierarchyStart.getDeclaringType());
		this.startMethod = hierarchyStart;
	}

	/**
	 * Checks if the context of this type adapter is a subtype of the passed superRef.
	 *
	 * @param superRef the super reference to check against
	 * @return true if the context of this type adapter is a subtype of the passed superRef
	 * @implNote This implementation behaves the same as {@code isSubtype(hierarchyStart,
	 * 	superRef)}
	 * @see #isSubtype(CtType, CtTypeReference)
	 */
	@SuppressWarnings("removal")
	public boolean isSubtypeOf(CtTypeReference<?> superRef) {
		if (superRef.getFactory().getEnvironment().useOldAndSoonDeprecatedClassContextTypeAdaption()) {
			return getOldClassTypingContext().isSubtypeOf(superRef);
		}
		// We have no declaration so we can't really do any subtype queries
		if (hierarchyStart == null) {
			return false;
		}

		boolean subtype = isSubtype(hierarchyStart, superRef);
		if (!subtype) {
			return false;
		}
		if (hierarchyStartReference.getActualTypeArguments().isEmpty() && superRef.getActualTypeArguments().isEmpty()) {
			return true;
		}
		return new ClassTypingContext(hierarchyStartReference).isSubtypeOf(superRef);
	}

	/**
	 * @return the context of this type adaptor
	 */
	public CtType<?> getHierarchyStart() {
		return hierarchyStart;
	}

	/**
	 * Checks whether the base is a subtype of the passed superref. Generic parameters in superRef are
	 * ignored.
	 *
	 * @param base the base type
	 * @param superRef the potential supertype
	 * @return true if base extends/implements the super type
	 */
	@SuppressWarnings("removal")
	public static boolean isSubtype(CtType<?> base, CtTypeReference<?> superRef) {
		if (base.getFactory().getEnvironment().useOldAndSoonDeprecatedClassContextTypeAdaption()) {
			return new TypeAdaptor(base).isSubtypeOf(superRef);
		}
		String superRefFqn = superRef.getTypeErasure().getQualifiedName();

		// Everything inherits from object
		if (superRef.getQualifiedName().equals("java.lang.Object")) {
			return true;
		}

		// Types are subtypes of themselves
		if (base.getQualifiedName().equals(superRefFqn)) {
			return true;
		}

		// Walk up the supertype hierarchy and see if we find the super ref type
		Queue<CtTypeReference<?>> workQueue = new ArrayDeque<>();
		workQueue.add(base.getReference());

		while (!workQueue.isEmpty()) {
			CtTypeReference<?> next = workQueue.poll();
			if (next.getQualifiedName().equals(superRefFqn)) {
				return true;
			}

			if (next.getSuperclass() != null) {
				workQueue.add(next.getSuperclass());
			}
			workQueue.addAll(next.getSuperInterfaces());
		}

		return false;
	}

	/**
	 * Adapts a given method to the context of this type adapter. The parent of the method will be set
	 * to the context of this adapter.
	 * <p>
	 * As an example: The method {@code method} in
	 * <pre>{@code
	 *  interface Parent<T, X> {
	 *    <R> R method(T t, X x);
	 *  }
	 * }</pre>
	 * adapted to
	 * <pre>{@code interface Child<Q> extends Parent<Q, String> {}}</pre>
	 * would return
	 * <pre>{@code <R> R method(Q t, String x);}</pre>.
	 *
	 * @param inputMethod the method to adapt
	 * @return the input method but with the return type, parameter types and thrown types adapted to
	 * 	the context of this type adapter
	 */
	public CtMethod<?> adaptMethod(CtMethod<?> inputMethod) {
		return (CtMethod<?>) new MethodTypingContext()
			.setClassTypingContext(getOldClassTypingContext())
			.setMethod(inputMethod)
			.getAdaptationScope();
	}

	private ClassTypingContext getOldClassTypingContext() {
		if (initializedWithReference) {
			return new ClassTypingContext(hierarchyStartReference);
		}
		return new ClassTypingContext(hierarchyStart);
	}

	/**
	 * Checks if two given methods are conflicting, i.e. they can not both be declared in the same
	 * class. This happens if the erasure of the methods is the same or one overrides the other. This
	 * method is used to remove methods that were already visited or were overwritten/shadowed by a
	 * subclass in various places.
	 *
	 * @param first the first method
	 * @param second the second method
	 * @return true if the methods are conflicting
	 */
	@SuppressWarnings("removal")
	public boolean isConflicting(CtMethod<?> first, CtMethod<?> second) {
		if (first.getFactory().getEnvironment().useOldAndSoonDeprecatedClassContextTypeAdaption()) {
			return getOldClassTypingContext().isSameSignature(first, second);
		}
		if (first.getParameters().size() != second.getParameters().size()) {
			return false;
		}
		if (!first.getSimpleName().equals(second.getSimpleName())) {
			return false;
		}

		for (int i = 0; i < first.getParameters().size(); i++) {
			CtParameter<?> firstParameter = first.getParameters().get(i);
			CtParameter<?> secondParameter = second.getParameters().get(i);
			CtTypeReference<?> firstType = firstParameter.getType().getTypeErasure();
			CtTypeReference<?> secondType = secondParameter.getType().getTypeErasure();

			if (!firstType.equals(secondType)) {
				// Oh no, we need to do complicated type adaption checking to properly account for
				// formal method parameters changing the erasure
				// TODO: Check if we can short-circuit based on that knowledge
				return isOverriding(first, second) || isOverriding(second, first);
			}
		}

		return true;
	}

	/**
	 * Checks if two methods have the same signature, <em>once you adapt both to the context of this
	 * type adapter</em>.
	 * <blockquote>
	 * Two methods, M and N, have the same signature if they have the same  name,  the  same  type
	 * parameters  (if  any)  (§8.4.4),  and,  after  adapting  the formal parameter types of N to the
	 * type parameters of M, the same formal parameter types.
	 * </blockquote>
	 * <br>
	 * Adapting both to the context of this adapter is needed when dealing with inherited methods:
	 * <pre>{@code
	 *   class TypeA {
	 *     void foo(String bar);
	 *   }
	 *   interface IFoo<T> {
	 *     void foo(T bar);
	 *   }
	 *   class Foo extends TypeA implements IFoo<String> {}
	 * }</pre>
	 * <p>
	 * Here {@code TypeA#foo} and {@code IFoo#foo} have the same signature if checked with {@code Foo}
	 * as the context. In fact, {@code TypeA#foo} actually implements the method from {@code IFoo},
	 * even though it does not share any inheritance relation with it.
	 *
	 * @param first the first method
	 * @param second the second method
	 * @return true if the two methods have the same signature
	 */
	@SuppressWarnings("removal")
	public boolean isSameSignature(CtMethod<?> first, CtMethod<?> second) {
		if (first.getFactory().getEnvironment().useOldAndSoonDeprecatedClassContextTypeAdaption()) {
			return getOldClassTypingContext().isSubSignature(first, second);
		}
		if (first.getParameters().size() != second.getParameters().size()) {
			return false;
		}
		if (!first.getSimpleName().equals(second.getSimpleName())) {
			return false;
		}

		CtMethod<?> adaptedFirst = adaptMethod(first);
		CtMethod<?> adaptedSecond = adaptMethod(second);

		return isConflicting(adaptedFirst, adaptedSecond);
	}

	/**
	 * Checks if {@code subMethod} overrides {@code superMethod}. A method overrides another, iff
	 * <ul>
	 *   <li>They have the same name</li>
	 *   <li>They have the same amount of parameters</li>
	 *   <li>They are not static</li>
	 *   <li>
	 *     The declaring type of {@code subMethod} is a subtype of the declaring type of
	 *     {@code superMethod}
	 *   </li>
	 *   <li>
	 *     The erasure of the parameters is equal, after {@link #adaptMethod(CtMethod) adapting} the
	 *     {@code superMethod} to the declaring type of {@code subMethod}. One needs to adapt the
	 *     whole method here and can not just check the erasure of the adapted parameter types, as
	 *     they might depend on formal type parameters declared on the method:
	 *     <pre>{@code
	 *       class Foo<T> {
	 *         <F extends T> void foo(F t);
	 *       }
	 *       class Sub<R extends String> extends Foo<R> {
	 *         <Q extends R> void foo(Q t);
	 *       }
	 *     }</pre>
	 *     If we did not adapt the whole method, we would not have a corresponding formal parameter
	 *     declaration with the correct upper bound we can adapt to and would erase to Object instead.
	 *   </li>
	 * </ul>
	 *
	 * @param subMethod the method that might override the other
	 * @param superMethod the method that might be overridden
	 * @return true if {@code subMethod} overrides {@code superMethod}
	 */
	@SuppressWarnings("removal")
	public boolean isOverriding(CtMethod<?> subMethod, CtMethod<?> superMethod) {
		if (subMethod.getFactory().getEnvironment().useOldAndSoonDeprecatedClassContextTypeAdaption()) {
			return getOldClassTypingContext().isOverriding(subMethod, superMethod);
		}
		if (subMethod.getParameters().size() != superMethod.getParameters().size()) {
			return false;
		}
		if (!subMethod.getSimpleName().equals(superMethod.getSimpleName())) {
			return false;
		}

		if (subMethod.isStatic() || superMethod.isStatic()) {
			return false;
		}

		CtType<?> subDeclaringType = subMethod.getDeclaringType();
		CtType<?> superDeclaringType = superMethod.getDeclaringType();

		if (!isSubtype(subDeclaringType, superDeclaringType.getReference())) {
			return false;
		}

		CtMethod<?> adapted = new TypeAdaptor(subMethod.getDeclaringType())
			.adaptMethod(superMethod);

		for (int i = 0; i < subMethod.getParameters().size(); i++) {
			CtParameter<?> subParam = subMethod.getParameters().get(i);
			CtParameter<?> superParam = adapted.getParameters().get(i);

			if (!subParam.getType().getTypeErasure().equals(superParam.getType().getTypeErasure())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adapts a type from a supertype to the context of this adaptor. In essence, this method builds
	 * the inheritance hierarchy from its context to the super reference (with some smarts to figure
	 * out what to do if superRef is actually a type parameter) and then walks backwards along that
	 * chain until it finds a type variable in the adaptor's context or a terminating type.
	 * <p>
	 * For example:
	 * <pre>{@code
	 *   interface Top<T, S> {}
	 *   interface Middle<Q> extends Top<Q, String> {}
	 *   interface Bottom<R> extends Middle<R> {}
	 * }</pre>
	 * If you adapt {@code T} from {@code Top} to {@code Middle} you get {@code Q}. If you adapt
	 * {@code T} from {@code Top} to {@code Bottom} you get {@code R}.
	 * <br>If you adapt {@code S} from {@code Top} to {@code Middle}/{@code Bottom} you get {@code
	 * String}.
	 *
	 * <br>If the input reference is a formal type parameter declared on a method, adaption is only
	 * possible if this adaptor was created using {@link #TypeAdaptor(CtMethod)}. Otherwise, there is
	 * no reference method to adapt to and the input will be returned unchanged.
	 * <br>If that constructor was used, this method will return the corresponding type parameter
	 * declared on the context method of this adaptor.
	 *
	 * @param superRef the super type to adapt
	 * @return the adapted type
	 */
	public CtTypeReference<?> adaptType(CtTypeReference<?> superRef) {
		if (startMethod != null) {
			return new MethodTypingContext()
				.setClassTypingContext(getOldClassTypingContext())
				.setMethod(startMethod)
				.adaptType(superRef);
		}
		return getOldClassTypingContext().adaptType(superRef);
	}

	/**
	 * Adapts a type from a supertype to the context of this adaptor.
	 *
	 * @param superType the super type to adapt
	 * @return the adapted type
	 * @see #adaptType(CtTypeReference)
	 * @implNote this implementation just delegates to {@code adaptType(superType.getReference());}
	 */
	public CtTypeReference<?> adaptType(CtType<?> superType) {
		return adaptType(superType.getReference());
	}
}
