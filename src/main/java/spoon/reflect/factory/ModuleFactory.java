package spoon.reflect.factory;

import spoon.reflect.declaration.CtModule;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtModuleRequirement;

import java.io.Serializable;

public class ModuleFactory extends SubFactory implements Serializable {

    public ModuleFactory(Factory factory) {
        super(factory);
    }

    public CtModule create(String moduleName) {
        return factory.Core().createModule().setSimpleName(moduleName);
    }

    public CtModuleReference createReference(CtModule module) {
        return factory.Core().createModuleReference().setSimpleName(module.getSimpleName());
    }

    public CtModuleRequirement createModuleRequirement(CtModule module) {
        return factory.Core().createModuleRequirement().setSimpleName(module.getSimpleName());
    }
}
