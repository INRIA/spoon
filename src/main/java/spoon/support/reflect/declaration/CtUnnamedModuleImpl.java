package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtVisitor;

import java.util.Objects;

public class CtUnnamedModuleImpl extends CtModuleImpl {
    public CtUnnamedModuleImpl(Factory factory) {
        super(factory);
        this.setSimpleName(CtModuleImpl.TOP_LEVEL_MODULE_NAME);
    }

    @Override
    public boolean isUnnamedModule() {
        return true;
    }

    @Override
    public <T extends CtNamedElement> T setSimpleName(String name) {
        return Objects.equals(name, CtModuleImpl.TOP_LEVEL_MODULE_NAME) ? super.setSimpleName(name) : (T) this;
    }

    @Override
    public String toString() {
        return this.getSimpleName();
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtModule(this);
    }
}