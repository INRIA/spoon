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
package spoon.pattern;

import java.util.List;
import java.util.Map;

import spoon.pattern.internal.DefaultGenerator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Experimental;
import spoon.support.util.ImmutableMap;
import spoon.support.util.ImmutableMapImpl;

/**
 * Represents a generator of new code where
 * (Pattern) + (pattern parameters) =&gt; (copy of pattern where parameters are replaced by parameter values)
 * This is done with {@link #substitute(Factory, Class, ImmutableMap)}
 *
 * Main documentation at http://spoon.gforge.inria.fr/pattern.html.
 *
 * Instances of {@link Generator} can created with {@link PatternBuilder} and then call {@link Pattern#generator()}.
 *
 */
@Experimental
public class Generator {
	private Pattern pattern;

	/** package-protected, must use {@link Pattern#generator()} */
	Generator(Pattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * Main method to generate a new AST made from substituting of parameters by values in `params`
	 * @param factory TODO
	 * @param valueType - the expected type of returned items
	 * @param params - the substitution parameters
	 * @return List of generated elements
	 */
	public <T extends CtElement> List<T> substitute(Factory factory, Class<T> valueType, ImmutableMap params) {
		return new DefaultGenerator(factory).setAddGeneratedBy(isAddGeneratedBy()).generateTargets(pattern.getModelValueResolver(), params, valueType);
	}

	/** Utility method that provides the same feature as {@link #substitute(Factory, Class, ImmutableMap)}, but with a Map as third parameter */
	public <T extends CtElement> List<T> substituteList(Factory factory, Class<T> valueType, Map<String, Object> params) {
		return substitute(factory, valueType, new ImmutableMapImpl(params));
	}

	/** Utility method that provides the same feature as {@link #substitute(Factory, Class, ImmutableMap)}, but returns a single element, and uses a map as parameter */
	public <T extends CtElement> T substituteSingle(Factory factory, Class<T> valueType, Map<String, Object> params) {
		return substituteSingle(factory, valueType, new ImmutableMapImpl(params));
	}

	/** Utility method that provides the same feature as {@link #substitute(Factory, Class, ImmutableMap)}, but returns a single element */
	public <T extends CtElement> T substituteSingle(Factory factory, Class<T> valueType, ImmutableMap params) {
		return new DefaultGenerator(factory).setAddGeneratedBy(isAddGeneratedBy()).generateSingleTarget(pattern.getModelValueResolver(), params, valueType);
	}

	/**
	 * Generates type with qualified name `typeQualifiedName` using this {@link Generator} and provided `params`.
	 *
	 * Note: the root of pattern element must be one or more types.
	 *
	 * @param typeQualifiedName the qualified name of to be generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	public <T extends CtType<?>> T createType(Factory factory, String typeQualifiedName, Map<String, Object> params) {
		CtTypeReference<?> newTypeRef = factory.Type().createReference(typeQualifiedName);
		CtPackage ownerPackage = newTypeRef.getFactory().Package().getOrCreate(newTypeRef.getPackage().getQualifiedName());
		return createType(ownerPackage, newTypeRef.getSimpleName(), params);
	}

	/**
	 * Generates type in the package `ownerPackage` with simple name `typeSimpleName` using this {@link Generator} and provided `params`
	 *
	 * Note: the root of pattern element must be one or more types.
	 *
	 * @param ownerPackage the target package
	 * @param typeSimpleName the simple name of future generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	@SuppressWarnings("unchecked")
	private <T extends CtType<?>> T createType(CtPackage ownerPackage, String typeSimpleName, Map<String, Object> params) {
		@SuppressWarnings({ "rawtypes" })
		List<CtType> types = substitute(ownerPackage.getFactory(), CtType.class, new ImmutableMapImpl(params,
				PatternBuilder.TARGET_TYPE, ownerPackage.getFactory().Type().createReference(getQualifiedName(ownerPackage, typeSimpleName))));
		T result = null;
		for (CtType<?> type : types) {
			ownerPackage.addType(type);
			if (type.getSimpleName().equals(typeSimpleName)) {
				result = (T) type;
			}
		}
		return result;
	}

	/**
	 * generates elements following this template with expected target scope `targetType`
	 * If they are {@link CtTypeMember} then adds them into `targetType`.
	 *
	 * @param targetType the existing type, which will contain newly generates {@link CtElement}s
	 * @param valueType the type of generated elements
	 * @param params the pattern parameters
	 * @return List of generated elements
	 */
	public <T extends CtElement> List<T> applyToType(CtType<?> targetType, Class<T> valueType,  Map<String, Object> params) {
		List<T> results = substitute(targetType.getFactory(), valueType, new ImmutableMapImpl(params, PatternBuilder.TARGET_TYPE, targetType.getReference()));
		for (T result : results) {
			if (result instanceof CtTypeMember) {
				targetType.addTypeMember((CtTypeMember) result);
			}
		}
		return results;
	}

	private static String getQualifiedName(CtPackage pckg, String simpleName) {
		if (pckg.isUnnamedPackage()) {
			return simpleName;
		}
		return pckg.getQualifiedName() + CtPackage.PACKAGE_SEPARATOR + simpleName;
	}

	public boolean isAddGeneratedBy() {
		return pattern.isAddGeneratedBy();
	}
}
