package spoon.reflect.reference;

import spoon.reflect.declaration.CtModule;

public interface CtModuleReference extends CtReference {

    CtModule getDeclaration();

    @Override
    CtModuleReference clone();
}
