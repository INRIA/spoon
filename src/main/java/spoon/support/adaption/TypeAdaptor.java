/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import spoon.SpoonException;
import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.MethodTypingContext;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Determines subtyping relationships and adapts generics from a super- to a subclass.
 */
public class TypeAdaptor {

	private final CtType<?> hierarchyStart;
	private final CtTypeReference<?> hierarchyStartReference;
	private final boolean initializedWithReference;
	private CtMethod<?> startMethod;
	private ClassTypingContext oldClassTypingContext;

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
	public boolean isSubtypeOf(CtTypeReference<?> superRef) {
		if (useLegacyTypeAdaption(superRef)) {
			return getOldClassTypingContext().isSubtypeOf(superRef);
		}
		if (hierarchyStart == null) {
			// We have no declaration, so we can't really do any subtype queries. This happens when the constructor was
			// called with a type reference to a class not on the classpath. Any subtype relationships of that class are
			// therefore ambiguous.
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
	 * Checks whether we should use the legacy or new type adaption API.
	 *
	 * @param element the element to obtain environment configuration from
	 * @return true if the legacy type adaption should be used instead
	 */
	@SuppressWarnings("removal")
	private static boolean useLegacyTypeAdaption(FactoryAccessor element) {
		return element.getFactory().getEnvironment().useLegacyTypeAdaption();
	}

	/**
	 * Checks whether the base is a subtype of the passed superref. Generic parameters in superRef are
	 * ignored.
	 *
	 * @param base the base type
	 * @param superRef the potential supertype
	 * @return true if base extends/implements the super type
	 */
	public static boolean isSubtype(CtType<?> base, CtTypeReference<?> superRef) {
		if (useLegacyTypeAdaption(base)) {
			return new TypeAdaptor(base).isSubtypeOf(superRef);
		}
		String superRefFqn = superRef.getTypeErasure().getQualifiedName();

		if (superRef.getQualifiedName().equals("java.lang.Object")) {
			return true;
		}

		if (base.getQualifiedName().equals(superRefFqn)) {
			return true;
		}

		return supertypeReachableInInheritanceTree(base, superRefFqn);
	}

	/**
	 * Checks whether a type with the passed qualified name is part of the supertype hierarchy of base.
	 *
	 * @param base the base to walk the inheritance tree for
	 * @param qualifiedSupertypeName the qualified name of the type to search for
	 * @return true if the type could be found in the supertype hierarchy of base, false otherwise
	 */
	private static boolean supertypeReachableInInheritanceTree(CtType<?> base, String qualifiedSupertypeName) {
		Queue<CtTypeReference<?>> workQueue = new ArrayDeque<>();
		workQueue.add(base.getReference());

		while (!workQueue.isEmpty()) {
			CtTypeReference<?> next = workQueue.poll();
			if (next.getQualifiedName().equals(qualifiedSupertypeName)) {
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
	@SuppressWarnings("unchecked")
	public CtMethod<?> adaptMethod(CtMethod<?> inputMethod) {
		if (useLegacyTypeAdaption(inputMethod)) {
			return legacyAdaptMethod(inputMethod);
		}
		CtMethod<?> clonedMethod = inputMethod.clone();

		for (int i = 0; i < clonedMethod.getFormalCtTypeParameters().size(); i++) {
			CtTypeParameter clonedParameter = clonedMethod.getFormalCtTypeParameters().get(i);
			CtTypeParameter realParameter = inputMethod.getFormalCtTypeParameters().get(i);

			if (realParameter.getSuperclass() != null) {
				clonedParameter.setSuperclass(adaptType(realParameter.getSuperclass()));
			}
			clonedParameter.setSuperInterfaces(
				realParameter.getSuperInterfaces()
					.stream()
					.map(this::adaptType)
					.collect(Collectors.toSet())
			);
		}

		// We do not know the return type of the input *or* the output (as it can change), so we can not
		// make any assumptions. Capture conversions correctly produces two different fresh type
		// variables and blocks this code. We do not have any assumption for the return type though and
		// return it as a wildcard so this is actually fine.
		@SuppressWarnings("rawtypes")
		CtTypeReference newReturnType = adaptType(inputMethod.getType());
		clonedMethod.setType(newReturnType);

		for (int i = 0; i < clonedMethod.getParameters().size(); i++) {
			// We need the rawtype as capture conversion would produce two different fresh type variables
			@SuppressWarnings("rawtypes")
			CtParameter newParameter = clonedMethod.getParameters().get(i);
			newParameter.setType(adaptType(inputMethod.getParameters().get(i).getType()));
		}

		Set<CtTypeReference<? extends Throwable>> newThrownTypes = clonedMethod.getThrownTypes()
			.stream()
			.map(this::adaptType)
			.map(it -> (CtTypeReference<? extends Throwable>) it)
			.collect(Collectors.toSet());
		clonedMethod.setThrownTypes(newThrownTypes);

		return clonedMethod.setParent(hierarchyStart);
	}

	private CtMethod<?> legacyAdaptMethod(CtMethod<?> inputMethod) {
		return (CtMethod<?>) new MethodTypingContext()
			.setClassTypingContext(getOldClassTypingContext())
			.setMethod(inputMethod)
			.getAdaptationScope();
	}

	private ClassTypingContext getOldClassTypingContext() {
		if (oldClassTypingContext == null) {
			if (initializedWithReference) {
				oldClassTypingContext = new ClassTypingContext(hierarchyStartReference);
			} else {
				oldClassTypingContext = new ClassTypingContext(hierarchyStart);
			}
		}
		return oldClassTypingContext;
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
	public boolean isConflicting(CtMethod<?> first, CtMethod<?> second) {
		if (useLegacyTypeAdaption(first)) {
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
	public boolean isSameSignature(CtMethod<?> first, CtMethod<?> second) {
		if (useLegacyTypeAdaption(first)) {
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
	public boolean isOverriding(CtMethod<?> subMethod, CtMethod<?> superMethod) {
		if (useLegacyTypeAdaption(subMethod)) {
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
		if (useLegacyTypeAdaption(superRef)) {
			return legacyAdaptType(superRef);
		}

		if (hierarchyStart.getQualifiedName().equals(superRef.getQualifiedName())) {
			// We are already in the same scope, just return super ref unchanged
			return superRef.clone()
				.setParent(superRef.isParentInitialized() ? superRef.getParent() : null);
		}

		Optional<CtTypeReference<?>> adaptedBetweenMethods = adaptBetweenMethods(superRef);
		if (adaptedBetweenMethods.isPresent()) {
			return adaptedBetweenMethods.get();
		}

		Node hierarchy = buildHierarchyFrom(hierarchyStartReference, hierarchyStart, superRef);

		if (hierarchy == null) {
			hierarchy = buildHierarchyFrom(
				hierarchyStartReference,
				findDeclaringType(hierarchyStartReference),
				superRef
			);
		}

		if (hierarchy == null) {
			return superRef.clone()
				.setParent(superRef.isParentInitialized() ? superRef.getParent() : null);
		}

		return AdaptionVisitor.adapt(superRef, hierarchy);
	}

	private CtTypeReference<?> legacyAdaptType(CtTypeReference<?> superRef) {
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
	 * @implNote this implementation just delegates to {@code adaptType(superType.getReference());}
	 * @see #adaptType(CtTypeReference)
	 */
	public CtTypeReference<?> adaptType(CtType<?> superType) {
		return adaptType(superType.getReference());
	}

	private Optional<CtTypeReference<?>> adaptBetweenMethods(CtTypeReference<?> superRef) {
		if (startMethod == null) {
			return Optional.empty();
		}
		Optional<CtMethod<?>> superMethodOpt = getDeclaringMethod(superRef);
		if (superMethodOpt.isEmpty()) {
			return Optional.empty();
		}
		CtMethod<?> superMethod = superMethodOpt.get();

		// We try to find the usage of the super ref in the method and take the corresponding value from our start
		// method. If a type parameter declared on a method is used in the return type of the method, we take the type
		// parameter representing the return type of the method in the subclass.
		if (superMethod.getType().equals(superRef)) {
			return Optional.of(startMethod.getType());
		}

		for (int i = 0; i < superMethod.getParameters().size(); i++) {
			CtParameter<?> parameter = superMethod.getParameters().get(i);
			if (parameter.getType().equals(superRef)) {
				return Optional.of(startMethod.getParameters().get(i).getType());
			}
		}

		throw new SpoonException("Did not find a type :(");
	}

	/**
	 * @param reference the reference to find out the declaring method for
	 * @return the method that declares the type parameter, or empty if the reference is not a {@link
	 *    CtTypeParameterReference} or it is not declared on a method
	 */
	private Optional<CtMethod<?>> getDeclaringMethod(CtTypeReference<?> reference) {
		if (!(reference instanceof CtTypeParameterReference)) {
			return Optional.empty();
		}
		CtType<?> typeParam = reference.getDeclaration();
		if (!typeParam.isParentInitialized()) {
			return Optional.empty();
		}
		CtElement parent = typeParam.getParent();
		if (!(parent instanceof CtMethod)) {
			return Optional.empty();
		}
		return Optional.of((CtMethod<?>) parent);
	}

	private Node buildHierarchyFrom(CtTypeReference<?> startReference, CtType<?> startType,
									CtTypeReference<?> end) {
		CtType<?> endType = findDeclaringType(end);
		Map<CtTypeReference<?>, Node> nodeMap = new HashMap<>();
		buildHierarchyFrom(startType.getReference(), endType, nodeMap);

		if (!startReference.getActualTypeArguments().isEmpty()) {
			nodeMap.get(startType.getReference())
				.addLower(Node.forReference(startReference));
		}

		return nodeMap.values().stream()
			.filter(it -> it.getInducedQualifiedName().equals(endType.getQualifiedName()))
			.filter(Node::isDeclarationNode)
			.findFirst()
			.orElse(null);
	}

	/**
	 * This method attempts to find a suitable end type for building our hierarchy.
	 * <br>
	 * it tries to find the type that declares the reference. It returns the CtType parent of the
	 * reference if possible, falling back to calling {@link CtTypeReference#getTypeDeclaration()} if
	 * the parent lookup fails.
	 * <br>
	 * If the reference refers to a type parameter it tries to return the type that declares the type
	 * parameter.
	 *
	 * @param reference the reference to find the declaring type for
	 * @return the declaring type
	 */
	private CtType<?> findDeclaringType(CtTypeReference<?> reference) {
		CtType<?> type = null;
		if (reference.isParentInitialized()) {
			type = reference.getParent(CtType.class);
		}
		if (type == null) {
			type = reference.getTypeDeclaration();
		}
		if (type instanceof CtTypeParameter) {
			CtFormalTypeDeclarer declarer = ((CtTypeParameter) type).getTypeParameterDeclarer();
			if (declarer instanceof CtType) {
				return (CtType<?>) declarer;
			}
			return declarer.getDeclaringType();
		}
		return type;
	}

	private Node buildHierarchyFrom(
		CtTypeReference<?> start,
		CtType<?> end,
		Map<CtTypeReference<?>, Node> nodeMap
	) {
		Node node = nodeMap.computeIfAbsent(start, adaptor -> Node.forReference(adaptor));

		if (!start.getActualTypeArguments().isEmpty()) {
			// If we found a reference with actual type arguments we build the hierarchy for the declaring
			// type and add ourselves as a glue node below.
			buildHierarchyFrom(start.getTypeDeclaration().getReference(), end, nodeMap)
				.addLower(node);
			return node;
		}

		if (end.getQualifiedName().equals(start.getQualifiedName())) {
			return node;
		}

		if (start.getSuperclass() != null) {
			buildHierarchyFrom(start.getSuperclass(), end, nodeMap)
				.addLower(node);
		}
		for (CtTypeReference<?> superInterface : start.getSuperInterfaces()) {
			buildHierarchyFrom(superInterface, end, nodeMap)
				.addLower(node);
		}

		return node;
	}
}
