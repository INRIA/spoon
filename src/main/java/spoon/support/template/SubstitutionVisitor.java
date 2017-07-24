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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.template.AbstractTemplate;
import spoon.template.Parameter;
import spoon.template.Template;
import spoon.template.TemplateParameter;

class DoNotFurtherTemplateThisElement extends SpoonException {
	private static final long serialVersionUID = 1L;

	Object skipped;

	DoNotFurtherTemplateThisElement(CtElement e) {
		//Do not use e.toString(), which computes expensive String representation of whole element,
		//which is sometime impossible to compute correctly in the middle of the substitution process
		super("Skipping " + e.getClass().getName());
		skipped = e;
	}

}

/**
 * This visitor implements the substitution engine of Spoon templates.
 */
public class SubstitutionVisitor extends CtScanner {

	private static final Object NULL_VALUE = new Object();
	private Context context;

	private class InheritanceSustitutionScanner extends CtInheritanceScanner {

		InheritanceSustitutionScanner() {
		}

		@Override
		public void visitCtComment(CtComment e) {
			e.setContent(context.substituteName(e.getContent()));
			super.visitCtComment(e);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> void visitCtLiteral(CtLiteral<T> e) {
			Object value = e.getValue();
			if (value instanceof String) {
				e.setValue((T) context.substituteName((String) value));
			}
			super.visitCtLiteral(e);
		}

		/**
		 * Replaces parameters in element names (even if detected as a
		 * substring).
		 */
		@Override
		public void scanCtNamedElement(CtNamedElement element) {
			Object value = context.getParameterValue(element.getSimpleName());
			if (value != null) {
				if (value instanceof String) {
					//the parameter value is a String. It is the case of substitution of the name only
					//replace parameter (sub)strings in simplename
					element.setSimpleName(context.substituteName(element.getSimpleName()));
				} else if (value instanceof CtTypeReference && element instanceof CtType) {
					//the parameter value is a type reference and the element is a type. Replace name of the type
					element.setSimpleName(((CtTypeReference) value).getSimpleName());
				} else {
					//this named element has to be replaced by zero one or more other elements
					List<? extends CtNamedElement> values = getParameterValueAsListOfClones(element.getClass(), value);
					throw context.replace(element, values);
				}
			} else {
				//try to substitute substring of the name
				element.setSimpleName(context.substituteName(element.getSimpleName()));
			}
			super.scanCtNamedElement(element);
		}

		@Override
		public void scanCtReference(CtReference reference) {
			Object value = context.getParameterValue(reference.getSimpleName());
			if (value != null) {
				if (reference instanceof CtTypeReference) {
					/**
					 * Replaces type parameters and references to the template type with
					 * references to the target type.
					 */
					// replace type parameters
					CtTypeReference<?> typeReference = (CtTypeReference<?>) reference;
					boolean paramHasActualTypeArguments = value instanceof CtTypeReference;
					CtTypeReference<?> tr = getParameterValueAsTypeReference(factory, value);
					if (paramHasActualTypeArguments) {
						//the origin parameter has actual type arguments, apply them
						typeReference.setActualTypeArguments(tr.getActualTypeArguments());
					}
					typeReference.setPackage(tr.getPackage());
					typeReference.setSimpleName(tr.getSimpleName());
					typeReference.setDeclaringType(tr.getDeclaringType());
				} else {
					if (value instanceof String) {
						//the parameter value is a String. It is the case of substitution of the name only
						//replace parameter (sub)strings in simplename
						reference.setSimpleName(context.substituteName(reference.getSimpleName()));
					} else {
						//we have to replace the expression by another expression or statement
						CtExpression<?> expr = reference.getParent(CtExpression.class);
						List<CtCodeElement> values = getParameterValueAsListOfClones(CtCodeElement.class, value);
						//TODO we might check consistency here, but we need to know context of the expr. Is it Statement or Expression?
						//replace expression with statements or expressions
						throw context.replace(expr, values);
					}
				}

			} else {
				//try to substitute substring of the name
				reference.setSimpleName(context.substituteName(reference.getSimpleName()));
			}
			super.scanCtReference(reference);
		}

		/** statically inline foreach */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void visitCtForEach(CtForEach foreach) {
			if (foreach.getExpression() instanceof CtFieldAccess) {
				CtFieldAccess<?> fa = (CtFieldAccess<?>) foreach.getExpression();
				Object value = context.getParameterValue(fa.getVariable().getSimpleName());
				if (value != null) {
					//create local context which holds local substitution parameter
					Context localContext = createContext();
					List<CtExpression> list = getParameterValueAsListOfClones(CtExpression.class, value);
					//ForEach always contains CtBlock. In some cases it is implicit.
					CtBlock<?> foreachBlock = (CtBlock<?>) foreach.getBody();
					String newParamName = foreach.getVariable().getSimpleName();
					List<CtStatement> newStatements = new ArrayList<>();
					for (CtExpression element : list) {
						//for each item of foreach expression copy foreach body and substitute it is using local context containing new parameter
						localContext.putParameter(newParamName, element);
						for (CtStatement st : foreachBlock.getStatements()) {
							newStatements.addAll(localContext.substitute(st.clone()));
						}
					}
					throw context.replace(foreach, newStatements);
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

		@SuppressWarnings({ "rawtypes" })
		private <T> void visitFieldAccess(final CtFieldAccess<T> fieldAccess) {
			CtFieldReference<?> ref = fieldAccess.getVariable();
			if ("length".equals(ref.getSimpleName())) {
				if (fieldAccess.getTarget() instanceof CtFieldAccess) {
					ref = ((CtFieldAccess<?>) fieldAccess.getTarget()).getVariable();
					Object value = context.getParameterValue(ref.getSimpleName());
					if (value != null) {
						//the items of this list are not cloned
						List<Object> list = getParameterValueAsNewList(value);
						throw context.replace(fieldAccess, (CtExpression) fieldAccess.getFactory().Code().createLiteral(list.size()));
					}
				}
			}
//			Object v = context.getParameterValue(Parameters.getParameterName(ref));
			Object v = context.getParameterValue(ref.getSimpleName());
			if (v != null) {
				// replace direct field parameter accesses
				Object value = getParameterValueAtIndex(Object.class, v, Parameters.getIndex(fieldAccess));
				CtExpression toReplace = fieldAccess;
				if (fieldAccess.getParent() instanceof CtArrayAccess) {
					toReplace = (CtExpression) fieldAccess.getParent();
				}
				if (!(value instanceof TemplateParameter)) {
					if (value instanceof Class) {
						throw context.replace(toReplace, factory.Code()
								.createClassAccess(factory.Type().createReference(((Class<?>) value).getName())));
					} else if (value instanceof Enum) {
						CtTypeReference<?> enumType = factory.Type().createReference(value.getClass());
						CtFieldRead<?> enumValueAccess = (CtFieldRead<?>) factory.Code().createVariableRead(
								factory.Field().createReference(enumType, enumType, ((Enum<?>) value).name()), true);
						enumValueAccess.setTarget(factory.Code().createTypeAccess(enumType));
						throw context.replace(toReplace, enumValueAccess);
					} else if ((value != null) && value.getClass().isArray()) {
						throw context.replace(toReplace, factory.Code().createLiteralArray((Object[]) value));
					} else {
						throw context.replace(toReplace, factory.Code().createLiteral(value));
					}
				} else {
					throw context.replace(toReplace, toReplace.clone());
				}
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
							context.getParameterValue(fa.getVariable().getSimpleName()), Parameters.getIndex(fa));
					List<CtCodeElement> subst = null;
					if (r != null) {
						subst = createContext().substitute(r);
					} else {
						subst = Collections.<CtCodeElement>emptyList();
					}
					throw context.replace(invocation, subst);
				}
			}
			super.visitCtInvocation(invocation);
		}
	}

	private Factory factory;

	private InheritanceSustitutionScanner inheritanceScanner;

	private CtExecutableReference<?> S;

	private boolean addGeneratedBy = false;

	/**
	 * Creates new substitution visitor based on instance of Template,
	 * which defines template model and template parameters
	 *
	 * @param f
	 * 		the factory
	 * @param targetType
	 * 		the target type of the substitution (can be null)
	 * @param template
	 * 		the template that holds the parameter values
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SubstitutionVisitor(Factory f, CtType<?> targetType, Template<?> template) {
		this(f, Parameters.getTemplateParametersAsMap(f, targetType, template));
		if (template instanceof AbstractTemplate) {
			addGeneratedBy(((AbstractTemplate) template).isAddGeneratedBy());
		}
	}

	/**
	 * Creates new substitution visitor
	 * with substitution model (doesn't have to implement {@link Template}) type
	 * and the substitution parameters (doesn't have to be bound to {@link TemplateParameter} or {@link Parameter}).
	 *
	 * @param f
	 * 		the factory
	 * @param templateParameters
	 * 		the parameter names and values which will be used during substitution
	 */
	public SubstitutionVisitor(Factory f, Map<String, Object> templateParameters) {
		this.inheritanceScanner = new InheritanceSustitutionScanner();
		this.factory = f;
		S = factory.Executable().createReference(factory.Type().createReference(TemplateParameter.class),
				factory.Type().createTypeParameterReference("T"), "S");
		this.context = new Context(null).putParameters(templateParameters);
	}

	/**
	 * @return true if the template engine ({@link SubstitutionVisitor}) adds Generated by ... comments into generated code
	 */
	public boolean isAddGeneratedBy() {
		return addGeneratedBy;
	}

	/**
	 * @param addGeneratedBy if true the template engine ({@link SubstitutionVisitor}) will add Generated by ... comments into generated code
	 */
	public SubstitutionVisitor addGeneratedBy(boolean addGeneratedBy) {
		this.addGeneratedBy = addGeneratedBy;
		return this;
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
			if (element != ignore.skipped) {
				//we have to skip more
				throw ignore;
			}
		}
	}

	/**
	 * Substitutes all template parameters of element and returns substituted element.
	 *
	 * @param element to be substituted model
	 * @return substituted model
	 */
	public <E extends CtElement> List<E> substitute(E element) {
		final Map<CtElement, String> elementToGeneratedByComment = addGeneratedBy ? new IdentityHashMap<CtElement, String>() : null;
		if (addGeneratedBy) {
			/*
			 * collect 'generated by' comments for each type member of the substituted element, before the substitution is done,
			 * so we know the origin names of the members.
			 */
			final CtInheritanceScanner internalScanner = new CtInheritanceScanner() {
				public void scanCtTypeMember(CtTypeMember typeMeber) {
					elementToGeneratedByComment.put(typeMeber, getGeneratedByComment(typeMeber));
				}
			};
			new CtScanner() {
				@Override
				public void scan(CtElement p_element) {
					internalScanner.scan(p_element);
					super.scan(p_element);
				}
			}.scan(element);
		}
		List<E> result = createContext().substitute(element);
		if (addGeneratedBy) {
			//add generated by comments after substitution, otherwise they would be substituted in comments too.
			applyGeneratedByComments(elementToGeneratedByComment);
		}
		return result;
	}

	private static String getGeneratedByComment(CtElement ele) {
		SourcePosition pos = ele.getPosition();
		if (pos != null) {
			CompilationUnit cu = pos.getCompilationUnit();
			if (cu != null) {
				CtType<?> mainType = cu.getMainType();
				if (mainType != null) {
					StringBuilder result = new StringBuilder();
					result.append("Generated by ");
					result.append(mainType.getQualifiedName());
					appendInnerTypedElements(result, mainType, ele);
					result.append('(');
					result.append(mainType.getSimpleName());
					result.append(".java:");
					result.append(pos.getLine());
					result.append(')');
					return  result.toString();
				}
			}
		}
		return null;
	}

	private static void appendInnerTypedElements(StringBuilder result, CtType<?> mainType, CtElement ele) {
		CtTypeMember typeMember = getFirst(ele, CtTypeMember.class);
		if (typeMember != null && typeMember != mainType) {
			if (typeMember.isParentInitialized()) {
				appendInnerTypedElements(result, mainType, typeMember.getParent());
			}
			if (typeMember instanceof CtType) {
				result.append('$');
			} else {
				result.append('#');
			}
			result.append(typeMember.getSimpleName());
		}
	}

	private static void applyGeneratedByComments(Map<CtElement, String> elementToGeneratedByComment) {
		for (Map.Entry<CtElement, String> e : elementToGeneratedByComment.entrySet()) {
			addGeneratedByComment(e.getKey(), e.getValue());
		}
	}

	private static void addGeneratedByComment(CtElement ele, String generatedBy) {
		if (generatedBy == null) {
			return;
		}
		String EOL = System.getProperty("line.separator");
		CtComment comment = getJavaDoc(ele);
		String content = comment.getContent();
		if (content.trim().length() > 0) {
			content += EOL + EOL;
		}
		content += generatedBy;
		comment.setContent(content);
	}
	private static CtComment getJavaDoc(CtElement ele) {
		for (CtComment comment : ele.getComments()) {
			if (comment.getCommentType() == CtComment.CommentType.JAVADOC) {
				return comment;
			}
		}
		CtComment c = ele.getFactory().Code().createComment("", CtComment.CommentType.JAVADOC);
		ele.addComment(c);
		return c;
	}

	@SuppressWarnings("unchecked")
	private static <T extends CtElement> T getFirst(CtElement ele, Class<T> clazz) {
		if (ele != null) {
			if (clazz.isAssignableFrom(ele.getClass())) {
				return (T) ele;
			}
			if (ele.isParentInitialized()) {
				return getFirst(ele.getParent(), clazz);
			}
		}
		return null;
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
	private static <T> List<T> getParameterValueAsListOfClones(Class<T> itemClass, Object parameterValue) {
		List<Object> list = getParameterValueAsNewList(parameterValue);
		for (int i = 0; i < list.size(); i++) {
			list.set(i, getParameterValueAsClass(itemClass, list.get(i)));
		}
		return (List<T>) list;
	}
	private static List<Object> getParameterValueAsNewList(Object parameterValue) {
		List<Object> list = new ArrayList<>();
		if (parameterValue != null) {
			if (parameterValue instanceof Object[]) {
				for (Object item : (Object[]) parameterValue) {
					list.add(item);
				}
			} else if (parameterValue instanceof Iterable) {
				for (Object item : (Iterable<Object>) parameterValue) {
					list.add(item);
				}
			} else {
				if (parameterValue != null && parameterValue != NULL_VALUE) {
					list.add(parameterValue);
				}
			}
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
		if (parameterValue == null || parameterValue == NULL_VALUE) {
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
		if (itemClass.isAssignableFrom(CtCodeElement.class)) {
			if (parameterValue instanceof CtTypeReference) {
				//convert type reference into code element as class access
				CtTypeReference<?> tr = (CtTypeReference<?>) parameterValue;
				return (T) tr.getFactory().Code().createClassAccess(tr);
			}
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
		} else if (parameterValue instanceof Class) {
			return ((Class) parameterValue).getSimpleName();
		} else if (parameterValue instanceof CtInvocation) {
			return getShortSignature(((CtInvocation<?>) parameterValue).getExecutable().getSignature());
		} else if (parameterValue instanceof CtExecutableReference) {
			return getShortSignature(((CtExecutableReference<?>) parameterValue).getSignature());
		} else if (parameterValue instanceof CtExecutable) {
			return getShortSignature(((CtExecutable<?>) parameterValue).getSignature());
		} else if (parameterValue instanceof CtLiteral) {
			Object val = ((CtLiteral<Object>) parameterValue).getValue();
			return val == null ? null : val.toString();
		}
		throw new SpoonException("Parameter value has unexpected class: " + parameterValue.getClass().getName() + ", whose conversion to String is not supported");
	}

	/*
	 * cut the package name. We always convert types to simple names here
	 */
	private static String getShortSignature(String fullSignature) {
		return fullSignature.substring(fullSignature.lastIndexOf('.') + 1);
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
		if (parameterValue == null || parameterValue == NULL_VALUE) {
			throw new SpoonException("The null value is not valid substitution for CtTypeReference");
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
			List<Object> list = getParameterValueAsNewList(parameterValue);
			if (list.size() > index) {
				//convert and clone the returned item
				return getParameterValueAsClass(itemClass, list.get(index));
			}
			return null;
		}
		//convert and clone the returned item
		return getParameterValueAsClass(itemClass, parameterValue);
	}

	private Context createContext() {
		//by default each new context has same input like parent and modifies same collection like parent context
		return new Context(this.context);
	}

	private class Context {
		private final Context parentContext;
		/**
		 * represents root element, which is target of the substitution.
		 * It can be substituted too.
		 */
		private CtElement input;
		/**
		 * represents replacement of the `input`.
		 * it is null if input was not replaced
		 */
		private List<CtElement> result;
		private Map<String, Object> parameterNameToValue;

		private Context(Context parent) {
			this.parentContext = parent;
		}

		private Context putParameter(String name, Object value) {
			if (parameterNameToValue == null) {
				parameterNameToValue = new LinkedHashMap<>();
			}
			if (value == null) {
				value = NULL_VALUE;
			}
			parameterNameToValue.put(name, value);
			return this;
		}

		private Context putParameters(Map<String, Object> parameters) {
			if (parameters != null && parameters.isEmpty() == false) {
				for (Map.Entry<String, Object> e : parameters.entrySet()) {
					putParameter(e.getKey(), e.getValue());
				}
			}
			return this;
		}

		private Object getParameterValue(String parameterName) {
			if (parameterNameToValue != null) {
				Object value = parameterNameToValue.get(parameterName);
				if (value != null) {
					return value;
				}
			}
			if (parentContext != null) {
				return parentContext.getParameterValue(parameterName);
			}
			return null;
		}

		private <E extends CtElement> DoNotFurtherTemplateThisElement replace(CtElement toBeReplaced, E replacement) {
			return replace(toBeReplaced, replacement == null ? Collections.<E>emptyList() : Collections.<E>singletonList(replacement));
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private <E extends CtElement> DoNotFurtherTemplateThisElement replace(CtElement toBeReplaced, List<E> replacements) {
			CtElement parentOfReplacement = toBeReplaced.isParentInitialized() ? toBeReplaced.getParent() : null;
			if (parentOfReplacement instanceof CtReturn) {
				if (replacements.size() == 1 && replacements.get(0) instanceof CtBlock) {
					replacements = (List) ((CtBlock) replacements.get(0)).getStatements();
				}
				if (replacements.size() > 1) {
					//replace return too, because return expression cannot contain more statements
					return context._replace(parentOfReplacement, replacements);
				} else if (replacements.size() == 1 && (replacements.get(0) instanceof CtExpression) == false) {
					//replace return too, because return expression cannot contain CtBlock
					return context._replace(parentOfReplacement, replacements);
				}
			}
			return context._replace(toBeReplaced, replacements);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private <E extends CtElement> DoNotFurtherTemplateThisElement _replace(CtElement toBeReplaced, List<E> replacements) {
			if (input == toBeReplaced) {
				if (result != null) {
					throw new SpoonException("Illegal state. SubstitutionVisitor.Context#result was already replaced!");
				}
				result = (List) replacements;
			} else {
				toBeReplaced.replace(replacements);
			}
			return new DoNotFurtherTemplateThisElement(toBeReplaced);
		}

		@SuppressWarnings("unchecked")
		private <E extends CtElement> List<E> substitute(E element) {
			if (input != null) {
				throw new SpoonException("Illegal state. SubstitutionVisitor.Context#input is already set.");
			}
			input = element;
			result = null;
			if (context != parentContext) {
				throw new SpoonException("Illegal state. Context != parentContext");
			}
			try {
				context = this;
				scan(element);
				if (result != null) {
					return (List<E>) result;
				}
				return Collections.<E>singletonList((E) input);
			} finally {
				context = this.parentContext;
				input = null;
			}
		}

		private String substituteName(String name) {
			if (name == null) {
				return null;
			}
			if (parameterNameToValue != null) {
				for (Map.Entry<String, Object> e : parameterNameToValue.entrySet()) {
					String pname = e.getKey();
					if (name.contains(pname)) {
						String value = getParameterValueAsString(e.getValue());
						name = name.replace(pname, value);
					}
				}
			}
			if (parentContext != null) {
				name = parentContext.substituteName(name);
			}
			return name;
		}

	}
}
