package spoon.testing.assertions.codegen;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.metamodel.Metamodel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.testing.assertions.SpoonAssert;

public class AssertJCodegen {

	private static final String GEN_ROOT = "src/test/java/";
	private static final Class<?> ASSERT_J_SUPERCLASS = AbstractObjectAssert.class;

	record AssertModelPair(CtClass<?> assertClass, CtInterface<?> modelInterface) {}

	@Test
	@Tag("codegen")
	void generateCode() throws IOException {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setComplianceLevel(17);
		Map<String, List<CtType<?>>> existingInterfaces = getExistingInterfaces();
		Set<CtType<?>> allMetamodelInterfaces = Metamodel.getAllMetamodelInterfaces();
		// Model -> AssertInterface
		Map<CtInterface<?>, CtInterface<?>> map = new HashMap<>();
		for (CtType<?> type : allMetamodelInterfaces) {
			if (!(type instanceof CtInterface<?> original)) {
				continue;
			}
			CtInterface<?> assertInterface = createInterface(type);
			copyExistingMethods(assertInterface, existingInterfaces);
			map.put(original, assertInterface);
		}
		List<AssertModelPair> assertClasses = new ArrayList<>();
		for (var entry : map.entrySet()) {
			CtInterface<?> assertInterface = entry.getValue();
			CtInterface<?> modelInterface = entry.getKey();
			Set<CtTypeReference<?>> interfaces = modelInterface.getSuperInterfaces();
			for (CtTypeReference<?> superInterface : interfaces) {
				CtInterface<?> superDeclaration = (CtInterface<?>) superInterface.getTypeDeclaration();
				CtInterface<?> superAssertInterface = map.get(superDeclaration);
				if (superAssertInterface != null) {
					CtTypeReference<?> reference = superAssertInterface.getReference();
					assertInterface
							.getFormalCtTypeParameters()
							.forEach(param -> reference.addActualTypeArgument(param.getReference()));
					assertInterface.addSuperInterface(reference);
				}
			}
			// TODO remove SpoonAssert interface if unneeded

			writeType(assertInterface, launcher);
			CtClass<?> impl = Metamodel.getImplementationOfInterface(modelInterface);
			if (impl == null) {
				continue;
			}
			CtClass<?> anAssert = createAssert(modelInterface, assertInterface);
			assertClasses.add(new AssertModelPair(anAssert, modelInterface));
			writeType(anAssert, launcher);
		}
		CtClass<?> spoonAssertions = createSpoonAssertions(assertClasses, launcher.getFactory());
		writeType(spoonAssertions, launcher);
	}

	private CtClass<?> createSpoonAssertions(List<AssertModelPair> assertClasses, Factory factory) {
		CtClass<?> spoonAssertions = factory.createClass("spoon.testing.assertions.SpoonAssertions");
		spoonAssertions.addModifier(ModifierKind.PUBLIC).addModifier(ModifierKind.FINAL);
		CtMethod<?> baseMethod = factory.createMethod();
		baseMethod.setModifiers(Set.of(ModifierKind.PUBLIC, ModifierKind.STATIC));
		baseMethod.setSimpleName("assertThat");

		for (var pair : assertClasses) {
			CtMethod<?> assertThatMethod = baseMethod.clone();
			assertThatMethod.setType(pair.assertClass().getReference());
			String simpleName = pair.modelInterface().getSimpleName();
			String paramName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
			factory.createParameter(assertThatMethod, createWildcardedReference(pair.modelInterface()), paramName);
			CtBlock<?> block = factory.createBlock();

			@SuppressWarnings("rawtypes")
			CtConstructorCall constructorCall = factory.createConstructorCall(
					pair.assertClass().getReference(), factory.createCodeSnippetExpression(paramName));
			@SuppressWarnings("unchecked")
			CtStatement ret = factory.<CtReturn<?>>createReturn().setReturnedExpression(constructorCall);
			block.addStatement(ret);
			assertThatMethod.setBody(block);
			spoonAssertions.addMethod(assertThatMethod);
		}
		return spoonAssertions;
	}

	/**
	 * Copies existing methods from a given interface to another interface.
	 *
	 * @param anInterface        the interface to copy existing methods to
	 * @param existingInterfaces a map of existing interfaces where the key is the qualified name of the interface and the value is a list of CtType objects representing the interfaces
	 */
	private static void copyExistingMethods(
			CtInterface<?> anInterface, Map<String, List<CtType<?>>> existingInterfaces) {
		Set<String> existingMethods = anInterface.getDeclaredExecutables().stream()
				.map(CtExecutableReference::getSignature)
				.collect(Collectors.toSet());
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
		return launcher.getModel().getAllTypes().stream()
				.filter(v -> v.getPackage().getQualifiedName().equals("spoon.testing.assertions"))
				.collect(Collectors.groupingBy(CtTypeInformation::getQualifiedName));
	}

	private static void writeType(CtType<?> type, Launcher launcher) throws IOException {
		String targetLocation = GEN_ROOT + "/" + type.getQualifiedName().replace(".", "/") + ".java";
		Path path = Path.of(targetLocation);
		Files.createDirectories(path.getParent());
		Files.writeString(path.toAbsolutePath(), launcher.createPrettyPrinter().printTypes(type));
		System.out.println(type.toStringWithImports());
	}

	private CtClass<?> createAssert(CtInterface<?> modelInterface, CtInterface<?> assertInterface) {
		Factory factory = assertInterface.getFactory();
		CtClass<?> ctClass =
				factory.createClass("spoon.testing.assertions." + modelInterface.getSimpleName() + "Assert");
		ctClass.addModifier(ModifierKind.PUBLIC);
		CtTypeReference<Object> abstractAssertRef = factory.createCtTypeReference(ASSERT_J_SUPERCLASS);
		abstractAssertRef
				.addActualTypeArgument(ctClass.getReference())
				.addActualTypeArgument(createWildcardedReference(modelInterface));
		ctClass.setSuperclass(abstractAssertRef);
		CtTypeReference<?> reference = assertInterface.getReference();
		for (CtTypeReference<?> typeArgument : abstractAssertRef.getActualTypeArguments()) {
			reference.addActualTypeArgument(typeArgument.clone());
		}
		ctClass.addSuperInterface(reference);
		createConstructor(ctClass);
		createMethods(ctClass, createWildcardedReference(modelInterface));
		return ctClass;
	}

	private void createMethods(CtClass<?> ctClass, CtTypeReference<?> model) {
		Factory factory = ctClass.getFactory();
		CtMethod<?> selfMethod = factory.createMethod();
		selfMethod.setSimpleName("self");
		selfMethod.setModifiers(Set.of(ModifierKind.PUBLIC));
		CtAnnotation<Annotation> overrideAnnotation =
				factory.createAnnotation(factory.createCtTypeReference(Override.class));
		selfMethod.addAnnotation(overrideAnnotation.clone());
		CtBlock<Object> codeBlock = factory.createBlock();
		codeBlock.addStatement(
				factory.createReturn().setReturnedExpression(factory.createCodeSnippetExpression("this")));
		selfMethod.setBody(codeBlock);
		selfMethod.setType(ctClass.getReference());

		CtMethod<?> actualMethod = factory.createMethod();
		actualMethod.setSimpleName("actual");
		actualMethod.setModifiers(Set.of(ModifierKind.PUBLIC));
		actualMethod.addAnnotation(overrideAnnotation.clone());
		codeBlock = factory.createBlock();
		codeBlock.addStatement(
				factory.createReturn().setReturnedExpression(factory.createCodeSnippetExpression("this.actual")));
		actualMethod.setBody(codeBlock);
		actualMethod.setType(model);

		CtMethod<?> failWithMessageMethod = factory.createMethod();
		failWithMessageMethod.setSimpleName("failWithMessage");
		failWithMessageMethod.setModifiers(Set.of(ModifierKind.PUBLIC));
		failWithMessageMethod.addAnnotation(overrideAnnotation.clone());
		codeBlock = factory.createBlock();
		codeBlock.addStatement(factory.createCodeSnippetStatement("super.failWithMessage(errorMessage, arguments)"));
		failWithMessageMethod.setBody(codeBlock);
		failWithMessageMethod.setType(factory.Type().voidPrimitiveType());
		factory.createParameter(failWithMessageMethod, factory.Type().stringType(), "errorMessage");
		factory.createParameter(
						failWithMessageMethod,
						factory.createArrayReference(factory.Type().objectType()),
						"arguments")
				.setVarArgs(true);

		ctClass.addMethod(selfMethod).addMethod(actualMethod).addMethod(failWithMessageMethod);
	}

	private <T> void createConstructor(CtClass<T> assertionClass) {
		Factory factory = assertionClass.getFactory();
		CtTypeReference<?> actualElementType =
				assertionClass.getSuperclass().getActualTypeArguments().get(1);
		CtMethod<Object> method = factory.createMethod();

		CtBlock<Object> codeBlock = factory.createBlock();
		CtParameter<Object> parameter = factory.createParameter();
		parameter.setType(actualElementType);
		parameter.setSimpleName("actual");
		method.addParameter(parameter);
		CtCodeSnippetStatement codeSnippetStatement = factory.Code()
				.createCodeSnippetStatement("super(actual, " + assertionClass.getSimpleName() + ".class)");
		codeBlock.addStatement(codeSnippetStatement);
		method.setBody(codeBlock);
		@SuppressWarnings("unchecked")
		CtConstructor<T> constructor = factory.createConstructor(assertionClass, method);
		constructor.setComments(List.of());
		assertionClass.addConstructor(constructor);
	}

	CtInterface<?> createInterface(CtType<?> type) {
		Factory factory = type.getFactory();
		CtInterface<?> ctInterface =
				factory.createInterface("spoon.testing.assertions." + type.getSimpleName() + "AssertInterface");
		CtTypeReference<?> abstractAssertRef = factory.createCtTypeReference(ASSERT_J_SUPERCLASS);
		CtTypeParameter a =
				factory.createTypeParameter().setSuperclass(abstractAssertRef).setSimpleName("A");

		CtTypeReference<?> ctElement = createWildcardedReference(type);
		CtTypeParameter w =
				factory.createTypeParameter().setSuperclass(ctElement).setSimpleName("W");

		abstractAssertRef.addActualTypeArgument(a.getReference()).addActualTypeArgument(w.getReference());
		ctInterface.addFormalCtTypeParameter(a).addFormalCtTypeParameter(w);
		CtTypeReference<?> spoonAssertRef = factory.createCtTypeReference(SpoonAssert.class);
		spoonAssertRef.addActualTypeArgument(a.getReference()).addActualTypeArgument(w.getReference());
		ctInterface.setSuperInterfaces(Set.of(spoonAssertRef));
		return ctInterface;
	}

	private static CtTypeReference<?> createWildcardedReference(CtType<?> type) {
		// add the wildcard type parameter to the type reference
		Factory factory = type.getFactory();
		CtTypeReference<?> ctElement = type.getReference();
		type.getFormalCtTypeParameters()
				.forEach(ctTypeParameter -> ctElement.addActualTypeArgument(factory.createWildcardReference()));
		return ctElement;
	}
}
