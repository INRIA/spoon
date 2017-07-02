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
package spoon.template;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.support.template.Parameters;
import spoon.support.template.SubstitutionVisitor;

/**
 * Builds a template based on any existing type of the Spoon model.
 */
public class TemplateBuilder {

	private final Factory factory;
	/**
	 * The origin (not cloned) template type
	 */
	private CtType<?> template;

	/**
	 * The list of excluding/including fitlers which defines what CtTypeMembers of the origin template type has to be used by built template
	 */
	private List<FilterRequest> filterRequests = new ArrayList<>();

	private boolean includeInterfaces = true;
	private boolean includeSuperClass = true;

	/**
	 * substitution parameters of the template
	 */
	private Map<String, Object> parameters = new HashMap<>();
	
	private Set<String> toBeCleanedAnnotations = new HashSet<>();

	/**
	 * Empty exception.
	 */
	public TemplateBuilder(Factory factory) {
		this.factory = factory;
	}

	public CtType<?> getTemplate() {
		return template;
	}

	public TemplateBuilder setTemplate(CtType<?> template) {
		this.template = template;
		return this;
	}

	public TemplateBuilder setTemplate(Class<?> template) {
		CtType<?> ctTypeTemplate = factory.Type().get(template);
		if (ctTypeTemplate == null) {
			throw new SpoonException("Template class " + template.getName() + " is not part of spoon model.");
		}
		return setTemplate(ctTypeTemplate);
	}

	public TemplateBuilder setTemplate(Template template) {
		CtClass<? extends Template<?>> ctTypeTemplate = Substitution.getTemplateCtClass(factory, template);;
		setTemplate(ctTypeTemplate);
		//apply defaults of legacy templates
		putSubstitutionParameters(Parameters.getNamesToValues(template, ctTypeTemplate));
		excludeSuperClass();
		excludeAnnotatedBy(Local.class, Parameter.class);
		exclude(new Filter<CtField<?>>() {
			@Override
			public boolean matches(CtField<?> element) {
				return TemplateParameter.class.getName().equals(element.getType().getQualifiedName());
			}
		});
		return this;
	}

	public TemplateBuilder substituteName(String oldName, String newName) {
		parameters.put(oldName, newName);
		return this;
	}
	public TemplateBuilder substituteClass(Class<?> clazz, CtTypeReference<?> typeRef) {
		return substituteClass(clazz.getSimpleName(), typeRef);
	}
	public TemplateBuilder substituteClass(CtType<?> type, CtTypeReference<?> typeRef) {
		return substituteClass(type.getSimpleName(), typeRef);
	}
	public TemplateBuilder substituteClass(String typeSimpleName, CtTypeReference<?> typeRef) {
		parameters.put(typeSimpleName, typeRef);
		return this;
	}
	public TemplateBuilder substituteTemplateParameter(String parameterName, CtCodeElement codeElement) {
		parameters.put(parameterName, codeElement);
		return this;
	}
	public TemplateBuilder substituteNamedElement(String elementName, CtCodeElement codeElement) {
		parameters.put(elementName, codeElement);
		return this;
	}
	public Map<String, Object> getSubstitutionParameters() {
		return Collections.unmodifiableMap(parameters);
	}
	public TemplateBuilder putSubstitutionParameters(Map<String, Object> parameters) {
		this.parameters.putAll(parameters);
		return this;
	}

	/**
	 * @param typeMemberName
	 * 		names of type members which has to be included. If empty, then all type members are included
	 */
	public TemplateBuilder includeAll(String... typeMemberName) {
		filterRequests.add(new FilterRequest(true, new TypeMemberFilter(null, typeMemberName)));
		return this;
	}
	/**
	 * @param filter
	 * 		includes all type members which matches the filter
	 */
	public TemplateBuilder include(Filter<? extends CtTypeMember> filter) {
		filterRequests.add(new FilterRequest(true, filter));
		return this;
	}
	/**
	 * @param fieldName
	 * 		names of fields which has to be included. If empty, then all fields are included
	 */
	public TemplateBuilder includeFields(String... fieldName) {
		filterRequests.add(new FilterRequest(true, new TypeMemberFilter(CtField.class, fieldName)));
		return this;
	}
	/**
	 * includes all constructors
	 */
	public TemplateBuilder includeConstructors() {
		filterRequests.add(new FilterRequest(true, new TypeMemberFilter(CtConstructor.class, null)));
		return this;
	}
	/**
	 * @param methodName
	 * 		names of methods which has to be included. If empty, then all methods are included
	 */
	public TemplateBuilder includeMethods(String... methodName) {
		filterRequests.add(new FilterRequest(true, new TypeMemberFilter(CtMethod.class, methodName)));
		return this;
	}
	/**
	 * @param typeName
	 * 		names of types which has to be included. If empty, then all types are included
	 */
	public TemplateBuilder includeInnerTypes(String... typeName) {
		filterRequests.add(new FilterRequest(true, new TypeMemberFilter(CtType.class, typeName)));
		return this;
	}
	/**
	 * @param typeMemberTypes
	 * 		classes of to be included type member types
	 * @param typeMemberName
	 * 		names of type members which has to be included. If empty, then all type members are included
	 */
	public TemplateBuilder includeAllTypeMembers(Class<?>[] typeMemberTypes, String... typeMemberName) {
		for (Class<?> type : typeMemberTypes) {
			filterRequests.add(new FilterRequest(true, new TypeMemberFilter(type, typeMemberName)));
		}
		return this;
	}

	public TemplateBuilder includeAnnotatedBy(Class<Annotation>... annotationClass) {
		for (Class<Annotation> annotation : annotationClass) {
			filterRequests.add(new FilterRequest(true, new AnnotationFilter<>(annotation)));
		}
		return this;
	}

	/**
	 * Assures that interfaces of the template will be used by templating.
	 * They are by default used
	 */
	public TemplateBuilder includeInterfaces() {
		includeInterfaces = true;
		return this;
	}

	/**
	 * Assures that super class of the template will be used by templating
	 * it is by default used
	 */
	public TemplateBuilder includeSuperClass() {
		includeSuperClass = true;
		return this;
	}

	/**
	 * @param typeMemberName
	 * 		names of type members which has to be excluded. If empty, then all type members are excluded
	 */
	public TemplateBuilder excludeAll(String... typeMemberName) {
		if (typeMemberName.length == 0) {
			includeInterfaces = false;
			includeSuperClass = false;
		}
		filterRequests.add(new FilterRequest(false, new TypeMemberFilter(null, typeMemberName)));
		return this;
	}
	/**
	 * @param filter
	 * 		excludes all type members which matches the filter
	 */
	public TemplateBuilder exclude(Filter<? extends CtTypeMember> filter) {
		filterRequests.add(new FilterRequest(false, filter));
		return this;
	}
	/**
	 * @param fieldName
	 * 		names of fields which has to be excluded. If empty, then all fields are excluded
	 */
	public TemplateBuilder excludeFields(String... fieldName) {
		filterRequests.add(new FilterRequest(false, new TypeMemberFilter(CtField.class, fieldName)));
		return this;
	}
	/**
	 * excludes all constructors
	 */
	public TemplateBuilder excludeConstructors() {
		filterRequests.add(new FilterRequest(false, new TypeMemberFilter(CtConstructor.class, null)));
		return this;
	}
	/**
	 * @param methodName
	 * 		names of methods which has to be excluded. If empty, then all methods are excluded
	 */
	public TemplateBuilder excludeMethods(String... methodName) {
		filterRequests.add(new FilterRequest(false, new TypeMemberFilter(CtMethod.class, methodName)));
		return this;
	}
	/**
	 * @param typeName
	 * 		names of types which has to be excluded. If empty, then all types are excluded
	 */
	public TemplateBuilder excludeInnerTypes(String... typeName) {
		filterRequests.add(new FilterRequest(false, new TypeMemberFilter(CtType.class, typeName)));
		return this;
	}
	/**
	 * @param typeMemberTypes
	 * 		classes of to be excluded type member types
	 * @param typeMemberName
	 * 		names of type members which has to be excluded. If empty, then all type members are excluded
	 */
	public TemplateBuilder excludeAllTypeMembers(Class<?>[] typeMemberTypes, String... typeMemberName) {
		for (Class<?> type : typeMemberTypes) {
			filterRequests.add(new FilterRequest(false, new TypeMemberFilter(type, typeMemberName)));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public TemplateBuilder excludeAnnotatedBy(Class<?>... annotationClass) {
		for (Class<?> annotation : annotationClass) {
			filterRequests.add(new FilterRequest(false, new AnnotationFilter<>((Class<? extends Annotation>) annotation)));
		}
		return this;
	}

	/**
	 * Assures that interfaces of the Template will be not used
	 */
	public TemplateBuilder excludeInterfaces() {
		includeInterfaces = false;
		return this;
	}

	/**
	 * Assures that super class of the Template will be not used
	 */
	public TemplateBuilder excludeSuperClass() {
		includeSuperClass = false;
		return this;
	}

	/**
	 * Creates new type by cloning of filtered template and substituting of parameters.
	 * @param qualifiedTypeName
	 * 		the qualified name of the newly created type
	 * @return the created type
	 */
	public <T extends CtType<?>> T createType(String qualifiedTypeName) {
		CtType<?> clonedTemplate = cloneAndFilterTemplate();
		CtTypeReference<T> targetTypeRef = factory.Type().createReference(qualifiedTypeName);
		CtPackage targetPackage = factory.Package().getOrCreate(targetTypeRef.getPackage().getSimpleName());
		substituteClass(clonedTemplate, targetTypeRef);
		List<CtType<?>> generated = new SubstitutionVisitor(factory, parameters).substitute(clonedTemplate);
		for (CtType<?> ctType : generated) {
			targetPackage.addType(ctType);
		}
		return (T) targetTypeRef.getTypeDeclaration();
	}

	/**
	 * @param target the {@link CtType}, which will get substituted superClass, superInterfaces, and type members of the template
	 * @return list of add type members. The applied superClass and superInterfaces (if any) are not listed there.
	 */
	public List<CtTypeMember> applyToType(CtType<?> target) {
		CtType<?> clonedTemplate = cloneAndFilterTemplate();
		substituteClass(clonedTemplate, target.getReference());
		List<CtType<?>> generated = new SubstitutionVisitor(factory, parameters).substitute(clonedTemplate);
		if (generated.size() > 1) {
			throw new SpoonException("Unexpected count of generated types");
		}
		CtType<?> substitutedType = generated.get(0);
		List<CtTypeMember> newTypeMembers = new ArrayList<>(substitutedType.getTypeMembers());
		for (CtTypeMember typeMember : newTypeMembers) {
			target.addTypeMember(typeMember);
		}
		if (includeSuperClass && clonedTemplate.getSuperclass() != null) {
			target.setSuperclass(clonedTemplate.getSuperclass());
		}
		if (includeInterfaces) {
			for (CtTypeReference<?> iface : clonedTemplate.getSuperInterfaces()) {
				target.addSuperInterface(iface);
			}
		}
		return newTypeMembers;
	}

	private CtType<?> cloneAndFilterTemplate() {
		CtType<?> clonedTemplate = this.template.clone();
		//set the parent package (but do not add it into that package) so "Generated by ..." knows it 
		clonedTemplate.setParent(this.template.getParent());
		//filter type members. Copy the list, because the origin will be modified later
		List<CtTypeMember> allTypeMembers = new ArrayList<>(clonedTemplate.getTypeMembers());
		Set<CtTypeMember> includedTypeMembers = Collections.newSetFromMap(new IdentityHashMap<>(allTypeMembers.size()));
		//by default all members are included
		includedTypeMembers.addAll(allTypeMembers);
		//apply all defined type member filters
		for (FilterRequest filterRequest : filterRequests) {
			for (CtTypeMember ctTypeMember : allTypeMembers) {
				if (filterRequest.filter.matches(ctTypeMember)) {
					if (filterRequest.including) {
						//include
						includedTypeMembers.add(ctTypeMember);
					} else {
						//exclude
						includedTypeMembers.remove(ctTypeMember);
					}
				}
			}
		}
		//delete all unwanted type members of clonedTemplate
		for (CtTypeMember ctTypeMember : allTypeMembers) {
			if (includedTypeMembers.contains(ctTypeMember) == false) {
				//member should be removed
				ctTypeMember.delete();
			} else {
				//member should be kept. 
				if(toBeCleanedAnnotations.size()>0) {
					//clean unwanted annotations
					for (CtAnnotation<? extends Annotation> l_ann : new ArrayList<>(ctTypeMember.getAnnotations()))
					{
						if(toBeCleanedAnnotations.contains(l_ann.getAnnotationType().getQualifiedName())) {
							l_ann.delete();
						}
					}
				}
			}
		}
		if (includeSuperClass == false) {
			clonedTemplate.setSuperclass(null);
		}
		if (includeInterfaces == false) {
			clonedTemplate.setSuperInterfaces(Collections.emptySet());
		}
		return clonedTemplate;
	}

	private static class TypeMemberFilter implements Filter<CtTypeMember> {

		private Class<?> memberType;
		private String[] memberNames;

		private TypeMemberFilter(Class memberType, String[] memberNames) {
			if (memberNames != null && memberNames.length == 0) {
				//ignore name
				memberNames = null;
			}
			this.memberType = memberType;
			this.memberNames = memberNames;
		}

		@Override
		public boolean matches(CtTypeMember element) {
			if (memberType != null) {
				if (memberType.isInstance(element) == false) {
					return false;
				}
			}
			if (memberNames != null) {
				boolean matches = false;
				String memberName = element.getSimpleName();
				for (String name : memberNames) {
					if (name.equals(memberName)) {
						matches = true;
						break;
					}
				}
				if (matches == false) {
					return false;
				}
			}
			return true;
		}
	}

	private static class FilterRequest {
		//true - including, false - excluding
		private boolean including;
		private Filter<CtTypeMember> filter;

		@SuppressWarnings("unchecked")
		private FilterRequest(boolean including, Filter<? extends CtTypeMember> filter) {
			super();
			this.including = including;
			this.filter = (Filter<CtTypeMember>) filter;
		}
	}

	public TemplateBuilder cleanAnnotation(Class<? extends Annotation> p_class)
	{
		toBeCleanedAnnotations.add(p_class.getName());
		return this;
	}

	/**
	 * Includes all type members annotated by {@link Local} with value containing `localName`
	 * @param localName
	 * 		required value of {@link Local} annotation
	 */
	public TemplateBuilder includeTaggedBy(String localName)
	{
		include(new Filter<CtTypeMember>(){
			@Override
			public boolean matches(CtTypeMember typeMember)
			{
				Tag local = typeMember.getAnnotation(Tag.class);
				if (local != null) {
					for (String value : local.value()) {
						if(localName.equals(value)) {
							return true;
						}
					}
				}
				return false;
			}
		});
		cleanAnnotation(Tag.class);
		return this;
	}
}
