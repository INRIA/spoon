/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.support.visitor.SubInheritanceHierarchyResolver;

/**
 * Expects a {@link CtTypeInformation} as input
 * and produces all sub classes and sub interfaces recursively.<br>
 * The output is produced in arbitrary order.
 */
public class SubInheritanceHierarchyFunction implements CtConsumableFunction<CtTypeInformation>, CtQueryAware {

	private boolean includingSelf = false;
	private boolean includingInterfaces = true;
	private CtQuery query;
	private boolean failOnClassNotFound = false;

	/**
	 * The mapping function created using this constructor
	 * will visit each sub class and sub interface
	 * following sub hierarchy.
	 */
	public SubInheritanceHierarchyFunction() {
	}

	/**
	 * @param includingSelf if true then input element is sent to output too. By default it is false.
	 */
	public SubInheritanceHierarchyFunction includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	/**
	 * @param includingInterfaces if false then interfaces are not visited - only super classes. By default it is true.
	 */
	public SubInheritanceHierarchyFunction includingInterfaces(boolean includingInterfaces) {
		this.includingInterfaces = includingInterfaces;
		return this;
	}

	/**
	 * @param failOnClassNotFound sets whether processing should throw an exception if class is missing in noClassPath mode
	 */
	public SubInheritanceHierarchyFunction failOnClassNotFound(boolean failOnClassNotFound) {
		this.failOnClassNotFound = failOnClassNotFound;
		return this;
	}

	@Override
	public void apply(CtTypeInformation input, final CtConsumer<Object> outputConsumer) {
		final SubInheritanceHierarchyResolver fnc = new SubInheritanceHierarchyResolver(((CtElement) input).getFactory().getModel().getRootPackage())
			.failOnClassNotFound(failOnClassNotFound)
			.includingInterfaces(includingInterfaces);
		if (includingSelf) {
			if (input instanceof CtTypeReference) {
				outputConsumer.accept(((CtTypeReference<?>) input).getTypeDeclaration());
			} else {
				outputConsumer.accept(((CtType<?>) input));
			}
		}
		fnc.addSuperType(input);
		fnc.forEachSubTypeInPackage(new CtConsumer<CtType>() {
			@Override
			public void accept(CtType typeInfo) {
				outputConsumer.accept(typeInfo);
				if (query.isTerminated()) {
					//Cannot terminate, because its support was removed.
					//I think there are cases where it might be useful.
//					fnc.terminate();
				}
			}
		});
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}
}
