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
package spoon.support.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.VariableAccessFilter;
import spoon.template.Local;
import spoon.template.Template;
import spoon.template.TemplateParameter;

class DoNotFurtherTemplateThisElement extends SpoonException {
	private static final long serialVersionUID = 1L;

	Object skipped;

	DoNotFurtherTemplateThisElement(Object e) {
		super("skipping " + e.toString());
		skipped = e;
	}

}

/**
 * This visitor implements the substitution engine of Spoon templates.
 */
public class SubstitutionVisitor extends CtScanner {

	public class InheritanceSustitutionScanner extends CtInheritanceScanner {

		SubstitutionVisitor parent = null;

		public InheritanceSustitutionScanner(SubstitutionVisitor parent) {
			this.parent = parent;
		}

		/**
		 * Replaces method parameters when defined as a list of
		 * {@link CtParameter}.
		 */
		@Override
		public <R> void scanCtExecutable(CtExecutable<R> e) {
			// replace method parameters
			List<CtParameter<?>> substitutedParams = new ArrayList<>(e.getParameters().size());
			boolean wasChanged = false;
			for (CtParameter<?> parameter : e.getParameters()) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				List<CtParameter<?>> list = (List) getParameterValueAsList(CtParameter.class, getParameterValue(parameter.getSimpleName()));
				if (list == null) {
					//it is normal parameter, keep it.
					substitutedParams.add(parameter);
				} else {
					wasChanged = true;
					//current parameter has to be substituted by a template parameter value
					substitutedParams.addAll(list);
				}
			}
			if (wasChanged) {
				e.setParameters(substitutedParams);
			}
			super.scanCtExecutable(e);
		}

		/**
		 * Remove template-specific {@link Local} annotations.
		 */
		@Override
		public void scanCtElement(CtElement e) {
			CtAnnotation<?> a = e.getAnnotation(e.getFactory().Type().createReference(Local.class));
			if (a != null) {
				e.removeAnnotation(a);
			}
			super.scanCtElement(e);
		}

		/**
		 * Replaces parameters in element names (even if detected as a
		 * substring).
		 */
		@Override
		public void scanCtNamedElement(CtNamedElement element) {
			if (element.getDocComment() != null) {
				element.setDocComment(substituteName(element.getDocComment()));
			}
			// replace parameters in names
			element.setSimpleName(substituteName(element.getSimpleName()));
			super.scanCtNamedElement(element);
		}

		@Override
		public void scanCtReference(CtReference reference) {
			reference.setSimpleName(substituteName(reference.getSimpleName()));
			super.scanCtReference(reference);
		}

		private String substituteName(String name) {
			for (Map.Entry<String, Object> e : namesToValues.entrySet()) {
				String pname = e.getKey();
				if (name.contains(pname)) {
					String value = getParameterValueAsString(e.getValue());
					name = name.replace(pname, value);
				}
			}
			return name;
		}

		/** statically inline foreach */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void visitCtForEach(CtForEach foreach) {
			if (foreach.getExpression() instanceof CtFieldAccess) {
				CtFieldAccess<?> fa = (CtFieldAccess<?>) foreach.getExpression();
				Object value = getParameterValue(fa.getVariable().getSimpleName());
				if (value != null && Parameters.isParameterSource(fa.getVariable())) {
					List<CtExpression> list = getParameterValueAsList(CtExpression.class, value);
					CtBlock<?> l = foreach.getFactory().Core().createBlock();
					CtStatement body = foreach.getBody();
					for (CtExpression element : list) {
						CtStatement b = body.clone();
						for (CtVariableAccess<?> va : Query.getElements(b, new VariableAccessFilter<>(foreach.getVariable().getReference()))) {
							va.replace(element);
						}
						if (b instanceof CtBlock && ((CtBlock) b).getStatements().size() == 1) {
							b = ((CtBlock) b).getStatement(0);
						}
						l.addStatement(b);
					}
					replace(foreach, l);
					throw new DoNotFurtherTemplateThisElement(foreach);
				}
			}
			super.visitCtForEach(foreach);
		}

		@Override
		public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
			visitFieldAccess(fieldRead);
		}

		@Override
		public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
			visitFieldAccess(fieldWrite);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private <T> void visitFieldAccess(final CtFieldAccess<T> fieldAccess) {
			CtFieldReference<?> ref = fieldAccess.getVariable();
			if ("length".equals(ref.getSimpleName())) {
				if (fieldAccess.getTarget() instanceof CtFieldAccess) {
					ref = ((CtFieldAccess<?>) fieldAccess.getTarget()).getVariable();
					Object value = getParameterValue(ref.getSimpleName());
					if (value != null && Parameters.isParameterSource(ref)) {
						//the items of this list are not cloned
						List<Object> list = getParameterValueAsList(Object.class, value);
						replace(fieldAccess, (CtExpression) fieldAccess.getFactory().Code().createLiteral(list.size()));
						throw new DoNotFurtherTemplateThisElement(fieldAccess);
					}
				}
			}
			Object v = getParameterValue(ref.getSimpleName());
			if (v != null && Parameters.isParameterSource(ref)) {
				// replace direct field parameter accesses
				Object value = getParameterValueAtIndex(Object.class, v, Parameters.getIndex(fieldAccess));
				CtExpression toReplace = fieldAccess;
				if (fieldAccess.getParent() instanceof CtArrayAccess) {
					toReplace = (CtExpression) fieldAccess.getParent();
				}
				if (!(value instanceof TemplateParameter)) {
					if (value instanceof Class) {
						replace(toReplace, factory.Code()
								.createClassAccess(factory.Type().createReference(((Class<?>) value).getName())));
					} else if (value instanceof Enum) {
						CtTypeReference<?> enumType = factory.Type().createReference(value.getClass());
						replace(toReplace, factory.Code().createVariableRead(
								factory.Field().createReference(enumType, enumType, ((Enum<?>) value).name()), true));
					} else if (value instanceof List) {
						// replace list of CtParameter for generic access to the
						// parameters
						List<CtParameter<?>> l = (List<CtParameter<?>>) value;
						List<CtExpression<?>> vas = factory.Code().createVariableReads(l);
						CtAbstractInvocation<?> inv = (CtAbstractInvocation<?>) fieldAccess.getParent();
						int i = inv.getArguments().indexOf(fieldAccess);
						inv.getArguments().remove(i);
						inv.getExecutable().getActualTypeArguments().remove(i);
						for (CtExpression<?> va : vas) {
							va.setParent(fieldAccess.getParent());
							inv.getArguments().add(i, va);
							inv.getExecutable().getActualTypeArguments().add(i, va.getType());
							i++;
						}
					} else if ((value != null) && value.getClass().isArray()) {
						replace(toReplace, factory.Code().createLiteralArray((Object[]) value));
					} else {
						replace(toReplace, factory.Code().createLiteral(value));
					}
				} else {
					replace(toReplace, toReplace.clone());
				}
				// do not visit if replaced
				throw new DoNotFurtherTemplateThisElement(fieldAccess);
			}
		}

		/**
		 * Replaces _xx_.S().
		 */
		@SuppressWarnings("unchecked")
		@Override
		public <T> void visitCtInvocation(CtInvocation<T> invocation) {
			if (invocation.getExecutable().isOverriding(S)) {
				CtFieldAccess<?> fa = null;
				if ((invocation.getTarget() instanceof CtFieldAccess)) {
					fa = (CtFieldAccess<?>) invocation.getTarget();
				}
				if (((invocation.getTarget() instanceof CtArrayAccess)
						&& (((CtArrayAccess<?, CtExpression<?>>) invocation.getTarget())
						.getTarget() instanceof CtFieldAccess))) {
					fa = (CtFieldAccess<?>) ((CtArrayAccess<?, CtExpression<?>>) invocation.getTarget()).getTarget();
				}
				if ((fa != null) && (fa.getTarget() == null || fa.getTarget() instanceof CtThisAccess)) {
					CtCodeElement r = getParameterValueAtIndex(CtCodeElement.class,
							getParameterValue(fa.getVariable().getSimpleName()), Parameters.getIndex(fa));
					if (r != null) {
						// substitute in the replacement (for fixing type
						// references
						// and
						// for recursive substitution)
						r.accept(parent);
					}
					if (invocation.isParentInitialized() && (invocation.getParent() instanceof CtReturn) && (r instanceof CtBlock)) {
						// block template parameters in returns should
						// replace
						// the return
						((CtReturn<?>) invocation.getParent()).replace((CtStatement) r);
					} else {
						replace(invocation, r);
					}
				}
				// do not visit the invocation if replaced
				throw new DoNotFurtherTemplateThisElement(invocation);
			}
			super.visitCtInvocation(invocation);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> void scanCtExpression(CtExpression<T> expression) {
			for (int i = 0; i < expression.getTypeCasts().size(); i++) {
				CtTypeReference<T> t = (CtTypeReference<T>) expression.getTypeCasts().get(i);
				CtTypeReference<T> value = getParameterValueAsTypeReference(factory, getParameterValue(t.getSimpleName()));
				if (value != null) {
					// replace type parameters
					// TODO: this would probably not work with inner classes!!!
					expression.getTypeCasts().set(i, value);
				}
			}
			super.scanCtExpression(expression);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> void scanCtTypedElement(CtTypedElement<T> e) {
			final CtTypeReference<T> typeOfE = e.getType();
			if (typeOfE != null) {
				CtTypeReference<T> o = getParameterValueAsTypeReference(factory, getParameterValue(typeOfE.getSimpleName()));
				if (o != null) {
					// replace type parameters
					// TODO: this would probably not work with inner classes!!!
					CtTypeReference<T> t;
					if ((typeOfE instanceof CtArrayTypeReference) && !(o instanceof CtArrayTypeReference)) {
						t = (CtArrayTypeReference<T>) e.getFactory().Type().createArrayReference(
								o,
								((CtArrayTypeReference<?>) typeOfE).getDimensionCount());
					} else {
						t = (CtTypeReference<T>) o;
					}
					e.setType(t);
				}
			}
			super.scanCtTypedElement(e);
		}

		// fixes the references to executables in templates
		@Override
		public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
			scanCtReference(reference);
			visitCtTypeReference(reference.getDeclaringType());
			scanCtActualTypeContainer(reference);
		}

		/**
		 * Replaces type parameters and references to the template type with
		 * references to the target type (only if the referenced element exists
		 * in the target).
		 */
		@Override
		public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
			// if (reference.equals(templateRef) || (!reference.isPrimitif() &&
			// f.Type().createReference(Template.class).isAssignableFrom(reference)
			// && reference.isAssignableFrom(templateRef))) {
			if (reference.equals(templateRef)) {
				// replace references to the template type with references
				// to the targetType (only if the referenced element exists
				// in the target)
				reference.setDeclaringType(targetRef.getDeclaringType());
				reference.setPackage(targetRef.getPackage());
				reference.setSimpleName(targetRef.getSimpleName());
			}
			Object o = getParameterValue(reference.getSimpleName());
			if (o != null) {
				// replace type parameters
				// TODO: this would probably not work with inner classes!!!
				boolean paramHasActualTypeArguments = o instanceof CtTypeReference;
				CtTypeReference<?> t = getParameterValueAsTypeReference(factory, o);
				if (paramHasActualTypeArguments) {
					//the origin parameter has actual type arguments, apply them
					reference.setActualTypeArguments(t.getActualTypeArguments());
				}
				reference.setPackage(t.getPackage());
				reference.setSimpleName(t.getSimpleName());
				reference.setDeclaringType(t.getDeclaringType());
			} else if (templateTypeRef.equals(reference)) {
				// this can only be a template inheritance case (to be verified)
				CtTypeReference<?> sc = targetRef.getSuperclass();
				if (sc != null) {
					reference.setDeclaringType(sc.getDeclaringType());
					reference.setPackage(sc.getPackage());
					reference.setSimpleName(sc.getSimpleName());
				} else {
					reference.setDeclaringType(null);
					reference.setPackage(factory.Package().createReference("java.lang"));
					reference.setSimpleName("Object");
				}
			}
			super.visitCtTypeReference(reference);
		}
	}

	Factory factory;

	InheritanceSustitutionScanner inheritanceScanner;

	CtExecutableReference<?> S;

	CtTypeReference<?> targetRef;

	CtType<?> targetType;

	Template<?> template;

	CtTypeReference<? extends Template> templateRef;

	CtTypeReference<Template> templateTypeRef;

	CtClass<? extends Template<?>> templateType;

	Map<String, Object> namesToValues;

	/**
	 * represents root element, which is target of the substitution.
	 * It can be substituted too.
	 */
	CtElement result;

	/**
	 * Creates a new substitution visitor.
	 *
	 * @param f
	 * 		the factory
	 * @param targetType
	 * 		the target type of the substitution
	 * @param template
	 * 		the template that holds the parameter values
	 */
	public SubstitutionVisitor(Factory f, CtType<?> targetType, Template<?> template) {
		inheritanceScanner = new InheritanceSustitutionScanner(this);
		this.factory = f;
		this.template = template;
		this.targetType = targetType;
		S = f.Executable().createReference(f.Type().createReference(TemplateParameter.class),
				f.Type().createTypeParameterReference("T"), "S");
		templateRef = f.Type().createReference(template.getClass());
		templateType = f.Class().get(templateRef.getQualifiedName());
		namesToValues = Parameters.getNamesToValues(this.template, templateType);
		templateTypeRef = f.Type().createReference(Template.class);
		if (targetType != null) {
			targetRef = f.Type().createReference(targetType);
			// first substitute target ref
			targetRef.accept(this);
		}

	}

	/**
	 * Override to scan on collection copies and avoid potential concurrent
	 * modification exceptions.
	 */
	@Override
	public void scan(Collection<? extends CtElement> elements) {
		super.scan(new ArrayList<>(elements));
	}

	@Override
	public void scan(CtElement element) {
		try {
			// doing the templating of this element
			inheritanceScanner.scan(element);

			// and then scan the children for doing the templating as well in them
			super.scan(element);
		} catch (DoNotFurtherTemplateThisElement ignore) {
		}
	}

	/**
	 * Substitutes all template parameters of element and returns substituted element.
	 *
	 * @param element to be substituted model
	 * @return substituted model
	 */
	public <E extends CtElement> E substitute(E element) {
		result = element;
		scan(element);
		return (E) result;
	}

	private void replace(CtElement toBeReplaced, CtElement replacement) {
		if (result == toBeReplaced) {
			result = replacement;
		} else {
			toBeReplaced.replace(replacement);
		}
	}

	private Object getParameterValue(String parameterName) {
		return namesToValues.get(parameterName);
	}
	/**
	 * 1) Converts `parameterValue` to List using these rules
	 * <ul>
	 * <li>Array is converted to List .
	 * <li>{@link Iterable} is converted to List .
	 * <li>Single item is add to list.
	 * </ul>
	 * 2) assures that each list item has expected type `itemClass`
	 * 3) if itemClass is sub type of CtElement then clones it
	 *
	 * @param itemClass the type of the items of resulting list.
	 * 	If some item cannot be converted to the itemClass then {@link SpoonException} is thrown
	 * @param parameterValue a value of an template parameter
	 * @return list where each item is assured to be of type itemClass
	 */
	@SuppressWarnings("unchecked")
	private static <T> List<T> getParameterValueAsList(Class<T> itemClass, Object parameterValue) {
		if (parameterValue == null) {
			return null;
		}
		List<T> list = new ArrayList<>();
		if (parameterValue instanceof Object[]) {
			for (T item : (T[]) parameterValue) {
				list.add(getParameterValueAsClass(itemClass, item));
			}
		} else if (parameterValue instanceof Iterable) {
			for (T item : (Iterable<T>) parameterValue) {
				list.add(getParameterValueAsClass(itemClass, item));
			}
		} else {
			list.add(getParameterValueAsClass(itemClass, parameterValue));
		}
		return list;
	}
	/**
	 * 1) Assures that parameterValue has expected type `itemClass`
	 * 2) if itemClass is sub type of CtElement then clones parameterValue
	 *
	 * @param itemClass required return class
	 * @param parameterValue a value of an template parameter
	 * @return parameterValue cast (in future potentially converted) to itemClass
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getParameterValueAsClass(Class<T> itemClass, Object parameterValue) {
		if (parameterValue == null) {
			return null;
		}

		if (itemClass.isInstance(parameterValue)) {
			if (CtElement.class.isAssignableFrom(itemClass)) {
				/*
				 * the cloning is defined by itemClass and not by parameterValue,
				 * because there are cases when we do not want to clone parameterValue.
				 * In this case itemClass == Object.class
				 */
				parameterValue = ((CtElement) parameterValue).clone();
			}
			return (T) parameterValue;
		}
		throw new SpoonException("Parameter value has unexpected class: " + parameterValue.getClass().getName() + ". Expected class is: " + itemClass.getName());
	}
	/**
	 * @param parameterValue a value of an template parameter
	 * @return parameter value converted to String
	 */
	private static String getParameterValueAsString(Object parameterValue) {
		if (parameterValue == null) {
			return null;
		}
		if (parameterValue instanceof String) {
			return (String) parameterValue;
		} else if (parameterValue instanceof CtNamedElement) {
			return ((CtNamedElement) parameterValue).getSimpleName();
		} else if (parameterValue instanceof CtReference) {
			return ((CtReference) parameterValue).getSimpleName();
		}
		throw new SpoonException("Parameter value has unexpected class: " + parameterValue.getClass().getName() + ", whose conversion to String is not supported");
	}

	/**
	 * Converts `parameterValue` to {@link CtTypeReference}.
	 * It assures that new reference is returned.
	 * If parameterValue is already a {@link CtTypeReference}, then it is cloned.
	 *
	 * @param factory a Spoon factory used to create CtTypeReference instance - if needed
	 * @param parameterValue a value of an template parameter
	 * @return parameter value converted to {@link CtTypeReference}
	 */
	@SuppressWarnings("unchecked")
	private static <T> CtTypeReference<T> getParameterValueAsTypeReference(Factory factory, Object parameterValue) {
		if (parameterValue == null) {
			return null;
		}
		if (parameterValue instanceof Class) {
			return factory.Type().createReference((Class<T>) parameterValue);
		} else if (parameterValue instanceof CtTypeReference) {
			return ((CtTypeReference<T>) parameterValue).clone();
		} else if (parameterValue instanceof CtType) {
			return ((CtType<T>) parameterValue).getReference();
		} else if (parameterValue instanceof String) {
			return factory.Type().createReference((String) parameterValue);
		} else {
			throw new RuntimeException("unsupported reference substitution");
		}
	}

	/**
	 * 1a) If index is null, then parameterValue must be a single item, which will be converted to itemClass
	 * 1b) If index is a number, then parameterValue is converted to List, the index-th item is converted to itemClass
	 * 2) if itemClass is sub type of CtElement then returned element is a clone
	 *
	 * @param itemClass required return class
	 * @param parameterValue a value of an template parameter
	 * @param index index of item from the list, or null if item is not expected to be a list
	 * @return parameterValue (optionally item from the list) cast (in future potentially converted) to itemClass
	 */
	private static <T> T getParameterValueAtIndex(Class<T> itemClass, Object parameterValue, Integer index) {
		if (index != null) {
			//convert to list, but do not clone
			List<Object> list = getParameterValueAsList(Object.class, parameterValue);
			if (list.size() > index) {
				//convert and clone the returned item
				return getParameterValueAsClass(itemClass, list.get(index));
			}
			return null;
		}
		//convert and clone the returned item
		return getParameterValueAsClass(itemClass, parameterValue);
	}
}
