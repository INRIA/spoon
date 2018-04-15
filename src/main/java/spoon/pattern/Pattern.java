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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.pattern.matcher.Match;
import spoon.pattern.matcher.MatchingScanner;
import spoon.pattern.node.ModelNode;
import spoon.pattern.parameter.ParameterInfo;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.support.util.ParameterValueProvider;
import spoon.support.util.ParameterValueProviderFactory;
import spoon.support.util.UnmodifiableParameterValueProvider;

/**
 * Represents a pattern for matching code. A pattern is composed of a list of AST models, where a model is an AST with some nodes being "pattern parameters".
 *
 * Differences with {@link spoon.template.TemplateMatcher}:
 * - it can match sequences of elements
 * - it can match inlined elements
 *
 * Instances can created with {@link PatternBuilder}.
 *
 * The {@link Pattern} can also be used to generate new code where
 * (Pattern) + (pattern parameters) =&gt; (copy of pattern where parameters are replaced by parameter values)
 *
 * This is done with {@link #substitute(Factory, Class, ParameterValueProvider)}
 */
public class Pattern {
	private ParameterValueProviderFactory parameterValueProviderFactory = UnmodifiableParameterValueProvider.Factory.INSTANCE;
	//TODO rename
	private ModelNode modelValueResolver;
	private boolean addGeneratedBy = false;

	/** package-protected, must use {@link PatternBuilder} */
	Pattern(ModelNode modelValueResolver) {
		this.modelValueResolver = modelValueResolver;
	}

	/**
	 * @return a {@link ModelNode} of this pattern
	 */
	//TODO rename
	public ModelNode getModelValueResolver() {
		return modelValueResolver;
	}

	/**
	 * @return Map of parameter names to {@link ParameterInfo} for each parameter of this {@link Pattern}
	 */
	public Map<String, ParameterInfo> getParameterInfos() {
		Map<String, ParameterInfo> parameters = new HashMap<>();
		modelValueResolver.forEachParameterInfo((parameter, valueResolver) -> {
			ParameterInfo existingParameter = parameters.get(parameter.getName());
			if (existingParameter != null) {
				if (existingParameter == parameter) {
					//OK, this parameter is already there
					return;
				}
				throw new SpoonException("There is already a parameter: " + parameter.getName());
			}
			parameters.put(parameter.getName(), parameter);
		});
		return Collections.unmodifiableMap(parameters);
	}

	/**
	 * Main method to generate a new AST made from substituting of parameters by values in `params`
	 * @param factory TODO
	 * @param valueType - the expected type of returned items
	 * @param params - the substitution parameters
	 * @return List of generated elements
	 */
	public <T extends CtElement> List<T> substitute(Factory factory, Class<T> valueType, ParameterValueProvider params) {
		return new DefaultGenerator(factory).setAddGeneratedBy(isAddGeneratedBy()).generateTargets(modelValueResolver, params, valueType);
	}

	/** Utility method that provides the same feature as {@link #substitute(Factory, Class, ParameterValueProvider)}, but with a Map as third parameter */
	public <T extends CtElement> List<T> substituteList(Factory factory, Class<T> valueType, Map<String, Object> params) {
		return substitute(factory, valueType, new UnmodifiableParameterValueProvider(params));
	}

	/** Utility method that provides the same feature as {@link #substitute(Factory, Class, ParameterValueProvider)}, but returns a single element, and uses a map as parameter */
	public <T extends CtElement> T substituteSingle(Factory factory, Class<T> valueType, Map<String, Object> params) {
		return substituteSingle(factory, valueType, new UnmodifiableParameterValueProvider(params));
	}

	/** Utility method that provides the same feature as {@link #substitute(Factory, Class, ParameterValueProvider)}, but returns a single element */
	public <T extends CtElement> T substituteSingle(Factory factory, Class<T> valueType, ParameterValueProvider params) {
		return new DefaultGenerator(factory).setAddGeneratedBy(isAddGeneratedBy()).generateSingleTarget(modelValueResolver, params, valueType);
	}

	/**
	 * Generates type with qualified name `typeQualifiedName` using this {@link Pattern} and provided `params`.
	 *
	 * Note: the root of pattern element must be one or more types.
	 *
	 * @param typeQualifiedName the qualified name of to be generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	public <T extends CtType<?>> T createType(Factory factory, String typeQualifiedName, Map<String, Object> params) {
		return createType(factory.Type().createReference(typeQualifiedName), params);
	}

	/**
	 * Generates type following `newTypeRef` using this {@link Pattern} and provided `params`
	 *
	 * Note: the root of pattern element must be one or more types.
	 *
	 * @param newTypeRef the type reference which refers to future generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	public <T extends CtType<?>> T createType(CtTypeReference<?> newTypeRef, Map<String, Object> params) {
		CtPackage ownerPackage = newTypeRef.getFactory().Package().getOrCreate(newTypeRef.getPackage().getQualifiedName());
		return createType(ownerPackage, newTypeRef.getSimpleName(), params);
	}

	/**
	 * Generates type in the package `ownerPackage` with simple name `typeSimpleName` using this {@link Pattern} and provided `params`
	 *
	 * Note: the root of pattern element must be one or more types.
	 *
	 * @param ownerPackage the target package
	 * @param typeSimpleName the simple name of future generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	@SuppressWarnings("unchecked")
	public <T extends CtType<?>> T createType(CtPackage ownerPackage, String typeSimpleName, Map<String, Object> params) {
		@SuppressWarnings({ "rawtypes" })
		List<CtType> types = substitute(ownerPackage.getFactory(), CtType.class, new UnmodifiableParameterValueProvider(params,
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
		List<T> results = substitute(targetType.getFactory(), valueType, new UnmodifiableParameterValueProvider(params, PatternBuilder.TARGET_TYPE, targetType.getReference()));
		for (T result : results) {
			if (result instanceof CtTypeMember) {
				targetType.addTypeMember((CtTypeMember) result);
			}
		}
		return results;
	}

	/**
	 * Finds all target program sub-trees that correspond to a template
	 * and calls consumer.accept(Match)
	 * @param input the root of to be searched AST
	 * @param consumer the receiver of matches
	 */
	public void forEachMatch(Object input, CtConsumer<Match> consumer) {
		if (input == null) {
			return;
		}
		if (input.getClass().isArray()) {
			input = Arrays.asList((Object[]) input);
		}

		MatchingScanner scanner = new MatchingScanner(modelValueResolver, parameterValueProviderFactory, consumer);
		ParameterValueProvider parameters = parameterValueProviderFactory.createParameterValueProvider();
		if (input instanceof Collection<?>) {
			scanner.scan(null, (Collection<CtElement>) input);
		} else if (input instanceof Map) {
			scanner.scan(null, (Map<String, ?>) input);
		} else {
			scanner.scan(null, (CtElement) input);
		}
	}

	/**
	 * Finds all target program sub-trees that correspond to a template
	 * and returns them.
	 * @param root the root of to be searched AST. It can be a CtElement or List, Set, Map of CtElements
	 * @return List of {@link Match}
	 */
	public List<Match> getMatches(Object root) {
		List<Match> matches = new ArrayList<>();
		forEachMatch(root, match -> {
			matches.add(match);
		});
		return matches;
	}

	@Override
	public String toString() {
		return modelValueResolver.toString();
	}

	private static String getQualifiedName(CtPackage pckg, String simpleName) {
		if (pckg.isUnnamedPackage()) {
			return simpleName;
		}
		return pckg.getQualifiedName() + CtPackage.PACKAGE_SEPARATOR + simpleName;
	}

	public boolean isAddGeneratedBy() {
		return addGeneratedBy;
	}

	public Pattern setAddGeneratedBy(boolean addGeneratedBy) {
		this.addGeneratedBy = addGeneratedBy;
		return this;
	}
}
