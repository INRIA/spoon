/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.reflect.visitor.filter.CtScannerFunction;
import spoon.reflect.visitor.filter.SuperInheritanceHierarchyFunction;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Expects a {@link CtPackage} as input
 * and  upon calls to forEachSubTypeInPackage produces all sub classes and sub interfaces,
 * which extends or implements super type(s) provided by call(s) of {@link #addSuperType(CtTypeInformation)}
 * and stored as `targetSuperTypes`.<br>
 *
 * The repeated processing of this mapping function on the same input returns only newly found sub types.
 * The instance of {@link SubInheritanceHierarchyResolver} returns found sub types only once.
 * So repeated call with same input package returns nothing.
 * Create and use new instance of {@link SubInheritanceHierarchyResolver} if you need to scan the subtype hierarchy again.
 */
public class SubInheritanceHierarchyResolver {

	/** where the subtypes will be looked for */
	private CtPackage inputPackage;

	/** whether interfaces are included in the result */
	private boolean includingInterfaces = true;
	/**
	 * Set of qualified names of all super types whose sub types we are searching for.
	 * Each found sub type is added to this set too
	 */
	private Set<String> targetSuperTypes = new HashSet<>();
	/**
	 * if true then we have to check if type is a subtype of superClass or superInterfaces too
	 * if false then it is enough to search in superClass hierarchy only (faster)
	 */
	private boolean hasSuperInterface = false;

	private boolean failOnClassNotFound = false;

	public SubInheritanceHierarchyResolver(CtPackage input) {
		inputPackage = input;
	}

	/**
	 * Add another super type to this mapping function.
	 * Using this function you can search parallel in more sub type hierarchies.
	 *
	 * @param superType - the type whose sub types will be returned by this mapping function too.
	 */
	public SubInheritanceHierarchyResolver addSuperType(CtTypeInformation superType) {
		targetSuperTypes.add(superType.getQualifiedName());
		if (hasSuperInterface == false) {
			hasSuperInterface = superType.isInterface();
		}
		return this;
	}

	/**
	 * @param includingInterfaces if false then interfaces are not visited - only super classes. By default it is true.
	 */
	public SubInheritanceHierarchyResolver includingInterfaces(boolean includingInterfaces) {
		this.includingInterfaces = includingInterfaces;
		return this;
	}

	/**
	 * @param failOnClassNotFound sets whether processing should throw an exception if class is missing in noClassPath mode
	 */
	public SubInheritanceHierarchyResolver failOnClassNotFound(boolean failOnClassNotFound) {
		this.failOnClassNotFound = failOnClassNotFound;
		return this;
	}

	/**
	 * Calls `outputConsumer.apply(subType)` for each sub type of the targetSuperTypes that are found in `inputPackage`.
	 * Each sub type is returned only once.
	 * It makes sense to call this method again for example after new super types are added
	 * by {@link #addSuperType(CtTypeInformation)}.
	 *
	 * 	If this method is called again with same input and configuration, nothing in sent to outputConsumer
	 * @param outputConsumer the consumer for found sub types
	 */
	public <T extends CtType<?>> void forEachSubTypeInPackage(final CtConsumer<T> outputConsumer) {
		/*
		 * Set of qualified names of all visited types, independent on whether they are sub types or not.
		 */
		final Set<String> allVisitedTypeNames = new HashSet<>();
		/*
		 * the queue of types whose super inheritance hierarchy we are just visiting.
		 * They are potential sub types of an `targetSuperTypes`
		 */
		final Deque<CtTypeReference<?>> currentSubTypes = new ArrayDeque<>();
		//algorithm
		//1) query step: scan input package for sub classes and sub interfaces
		final CtQuery q = inputPackage.map(new CtScannerFunction());
		//2) query step: visit only required CtTypes
		if (includingInterfaces) {
			//the client is interested in sub inheritance hierarchy of interfaces too. Check interfaces, classes, enums, Annotations, but not CtTypeParameters.
			q.select(typeFilter);
		} else {
			//the client is not interested in sub inheritance hierarchy of interfaces. Check only classes and enums.
			q.select(classFilter);
		}
		/*
		 * 3) query step: for each found CtType, visit it's super inheritance hierarchy and search there for a type which is equal to one of targetSuperTypes.
		 * If found then all sub types in hierarchy (variable `currentSubTypes`) are sub types of targetSuperTypes. So return them
		 */
		q.map(new SuperInheritanceHierarchyFunction()
			//if there is any interface between `targetSuperTypes`, then we have to check superInterfaces too
			.includingInterfaces(hasSuperInterface)
			.failOnClassNotFound(failOnClassNotFound)
			/*
			 * listen for types in super inheritance hierarchy
			 * 1) to collect `currentSubTypes`
			 * 2) to check if we have already found a targetSuperType
			 * 3) if found then send `currentSubTypes` to `outputConsumer` and skip visiting of further super types
			 */
			.setListener(new CtScannerListener() {
				@Override
				public ScanningMode enter(CtElement element) {
					final CtTypeReference<?> typeRef = (CtTypeReference<?>) element;
					String qName = typeRef.getQualifiedName();
					if (targetSuperTypes.contains(qName)) {
						/*
						 * FOUND! we are in super inheritance hierarchy, which extends from an searched super type(s).
						 * All `currentSubTypes` are sub types of searched super type
						 */
						while (!currentSubTypes.isEmpty()) {
							final CtTypeReference<?> currentTypeRef  = currentSubTypes.pop();
							String currentQName = currentTypeRef.getQualifiedName();
							/*
							 * Send them to outputConsumer and add then as targetSuperTypes too, to perform faster with detection of next sub types.
							 */
							if (!targetSuperTypes.contains(currentQName)) {
								targetSuperTypes.add(currentQName);
								outputConsumer.accept((T) currentTypeRef.getTypeDeclaration());
							}
						}
						//we do not have to go deeper into super inheritance hierarchy. Skip visiting of further super types
						//but continue visiting of siblings (do not terminate query)
						return ScanningMode.SKIP_ALL;
					}
					if (allVisitedTypeNames.add(qName) == false) {
						/*
						 * this type was already visited, by another way. So it is not sub type of `targetSuperTypes`.
						 * Stop visiting it's inheritance hierarchy.
						 */
						return ScanningMode.SKIP_ALL;
					}
					/*
					 * This type was not visited yet.
					 * We still do not know whether this type is a sub type of any target super type(s)
					 * continue searching in super inheritance hierarchy
					 */
					currentSubTypes.push(typeRef);
					return ScanningMode.NORMAL;
				}
				@Override
				public void exit(CtElement element) {
					CtTypeInformation type = (CtTypeInformation) element;
					if (currentSubTypes.isEmpty() == false) {
						//remove current type, which is not a sub type of targetSuperTypes from the currentSubTypes
						CtTypeInformation stackType = currentSubTypes.pop();
						if (stackType != type) {
							//the enter/exit was not called consistently. There is a bug in SuperInheritanceHierarchyFunction
							throw new SpoonException("CtScannerListener#exit was not called after enter.");
						}
					}
				}
			})
		).forEach(new CtConsumer<CtType<?>>() {
			@Override
			public void accept(CtType<?> type) {
				//we do not care about types visited by query `q`.
				//the result of whole mapping function was already produced by `sendResult` call
				//but we have to consume all these results to let query running
			}
		});
	}

	/**
	 * accept all {@link CtType} excluding {@link CtTypeParameter}
	 */
	private static final Filter<CtType<?>> typeFilter = new Filter<CtType<?>>() {
		@Override
		public boolean matches(CtType<?> type) {
			return !(type instanceof CtTypeParameter);
		}
	};

	/**
	 * Accept all {@link CtClass}, {@link CtEnum}
	 */
	private static final Filter<CtClass<?>> classFilter = new TypeFilter<>(CtClass.class);
}
