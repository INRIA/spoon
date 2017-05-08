/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
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
package spoon.reflect.visitor.filter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.SubInheritanceHierarchyResolver;

/**
 * Returns all methods/lambdas with same signature in related inheritance hierarchies.
 * It can be be used to found all other methods, which has to be changed if signature of method or lambda expression has to be changed.<br>
 *
 * Expects {@link CtExecutable} as input
 * and produces all {@link CtExecutable}s,
 * which have same signature and are declared in sub/super classes or sub/super interfaces of this or related inheritance hierarchy.<br>
 *
 * It makes sense to call this mapping functions for {@link CtMethod} and {@link CtLambda} instances
 * and then it returns {@link CtMethod} and {@link CtLambda} instance which overrides each other or have same signature.
 */
public class AllMethodsSameSignatureFunction implements CtConsumableFunction<CtExecutable<?>>, CtQueryAware {

	private boolean includingSelf = false;
	private CtQuery query;

	public AllMethodsSameSignatureFunction() {
	}

	/**
	 * @param includingSelf if true then input element is sent to output too. By default it is false.
	 */
	public AllMethodsSameSignatureFunction includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	@Override
	public void apply(CtExecutable<?> targetExecutable, final CtConsumer<Object> outputConsumer) {
		final List<CtExecutable<?>> targetMethods = new ArrayList<>();
		targetMethods.add(targetExecutable);
		if (includingSelf) {
			outputConsumer.accept(targetExecutable);
			if (query.isTerminated()) {
				return;
			}
		}
		CtMethod<?> targetMethod;
		if (targetExecutable instanceof CtLambda) {
			targetMethod = ((CtLambda) targetExecutable).getOverriddenMethod();
		} else if (targetExecutable instanceof CtMethod) {
			targetMethod = (CtMethod<?>) targetExecutable;
		} else {
			//CtConstructor or CtAnonymousExecutable never overrides other executable. We are done
			return;
		}
		CtType<?> declaringType = targetMethod.getDeclaringType();
		//search for all declarations and implementations of this method in sub and super classes and interfaces of all related hierarchies.
		class Context {
			boolean haveToSearchForSubtypes;
		}
		final Context context = new Context();
		//at the beginning we know that we have to always search for sub types too. 
		context.haveToSearchForSubtypes = true;
		//Sub inheritance hierarchy function, which remembers visited sub types and does not returns/visits them again
		final SubInheritanceHierarchyResolver subHierarchyFnc = new SubInheritanceHierarchyResolver(declaringType.getFactory().getModel().getRootPackage());
		//add hierarchy of `targetMethod` as to be checked for sub types of declaring type
		subHierarchyFnc.addSuperType(declaringType);
		//unique names of all types whose super inheritance hierarchy was searched for rootType
		Set<String> typesCheckedForRootType = new HashSet<>();
		//list of sub types whose inheritance hierarchy has to be checked
		final List<CtType<?>> toBeCheckedSubTypes = new ArrayList<>();
		//add hierarchy of `targetMethod` as to be checked for super types of declaring type
		toBeCheckedSubTypes.add(declaringType);
		while (toBeCheckedSubTypes.size() > 0) {
			for (CtType<?> subType : toBeCheckedSubTypes) {
				ClassTypingContext ctc = new ClassTypingContext(subType);
				//search for first target method from the same type inheritance hierarchy
				targetMethod = getTargetMethodOfHierarchy(targetMethods, ctc);
				//search for all methods with same signature in inheritance hierarchy of `subType`
				forEachOverridenMethod(ctc, targetMethod, typesCheckedForRootType, new CtConsumer<CtMethod<?>>() {
					@Override
					public void accept(CtMethod<?> overriddenMethod) {
						targetMethods.add(overriddenMethod);
						outputConsumer.accept(overriddenMethod);
						CtType<?> type = overriddenMethod.getDeclaringType();
						subHierarchyFnc.addSuperType(type);
						//mark that new super type was added, so we have to search for sub types again
						context.haveToSearchForSubtypes = true;
					}
				});
				if (query.isTerminated()) {
					return;
				}
			}
			toBeCheckedSubTypes.clear();
			if (context.haveToSearchForSubtypes) {
				context.haveToSearchForSubtypes = false;
				//there are some new super types, whose sub inheritance hierarchy has to be checked
				//search their inheritance hierarchy for sub types
				subHierarchyFnc.forEachSubTypeInPackage(new CtConsumer<CtType<?>>() {
					@Override
					public void accept(CtType<?> type) {
						toBeCheckedSubTypes.add(type);
					}
				});
			}
		}
	}

	/**
	 * calls outputConsumer for each method which is overridden by 'thisMethod' in scope of `ctc`.
	 * There is assured that each method is returned only once.
	 *
	 * @param ctc - class typing context whose scope is searched for overridden methods
	 * @param thisMethod - the
	 * @param distintTypesSet set of qualified names of types which were already visited
	 * @param outputConsumer result handling consumer
	 */
	private void forEachOverridenMethod(final ClassTypingContext ctc, final CtMethod<?> thisMethod, Set<String> distintTypesSet, final CtConsumer<CtMethod<?>> outputConsumer) {
		final CtQuery q = ctc.getAdaptationScope()
			.map(new AllTypeMembersFunction(CtMethod.class).distinctSet(distintTypesSet));
		q.forEach(new CtConsumer<CtMethod<?>>() {
			@Override
			public void accept(CtMethod<?> thatMethod) {
				if (thisMethod == thatMethod) {
					//do not return scope method
					return;
				}
				//check whether method is overridden by searched method
				/*
				 * note: we are in super inheritance hierarchy of type declaring input `method`, so we do not have to check isSubTypeOf.
				 * Check for isSubSignature is enough
				 */
				if (ctc.isSubSignature(thisMethod, thatMethod)) {
					outputConsumer.accept(thatMethod);
					if (query.isTerminated()) {
						q.terminate();
					}
				}
			}
		});
	}

	private CtMethod<?> getTargetMethodOfHierarchy(List<CtExecutable<?>> targetMethods, ClassTypingContext ctc) {
		for (CtExecutable<?> ctExecutable : targetMethods) {
			if (ctExecutable instanceof CtMethod) {
				CtMethod<?> method = (CtMethod<?>) ctExecutable;
				CtType<?> declaringType = method.getDeclaringType();
				if (ctc.isSubtypeOf(declaringType.getReference())) {
					return method;
				}
			}
		}
		//this should never happen
		throw new SpoonException("No target executable was found in super type hiearchy of class typing context");
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}
}
