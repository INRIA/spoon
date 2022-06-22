package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.CtVisitor;

import java.util.Objects;

public class CtRootPackageImpl extends CtPackageImpl {
    public CtRootPackageImpl(CtModule module) {
        super(module);
        this.setSimpleName(CtPackage.TOP_LEVEL_PACKAGE_NAME);
        this.setParent(module);
    }

    @Override
    public boolean isUnnamedPackage() {
        return true;
    }

    @Override
    public String getQualifiedName() {
        return "";
    }

    @Override
    public <T extends CtNamedElement> T setSimpleName(String name) {
        return Objects.equals(name, CtPackage.TOP_LEVEL_PACKAGE_NAME) ? super.setSimpleName(name) : (T) this;
    }

    @Override
    public String toString() {
        return this.getSimpleName();
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtPackage(this);
    }

    @Override
    public CtPackageImpl clone() {
        return (CtPackageImpl) super.clone();
    }
}