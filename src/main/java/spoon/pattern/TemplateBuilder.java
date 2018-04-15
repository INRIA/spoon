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

import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.template.Parameters;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Substitution;
import spoon.template.Template;
import spoon.template.TemplateParameter;

/**
 * The builder which creates a {@link Pattern} from the {@link Template}
 */
public class TemplateBuilder {

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
			TemplateModelBuilder tv = new TemplateModelBuilder(templateType);
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
					if (typeMember instanceof CtField<?> && ((CtField<?>) typeMember).getType().isSubtypeOf(templateParamRef)) {
						return false;
					}
					//all other type members have to be part of the pattern model
					return true;
				});
				//remove `... extends Template`, which doesn't have to be part of pattern model
				tv.removeSuperClass();
			};
			pb = PatternBuilder.create(templateType.getReference(), tv.getTemplateModels());
		} else {
			pb = PatternBuilder.create(templateType.getReference(), templateRoot);
		}
		Map<String, Object> templateParameters = template == null ? null : Parameters.getTemplateParametersAsMap(f, null, template);
		//legacy templates always automatically simplifies generated code
		pb.setAutoSimplifySubstitutions(true);
		pb.configureTemplateParameters(templateParameters);
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
		return build().substituteSingle(targetType.getFactory(), itemType, getTemplateParameters(targetType));
	}
	/**
	 * generates a new AST nodes made by cloning of `patternModel` and by substitution of parameters by their values
	 * @param factory TODO
	 * @param targetType the CtType, which will receive the result of substitution
	 * @return List of substituted elements
	 */
	public <T extends CtElement> List<T> substituteList(Factory factory, CtType<?> targetType, Class<T> itemType) {
		return build().substituteList(factory, itemType, getTemplateParameters(targetType));
	}
}
