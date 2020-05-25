/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

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

import java.util.Set;

/**
 * Expects a {@link CtTypeInformation} as input
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
	private boolean interfacesExtendObject = false;

	/**
	 * Super inheritance hierarchy scanning listener.
	 * Use it instead of {@link CtScannerListener}
	 * if you need to know whether visited type reference is class or interface
	 */
	private static class Listener implements CtScannerListener {

		/**
		 * Called before the scanner enters an type
		 *
		 * @param typeRef the type reference to be scanned.
		 * @param isClass true if type reference refers to class, false if it is an interface
		 * @return a {@link ScanningMode} that drives how the scanner processes this element and its children.
		 * For instance, returning {@link ScanningMode#SKIP_ALL} causes that element and all children to be skipped and {@link #exit(CtElement)} are be NOT called for that element.
		 */
		public ScanningMode enter(CtTypeReference<?> typeRef, boolean isClass) {
			return enter((CtElement) typeRef);
		}

		/**
		 * This method is called after the element and all its children have been visited.
		 * This method is NOT called if an exception is thrown in {@link #enter(CtElement)} or during the scanning of the element or any of its children element.
		 * This method is NOT called for an element for which {@link #enter(CtElement)} returned {@link ScanningMode#SKIP_ALL}.
		 *
		 * @param typeRef the type reference that has just been scanned.
		 * @param isClass true if type reference refers to class, false if it is an interface
		 */
		public void exit(CtTypeReference<?> typeRef, boolean isClass) {
			exit((CtElement) typeRef);
		}

		@Override
		public ScanningMode enter(CtElement element) {
			return ScanningMode.NORMAL;
		}

		@Override
		public void exit(CtElement element) {
		}
	}

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
	public static class DistinctTypeListener extends Listener {
		Set<String> visitedSet;

		public DistinctTypeListener(Set<String> visitedSet) {
			this.visitedSet = visitedSet;
		}

		@Override
		public ScanningMode enter(CtElement element) {
			if (visitedSet.add(((CtTypeInformation) element).getQualifiedName())) {
				return ScanningMode.NORMAL;
			}
			return ScanningMode.SKIP_ALL;
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
	 * configures whether it should visit {@link Object} at the end of interface extends interface hierarchy.
	 * Note: interface cannot extend Object (only other interfaces),
	 * but note that interface inherits all public type members of {@link Object},
	 * so there are use cases where client wants to visit Object as last member of interface inheritance hierarchy
	 * @param interfacesExtendObject if true then {@link Object} is visited at the end too
	 * @return this to support fluent API
	 */
	public SuperInheritanceHierarchyFunction interfacesExtendObject(boolean interfacesExtendObject) {
		this.interfacesExtendObject = interfacesExtendObject;
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
		CtType<?> type;
		//detect whether input is a class or something else (e.g. interface)
		boolean isClass;
		if (input instanceof CtType) {
			type = (CtType<?>) input;
			typeRef = type.getReference();
		} else {
			typeRef = (CtTypeReference<?>) input;
			try {
				type = typeRef.getTypeDeclaration();
			} catch (SpoonClassNotFoundException e) {
				if (typeRef.getFactory().getEnvironment().getNoClasspath() == false) {
					throw e;
				}
				type = null;
			}
		}
		//if the type is unknown, than we expect it is interface, otherwise we would visit java.lang.Object too, even for interfaces
		isClass = type instanceof CtClass;
		if (isClass == false && includingInterfaces == false) {
			//the input is interface, but this scanner should visit only interfaces. Finish
			return;
		}
		ScanningMode mode = enter(typeRef, isClass);
		if (mode == ScanningMode.SKIP_ALL) {
			//listener decided to not visit that input. Finish
			return;
		}
		if (includingSelf) {
			sendResult(typeRef, outputConsumer);
			if (query.isTerminated()) {
				mode = ScanningMode.SKIP_CHILDREN;
			}
		}
		if (mode == ScanningMode.NORMAL) {
			if (isClass == false) {
				visitSuperInterfaces(typeRef, outputConsumer);
				if (interfacesExtendObject) {
					//last visit Object.class, because interface inherits all public type members of Object.class
					sendResultWithListener(typeRef.getFactory().Type().OBJECT, isClass, outputConsumer, (ref) -> { });
				}
			} else {
				//call visitSuperClasses only for input of type class. The contract of visitSuperClasses requires that
				visitSuperClasses(typeRef, outputConsumer, includingInterfaces);
			}
		}
		exit(typeRef, isClass);
	}

	/**
	 * calls `outputConsumer.accept(superClass)` for all super classes of superType.
	 *
	 * @param superTypeRef the reference to a class. This method is called only for classes. Never for interface
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
			//only CtClasses extend object,
			//this method is called only for classes (not for interfaces) so we know we can visit java.lang.Object now too
			superClassRef = superTypeRef.getFactory().Type().OBJECT;
		}
		sendResultWithListener(superClassRef, true,
				outputConsumer, (classRef) -> visitSuperClasses(classRef, outputConsumer, includingInterfaces));
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
			sendResultWithListener(ifaceRef, false,
					outputConsumer, (ref) -> visitSuperInterfaces(ref, outputConsumer));
			if (query.isTerminated()) {
				return;
			}
		}
	}

	private void sendResultWithListener(CtTypeReference<?> classRef, boolean isClass, CtConsumer<Object> outputConsumer, CtConsumer<CtTypeReference<?>> runNext) {
		ScanningMode mode = enter(classRef, isClass);
		if (mode == ScanningMode.SKIP_ALL) {
			return;
		}
		sendResult(classRef, outputConsumer);
		if (mode == ScanningMode.NORMAL && query.isTerminated() == false) {
			runNext.accept(classRef);
		}
		exit(classRef, isClass);
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}

	private ScanningMode enter(CtTypeReference<?> type, boolean isClass) {
		if (listener == null) {
			return ScanningMode.NORMAL;
		}
		if (listener instanceof Listener) {
			Listener typeListener = (Listener) listener;
			return typeListener.enter(type, isClass);
		}
		return listener.enter(type);
	}

	private void exit(CtTypeReference<?> type, boolean isClass) {
		if (listener != null) {
			if (listener instanceof Listener) {
				((Listener) listener).exit(type, isClass);
			} else {
				listener.exit(type);
			}
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
