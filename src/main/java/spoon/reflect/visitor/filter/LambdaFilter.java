/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import java.util.HashSet;
import java.util.Set;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

/**
 * This filter matches all the {@link CtLambda} elements, which implements defined interface(s)
 */
public class LambdaFilter implements Filter<CtLambda<?>> {

	private Set<String> qualifiedNamesOfInterfaces = new HashSet<>();

	/**
	 * Use {@link #addImplementingInterface(CtTypeInformation)} to define set of interfaces whose lambdas it is search for
	 */
	public LambdaFilter() {
	}

	/**
	 * Matches all lambdas implementing `iface`
	 * Use {@link #addImplementingInterface(CtTypeInformation)} to define set of interfaces whose lambdas it is search for
	 */
	public LambdaFilter(CtInterface<?> iface) {
		addImplementingInterface(iface);
	}

	/**
	 * Matches all lambdas implementing `iface`
	 * Use {@link #addImplementingInterface(CtTypeInformation)} to define set of interfaces whose lambdas it is search for
	 */
	public LambdaFilter(CtTypeReference<?> iface) {
		addImplementingInterface(iface);
	}

	/**
	 * Allows to search for lambdas implemented by different interfaces.
	 * @param typeInfo interface whose lambda implementations it is searching for
	 */
	public LambdaFilter addImplementingInterface(CtTypeInformation typeInfo) {
		if (typeInfo instanceof CtType) {
			if (typeInfo instanceof CtInterface) {
				qualifiedNamesOfInterfaces.add(typeInfo.getQualifiedName());
			} //else ignore that request, because lambda can implement only interfaces
		} else {
			//do not check if it is interface or not. That check needs CtType in model and it might be not available in some modes
			//it is OK to search for non interface types. It simply founds no lambda implementing that
			qualifiedNamesOfInterfaces.add(typeInfo.getQualifiedName());
		}
		return this;
	}

	@Override
	public boolean matches(CtLambda<?> lambda) {
		return qualifiedNamesOfInterfaces.contains(lambda.getType().getQualifiedName());
	}
}
