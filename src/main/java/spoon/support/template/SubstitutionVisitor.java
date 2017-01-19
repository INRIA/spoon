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
			for (CtParameter<?> parameter : new ArrayList<>(e.getParameters())) {
				String name = parameter.getSimpleName();
				for (String pname : parameterNames) {
					if (name.equals(pname)) {
						Object value = Parameters.getValue(template, pname, null);
						int i = parameter.getParent().getParameters().indexOf(parameter);
						if (value instanceof List) {
							List<?> l = (List<?>) value;
							for (Object p : l) {
								CtParameter<?> p2 = ((CtParameter<?>) p).clone();
								p2.setParent(parameter.getParent());
								parameter.getParent().getParameters().add(i++, p2);
							}
							parameter.getParent().getParameters().remove(parameter);
						}
					}
				}
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
				element.setDocComment(substituteInDocComment(element.getDocComment()));
			}
			// replace parameters in names
			String name = element.getSimpleName();
			for (String pname : parameterNames) {
				if (name.contains(pname)) {
					Object value = Parameters.getValue(template, pname, null);
					if (value instanceof String) {
						// replace with the string value
						name = name.replace(pname, (String) value);
						element.setSimpleName(name);
					} else if ((value instanceof CtTypeReference) && (element instanceof CtType)) {
						// replace with the type reference's name
						name = name.replace(pname, ((CtTypeReference<?>) value).getSimpleName());
						element.setSimpleName(name);
					}
				}
			}
			super.scanCtNamedElement(element);
		}

		private String substituteInDocComment(String docComment) {
			String result = docComment;
			for (String pname : parameterNames) {
				Object value = Parameters.getValue(template, pname, null);
				if (value instanceof String) {
					result = result.replace(pname, (String) value);
				}
			}
			return result;
		}

		/** statically inline foreach */
		@Override
		public void visitCtForEach(CtForEach foreach) {
			if (foreach.getExpression() instanceof CtFieldAccess) {
				CtFieldAccess<?> fa = (CtFieldAccess<?>) foreach.getExpression();
				if (Parameters.isParameterSource(fa.getVariable())) {
					Object[] value = (Object[]) Parameters.getValue(template, fa.getVariable().getSimpleName(), null);
					CtBlock<?> l = foreach.getFactory().Core().createBlock();
					CtStatement body = foreach.getBody();
					for (Object element : value) {
						CtStatement b = body.clone();
						for (CtVariableAccess<?> va : Query.getElements(b, new VariableAccessFilter<>(foreach.getVariable().getReference()))) {
							va.replace((CtExpression) element);
						}
						if (b instanceof CtBlock && ((CtBlock) b).getStatements().size() == 1) {
							b = ((CtBlock) b).getStatement(0);
						}
						l.addStatement(b);
					}
					foreach.replace(l);
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

		private <T> void visitFieldAccess(CtFieldAccess<T> fieldAccess) {
			CtFieldReference<?> ref = fieldAccess.getVariable();
			if ("length".equals(ref.getSimpleName())) {
				if (fieldAccess.getTarget() instanceof CtFieldAccess) {
					ref = ((CtFieldAccess<?>) fieldAccess.getTarget()).getVariable();
					if (Parameters.isParameterSource(ref)) {
						Object[] value = (Object[]) Parameters.getValue(template, ref.getSimpleName(), null);
						fieldAccess.replace((CtExpression) fieldAccess.getFactory().Code().createLiteral(value
								.length));
						throw new DoNotFurtherTemplateThisElement(fieldAccess);
					}
				}
			}
			if (Parameters.isParameterSource(ref)) {
				// replace direct field parameter accesses
				Object value = Parameters.getValue(template, ref.getSimpleName(), Parameters.getIndex(fieldAccess));
				CtExpression toReplace = fieldAccess;
				if (fieldAccess.getParent() instanceof CtArrayAccess) {
					toReplace = (CtExpression) fieldAccess.getParent();
				}
				if (!(value instanceof TemplateParameter)) {
					if (value instanceof Class) {
						toReplace.replace(factory.Code()
								.createClassAccess(factory.Type().createReference(((Class<?>) value).getName())));
					} else if (value instanceof Enum) {
						CtTypeReference<?> enumType = factory.Type().createReference(value.getClass());
						toReplace.replace(factory.Code().createVariableRead(
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
						toReplace.replace(factory.Code().createLiteralArray((Object[]) value));
					} else {
						toReplace.replace(factory.Code().createLiteral(value));
					}
				} else {
					toReplace.clone();
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
					TemplateParameter<?> tparamValue = (TemplateParameter<?>) Parameters
							.getValue(template, fa.getVariable().getSimpleName(), Parameters.getIndex(fa));
					CtCodeElement r = null;
					if (tparamValue != null) {
						r = ((CtCodeElement) tparamValue).clone();
						// substitute in the replacement (for fixing type
						// references
						// and
						// for recursive substitution)
						r.accept(parent);
					}
					if ((invocation.getParent() instanceof CtReturn) && (r instanceof CtBlock)) {
						// block template parameters in returns should
						// replace
						// the return
						((CtReturn<?>) invocation.getParent()).replace((CtStatement) r);
					} else {
						invocation.replace(r);
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
				if (parameterNames.contains(t.getSimpleName())) {
					// replace type parameters
					// TODO: this would probably not work with inner classes!!!
					Object o = Parameters.getValue(template, t.getSimpleName(), null);
					if (o instanceof Class) {
						t = factory.Type().createReference(((Class<T>) o));
					} else if (o instanceof CtTypeReference) {
						t = (CtTypeReference<T>) o;
						expression.getTypeCasts().set(i, t);
					} else {
						throw new RuntimeException("unsupported reference substitution");
					}
				}
			}
			super.scanCtExpression(expression);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> void scanCtTypedElement(CtTypedElement<T> e) {
			if ((e.getType() != null) && parameterNames.contains(e.getType().getSimpleName())) {
				// replace type parameters
				// TODO: this would probably not work with inner classes!!!
				CtTypeReference<T> t;
				Object o = Parameters.getValue(template, e.getType().getSimpleName(), null);
				if (o instanceof Class) {
					o = factory.Type().createReference(((Class<T>) o));
				}
				if (o instanceof CtTypeReference) {
					if ((e.getType() instanceof CtArrayTypeReference) && !(o instanceof CtArrayTypeReference)) {
						t = (CtArrayTypeReference<T>) e.getFactory().Type().createArrayReference(
								(CtTypeReference<?>) o,
								((CtArrayTypeReference<?>) e.getType()).getDimensionCount());
					} else {
						t = (CtTypeReference<T>) o;
					}
					e.setType(t);
				} else {
					throw new RuntimeException("unsupported reference substitution");
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
			if (parameterNames.contains(reference.getSimpleName())) {
				// replace type parameters
				// TODO: this would probably not work with inner classes!!!
				CtTypeReference<?> t;
				Object o = Parameters.getValue(template, reference.getSimpleName(), null);
				if (o instanceof Class) {
					t = factory.Type().createReference(((Class<?>) o));
				} else if (o instanceof CtTypeReference) {
					t = ((CtTypeReference<?>) o).clone();
					reference.setActualTypeArguments(t.getActualTypeArguments());
				} else {
					throw new RuntimeException(
							"unsupported reference substitution: " + reference.getSimpleName() + " with value " + o);
				}
				reference.setPackage(t.getPackage());
				reference.setSimpleName(t.getSimpleName());
				reference.setDeclaringType(t.getDeclaringType());
			} else if (templateTypeRef.isSubtypeOf(reference)) {
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

	Collection<String> parameterNames;

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
		parameterNames = Parameters.getNames(templateType);
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
}
