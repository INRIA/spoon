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

package spoon.support.reflect.reference;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtTypeParameterReferenceImpl extends CtTypeReferenceImpl<Object>
        implements CtTypeParameterReference {
    private static final long serialVersionUID = 1L;

    List<CtTypeReference<?>> bounds = new ArrayList<CtTypeReference<?>>();

    boolean upper = true;

    public CtTypeParameterReferenceImpl() {
        super();
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtTypeParameterReference(this);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    public List<CtTypeReference<?>> getBounds() {
        return bounds;
    }

    public boolean isUpper() {
        return upper;
    }

    public void setBounds(List<CtTypeReference<?>> bounds) {
        this.bounds = bounds;
    }

    public void setUpper(boolean upper) {
        this.upper = upper;
    }

    @Override
    public boolean isAssignableFrom(CtTypeReference<?> type) {
        return false;
    }

    @Override
    public boolean isSubtypeOf(CtTypeReference<?> type) {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public void setSimpleName(String simplename) {
        this.simplename = simplename;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Object> getActualClass() {
        if (isUpper()) {
            if (getBounds().isEmpty()) {
                return Object.class;
            }
            return (Class) getBounds().get(0).getActualClass();
        }
        return null;
    }

}
