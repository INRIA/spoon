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

import java.util.Set;

import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.support.SpoonClassNotFoundException;

/**
 * Expects a {@link CtType} as input
 * and produces all super classes and super interfaces recursively.<br>
 * The output is produced in following order:
 * <ol>
 * <li>input type. if `includingSelf==true`
 * <li>all interfaces of type recursively
 * <li>parent class of type
 * <li>goto 1: using  parent class as input type
 * </ol>
 */
public class SuperInheritanceHierarchyFunction implements CtConsumableFunction<CtType<?>>, CtQueryAware {
	private boolean includingSelf = false;
	private boolean includingInterfaces = true;
	private Set<String> visitedSet;
	private CtQuery query;
	private boolean failOnClassNotFound = false;

	/**
	 * The mapping function create using this constructor
	 * will visit each super class and super interface
	 * following super hierarchy. It can happen
	 * that some interfaces will be visited more then once
	 * if they are in hierarchy more then once.<br>
	 * Use second constructor if you want to visit each interface only once.
	 */
	public SuperInheritanceHierarchyFunction() {
	}

	/**
	 * @param visitedSet assures that each class/interface is visited only once
	 */
	public SuperInheritanceHierarchyFunction(Set<String> visitedSet) {
		this.visitedSet = visitedSet;
	}

	/**
	 * @param includingSelf if true then input element is sent to output too. By default it is false.
	 */
	public SuperInheritanceHierarchyFunction includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	/**
	 * @param includingInterfaces if false then interfaces are not visited - only super classes. By default it is true.
	 */
	public SuperInheritanceHierarchyFunction includingInterfaces(boolean includingInterfaces) {
		this.includingInterfaces = includingInterfaces;
		return this;
	}

	@Override
	public void apply(CtType<?> input, CtConsumer<Object> outputConsumer) {
		if (includingSelf) {
			if (canVisitType(input.getQualifiedName()) == false) {
				return;
			}
			outputConsumer.accept(input);
			if (query.isTerminated()) {
				return;
			}
		}
		if (input instanceof CtClass) {
			visitSuperClasses(input, outputConsumer, includingInterfaces);
		} else if (input instanceof CtInterface) {
			visitSuperInterfaces(input, outputConsumer);
		} else if (input instanceof CtAnnotationType) {
			return;
		} else if (input instanceof CtTypeParameter) {
			visitSuperClasses(input, outputConsumer, false);
		}
	}

	/**
	 * calls `outputConsumer.accept(superClass)` all super classes of superType.
	 *
	 * @param includingInterfaces if true then all superInterfaces of each type are sent to `outputConsumer` too.
	 */
	protected void visitSuperClasses(CtType<?> superType, CtConsumer<Object> outputConsumer, boolean includingInterfaces) {
		while (true) {
			if (includingInterfaces) {
				if (visitSuperInterfaces(superType, outputConsumer) == false) {
					return;
				}
			}
			CtTypeReference<?> superClassRef = superType.getSuperclass();
			if (superClassRef == null) {
				if (superType instanceof CtClass) {
					// only CtCLasses extend object, so visit Object too
					superType = superType.getFactory().Type().get(Object.class);
					if (canVisitType(Object.class.getName())) {
						outputConsumer.accept(superType);
					}
				}
				return;
			}
			if (canVisitType(superClassRef.getQualifiedName()) == false) {
				return;
			}
			try {
				superType = superClassRef.getTypeDeclaration();
			} catch (SpoonClassNotFoundException e) {
				if (failOnClassNotFound) {
					throw e;
				}
			}
			outputConsumer.accept(superType);
			if (query.isTerminated()) {
				return;
			}
		}
	}

	/**
	 * calls `outputConsumer.accept(interface)` for all superInterfaces of type recursively.
	 * @return false if query is terminated
	 */
	protected boolean visitSuperInterfaces(CtType<?> type, CtConsumer<Object> outputConsumer) {
		for (CtTypeReference<?> ifaceRef : type.getSuperInterfaces()) {
			if (canVisitType(ifaceRef.getQualifiedName()) == false) {
				continue;
			}
			CtType<?> superType;
			try {
				superType = ifaceRef.getTypeDeclaration();
			} catch (SpoonClassNotFoundException e) {
				if (failOnClassNotFound) {
					throw e;
				}
				continue;
			}
			if (superType == null) {
				continue;
			}
			outputConsumer.accept(superType);
			if (query.isTerminated()) {
				return false;
			}
			if (visitSuperInterfaces(superType, outputConsumer) == false) {
				return false;
			}
		}
		return true;
	}

	protected boolean canVisitType(String qualifiedName) {
		if (visitedSet != null) {
			return visitedSet.add(qualifiedName);
		}
		return true;
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}

	/**
	 * @param failOnClassNotFound sets whether processing should throw an exception if class is missing in noClassPath mode
	 */
	public SuperInheritanceHierarchyFunction failOnClassNotFound(boolean failOnClassNotFound) {
		this.failOnClassNotFound = failOnClassNotFound;
		return this;
	}
}
