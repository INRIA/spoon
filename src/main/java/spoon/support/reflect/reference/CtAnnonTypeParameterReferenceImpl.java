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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtAnnonTypeParameterReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtAnnonTypeParameterReferenceImpl extends CtTypeParameterReferenceImpl
        implements CtAnnonTypeParameterReference {
	
    private static final long serialVersionUID = 1L;
    
	private CtTypeReference<Object> ref;
	
    public CtAnnonTypeParameterReferenceImpl() {
        super();
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtTypeParameterReference(this);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return ref.getAnnotation(annotationType);
    }

    @Override
    public List<CtTypeReference<?>> getBounds() {
        return null;
    }

    @Override
    public boolean isUpper() {
        return false;
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
    public Class<Object> getActualClass() {
        return ref.getActualClass();
    }
    
    @Override
    public CtTypeReference<?> box() {
    	return ref.box();
    }
    
    @Override
    public int compareTo(CtReference o) {
    	return ref.compareTo(o);
    }

    @Override
    public List<CtTypeReference<?>> getActualTypeArguments() {
    	return null;
    }
    
    @Override
    public Collection<CtExecutableReference<?>> getAllExecutables() {
    	return null;
    }
    
    @Override
    public Collection<CtFieldReference<?>> getAllFields() {
    	return null;
    }
    
    @Override
    public Annotation[] getAnnotations() {
    	return null;
    }
    
    @Override
    public CtSimpleType<Object> getDeclaration() {
    	return null;
    }
    
    @Override
    public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
    	return null;
    }
    
    @Override
    public Collection<CtFieldReference<?>> getDeclaredFields() {
    	return null;
    }
    
    @Override
    public CtTypeReference<?> getDeclaringType() {
    	return ref.getDeclaringType();
    }
    
    @Override
    public Factory getFactory() {
    	return factory;
    }
    
//    @Override
//    public Set<ModifierKind> getModifiers() {
//    	return ref.getModifiers();
//    }
    
    @Override
    public CtPackageReference getPackage() {
    	return ref.getPackage();
    }
    
    @Override
    public String getQualifiedName() {
    	return ref.getQualifiedName();
    }
    @Override
    public String getSimpleName() {
    	return ref.getSimpleName();
    }
    @Override
    public CtTypeReference<?> getSuperclass() {
    	return null;
    }
    @Override
    public Set<CtTypeReference<?>> getSuperInterfaces() {
    	return null;
    }
    @Override
    public boolean isAnonymous() {
    	return ref.isAnonymous();
    }
    @Override
    public boolean isSuperReference() {
    	return ref.isSuperReference();
    }

    @Override
    public CtTypeReference<?> unbox() {
    	return ref.unbox();
    }
    
	@SuppressWarnings("unchecked")
	public void setRealRef(CtTypeReference<?> ctTypeReference) {
		ref = (CtTypeReference<Object>)ctTypeReference;
	}
	
    @Override
    public void setActualTypeArguments(
    		List<CtTypeReference<?>> actualTypeArguments) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }
    
    @Override
    public void setDeclaringType(CtTypeReference<?> declaringType) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }
    
    @Override
    public void setPackage(CtPackageReference pack) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }
    
    @Override
    public void setSuperReference(boolean b) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }

    @Override
    public void setSimpleName(String simplename) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }

    @Override
    public void setBounds(List<CtTypeReference<?>> bounds) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }

    @Override
    public void setUpper(boolean upper) {
        throw new UnsupportedOperationException("this is a fake reference, use setRealRef instead.");
    }

}
