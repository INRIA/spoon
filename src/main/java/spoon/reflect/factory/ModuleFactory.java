package spoon.reflect.factory;

import spoon.reflect.CtModelImpl;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtModuleRequirement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtModuleImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModuleFactory extends SubFactory implements Serializable {

    public static class CtUnnamedModule extends CtModuleImpl {
        {
            this.setSimpleName(CtModule.TOP_LEVEL_MODULE_NAME);
            this.setParent(new CtElementImpl() {
                @Override
                public void accept(CtVisitor visitor) {

                }

                @Override
                public CtElement getParent() throws ParentNotInitializedException {
                    return null;
                }

                @Override
                public Factory getFactory() {
                    return CtUnnamedModule.this.getFactory();
                }
            });

            this.setRootPackage(new CtModelImpl.CtRootPackage());
        }

        @Override
        public <T extends CtNamedElement> T setSimpleName(String name) {
            if (name == null) {
                return (T) this;
            }

            if (name.equals(CtModule.TOP_LEVEL_MODULE_NAME)) {
                return super.setSimpleName(name);
            }

            return (T) this;
        }

        @Override
        public String toString() {
            return CtModule.TOP_LEVEL_MODULE_NAME;
        }
    }

    final Map<String,CtModule> allModules = new HashMap<>();

    public ModuleFactory(Factory factory) {
        super(factory);
        CtModule unnamedModule = new CtUnnamedModule();
        unnamedModule.setFactory(factory);
        allModules.put(CtModule.TOP_LEVEL_MODULE_NAME, unnamedModule);
    }

    public Collection<CtModule> getAllModules() {
        return Collections.unmodifiableCollection(allModules.values());
    }

    public CtModule getOrCreate(String moduleName) {
        if (moduleName == null || moduleName.isEmpty()) {
            return allModules.get(CtModule.TOP_LEVEL_MODULE_NAME);
        }

        if (!allModules.containsKey(moduleName)) {
            allModules.put(moduleName, factory.Core().createModule().setSimpleName(moduleName));
        }
        return allModules.get(moduleName);
    }

    public CtModuleReference createReference(CtModule module) {
        return factory.Core().createModuleReference().setSimpleName(module.getSimpleName());
    }

    public CtModuleRequirement createModuleRequirement(CtModule module) {
        return factory.Core().createModuleRequirement().setSimpleName(module.getSimpleName());
    }
}
