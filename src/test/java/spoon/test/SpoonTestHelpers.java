/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.test;

import spoon.metamodel.Metamodel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.OverridingMethodFilter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.ArrayList;
import java.util.List;

public class SpoonTestHelpers {
	// only static methods
	private SpoonTestHelpers(){
	}

	public static List<CtType<? extends CtElement>> getAllInstantiableMetamodelInterfaces() {
		return Metamodel.getInstance().getAllInstantiableMetamodelInterfaces();
	}

	/**
	 * returns true if typeReference point to a class of the metamodel or a List/set of a class of the metamodel.
	 */
	public static boolean isMetamodelRelatedType(CtTypeReference<?> typeReference) {
		CtTypeReference<Object> ctElRef = typeReference.getFactory().Code().createCtTypeReference(CtElement.class);

		// simple case, a sublcass of CtElement
		if (typeReference.isSubtypeOf(ctElRef)) {
			return true;
		}
		// limit case because of a bug to be fixed
		if (!typeReference.getActualTypeArguments().isEmpty() && "?".equals(typeReference.getActualTypeArguments()
				.get(0).getQualifiedName())) {
			return false;
		}
		return (!typeReference.getActualTypeArguments().isEmpty()
				&& typeReference.getActualTypeArguments()
				.get(0).getTypeDeclaration()
				.isSubtypeOf(ctElRef))
				;
	}

	/**
	 * The default contains based on Method.equals takes into account the return type
	 * And we don't want this, because we need to capture
	 * the annotation of the implementation method.
	 */
	private static boolean containsMethodBasedOnName(List<CtMethod<?>> l, CtMethod setter) {
		for(CtMethod<?> m : l ) {
			if (m.getSimpleName().equals(setter.getSimpleName())) {
				return true;
			}
		}
		return false;
	}

	/** returns all possible methods in the order class then interface, and up again */
	public static List<CtMethod<?>> getAllMetamodelMethods(CtType<?> baseType) {
		List<CtMethod<?>> result = new ArrayList<>();
		for (CtMethod<?> m : baseType.getMethods()) {
			if (!containsMethodBasedOnName(result, m)) {
				result.add(m);
			}
		}
		for (CtTypeReference<?> itf : baseType.getSuperInterfaces()) {
			for (CtMethod<?> up : getAllSetters(itf.getTypeDeclaration())) {
				if (!containsMethodBasedOnName(result, up)) {
					result.add(up);
				}
			}
		}
		return result;
	}

	/** returns all possible setters related to CtElement */
	public static List<CtMethod<?>> getAllSetters(CtType<?> baseType) {
		List<CtMethod<?>> result = new ArrayList<>();
		for (CtMethod<?> m : getAllMetamodelMethods(baseType)) {
			if("setParent".equals(m.getSimpleName())) {
				//parent is a special kind of setter, which does not influence model properties of element, but link to parent element.
				continue;
			}
			if (! (m.getSimpleName().startsWith("set") || m.getSimpleName().startsWith("add"))) {
				continue;
			}
			if (m.hasAnnotation(UnsettableProperty.class)) {
				continue;
			}
			if (m.getParameters().size()!=1) {
				continue;
			}
			if (!isMetamodelRelatedType(m.getParameters().get(0).getType())) {
				continue;
			}
			result.add(m);
		}
		return result;
	}

	/** returns the corresponding setter, if several are possible returns the lowest one in the hierarchy */
	public static CtMethod<?> getSetterOf(CtType<?> baseType, CtMethod<?> getter) {
		String setterName = getter.getSimpleName().replaceFirst("^get", "set");
		Object[] tentativeSetters = baseType.getAllMethods().stream().filter(x->x.getSimpleName().equals(setterName)).toArray();
		if (tentativeSetters.length==0) {
			return null;
		}

		// return one that is as low as possible in the hierarchy
		for(Object o : tentativeSetters) {
			if (baseType.getPackage().getElements(new OverridingMethodFilter((CtMethod<?>) o)).isEmpty()) {
				return (CtMethod<?>) o;
			}
		}

		return (CtMethod<?>) tentativeSetters[0];
	}

	/** specifies what a metamodel property is: a getter than returns a metamodel-related class and that is not derived */
	public static boolean isMetamodelProperty(CtType<?> baseType, CtMethod<?> m) {
		return
				m.getSimpleName().startsWith("get")
						&& m.getParameters().isEmpty() // a getter has no parameter
						&& !m.hasAnnotation(DerivedProperty.class)
						&&
						// return type
						isMetamodelRelatedType(m.getType());
	}
}
