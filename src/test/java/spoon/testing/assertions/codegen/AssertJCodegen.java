package spoon.testing.assertions.codegen;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.metamodel.Metamodel;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.testing.assertions.SpoonAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class AssertJCodegen {

	private static final String GEN_ROOT = "src/test/java/";

	@Test
	@Tag("codegen")
	void generateCode() throws IOException {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setComplianceLevel(17);

		Metamodel instance = Metamodel.getInstance();
		Set<CtType<?>> allMetamodelInterfaces = Metamodel.getAllMetamodelInterfaces();
		for (CtType<?> type : allMetamodelInterfaces) {
			if (!(type instanceof CtInterface<?>)) continue;
			CtInterface<?> anInterface = createInterface(type);
			//TODO:copy methods if interface is already present
			writeType(anInterface, launcher);
		}
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
		return ctInterface;
	}
}
