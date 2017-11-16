package spoon.support.reflect.reference;

import spoon.reflect.declaration.CtModule;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.reflect.AnnotatedElement;

public class CtModuleReferenceImpl extends CtReferenceImpl implements CtModuleReference {

    public CtModuleReferenceImpl() {
        super();
    }

    @Override
    public CtModule getDeclaration() {
        return null;
    }

    @Override
    protected AnnotatedElement getActualAnnotatedElement() {
        return null;
    }

    @Override
    public void accept(CtVisitor visitor) {

    }

    @Override
    public CtModuleReference clone() {
        return (CtModuleReference) super.clone();
    }
}
