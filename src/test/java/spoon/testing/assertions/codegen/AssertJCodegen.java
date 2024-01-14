package spoon.testing.assertions.codegen;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.metamodel.Metamodel;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.testing.assertions.SpoonAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssertJCodegen {

	private static final String GEN_ROOT = "src/test/java/";

	@Test
	@Tag("codegen")
	void generateCode() throws IOException {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setComplianceLevel(17);
		Map<String, List<CtType<?>>> existingInterfaces = getExistingInterfaces();
		Metamodel instance = Metamodel.getInstance();
		Set<CtType<?>> allMetamodelInterfaces = Metamodel.getAllMetamodelInterfaces();
		for (CtType<?> type : allMetamodelInterfaces) {
			if (!(type instanceof CtInterface<?>)) continue;
			CtInterface<?> anInterface = createInterface(type);
			copyExistingMethods(anInterface, existingInterfaces);
			//TODO:copy methods if interface is already present
			writeType(anInterface, launcher);
		}
	}


	/**
	 * Copies existing methods from a given interface to another interface.
	 *
	 * @param anInterface         the interface to copy existing methods to
	 * @param existingInterfaces a map of existing interfaces where the key is the qualified name of the interface and the value is a list of CtType objects representing the interfaces
	 *
	 */
	private static void copyExistingMethods(CtInterface<?> anInterface, Map<String, List<CtType<?>>> existingInterfaces) {
		Set<String> existingMethods = anInterface.getDeclaredExecutables().stream().map(CtExecutableReference::getSignature).collect(Collectors.toSet());
		if (existingInterfaces.containsKey(anInterface.getQualifiedName())) {
			List<CtType<?>> ctTypes = existingInterfaces.get(anInterface.getQualifiedName());
			if (ctTypes.size() == 1) {
				CtType<?> existingInterface = ctTypes.get(0);
				existingInterface.getMethods().forEach(ctMethod -> {
					if (!existingMethods.contains(ctMethod.getSignature())) {
						anInterface.addMethod(ctMethod.clone());
					}
				});
			}
		}
	}

	private Map<String, List<CtType<?>>> getExistingInterfaces() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.addInputResource("src/test/java/spoon/testing/assertions");
		launcher.buildModel();
		return launcher.getModel().getAllTypes().stream().filter(v -> v.getPackage().getQualifiedName().equals("spoon.testing.assertions")).collect(Collectors.groupingBy(CtTypeInformation::getQualifiedName));
	}

	private static void writeType(CtType<?> type, Launcher launcher) throws IOException {
		String targetLocation = GEN_ROOT + "/" + type.getQualifiedName().replace(".", "/") + ".java";
		Path path = Path.of(targetLocation);
		Files.createDirectories(path.getParent());
		Files.writeString(path.toAbsolutePath(), launcher.createPrettyPrinter().printTypes(type));
		System.out.println(type.toStringWithImports());
	}

	/*
	private interface CtTypeAssertInterface<A extends AbstractAssert<A, W>, W extends CtType<?>> extends SpoonAssert<A, W> {
		default A hasSimpleName(String simpleName) {
			self().isNotNull();
			if (!actual().getSimpleName().equals(simpleName)) {
				failWithMessage("Expected parent to be <%s> but was <%s>", "parent", actual().getParent());
			}
			return self();
		}

	}
	 */
	CtInterface<?> createInterface(CtType<?> type) {
		Factory factory = type.getFactory();
		CtInterface<?> ctInterface = factory.createInterface("spoon.testing.assertions." + type.getSimpleName() + "AssertInterface");
		CtTypeReference<?> abstractAssertRef = factory.createCtTypeReference(AbstractAssert.class);
		CtTypeParameter a = factory.createTypeParameter().setSuperclass(abstractAssertRef).setSimpleName("A");

		// add the wildcard type parameter to the type reference
		CtTypeReference<?> ctElement = type.getReference();
		if(ctElement.getTypeDeclaration() != null) {
			ctElement.getTypeDeclaration().getFormalCtTypeParameters().forEach(ctTypeParameter -> ctElement.addActualTypeArgument(factory.createWildcardReference()));
		}
		CtTypeParameter w = factory.createTypeParameter().setSuperclass(ctElement).setSimpleName("W");

		abstractAssertRef
			.addActualTypeArgument(a.getReference())
			.addActualTypeArgument(w.getReference());
		ctInterface
			.addFormalCtTypeParameter(a)
			.addFormalCtTypeParameter(w);
		CtTypeReference<?> spoonAssertRef = factory.createCtTypeReference(SpoonAssert.class);
		spoonAssertRef
			.addActualTypeArgument(a.getReference())
			.addActualTypeArgument(w.getReference());
		ctInterface.setSuperInterfaces(Set.of(spoonAssertRef));
		return ctInterface;
	}
}
