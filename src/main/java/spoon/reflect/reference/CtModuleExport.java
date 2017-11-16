package spoon.reflect.reference;

import java.util.Set;

public interface CtModuleExport extends CtPackageReference {

    Set<CtModuleReference> getTargetExport();

    <T extends CtModuleExport> T setTargetExport(Set<CtModuleReference> targetExport);
}
