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
package spoon.generating;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractManualProcessor;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.visitor.ClassTypingContext;

public class CtRoleScannerGenerator extends AbstractManualProcessor {
	private static final String TARGET_ROLECANNER_PACKAGE = "spoon.reflect.visitor";

	public void process() {
		CtClass<?> roleScanner = getFactory().Class().get(CtScanner.class).clone();
		roleScanner.setSimpleName("CtRoleScanner");
		getFactory().Package().get(TARGET_ROLECANNER_PACKAGE).addType(roleScanner);

		for (CtTypeMember member : roleScanner.getTypeMembers()) {
			if ((member instanceof CtMethod<?>) == false) {
				continue;
			}
			CtMethod<?> method = (CtMethod<?>) member;
			if (method.getSimpleName().equals("scan")) {
				CtParameter<?> roleParameter = addRoleParameter(method);
				method.filterChildren(new TypeFilter<>(CtInvocation.class)).forEach((CtInvocation<?> invocation) -> {
					if (invocation.getExecutable().getSimpleName().equals("scan")) {
						useRoleParameter(invocation, roleParameter);
					}
				});
			}
			if (method.getSimpleName().startsWith("visit")) {
				method.filterChildren(new TypeFilter<>(CtInvocation.class)).forEach((CtInvocation<?> invocation) -> {
					if (invocation.getExecutable().getSimpleName().equals("scan")) {
						addRoleParameterByArgument(invocation);
					}
				});
			}
		}
	}

	private void addRoleParameterByArgument(CtInvocation<?> invocation) {
		List<CtExpression<?>> args = new ArrayList<>(invocation.getArguments());
		args.add(0, getRoleOfInvocation(args.get(0)).clone());
		invocation.setArguments(args);

		List<CtTypeReference<?>> params = new ArrayList<>(invocation.getExecutable().getParameters());
		params.add(0, getFactory().createCtTypeReference(CtRole.class));
		invocation.getExecutable().setParameters(params);
	}

	private CtExpression<?> getRoleOfInvocation(CtExpression<?> expression) {
		CtInvocation<?> inv = (CtInvocation<?>) expression;
		CtAnnotation<PropertyGetter> annotation = getInheritedAnnotation((CtMethod<?>) inv.getExecutable().getDeclaration(), getFactory().createCtTypeReference(PropertyGetter.class));

		if (annotation == null) {
			this.getClass();
		}
		CtFieldRead<?> role = annotation.getValue("role");
		return role;
	}

	/**
	 * Looks for method in superClass and superInterface hierarchy for the method with required annotationType
	 * @param method
	 * @param annotationType
	 * @return
	 */
	private <A extends Annotation> CtAnnotation<A> getInheritedAnnotation(CtMethod<?> method, CtTypeReference<A> annotationType) {
		CtAnnotation<A> annotation = method.getAnnotation(annotationType);
		if (annotation == null) {
			CtType<?> declType = method.getDeclaringType();
			final ClassTypingContext ctc = new ClassTypingContext(declType);
			annotation = declType.map(new AllTypeMembersFunction(CtMethod.class)).map((CtMethod<?> currentMethod) -> {
				if (method == currentMethod) {
					return null;
				}
				if (ctc.isSameSignature(method, currentMethod)) {
					CtAnnotation<A> annotation2 = currentMethod.getAnnotation(annotationType);
					if (annotation2 != null) {
						return annotation2;
					}
				}
				return null;
			}).first();
		}
		return annotation;
	}

	private void useRoleParameter(CtInvocation<?> invocation, CtParameter<?> roleParameter) {
		List<CtExpression<?>> args = new ArrayList<>(invocation.getArguments());
		args.add(0, getFactory().createVariableRead(getFactory().createParameterReference(roleParameter), false));
		invocation.setArguments(args);

		List<CtTypeReference<?>> params = new ArrayList<>(invocation.getExecutable().getParameters());
		params.add(0, roleParameter.getType().clone());
		invocation.getExecutable().setParameters(params);
	}

	private CtParameter<?> addRoleParameter(CtMethod<?> method) {
		List<CtParameter<?>> params = new ArrayList<>(method.getParameters());
		CtParameter<?> roleParam = getFactory().createParameter(method, getFactory().createCtTypeReference(CtRole.class), "role");
		params.add(0, roleParam);
		method.setParameters(params);
		return roleParam;
	}
}
