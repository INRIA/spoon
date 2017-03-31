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

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.support.SpoonClassNotFoundException;

import static spoon.reflect.visitor.chain.ScanningMode.NORMAL;
import static spoon.reflect.visitor.chain.ScanningMode.SKIP_ALL;
import static spoon.reflect.visitor.chain.ScanningMode.SKIP_CHILDREN;

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
public class SuperInheritanceHierarchyFunction implements CtConsumableFunction<CtTypeInformation>, CtQueryAware {
	private boolean includingSelf = false;
	private boolean includingInterfaces = true;
	private CtQuery query;
	private boolean failOnClassNotFound = false;
	private CtScannerListener listener;
	private boolean returnTypeReferences = false;

	/**
	 * The mapping function created using this constructor
	 * will visit each super class and super interface
	 * following super hierarchy. It can happen
	 * that some interfaces will be visited more then once
	 * if they are in super inheritance hierarchy more then once.<br>
	 * Use second constructor if you want to visit each interface only once.
	 */
	public SuperInheritanceHierarchyFunction() {
	}

	/**
	 * The mapping function created using this constructor
	 * will visit each super class and super interface
	 * following super hierarchy. It is assured
	 * that interfaces will be visited only once
	 * even if they are in super inheritance hierarchy more then once.<br>
	 *
	 * @param visitedSet assures that each class/interface is visited only once
	 * The types which are already contained in `visitedSet` are not visited
	 * and not returned by this mapping function.
	 */
	public SuperInheritanceHierarchyFunction(Set<String> visitedSet) {
		listener = new DistinctTypeListener(visitedSet);
	}

	/**
	 * Implementation of {@link CtScannerListener},
	 * which is used to assure that each interface is visited only once.
	 * It can be extended to implement more powerful listener
	 */
	public static class DistinctTypeListener implements CtScannerListener {
		Set<String> visitedSet;

		public DistinctTypeListener(Set<String> visitedSet) {
			this.visitedSet = visitedSet;
		}

		@Override
		public ScanningMode enter(CtElement element) {
			if (visitedSet.add(((CtTypeInformation) element).getQualifiedName())) {
				return NORMAL;
			}
			return SKIP_ALL;
		}
		@Override
		public void exit(CtElement element) {
		}
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

	/**
	 * configures whether {@link CtType} or {@link CtTypeReference} instances are returned by this mapping function
	 * @param returnTypeReferences if true then {@link CtTypeReference} instances are returned by this mapping function
	 * @return this to support fluent API
	 */
	public SuperInheritanceHierarchyFunction returnTypeReferences(boolean returnTypeReferences) {
		this.returnTypeReferences = returnTypeReferences;
		return this;
	}

	/**
	 * The listener evens are called in this order:
	 * <ol>
	 * <li> enter(input element)
	 * <li> return input element
	 * <li> enter/exit for each super interface of input element recursively
	 * <li> call 1-5) recursively where input element is super class of input element
	 * <li> exit(input element)
	 * </ol>
	 * Note: this order is assured and some algorithms already depend on it!
	 *
	 * @param listener the implementation of {@link CtScannerListener}, which will listen for enter/exit of {@link CtTypeReference} during type hierarchy scanning
	 * @return this to support fluent API
	 */
	public SuperInheritanceHierarchyFunction setListener(CtScannerListener listener) {
		if (this.listener != null) {
			throw new SpoonException("Cannot register listener on instance created with constructor which accepts the Set<String>. Use the no parameter constructor if listener has to be registered");
		}
		this.listener = listener;
		return this;
	}

	/**
	 * @param failOnClassNotFound sets whether processing should throw an exception if class is missing in noClassPath mode
	 */
	public SuperInheritanceHierarchyFunction failOnClassNotFound(boolean failOnClassNotFound) {
		this.failOnClassNotFound = failOnClassNotFound;
		return this;
	}

	@Override
	public void apply(CtTypeInformation input, CtConsumer<Object> outputConsumer) {
		CtTypeReference<?> typeRef;
		if (input instanceof CtType) {
			typeRef = ((CtType<?>) input).getReference();
		} else {
			typeRef = (CtTypeReference<?>) input;
		}
		ScanningMode mode = enter(typeRef);
		if (mode == SKIP_ALL) {
			//listener decided to not visit that input. Finish
			return;
		}
		if (includingSelf) {
			sendResult(typeRef, outputConsumer);
			if (query.isTerminated()) {
				mode = SKIP_CHILDREN;
			}
		}
		if (mode == NORMAL) {
			visitSuperClasses(typeRef, outputConsumer, includingInterfaces);
		}
		exit(typeRef);
	}

	/**
	 * calls `outputConsumer.accept(superClass)` for all super classes of superType.
	 *
	 * @param includingInterfaces if true then all superInterfaces of each type are sent to `outputConsumer` too.
	 */
	protected void visitSuperClasses(CtTypeReference<?> superTypeRef, CtConsumer<Object> outputConsumer, boolean includingInterfaces) {
		if (Object.class.getName().equals(superTypeRef.getQualifiedName())) {
			//java.lang.Object has no interface or super classes
			return;
		}
		if (includingInterfaces) {
			visitSuperInterfaces(superTypeRef, outputConsumer);
			if (query.isTerminated()) {
				return;
			}
		}
		CtTypeReference<?> superClassRef = superTypeRef.getSuperclass();
		if (superClassRef == null) {
			CtType<?> superType;
			try {
				superType = superTypeRef.getTypeDeclaration();
			} catch (SpoonClassNotFoundException e) {
				if (failOnClassNotFound) {
					throw e;
				}
				return;
			}
			if (superType instanceof CtClass) {
				// only CtCLasses extend object, so visit Object too
				superClassRef = superTypeRef.getFactory().Type().OBJECT;
			} else {
				return;
			}
		}
		ScanningMode mode = enter(superClassRef);
		if (mode == SKIP_ALL) {
			return;
		}
		sendResult(superClassRef, outputConsumer);
		if (mode == NORMAL && query.isTerminated() == false) {
			visitSuperClasses(superClassRef, outputConsumer, includingInterfaces);
		}
		exit(superClassRef);
	}

	/**
	 * calls `outputConsumer.accept(interface)` for all superInterfaces of type recursively.
	 */
	protected void visitSuperInterfaces(CtTypeReference<?> type, CtConsumer<Object> outputConsumer) {
		Set<CtTypeReference<?>> superInterfaces;
		try {
			superInterfaces = type.getSuperInterfaces();
		} catch (SpoonClassNotFoundException e) {
			if (failOnClassNotFound) {
				throw e;
			}
			Launcher.LOGGER.warn("Cannot load class: " + type.getQualifiedName() + " with class loader "
					+ Thread.currentThread().getContextClassLoader());
			return;
		}
		for (CtTypeReference<?> ifaceRef : superInterfaces) {
			ScanningMode mode = enter(ifaceRef);
			if (mode == SKIP_ALL) {
				continue;
			}
			sendResult(ifaceRef, outputConsumer);
			if (mode == NORMAL && query.isTerminated() == false) {
				visitSuperInterfaces(ifaceRef, outputConsumer);
			}
			exit(ifaceRef);
			if (query.isTerminated()) {
				return;
			}
		}
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}

	private ScanningMode enter(CtTypeReference<?> type) {
		return listener == null ? NORMAL : listener.enter(type);
	}

	private void exit(CtTypeReference<?> type) {
		if (listener != null) {
			listener.exit(type);
		}
	}

	protected void sendResult(CtTypeReference<?> typeRef, CtConsumer<Object> outputConsumer) {
		if (returnTypeReferences) {
			outputConsumer.accept(typeRef);
		} else {
			CtType<?> type;
			try {
				type = typeRef.getTypeDeclaration();
			} catch (SpoonClassNotFoundException e) {
				if (failOnClassNotFound) {
					throw e;
				}
				Launcher.LOGGER.warn("Cannot load class: " + typeRef.getQualifiedName() + " with class loader "
						+ Thread.currentThread().getContextClassLoader());
				return;
			}
			outputConsumer.accept(type);
		}
	}
}
