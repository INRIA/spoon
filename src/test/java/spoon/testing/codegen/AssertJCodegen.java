package spoon.testing.codegen;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.metamodel.Metamodel;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.testing.assertions.declaration.CtModuleAssert;

import javax.annotation.processing.Generated;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class AssertJCodegen {


    public static final Set<String> MODEL_CLASS_PACKAGES = Set.of(
            "code",
            "declaration",
            "reference"
    );
    private static final String GEN_ROOT = "src/test/java/spoon/testing/assertions";
    @Test
    @Tag("codegen")
    void generateAssertionClasses() throws IOException {
        Launcher launcher = new Launcher();
        launcher.addInputResource(GEN_ROOT);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setAutoImports(true);
        CtModel ctModel = launcher.buildModel();

        List<CtType<?>> alreadyGeneratedTypes = findAlreadyGeneratedTypes(ctModel);
        CtTypeReference<AbstractAssert> reference = launcher.getFactory().Type().createReference(AbstractAssert.class);
        Set<CtType<?>> metamodel = Metamodel.getAllMetamodelInterfaces();
        for (CtType<?> ctType : metamodel) {
            CtClass<?> newAssertionClass = generateAssertionBaseClass(ctType);

            Optional<CtType<?>> matchedType = findExistingType(alreadyGeneratedTypes, newAssertionClass);
            if(matchedType.isPresent()) {
                CtType<?> presentType = matchedType.get();
                Set<CtType<?>> allSuperTypes = getAllSuperTypes(ctType);
                Set<String> executablesBySignature = newAssertionClass.getDeclaredExecutables().stream().map(CtExecutableReference::getSignature).collect(Collectors.toSet());

                for (CtType<?> superType : allSuperTypes) {
                    Optional<CtType<?>> superTypeOptional = findAlreadyGeneratedSuperType(superType, alreadyGeneratedTypes);
                    if(superTypeOptional.isPresent()) {
                        CtType<?> presentSuperType = superTypeOptional.get();
                        // copy all methods from the super type that are not already present in the new assertion class
                        presentSuperType.getMethods().stream()
                                .filter(v -> v.getType().isSubtypeOf(reference))
                                .filter(v -> !executablesBySignature.contains(v.getSignature()))
                                .map(CtMethod::clone)
                                .map(v -> v.<CtMethod<?>>setType(newAssertionClass.getReference()))
                                .map(v -> v.<CtMethod<?>>addAnnotation(launcher.getFactory().Code().createAnnotation(launcher.getFactory().Type().createReference(Generated.class))))
                                .forEach(newAssertionClass::addMethod);
                    }
                }
                // copy the doc comment from the super type
                newAssertionClass.setComments(presentType.getComments());
                presentType.getMethods().stream()
                        .filter(v -> v.getType().isSubtypeOf(reference))
                        .filter(v -> !executablesBySignature.contains(v.getSignature()))
                        .forEach(newAssertionClass::addMethod);
                SortedSet<CtMethod<?>> set = new TreeSet<>(Comparator.comparing(CtMethod::getSimpleName));
                newAssertionClass.getMethods().stream().sorted(Comparator.comparing(CtMethod::getSimpleName)).forEach(set::add);
                newAssertionClass.setMethods(Collections.emptySet());
                newAssertionClass.setMethods(set);
            }
            // write the new assertion class to disk
            //TODO: /r/n vs /n
            String targetLocation = GEN_ROOT + "/" + ctType.getQualifiedName().replace("spoon.reflect", "").replace(".", "/") + "Assert.java";
            Path path = Path.of(targetLocation);
            Files.createDirectories(path.getParent());
            Files.writeString(path.toAbsolutePath(), launcher.createPrettyPrinter().printTypes(newAssertionClass), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

    }

    private static Optional<CtType<?>> findAlreadyGeneratedSuperType(CtType<?> superType, List<CtType<?>> alreadyGeneratedTypes) {
        return alreadyGeneratedTypes.stream().filter(v -> v.getSimpleName().equals(superType.getSimpleName() + "Assert")).findFirst();
    }

    private static Optional<CtType<?>> findExistingType(List<CtType<?>> alreadyGeneratedTypes, CtClass<?> newAssertionClass) {
        return alreadyGeneratedTypes.stream().filter(v -> v.getSimpleName().equals(newAssertionClass.getSimpleName())).findFirst();
    }

    private static List<CtType<?>> findAlreadyGeneratedTypes(CtModel ctModel) {
        return ctModel.getAllPackages().stream().filter(ctPackage -> MODEL_CLASS_PACKAGES.contains(ctPackage.getSimpleName())).flatMap(v -> v.getTypes().stream()).collect(Collectors.toList());
    }


    /**
     * Retrieves all super types of a given CtType.
     *
     * @param ctType the CtType for which to retrieve the super types
     * @return a Set of CtType objects representing the super types
     */
    private Set<CtType<?>> getAllSuperTypes(CtType<?> ctType) {
        Set<CtType<?>> result = new HashSet<>();
        ctType.getSuperInterfaces().forEach(v -> {
            CtType<?> typeDeclaration = v.getTypeDeclaration();
            result.add(typeDeclaration);
            result.addAll(getAllSuperTypes(typeDeclaration));
        });
        CtTypeReference<?> superclass = ctType.getSuperclass();
        if(superclass != null) {
            CtType<?> superType = superclass.getTypeDeclaration();
            result.add(superType);
            result.addAll(getAllSuperTypes(superType));
        }
        result.remove(ctType);
        return result;
    }



    private CtClass<?> generateAssertionBaseClass(CtType<?> ctType) {
        CtClass<Object> assertionClass = ctType.getFactory().Class().create("spoon.testing.assertions"+ctType.getQualifiedName().replace("spoon.reflect", "") + "Assert");
        assertionClass.setModifiers(Set.of(ModifierKind.PUBLIC));

        @SuppressWarnings("rawtypes")
        CtTypeReference<AbstractAssert> typeReference = ctType.getFactory().Type().createReference(AbstractAssert.class);
        List<CtTypeReference<?>> generics = new ArrayList<>();
        generics.add(assertionClass.getReference());
        generics.add(ctType.getReference());

        typeReference.setActualTypeArguments(generics);
        assertionClass.setSuperclass(typeReference);
        createConstructor(assertionClass);
        return assertionClass;
    }

    private void createConstructor(CtClass<?> assertionClass) {
        Factory factory = assertionClass.getFactory();
        CtTypeReference<?> actualElementType = assertionClass.getSuperclass().getActualTypeArguments().get(1);
        CtMethod<Object> method = factory.createMethod();

        CtBlock<Object> codeBlock = factory.createBlock();
        CtParameter<Object> parameter = factory.createParameter();
        parameter.setType(actualElementType);
        parameter.setSimpleName("actual");
        method.addParameter(parameter);
        CtCodeSnippetStatement codeSnippetStatement = factory.Code().createCodeSnippetStatement("super(actual, " + assertionClass.getSimpleName() + ".class)");
        codeBlock.addStatement(codeSnippetStatement);
        method.setBody(codeBlock);
        @SuppressWarnings("rawtypes") CtConstructor constructor = factory.createConstructor(assertionClass, method);
        constructor.setComments(List.of());
        constructor.setModifiers(Set.of(ModifierKind.PUBLIC));
        assertionClass.addConstructor(constructor);
    }

}
