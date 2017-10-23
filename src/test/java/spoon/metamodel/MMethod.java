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
package spoon.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.MethodTypingContext;

import static spoon.metamodel.SpoonMetaModel.addUniqueObject;

/**
 * Represents a method of a {@link MMField} of a {@link MMType}.
 * Each MMMethod belongs to one MMField
 */
public class MMethod {
	final MMField field;
	final CtMethod<?> method;
	final List<CtMethod<?>> ownMethods = new ArrayList<>();
	final List<MMethod> superMethods = new ArrayList<>();
	final String signature;

	MMethod(MMField field, CtMethod<?> method) {
		this.field = field;
		//adapt method to scope of field.ownType
		MethodTypingContext mtc = new MethodTypingContext().setClassTypingContext(field.getOwnerType().getTypeContext()).setMethod(method);
		this.method = (CtMethod<?>) mtc.getAdaptationScope();
		signature = this.method.getSignature();
	}

	public CtMethod<?> getMethod() {
		return method;
	}

	public CtMethod<?> getMethodOf(MMType targetType) {
		for (CtMethod<?> ctMethod : ownMethods) {
			if (targetType.getTypeContext().isSubtypeOf(ctMethod.getDeclaringType().getReference())) {
				return ctMethod;
			}
		}
		for (MMethod mmMethod : superMethods) {
			CtMethod<?> m = mmMethod.getMethodOf(targetType);
			if (m != null) {
				return m;
			}
		}
		return null;
	}

	boolean overrides(CtMethod<?> method) {
		return field.getOwnerType().getTypeContext().isOverriding(this.method, method);
	}

	public String getSignature() {
		return signature;
	}

	public void addSuperMethod(MMethod superMethod) {
		addUniqueObject(superMethods, superMethod);
	}

	public MMField getField() {
		return field;
	}

	public List<CtMethod<?>> getOwnMethods() {
		return Collections.unmodifiableList(ownMethods);
	}

	public List<MMethod> getSuperMethods() {
		return Collections.unmodifiableList(superMethods);
	}

	public String getSimpleName() {
		return method.getSimpleName();
	}

	public CtTypeReference<?> getType() {
		return method.getType();
	}

	@Override
	public String toString() {
		return getField().getOwnerType().getName() + "#" + getSignature();
	}

	public CtTypeReference<?> getValueType() {
		if (method.getParameters().isEmpty()) {
			return method.getType();
		}
		return method.getParameters().get(method.getParameters().size() - 1).getType();
	}
}
