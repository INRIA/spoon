package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtImport;

import java.util.Set;

import static spoon.reflect.path.CtRole.IMPORT;

/**
 * An ImportHolder is any element that can support an header with an import.
 * Currently it can be a CtType or a CtPackage via a package-info.ava
 */
public interface CtImportHolder {

    @PropertyGetter(role = IMPORT)
    Set<CtImport> getImports();

    @PropertySetter(role = IMPORT)
    <C extends CtImportHolder> C addImport(CtImport ctImport);

    @PropertySetter(role = IMPORT)
    <C extends CtImportHolder> C setImports(Set<CtImport> ctImport);
}
