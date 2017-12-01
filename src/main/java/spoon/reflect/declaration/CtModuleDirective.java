package spoon.reflect.declaration;

/**
 * Represents a directive of a {@link CtModule}
 *
 * The directives of a module declaration specify the module's dependences on other modules {@link CtModuleRequirement},
 * the packages it makes available to other modules {@link CtPackageExport},
 * the services it consumes {@link CtUsedService},
 * and the services it provides {@link CtProvidedService}.
 */
public interface CtModuleDirective extends CtElement {


}
