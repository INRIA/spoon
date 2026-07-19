package spoon.test.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.SerializationModelStreamer;

class TypeParameterErasureRecursionTest {
	@Test
	void genericAnonymousIteratorTraversalTerminates() {
		// contract: type-parameter erasure does not re-enter executable declaration lookup
		// (#6802)
		// given
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/TypeParameterErasureRecursion.java");

		// when
		var model = launcher.buildModel();

		// then
		assertThat(model.getElements(element -> true))
				.allSatisfy(element -> assertThatCode(element::toString).doesNotThrowAnyException());
	}

	@Test
	void wildcardErasureUsesItsJavaLanguageBound() {
		// contract: wildcard erasure does not require a type-parameter declaration
		// (#6802)
		// given
		Launcher launcher = new Launcher();
		CtWildcardReference wildcard = launcher.getFactory().Core().createWildcardReference();
		wildcard.setBoundingType(launcher.getFactory().Type().stringType());

		// when / then
		assertThat(wildcard.getTypeErasure().getQualifiedName()).isEqualTo("java.lang.String");
		assertThat(wildcard.setUpper(false).getTypeErasure().getQualifiedName()).isEqualTo("java.lang.Object");
	}

	@Test
	void adaptedExecutableTypeParameterResolvesToLexicalDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/TypeParameterErasureRecursion.java");

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "apply");
		CtWildcardReference wildcard = (CtWildcardReference) invocation.getExecutable().getParameters().get(0);
		CtTypeParameterReference adaptedTypeParameter = (CtTypeParameterReference) wildcard.getBoundingType();

		// then
		assertThat(adaptedTypeParameter.getDeclaration().getParent(CtMethod.class).getSimpleName())
				.isEqualTo("mapAll");
	}

	@Test
	void adaptedForEachTypeParameterRetainsItsDeclaringTypeParameter() {
		// given
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/TypeParameterErasureRecursion.java");

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "forEach");
		CtWildcardReference consumerArgument = (CtWildcardReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);
		CtTypeParameterReference adaptedTypeParameter =
				(CtTypeParameterReference) consumerArgument.getBoundingType();

		// then
		assertThat(adaptedTypeParameter)
				.as("adapted for-each type parameter [declaration owner, erasure]")
				.extracting(
						type -> type.getDeclaration().getParent(CtType.class).getQualifiedName(),
						type -> type.getTypeErasure().getQualifiedName())
				.containsExactly("java.lang.Iterable", "java.lang.Object");
	}

	@Test
	void nestedExecutableTypeParameterResolvesToCalleeDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends Number> void callee(java.util.List<T> value) {}
				<T extends Number> void caller(java.util.List<T> value) { callee(value); }
			}
			""", "GenericProbe.java"));

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter.getDeclaration().getParent(CtMethod.class).getSimpleName())
				.isEqualTo("callee");
	}

	@Test
	void nestedDeclaringTypeParameterDoesNotResolveToShadowingCallerDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe<T> {
				void callee(java.util.List<T> value) {}
				<T> void caller() { callee(null); }
			}
			""", "GenericProbe.java"));

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter.getDeclaration())
				.isSameAs(model.getAllTypes().iterator().next().getFormalCtTypeParameters().get(0));
	}

	@Test
	void adaptedCallerTypeParameterDoesNotResolveToUnrelatedCalleeParameterWithSameName() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends CharSequence> void callee(java.util.List<T> value) {}
				<T extends Number> void caller(java.util.List<T> value) { callee(value); }
			}
			""", "GenericProbe.java"));
		var model = launcher.buildModel();
		CtMethod<?> caller = model.getAllTypes().iterator().next().getMethodsByName("caller").get(0);
		CtInvocation<?> invocation = invocationNamed(caller, "callee");
		CtTypeParameterReference adaptedTypeParameter = caller.getFormalCtTypeParameters().get(0).getReference();
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(adaptedTypeParameter));

		// when
		var declaration = adaptedTypeParameter.getDeclaration();

		// then
		assertThat(declaration).isSameAs(caller.getFormalCtTypeParameters().get(0));
	}

	@Test
	@SuppressWarnings("unchecked")
	void generatedExecutableReferenceRetainsCalleeTypeParameter() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends Number> void callee(java.util.List<T> value) {}
				<T extends CharSequence> void caller() { callee(null); }
			}
			""", "GenericProbe.java"));
		var model = launcher.buildModel();
		CtType<?> type = model.getAllTypes().iterator().next();
		CtMethod<?> callee = type.getMethodsByName("callee").get(0);
		CtMethod<?> caller = type.getMethodsByName("caller").get(0);
		CtInvocation<?> invocation = invocationNamed(caller, "callee");
		CtTypeParameterReference calleeTypeParameter = callee.getFormalCtTypeParameters().get(0).getReference();
		callee.getParameters().get(0).getType().setActualTypeArguments(List.of(calleeTypeParameter));
		CtExecutableReference<Object> executableReference =
				(CtExecutableReference<Object>) callee.getReference();
		((CtInvocation<Object>) invocation).setExecutable(executableReference);

		// when
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) executableReference
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter)
				.as("generated executable type parameter")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(callee.getFormalCtTypeParameters().get(0));
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	void positionlessOverloadsRetainTheCalleeTypeParameter() {
		// contract: declaration ownership distinguishes generated same-name, same-arity overloads
		// (#6802)
		// given
		Factory factory = new Launcher().getFactory();
		CtClass<?> type = factory.Class().create("GenericProbe");
		CtMethod<Void> callee = createPositionlessGenericMethod(
				factory, type, List.class, factory.Type().createReference(Number.class));
		CtMethod<Void> caller = createPositionlessGenericMethod(
				factory, type, Set.class, factory.Type().createReference(CharSequence.class));
		CtInvocation<Void> invocation = factory.Code().createInvocation(null, callee.getReference());
		caller.setBody(factory.Core().createBlock().addStatement(invocation));

		// when
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter.getMetadata("spoon.typeParameterDeclarationOwner"))
				.as("callee declaration-owner metadata")
				.isNotNull()
				.isNotEqualTo(caller.getFormalCtTypeParameters().get(0).getReference()
						.getMetadata("spoon.typeParameterDeclarationOwner"));
		assertThat(nestedTypeParameter)
				.as("positionless overload type parameter")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(callee.getFormalCtTypeParameters().get(0));
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	void positionlessTypeParameterOwnershipSurvivesExecutableRenaming() {
		// contract: declaration ownership remains attached to the declaration across model mutations
		// (#6802)
		// given
		Factory factory = new Launcher().getFactory();
		CtClass<?> type = factory.Class().create("MutableGenericProbe");
		CtMethod<Void> callee = createPositionlessGenericMethod(
				factory, type, List.class, factory.Type().createReference(Number.class));
		CtMethod<Void> caller = createPositionlessGenericMethod(
				factory, type, Set.class, factory.Type().createReference(CharSequence.class));
		CtInvocation<Void> invocation = factory.Code().createInvocation(null, callee.getReference());
		caller.setBody(factory.Core().createBlock().addStatement(invocation));
		CtTypeParameterReference callerTypeParameter = caller.getFormalCtTypeParameters().get(0).getReference();
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(callerTypeParameter));

		// when
		caller.setSimpleName("renamed");

		// then
		assertThat(callerTypeParameter)
				.as("type parameter from the renamed caller")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(caller.getFormalCtTypeParameters().get(0));
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.CharSequence");
				});
	}

	@Test
	void parsedTypeParameterOwnershipSurvivesExecutableRenaming() {
		// contract: source declaration ownership uses immutable source identity across model mutations
		// (#6802)
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
				class ParsedGenericProbe {
					<T extends CharSequence> void method(java.util.List<T> value) {}
					<T extends Number> void caller(java.util.Set<T> value) { method(null); }
				}
				""", "ParsedGenericProbe.java"));
		CtType<?> type = launcher.buildModel().getAllTypes().iterator().next();
		CtMethod<?> caller = type.getMethodsByName("caller").get(0);
		CtTypeParameter callerTypeParameter = caller.getFormalCtTypeParameters().get(0);
		CtTypeParameterReference callerReference = (CtTypeParameterReference) caller.getParameters().get(0)
				.getType().getActualTypeArguments().get(0);
		CtInvocation<?> invocation = invocationNamed(caller, "method");
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(callerReference));

		// when
		caller.setSimpleName("renamed");

		// then
		assertThat(callerReference)
				.as("parsed type parameter from the renamed caller")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(callerTypeParameter);
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	void parsedTypeParameterOwnershipSurvivesDeclaringTypeRenaming() {
		// contract: source type-parameter ownership uses immutable source identity across model mutations
		// (#6802)
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
				class ParsedTypeProbe<T extends Number> {
					<T extends CharSequence> void method(java.util.List<T> value) {}
					void caller(java.util.Set<T> value) { method(null); }
				}
				""", "ParsedTypeProbe.java"));
		CtType<?> type = launcher.buildModel().getAllTypes().iterator().next();
		CtTypeParameter typeParameter = type.getFormalCtTypeParameters().get(0);
		CtMethod<?> caller = type.getMethodsByName("caller").get(0);
		CtTypeParameterReference callerReference = (CtTypeParameterReference) caller.getParameters().get(0)
				.getType().getActualTypeArguments().get(0);
		CtInvocation<?> invocation = invocationNamed(caller, "method");
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(callerReference));

		// when
		type.setSimpleName("RenamedTypeProbe");

		// then
		assertThat(callerReference)
				.as("parsed type parameter from the renamed type")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(typeParameter);
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	void unnamedVirtualFileDoesNotAuthenticateMissingDeclarationOwner() {
		// contract: missing model-side source identity does not match a different shadowing declaration
		// (#6802)
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
				class VirtualGenericProbe<T extends Number> {
					void method(java.util.List<T> value) {}
					<T extends CharSequence> void caller() { method(null); }
				}
				"""));
		CtType<?> type = launcher.buildModel().getAllTypes().iterator().next();
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) type.getMethodsByName("method")
				.get(0).getParameters().get(0).getType().getActualTypeArguments().get(0);
		CtInvocation<?> invocation = invocationNamed(type.getMethodsByName("caller").get(0), "method");

		// when
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(nestedTypeParameter));

		// then
		assertThat(nestedTypeParameter)
				.as("callee parameter from an unnamed virtual compilation unit")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(type.getFormalCtTypeParameters().get(0));
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	void positionlessTypeParameterReferenceAllowsUntypedParameters() {
		// contract: partially built generated methods do not require parameter types for declaration ownership
		// (#6802)
		// given
		Factory factory = new Launcher().getFactory();
		CtClass<?> type = factory.Class().create("PartialGenericProbe");
		CtMethod<Void> method = factory.createMethod();
		method.setSimpleName("method");
		method.setType(factory.Type().voidPrimitiveType());
		type.addMethod(method);
		CtTypeParameter typeParameter = factory.createTypeParameter();
		typeParameter.setSimpleName("T");
		method.addFormalCtTypeParameter(typeParameter);
		CtParameter<?> parameter = factory.createParameter();
		parameter.setSimpleName("value");
		method.addParameter(parameter);

		// when
		CtTypeParameterReference reference = typeParameter.getReference();

		// then
		assertThat(reference.getDeclaration()).isSameAs(typeParameter);
	}

	@Test
	void directTypeParameterBoundRetainsItsDeclarationAfterReparenting() {
		// contract: using T as another parameter's bound must not change T's declaration ownership
		// (#6802)
		// given
		Factory factory = new Launcher().getFactory();
		CtClass<?> type = factory.Class().create("BoundGenericProbe");
		CtTypeParameter outerTypeParameter = factory.createTypeParameter();
		outerTypeParameter.setSimpleName("T");
		outerTypeParameter.setSuperclass(factory.Type().createReference(Number.class));
		type.addFormalCtTypeParameter(outerTypeParameter);
		CtMethod<Void> callee = createPositionlessGenericMethod(
				factory, type, List.class, factory.Type().createReference(CharSequence.class));
		CtMethod<Void> caller = factory.createMethod();
		caller.setSimpleName("caller");
		caller.setType(factory.Type().voidPrimitiveType());
		type.addMethod(caller);
		CtTypeParameter callerTypeParameter = factory.createTypeParameter();
		callerTypeParameter.setSimpleName("U");
		caller.addFormalCtTypeParameter(callerTypeParameter);
		CtTypeParameterReference outerReference = outerTypeParameter.getReference();
		callerTypeParameter.setSuperclass(outerReference);
		CtInvocation<Void> invocation = factory.Code().createInvocation(null, callee.getReference());
		caller.setBody(factory.Core().createBlock().addStatement(invocation));

		// when
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(outerReference));

		// then
		assertThat(outerReference)
				.as("reparented direct bound")
				.satisfies(reference -> {
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(outerTypeParameter);
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	@SuppressWarnings("unchecked")
	void metadataFreeExecutableReferenceRetainsCalleeTypeParameter() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends Number> void callee(java.util.List<T> value) {}
				<T extends CharSequence> void caller() { callee(null); }
			}
			""", "GenericProbe.java"));
		var model = launcher.buildModel();
		CtType<?> type = model.getAllTypes().iterator().next();
		CtMethod<?> callee = type.getMethodsByName("callee").get(0);
		CtMethod<?> caller = type.getMethodsByName("caller").get(0);
		CtInvocation<?> invocation = invocationNamed(caller, "callee");
		CtTypeParameterReference metadataFreeTypeParameter = launcher.getFactory().Core()
				.createTypeParameterReference();
		metadataFreeTypeParameter.setSimpleName("T");
		callee.getParameters().get(0).getType().setActualTypeArguments(List.of(metadataFreeTypeParameter));
		CtExecutableReference<Object> executableReference =
				(CtExecutableReference<Object>) callee.getReference();
		((CtInvocation<Object>) invocation).setExecutable(executableReference);

		// when
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) executableReference
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter)
				.as("metadata-free type parameter")
				.satisfies(reference -> {
					assertThat(reference.getMetadata("spoon.typeParameterDeclarationOwner"))
							.as("declaration-owner metadata")
							.isNull();
					assertThat(reference.getDeclaration())
							.as("declaration")
							.isSameAs(callee.getFormalCtTypeParameters().get(0));
					assertThat(reference.getTypeErasure().getQualifiedName())
							.as("erasure")
							.isEqualTo("java.lang.Number");
				});
	}

	@Test
	void adaptedCallerTypeParameterOwnershipSurvivesCloneAndSerialization() throws Exception {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends CharSequence> void callee(java.util.List<T> value) {}
				<T extends Number> void caller(java.util.List<T> value) { callee(value); }
			}
			""", "GenericProbe.java"));
		var model = launcher.buildModel();
		CtMethod<?> caller = model.getAllTypes().iterator().next().getMethodsByName("caller").get(0);
		CtInvocation<?> invocation = invocationNamed(caller, "callee");
		CtTypeParameterReference adaptedTypeParameter = caller.getFormalCtTypeParameters().get(0).getReference();
		invocation.getExecutable().getParameters().get(0).setActualTypeArguments(List.of(adaptedTypeParameter));

		// when
		CtMethod<?> clonedCaller = caller.clone();
		CtInvocation<?> clonedInvocation = invocationNamed(clonedCaller, "callee");
		CtTypeParameterReference clonedTypeParameter = (CtTypeParameterReference) clonedInvocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		ByteArrayOutputStream serialized = new ByteArrayOutputStream();
		SerializationModelStreamer streamer = new SerializationModelStreamer();
		streamer.save(launcher.getFactory(), serialized);
		Factory loadedFactory = streamer.load(new ByteArrayInputStream(serialized.toByteArray()));
		CtMethod<?> loadedCaller = loadedFactory.Type().get("GenericProbe").getMethodsByName("caller").get(0);
		CtInvocation<?> loadedInvocation = invocationNamed(loadedCaller, "callee");
		CtTypeParameterReference loadedTypeParameter = (CtTypeParameterReference) loadedInvocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(clonedTypeParameter.getDeclaration()).isSameAs(clonedCaller.getFormalCtTypeParameters().get(0));
		assertThat(loadedTypeParameter.getDeclaration()).isSameAs(loadedCaller.getFormalCtTypeParameters().get(0));
	}

	@Test
	void nestedConstructorTypeParameterResolvesToConstructorDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends Number> GenericProbe(java.util.List<T> value) {}
				static <T extends Number> void caller(java.util.List<T> value) { new GenericProbe(value); }
			}
			""", "GenericProbe.java"));

		// when
		var model = launcher.buildModel();
		CtConstructorCall<?> constructorCall = model
				.filterChildren(new TypeFilter<>(CtConstructorCall.class))
				.first(CtConstructorCall.class);
		assertThat(constructorCall).as("constructor call in generic probe").isNotNull();
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) constructorCall.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter.getDeclaration().getParent()).isInstanceOf(CtConstructor.class);
	}

	@Test
	void deeplyNestedExecutableTypeParameterResolvesToCalleeDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T extends Number> void callee(java.util.List<java.util.List<T>> value) {}
				<T extends Number> void caller(java.util.List<java.util.List<T>> value) { callee(value); }
			}
			""", "GenericProbe.java"));

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter.getDeclaration().getParent(CtMethod.class).getSimpleName())
				.isEqualTo("callee");
	}

	@ParameterizedTest(name = "receiver type {0}")
	@ValueSource(strings = { "Box", "Box<T>" })
	void receiverTypeParameterResolvesToDeclaringType(String receiverType) {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class Box<T> {
				void callee(java.util.List<java.util.List<T>> value) {}
			}
			class Caller<T> {
				void caller(%s box) { box.callee(null); }
			}
			""".formatted(receiverType), "GenericProbe.java"));

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		CtTypeParameterReference nestedTypeParameter = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(nestedTypeParameter.getDeclaration().getParent(CtType.class).getSimpleName()).isEqualTo("Box");
	}

	@Test
	void directGenericExecutableParameterResolvesToCalleeDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T> void callee(T value) {}
				void caller() { callee(null); }
			}
			""", "GenericProbe.java"));

		// when
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		// then
		assertThat(invocation.getExecutable())
				.as("direct generic invocation [parameter erasure, declaration]")
				.extracting(
						executable -> executable.getParameters().get(0).getQualifiedName(),
						executable -> executable.getExecutableDeclaration().getSimpleName())
				.containsExactly("java.lang.Object", "callee");
	}

	@Test
	void siblingExecutableTypeParametersResolveRepeatedlyWithoutLeakingGuardState() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<A, B> void callee(java.util.Map<A, java.util.List<B>> value) {}
				<A, B> void caller() { callee(null); }
			}
			""", "GenericProbe.java"));
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		CtTypeParameterReference first = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);
		CtTypeParameterReference second = (CtTypeParameterReference) invocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(1).getActualTypeArguments().get(0);

		// when / then
		for (int iteration = 0; iteration < 3; iteration++) {
			assertThat(List.of(first, second))
					.as("sibling type-parameter declaration owners at iteration %d", iteration)
					.extracting(type -> type.getDeclaration().getParent(CtMethod.class).getSimpleName())
					.containsExactly("callee", "callee");
		}

		// and when
		Launcher separateLauncher = new Launcher();
		separateLauncher.addInputResource(new VirtualFile("""
			class SeparateProbe {
				<T> void callee(java.util.List<T> value) {}
				<T> void caller() { callee(null); }
			}
			""", "SeparateProbe.java"));
		var separateModel = separateLauncher.buildModel();
		CtInvocation<?> separateInvocation = invocationNamed(separateModel, "callee");
		CtTypeParameterReference separateTypeParameter = (CtTypeParameterReference) separateInvocation.getExecutable()
				.getParameters().get(0).getActualTypeArguments().get(0);

		// then
		assertThat(separateTypeParameter.getDeclaration().getParent(CtMethod.class).getSimpleName())
				.isEqualTo("callee");
	}

	@Test
	void adaptedReturnTypeParameterResolvesToCallerDeclaration() {
		// given
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("""
			class GenericProbe {
				<T> java.util.List<T> callee() { return null; }
				<T> void caller() { java.util.List<T> value = callee(); }
			}
			""", "GenericProbe.java"));
		var model = launcher.buildModel();
		CtInvocation<?> invocation = invocationNamed(model, "callee");
		CtTypeParameterReference returnTypeParameter = (CtTypeParameterReference) invocation.getExecutable()
				.getType().getActualTypeArguments().get(0);

		// when
		var declaration = returnTypeParameter.getDeclaration();

		// then
		assertThat(declaration.getParent(CtMethod.class).getSimpleName()).isEqualTo("caller");
	}

	private static CtMethod<Void> createPositionlessGenericMethod(
			Factory factory, CtClass<?> declaringType, Class<?> parameterType, CtTypeReference<?> bound) {
		CtMethod<Void> method = factory.createMethod();
		method.setSimpleName("method");
		method.setType(factory.Type().voidPrimitiveType());
		declaringType.addMethod(method);
		CtTypeParameter typeParameter = factory.createTypeParameter();
		typeParameter.setSimpleName("T");
		typeParameter.setSuperclass(bound);
		method.addFormalCtTypeParameter(typeParameter);
		CtTypeReference<?> parameterReference = factory.Type().createReference(parameterType);
		CtParameter<?> parameter = factory.createParameter();
		parameter.setSimpleName("value");
		parameter.setType(parameterReference);
		method.addParameter(parameter);
		parameterReference.addActualTypeArgument(typeParameter.getReference());
		return method;
	}

	private static CtInvocation<?> invocationNamed(CtQueryable root, String name) {
		CtInvocation<?> invocation = root
				.filterChildren(new TypeFilter<>(CtInvocation.class))
				.select((CtInvocation<?> candidate) -> candidate.getExecutable().getSimpleName().equals(name))
				.first(CtInvocation.class);
		assertThat(invocation).as("invocation named <%s>", name).isNotNull();
		return invocation;
	}
}
