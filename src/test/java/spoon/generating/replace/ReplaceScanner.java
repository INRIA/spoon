/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.replace;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.generating.ReplacementVisitorGenerator;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.util.internal.ElementNameMap;
import spoon.support.util.ModelList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReplaceScanner extends CtScanner {
	public static final String TARGET_REPLACE_PACKAGE = "spoon.support.visitor.replace";
	public static final String GENERATING_REPLACE_PACKAGE = "spoon.generating.replace";
	public static final String GENERATING_REPLACE_VISITOR = GENERATING_REPLACE_PACKAGE + ".ReplacementVisitor";

	private final Map<String, CtClass<?>> listeners = new HashMap<>();
	private final CtClass<Object> target;
	private final CtExecutableReference<?> element;
	private final CtExecutableReference<?> list;
	private final CtExecutableReference<?> map;
	private final CtExecutableReference<?> set;

	public ReplaceScanner(CtClass<Object> target) {
		this.target = target;
		this.element = target.getMethodsByName("replaceElementIfExist").get(0).getReference();
		this.list = target.getMethodsByName("replaceInListIfExist").get(0).getReference();
		this.map = target.getMethodsByName("replaceInMapIfExist").get(0).getReference();
		this.set = target.getMethodsByName("replaceInSetIfExist").get(0).getReference();
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> element) {
		if (!element.getSimpleName().startsWith("visitCt")) {
			return;
		}

		Factory factory = element.getFactory();
		CtMethod<T> clone = element.clone();
		factory.Annotation().annotate(clone, Override.class);
		clone.getBody().getStatements().clear();
		for (int i = 1; i < element.getBody().getStatements().size() - 1; i++) {
			CtInvocation<?> inv = element.getBody().getStatement(i);
			List<CtExpression<?>> invArgs = new ArrayList<>(inv.getArguments());
			if (invArgs.size() <= 1) {
				throw new RuntimeException("You forget the role argument in line " + i + " of method " + element.getSimpleName() + " from " + element.getDeclaringType().getQualifiedName());
			}
			//remove role argument
			invArgs.remove(0);
			CtInvocation<?> getter = (CtInvocation<?>) invArgs.get(0);

			if (clone.getComments().isEmpty()) {
				// Add auto-generated comment.
				final CtComment comment = factory.Core().createComment();
				comment.setCommentType(CtComment.CommentType.INLINE);
				comment.setContent("auto-generated, see " + ReplacementVisitorGenerator.class.getName());
				clone.addComment(comment);
			}
			Class<?> actualClass = getter.getType().getActualClass();
			CtInvocation<?> invocation = createInvocation(factory, element, invArgs, getter, actualClass);
			clone.getBody().addStatement(invocation);
		}
		target.addMethod(clone);
	}

	private static Set<String> modelCollectionTypes = new HashSet<>(Arrays.asList(ModelList.class.getName(), ElementNameMap.class.getName()));
	
	private <T> CtInvocation<?> createInvocation(Factory factory, CtMethod<T> candidate, List<CtExpression<?>> invArgs, CtInvocation<?> getter, Class<?> getterTypeClass) {
		CtInvocation<?> invocation;
		Type type;
		if (getterTypeClass.equals(Collection.class) || List.class.isAssignableFrom(getterTypeClass)) {
			invocation = factory.Code().createInvocation(null, this.list, invArgs);
			type = Type.LIST;
		} else if (getterTypeClass.equals(Map.class)) {
			invocation = factory.Code().createInvocation(null, this.map, invArgs);
			type = Type.MAP;
		} else if (getterTypeClass.equals(Set.class)) {
			invocation = factory.Code().createInvocation(null, this.set, invArgs);
			type = Type.SET;
		} else {
			invocation = factory.Code().createInvocation(null, this.element, invArgs);
			type = Type.ELEMENT;
		}
		// Listener
		final String name = getter.getExecutable().getSimpleName().substring(3);
		final String listenerName = getter.getExecutable().getDeclaringType().getSimpleName() + name + "ReplaceListener";

		CtClass<?> listener;
		if (listeners.containsKey(listenerName)) {
			listener = listeners.get(listenerName);
		} else {
			final CtTypeReference<?> getterType = getGetterType(factory, getter);
			CtTypeReference<?> setterParamType = getterType;
			if (modelCollectionTypes.contains(setterParamType.getQualifiedName())) {
				setterParamType = factory.Type().createReference(Collection.class);
			}
			listener = createListenerClass(factory, listenerName, setterParamType, type);
			final CtMethod<?> setter = getSetter(name, getter.getTarget().getType().getDeclaration());
			final CtField<?> field = updateField(listener, setter.getDeclaringType().getReference());
			updateConstructor(listener, setter.getDeclaringType().getReference());
			updateSetter(factory, (CtMethod<?>) listener.getMethodsByName("set").get(0), setterParamType, field, setter);
			// Add auto-generated comment.
			final CtComment comment = factory.Core().createComment();
			comment.setCommentType(CtComment.CommentType.INLINE);
			comment.setContent("auto-generated, see " + ReplacementVisitorGenerator.class.getName());
			listener.addComment(comment);
			listeners.put(listenerName, listener);
		}

		invocation.addArgument(getConstructorCall(listener, factory.Code().createVariableRead(candidate.getParameters().get(0).getReference(), false)));
		return invocation;
	}

	private CtTypeReference<?> getGetterType(Factory factory, CtInvocation<?> getter) {
		CtTypeReference<?> getterType;
		final CtTypeReference<?> type = getter.getType();
		if (type instanceof CtTypeParameterReference) {
			getterType = getTypeFromTypeParameterReference((CtTypeParameterReference) getter.getExecutable().getDeclaration().getType());
		} else {
			getterType = type.clone();
		}
		getterType.getActualTypeArguments().clear();
		return getterType;
	}

	private CtTypeReference<?> getTypeFromTypeParameterReference(CtTypeParameterReference ctTypeParameterRef) {
		final CtMethod<?> parentMethod = ctTypeParameterRef.getParent(CtMethod.class);
		for (CtTypeParameter formal : parentMethod.getFormalCtTypeParameters()) {
			if (formal.getSimpleName().equals(ctTypeParameterRef.getSimpleName())) {
				return ((CtTypeParameterReference) formal).getBoundingType();
			}
		}
		final CtInterface<?> parentInterface = ctTypeParameterRef.getParent(CtInterface.class);
		for (CtTypeParameter formal : parentInterface.getFormalCtTypeParameters()) {
			if (formal.getSimpleName().equals(ctTypeParameterRef.getSimpleName())) {
				return formal.getReference().getBoundingType();
			}
		}
		throw new SpoonException("Can't get the type of the CtTypeParameterReference " + ctTypeParameterRef);
	}

	private CtClass<?> createListenerClass(Factory factory, String listenerName, CtTypeReference<?> getterType, Type type) {
		CtClass<?> listener;
		// prototype class to use, we'll change its name and code later
		listener = Launcher.parseClass("static class XXX implements ReplaceListener<CtElement> { \n"
					       + "private final CtElement element XXX(CtElement element) { this.element = element; }\n"
					       + "@java.lang.Override public void set(CtElement replace) {}\n"
					       + "}");
		
		listener.setSimpleName(listenerName);
		target.addNestedType(listener);
		final List<CtTypeReference<?>> references = listener.getElements(new TypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				return (TARGET_REPLACE_PACKAGE + ".CtListener").equals(reference.getQualifiedName());
			}
		});
		for (CtTypeReference<?> reference : references) {
			reference.setPackage(listener.getPackage().getReference());
		}
		final CtTypeReference<Object> theInterface = factory.Class().createReference(TARGET_REPLACE_PACKAGE + "." + type.name);
		theInterface.addActualTypeArgument(getterType);
		final Set<CtTypeReference<?>> interfaces = new HashSet<>();
		interfaces.add(theInterface);
		listener.setSuperInterfaces(interfaces);
		return listener;
	}

	private CtParameter<?> updateConstructor(CtClass<?> listener, CtTypeReference<?> type) {
		final CtConstructor<?> ctConstructor = (CtConstructor<?>) listener.getConstructors().toArray(new CtConstructor[listener.getConstructors().size()])[0];
		CtAssignment<?,?> assign = (CtAssignment<?,?>) ctConstructor.getBody().getStatement(1);
		CtThisAccess<?> fieldAccess = (CtThisAccess<?>) ((CtFieldAccess<?>) assign.getAssigned()).getTarget();
		((CtTypeAccess<?>) fieldAccess.getTarget()).getAccessedType().setImplicit(true);
		final CtParameter<?> aParameter = (CtParameter<?>) ctConstructor.getParameters().get(0);
		aParameter.setType(type);
		return aParameter;
	}

	private CtField<?> updateField(CtClass<?> listener, CtTypeReference<?> type) {
		final CtField<?> field = listener.getField("element");
		field.setType(type);
		return field;
	}

	private void updateSetter(Factory factory, CtMethod<?> setListener, CtTypeReference<?> getterType, CtField<?> field, CtMethod<?> setter) {
		setListener.getParameters().get(0).setType(getterType);

		CtInvocation<?> ctInvocation = factory.Code().createInvocation(//
				factory.Code().createVariableRead(field.getReference(), false), //
				setter.getReference(), //
				factory.Code().createVariableRead(setListener.getParameters().get(0).getReference(), false) //
		);
		CtBlock<?> ctBlock = factory.Code().createCtBlock(ctInvocation);
		setListener.setBody(ctBlock);
	}

	private CtMethod<?> getSetter(String name, CtType<?> declaration) {
		Set<CtMethod<?>> allMethods = declaration.getAllMethods();
		CtMethod<?> setter = null;
		for (CtMethod<?> aMethod : allMethods) {
			if (("set" + name).equals(aMethod.getSimpleName())) {
				setter = aMethod;
				break;
			}
		}
		return setter;
	}

	private CtConstructorCall<?> getConstructorCall(CtClass<?> listener, CtExpression<?> argument) {
		return listener.getFactory().Code().createConstructorCall(listener.getReference(), argument);
	}

	enum Type {
		ELEMENT("ReplaceListener"), LIST("ReplaceListListener"), SET("ReplaceSetListener"), MAP("ReplaceMapListener");

		String name;

		Type(String name) {
			this.name = name;
		}
	}
}
