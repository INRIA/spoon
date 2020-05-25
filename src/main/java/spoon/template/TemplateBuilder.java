/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import spoon.SpoonException;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilderHelper;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.template.Parameters;
import spoon.support.util.ImmutableMapImpl;

/**
 * Internal class used to provide pattern-based implementation of Template and TemplateMatcher
 */
class TemplateBuilder {

	/**
	 * Creates a {@link TemplateBuilder}, which builds {@link Pattern} from {@link Template}
	 * @param templateRoot the root element of {@link Template} model
	 * @param template a instance of the {@link Template}. It is needed here,
	 * 			because parameter value types influences which AST nodes will be the target of substitution
	 * @return {@link TemplateBuilder}
	 */
	public static TemplateBuilder createPattern(CtElement templateRoot, Template<?> template) {
		CtClass<? extends Template<?>> templateType = Substitution.getTemplateCtClass(templateRoot.getFactory(), template);
		return createPattern(templateRoot, templateType, template);
	}
	//needed to provide access to protected members
	private static class PatternBuilder extends spoon.pattern.PatternBuilder {

		PatternBuilder(List<CtElement> template) {
			super(template);
		}

		ListOfNodes getListOfNodes() {
			return new ListOfNodes(patternNodes.getNodes());
		}
	}

	/**
	 * Creates a {@link TemplateBuilder}, which builds {@link Pattern} from {@link Template}
	 * @param templateRoot the root element of {@link Template} model
	 * @param templateType {@link CtClass} model of `template`
	 * @param template a instance of the {@link Template}. It is needed here,
	 * 			because parameter value types influences which AST nodes will be the target of substitution
	 * @return
	 */
	public static TemplateBuilder createPattern(CtElement templateRoot, CtClass<?> templateType, Template<?> template) {
		Factory f = templateRoot.getFactory();

		if (template != null && templateType.getQualifiedName().equals(template.getClass().getName()) == false) {
			throw new SpoonException("Unexpected template instance " + template.getClass().getName() + ". Expects " + templateType.getQualifiedName());
		}

		PatternBuilder pb;

		@SuppressWarnings("rawtypes")
		CtTypeReference<TemplateParameter> templateParamRef = f.Type().createReference(TemplateParameter.class);
		if (templateType == templateRoot) {
			//templateRoot is a class which extends from Template. We have to remove all Templating stuff from the patter model
			PatternBuilderHelper tv = new PatternBuilderHelper(templateType);
			{
				tv.keepTypeMembers(typeMember -> {
					if (typeMember.getAnnotation(Parameter.class) != null) {
						//remove all type members annotated with @Parameter
						return false;
					}
					if (typeMember.getAnnotation(Local.class) != null) {
						//remove all type members annotated with @Local
						return false;
					}
					//remove all Fields of type TemplateParameter
					return !(typeMember instanceof CtField<?>) || !((CtField<?>) typeMember).getType().isSubtypeOf(templateParamRef);
				});
				//remove `... extends Template`, which doesn't have to be part of pattern model
				tv.removeSuperClass();
			}
			pb = new PatternBuilder(tv.getPatternElements());
		} else {
			pb = new PatternBuilder(Collections.singletonList(templateRoot));
		}
		Map<String, Object> templateParameters = template == null ? null : Parameters.getTemplateParametersAsMap(f, null, template);
		//legacy templates always automatically simplifies generated code
		pb.setAutoSimplifySubstitutions(true);
		pb.configurePatternParameters(pc -> {
			pc.byTemplateParameter(templateParameters);
			pc.byParameterValues(templateParameters);
		});

		return new TemplateBuilder(templateType, pb, template);
	}

	private Template<?> template;
	private PatternBuilder patternBuilder;
	private CtClass<?> templateType;

	private TemplateBuilder(CtClass<?> templateType, PatternBuilder patternBuilder, Template<?> template) {
		this.template = template;
		this.patternBuilder = patternBuilder;
		this.templateType = templateType;
	}

	/**
	 * @return a {@link Pattern} built by this {@link TemplateBuilder}
	 */
	public Pattern build() {
		return patternBuilder.build();
	}

	Pattern build(Consumer<ListOfNodes> nodes) {
		nodes.accept(patternBuilder.getListOfNodes());
		return build();
	}

	/**
	 * @param addGeneratedBy true if "generated by" comments has to be added into code generated by {@link Pattern} made by this {@link TemplateBuilder}
	 * @return this to support fluent API
	 */
	public TemplateBuilder setAddGeneratedBy(boolean addGeneratedBy) {
		patternBuilder.setAddGeneratedBy(addGeneratedBy);
		return this;
	}

	/**
	 * @return Map of template parameters from `template`
	 */
	public Map<String, Object> getTemplateParameters() {
		return getTemplateParameters(null);
	}
	/**
	 * @param targetType the type which will receive the model generated using returned parameters
	 * @return Map of template parameters from `template`
	 */
	public Map<String, Object> getTemplateParameters(CtType<?> targetType) {
		Factory f = templateType.getFactory();
		return Parameters.getTemplateParametersAsMap(f, targetType, template);
	}

	/**
	 * generates a new AST node made by cloning of `patternModel` and by substitution of parameters by their values
	 * @param targetType the CtType, which will receive the result of substitution
	 * @return a substituted element
	 */
	public <T extends CtElement> T substituteSingle(CtType<?> targetType, Class<T> itemType) {
		return build().generator().generate(itemType, new ImmutableMapImpl(getTemplateParameters(targetType))).get(0);
	}
	/**
	 * generates a new AST nodes made by cloning of `patternModel` and by substitution of parameters by their values
	 * @param factory TODO
	 * @param targetType the CtType, which will receive the result of substitution
	 * @return List of substituted elements
	 */
	public <T extends CtElement> List<T> substituteList(Factory factory, CtType<?> targetType, Class<T> itemType) {
		return build().generator().generate(itemType, getTemplateParameters(targetType));
	}
}
