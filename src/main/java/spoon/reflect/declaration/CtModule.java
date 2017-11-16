package spoon.reflect.declaration;

import spoon.reflect.reference.CtModuleExport;
import spoon.reflect.reference.CtModuleProvidedService;
import spoon.reflect.reference.CtModuleRequirement;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

public interface CtModule extends CtNamedElement {

    /**
     * The name for the top level module.
     */
    String TOP_LEVEL_MODULE_NAME = "unnamed module";

    boolean isOpenModule();

    <T extends CtModule> T setIsOpenModule(boolean openModule);

    Set<CtTypeReference> getConsumedServices();

    <T extends CtModule> T setConsumedServices(Set<CtTypeReference> consumedServices);

    Set<CtModuleExport> getExportedPackages();

    <T extends CtModule> T setExportedPackages(Set<CtModuleExport> exportedPackages);

    Set<CtModuleExport> getOpenedPackages();

    <T extends CtModule> T setOpenedPackages(Set<CtModuleExport> openedPackages);

    Set<CtModuleRequirement> getRequiredModules();

    <T extends CtModule> T setRequiredModules(Set<CtModuleRequirement> requiredModules);

    Set<CtModuleProvidedService> getProvidedServices();

    <T extends CtModule> T setProvidedServices(Set<CtModuleProvidedService> providedServices);

    CtPackage getRootPackage();

    <T extends CtModule> T setRootPackage(CtPackage rootPackage);
}
