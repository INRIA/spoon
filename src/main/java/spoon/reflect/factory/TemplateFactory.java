/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.factory;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.template.Template;
import spoon.template.TemplateException;

/**
 * A factory for storing, accessing and creating templates and associated types.
 */
public class TemplateFactory extends SubFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new template factory.
	 * 
	 * @param factory
	 *            the parent factory
	 */
	public TemplateFactory(Factory factory) {
		super(factory);
	}

	private Map<String, CtClass<?>> templates = new TreeMap<String, CtClass<?>>();

	/**
	 * Adds a template to this factory.
	 * 
	 * @param template
	 *            the template class
	 */
	public void add(CtClass<?> template) {
		if (templates.containsKey(template.getQualifiedName()))
			templates.remove(template.getQualifiedName());
		templates.put(template.getQualifiedName(), template);
		new CtScanner() {
			public void enter(CtElement e) {
				e.setFactory(factory);
				super.enter(e);
			}

			@Override
			protected void enterReference(CtReference e) {
				e.setFactory(factory);
				super.enterReference(e);
			}
		}.scan(template);
	}

	/**
	 * Gets all the templates in this factory.
	 * 
	 * @return the stored templates as a map
	 */
	public Map<String, CtClass<?>> getAll() {
		return templates;
	}

	/**
	 * Gets a template CT class from its actual class.
	 * 
	 * @param templateClass
	 *            the runtime class
	 * @return the compile-time class
	 */
	public <T> CtClass<T> get(Class<?> templateClass) {
		return get(templateClass.getName());
	}

	/**
	 * Gets a template class from its qualified name.
	 * 
	 * @param templateName
	 *            the template's fully qualified name
	 * @return the template class
	 */
	@SuppressWarnings("unchecked")
	public <T> CtClass<T> get(String templateName) {
		if (!templates.containsKey(templateName))
			throw new TemplateException("Unable to load template \""
					+ templateName + "\". Check your template source path.");
		return (CtClass<T>) templates.get(templateName);
	}

	/**
	 * Look for template classes in the parent factory and add them to this
	 * factory.
	 */
	@SuppressWarnings("unchecked")
	public void parseTypes() {
		for (CtSimpleType<?> t : factory.Type().getAll()) {
			if (t instanceof CtClass) {
				for (CtSimpleType nested : ((CtType<?>) t).getNestedTypes()) {
					if (nested instanceof CtClass)
						scanType((CtClass<? extends Template>) nested);
				}
				scanType((CtClass<? extends Template>) t);
			}
		}
	}

	private void scanType(CtClass<? extends Template> t) {
		if (factory.Type().createReference(Template.class).isAssignableFrom(
				t.getReference())) {
			add(t);
		}
	}

	/**
	 * Helper method to know if a given type reference points to a Template or
	 * not
	 * 
	 * @param candidate
	 *            the reference to check
	 * 
	 * @return <code> true </code> if the reference points to a template type
	 */
	public boolean isTemplate(CtTypeReference<?> candidate) {
		return templates.containsKey(candidate.getQualifiedName());
	}
}