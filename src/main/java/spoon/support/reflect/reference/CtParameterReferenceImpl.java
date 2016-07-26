/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.reflect.reference;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.declaration.CtElement;

public class CtParameterReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtParameterReference<T> {

    private static final long serialVersionUID = 1L;
    private CtParameter declaration;
    CtExecutableReference<?> executable;

    public CtParameterReferenceImpl() {
        super();
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtParameterReference(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CtParameter<T> getDeclaration() {
        if (declaration != null) {
            return declaration;
        }
        CtElement element = this;
        do {
            CtExecutable executable = element.getInitializedParent(CtExecutable.class);
            if (executable == null) {
                return null;
            }
            declaration = filter(executable.getParameters(), CtParameter.class);
            element = executable;
        } while (declaration == null);
        return declaration;
    }

    @Override
    public CtExecutableReference<?> getDeclaringExecutable() {
        return executable;
    }

    @Override
    public <C extends CtParameterReference<T>> C setDeclaringExecutable(CtExecutableReference<?> executable) {
        if (executable != null) {
            executable.setParent(this);
        }
        this.executable = executable;
        return (C) this;
    }

    @Override
    public CtParameterReference<T> clone() {
        return (CtParameterReference<T>) super.clone();
    }

    @Override
    public <C extends CtParameterReference<T>> C setDeclaration(CtParameter<T> declaration) {
        this.declaration = declaration;
        return (C) this;
    }
}
