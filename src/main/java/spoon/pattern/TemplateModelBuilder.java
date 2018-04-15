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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

/**
 * Utility class to select parts of AST to be used as a model of a {@link Pattern}.
 */
public class TemplateModelBuilder {
	/**
	 * The original type, which contains the AST of pattern model
	 */
	private final CtType<?> templateType;
	/**
	 * optional clone of templateType. It is created when AST of CtType has to be modified
	 * before it can become a model of {@link Pattern}
	 */
	private CtType<?> clonedTemplateType;
	/**
	 * holds the built pattern model
	 */
	private List<CtElement> templateModel = null;

	public TemplateModelBuilder(CtType<?> templateTemplate) {
		this.templateType = templateTemplate;
	}

	/**
	 * Returns clone of the templateType.
	 * The clone is done only once. Later calls returns cached clone.
	 * @return
	 */
	private CtType<?> getClonedTemplateType() {
		if (clonedTemplateType == null) {
			clonedTemplateType = templateType.clone();
			if (templateType.isParentInitialized()) {
				//set parent package, to keep origin qualified name of the Template. It is needed for correct substitution of Template name by target type reference
				clonedTemplateType.setParent(templateType.getParent());
			}
		}
		return clonedTemplateType;
	}

	/**
	 * Sets a template model from {@link CtTypeMember} of a template type
	 * @param typeMemberName the name of the {@link CtTypeMember} of a template type
	 */
	public TemplateModelBuilder setTypeMember(String typeMemberName) {
		setTypeMember(tm -> typeMemberName.equals(tm.getSimpleName()));
		return this;
	}
	/**
	 * Sets a template model from {@link CtTypeMember} of a template type
	 * @param filter the {@link Filter} whose match defines to be used {@link CtTypeMember}
	 */
	public TemplateModelBuilder setTypeMember(Filter<CtTypeMember> filter) {
		setTemplateModel(getByFilter(filter));
		return this;
	}

	/**
	 * removes all annotations of type defined by `classes` from the clone of the source {@link CtType}
	 * @param classes list of classes which defines types of to be removed annotations
	 * @return this to support fluent API
	 */
	public TemplateModelBuilder removeTag(Class... classes) {
		List<CtElement> elements = getClonedTemplateModel();
		for (Class class1 : classes) {
			for (CtElement element : elements) {
				CtAnnotation<?> annotation = element.getAnnotation(element.getFactory().Type().createReference(class1));
				if (annotation != null) {
					element.removeAnnotation(annotation);
				}
			}
		}
		return this;
	}

	private List<CtElement> getClonedTemplateModel() {
		if (templateModel == null) {
			throw new SpoonException("Template model is not defined yet");
		}
		for (ListIterator<CtElement> iter = templateModel.listIterator(); iter.hasNext();) {
			CtElement ele = iter.next();
			if (ele.getRoleInParent() != null) {
				iter.set(ele.clone());
			}
		}
		return templateModel;
	}

	/**
	 * Sets a template model from body of the method of template type
	 * @param methodName the name of {@link CtMethod}
	 */
	public TemplateModelBuilder setBodyOfMethod(String methodName) {
		setBodyOfMethod(tm -> methodName.equals(tm.getSimpleName()));
		return this;
	}
	/**
	 * Sets a template model from body of the method of template type selected by filter
	 * @param filter the {@link Filter} whose match defines to be used {@link CtMethod}
	 */
	public void setBodyOfMethod(Filter<CtMethod<?>> filter) {
		CtBlock<?> body =  getOneByFilter(filter).getBody();
		setTemplateModel(body.getStatements());
	}

	/**
	 * Sets a template model from return expression of the method of template type selected by filter
	 * @param methodName the name of {@link CtMethod}
	 */
	public void setReturnExpressionOfMethod(String methodName) {
		setReturnExpressionOfMethod(tm -> methodName.equals(tm.getSimpleName()));
	}
	/**
	 * Sets a template model from return expression of the method of template type selected by filter
	 * @param filter the {@link Filter} whose match defines to be used {@link CtExecutable}
	 */
	public void setReturnExpressionOfMethod(Filter<CtMethod<?>> filter) {
		CtMethod<?> method = getOneByFilter(filter);
		CtBlock<?> body = method.getBody();
		if (body.getStatements().size() != 1) {
			throw new SpoonException("The body of " + method.getSignature() + " must contain exactly one statement. But there is:\n" + body.toString());
		}
		CtStatement firstStatement = body.getStatements().get(0);
		if (firstStatement instanceof CtReturn<?> == false) {
			throw new SpoonException("The body of " + method.getSignature() + " must contain return statement. But there is:\n" + body.toString());
		}
		setTemplateModel(((CtReturn<?>) firstStatement).getReturnedExpression());
	}

	private <T extends CtElement> List<T> getByFilter(Filter<T> filter) {
		List<T> elements = templateType.filterChildren(filter).list();
		if (elements == null || elements.isEmpty()) {
			throw new SpoonException("Element not found in " + templateType.getShortRepresentation());
		}
		return elements;
	}
	private <T extends CtElement> T getOneByFilter(Filter<T> filter) {
		List<T> elements = getByFilter(filter);
		if (elements.size() != 1) {
			throw new SpoonException("Only one element must be selected, but there are: " + elements);
		}
		return elements.get(0);
	}
	/**
	 * @param filter whose matches will be removed from the template model
	 */
	public TemplateModelBuilder removeTypeMembers(Filter<CtTypeMember> filter) {
		for (CtTypeMember ctTypeMember : new ArrayList<>(getClonedTemplateType().getTypeMembers())) {
			if (filter.matches(ctTypeMember)) {
				ctTypeMember.delete();
			}
		}
		return this;
	}

	/**
	 * Removes all type members which are annotated by `annotationClass`
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TemplateModelBuilder removeTypeMembersAnnotatedBy(Class<?>... annotationClass) {
		for (Class<?> ac : annotationClass) {
			removeTypeMembers(tm -> tm.getAnnotation((Class) ac) != null);
		}
		return this;
	}

	/**
	 * @param filter whose matches will be kept in the template. All others will be removed
	 */
	public TemplateModelBuilder keepTypeMembers(Filter<? super CtElement> filter) {
		for (CtTypeMember ctTypeMember : new ArrayList<>(getClonedTemplateType().getTypeMembers())) {
			if (filter.matches(ctTypeMember) == false) {
				ctTypeMember.delete();
			}
		}
		return this;
	}

	/**
	 * Keeps only type members, which are annotated by `annotationClass`. All others will be removed
	 */
	public TemplateModelBuilder keepTypeMembersAnnotatedBy(Class<? extends Annotation> annotationClass) {
		keepTypeMembers(tm -> tm.getAnnotation(annotationClass) != null);
		return this;
	}

	/**
	 * removes super class from the template
	 */
	public TemplateModelBuilder removeSuperClass() {
		getClonedTemplateType().setSuperclass(null);
		return this;
	}

	/**
	 * @param filter super interfaces which matches the filter will be removed
	 */
	public TemplateModelBuilder removeSuperInterfaces(Filter<CtTypeReference<?>> filter) {
		Set<CtTypeReference<?>> superIfaces = new HashSet<>(getClonedTemplateType().getSuperInterfaces());
		boolean changed = false;
		for (Iterator<CtTypeReference<?>> iter = superIfaces.iterator(); iter.hasNext();) {
			if (filter.matches(iter.next())) {
				iter.remove();
				changed = true;
			}
		}
		if (changed) {
			getClonedTemplateType().setSuperInterfaces(superIfaces);
		}
		return this;
	}

	/**
	 * @param filter super interfaces which matches the filter will be kept. Others will be removed
	 */
	public TemplateModelBuilder keepSuperInterfaces(Filter<CtTypeReference<?>> filter) {
		Set<CtTypeReference<?>> superIfaces = new HashSet<>(getClonedTemplateType().getSuperInterfaces());
		boolean changed = false;
		for (Iterator<CtTypeReference<?>> iter = superIfaces.iterator(); iter.hasNext();) {
			if (filter.matches(iter.next())) {
				iter.remove();
				changed = true;
			}
		}
		if (changed) {
			getClonedTemplateType().setSuperInterfaces(superIfaces);
		}
		return this;
	}

	/**
	 * @return a List of {@link CtElement}s, which has to be used as pattern model
	 */
	public List<CtElement> getTemplateModels() {
		return templateModel;
	}

	/**
	 * @param template a {@link CtElement}, which has to be used as pattern model
	 */
	public void setTemplateModel(CtElement template) {
		this.templateModel = Collections.singletonList(template);
	}

	/**
	 * @param template a List of {@link CtElement}s, which has to be used as pattern model
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setTemplateModel(List<? extends CtElement> template) {
		this.templateModel = (List) template;
	}
}
