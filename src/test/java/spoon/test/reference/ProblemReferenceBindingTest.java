package spoon.test.reference;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.TypeFilter;

class ProblemReferenceBindingTest {
	@Test
	void conditionalAnonymousResourceCanBeUsedAsAConstructorArgument() {
		// contract: inferred anonymous resources nested in argument expressions retain their declared type
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		assertThat(constructorParameter(model, "MissingConditionalWrapper").getSimpleName())
				.isEqualTo("MissingResource");
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"conditional invocation argument, consumeConditional",
		"direct invocation argument, consume"
	})
	void anonymousResourceCanBeUsedAsAnInvocationArgument(String scenario, String invocationName) {
		// contract: inferred anonymous resources used as invocation arguments retain their declared type
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtInvocation<?> invocation = invocationNamed(model, invocationName, 1);
		assertThat(invocation.getExecutable().getParameters())
				.as(scenario)
				.singleElement()
				.extracting(spoon.reflect.reference.CtTypeReference::getSimpleName)
				.isEqualTo("MissingResource");
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"resolved method parameter, acceptObject, java.lang.Object",
		"closest matching method signature, acceptString, java.lang.String"
	})
	void anonymousResourceKeepsItsExpectedMethodParameter(
			String scenario, String invocationName, String expectedType) {
		// contract: recovery preserves resolved and closest-matching method parameter types
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtInvocation<?> invocation = invocationNamed(model, invocationName);
		assertThat(invocation.getExecutable().getParameters())
				.as(scenario)
				.singleElement()
				.extracting(spoon.reflect.reference.CtTypeReference::getQualifiedName)
				.isEqualTo(expectedType);
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"closest matching constructor signature, KnownStringWrapper, java.lang.String",
		"different conditional resource types, MissingMixedWrapper, java.lang.Object",
		"different conditional resource parameterizations, MissingGenericMixedWrapper, java.lang.Object",
		"imported resource type, MissingImportedWrapper, ext.ImportedResource",
		"directly imported nested resource type, MissingDirectNestedWrapper, ext.QualifiedOuter$Resource"
	})
	void constructorArgumentKeepsItsExpectedQualifiedType(
			String scenario, String constructedType, String expectedType) {
		// contract: argument recovery preserves the type selected by imports and executable applicability
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		assertThat(constructorParameter(model, constructedType).getQualifiedName())
				.as(scenario)
				.isEqualTo(expectedType);
	}

	@Test
	void parameterizedAnonymousResourcePreservesItsTypeArgument() {
		// contract: recovery uses the source type when an unresolved binding omits generic arguments
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, "parameterizedCall").getTarget();
		assertThat(variableRead.getVariable().getType().getActualTypeArguments())
				.singleElement()
				.extracting(CtTypeReference::getQualifiedName)
				.isEqualTo("java.lang.String");
	}

	@Test
	void parameterizedAnonymousResourceKeepsItsConstructorArgumentType() {
		// contract: executable argument recovery retains source-declared generic arguments
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		assertThat(constructorParameter(model, "MissingParameterizedWrapper").getActualTypeArguments())
				.singleElement()
				.extracting(CtTypeReference::getQualifiedName)
				.isEqualTo("java.lang.String");
	}

	@Test
	void nullConditionalBranchesKeepTheAnonymousResourceType() {
		// contract: null does not erase the inferred resource type from either conditional branch
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		assertThat(List.of(
				constructorParameter(model, "MissingNullableWrapper"),
				constructorParameter(model, "MissingNullFirstWrapper")))
				.as("constructor parameter types for both null-conditional branches")
				.extracting(CtTypeReference::getSimpleName)
				.containsExactly("MissingResource", "MissingResource");
	}

	@Test
	void anonymousResourceProblemBindingUsesItsDeclaredTypeName() {
		// contract: unresolved anonymous resources use a legal type name for later invocations
		// (#6800)
		// given
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/ProblemReferenceBinding.java");

		// when
		var model = launcher.buildModel();

		// then
		CtInvocation<?> invocation = invocationNamed(model, "call");
		assertThat(invocation.getTarget())
				.isInstanceOfSatisfying(CtVariableRead.class, variableRead -> assertThat(
						variableRead.getVariable().getType().getSimpleName())
						.as("anonymous-resource target type")
						.isEqualTo("MissingResource"));
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"parameterized inferred resource, parameterizedCall, MissingResource",
		"explicit resource, baseCall, BaseResource"
	})
	void resourceTargetRetainsItsExpectedSimpleType(String scenario, String invocationName, String expectedType) {
		// contract: inferred normalization and explicit declarations retain legal simple type names
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, invocationName).getTarget();
		assertThat(variableRead.getVariable().getType().getSimpleName())
				.as(scenario)
				.isEqualTo(expectedType);
	}

	@Test
	void unresolvedExecutableUsesTheRecoveredResourceType() {
		// contract: executable references reuse recovered inferred-resource evidence
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtInvocation<?> invocation = invocationNamed(model, "configure");
		assertThat(invocation.getExecutable().getDeclaringType().getSimpleName()).isEqualTo("MissingService");
	}

	@Test
	void nestedAnonymousResourcePreservesItsDeclaringType() {
		// contract: a nested resource problem binding represents the nested and declaring names separately
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, "nestedCall").getTarget();
		assertThat(variableRead.getVariable().getType())
				.as("nested resource type [simple name, declaring type, qualified name]")
				.extracting(
						CtTypeReference::getSimpleName,
						type -> type.getDeclaringType().getSimpleName(),
						CtTypeReference::getQualifiedName)
				.containsExactly("Resource", "Outer", "spoon.test.reference.Outer$Resource");
	}

	@Test
	void nestedParameterizedAnonymousResourcePreservesEveryTypeSegment() {
		// contract: erasing nested type arguments does not discard later nested type names
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, "nestedGenericCall").getTarget();
		assertThat(variableRead.getVariable().getType())
				.as("nested generic resource type [simple name, declaring type]")
				.extracting(
						CtTypeReference::getSimpleName,
						type -> type.getDeclaringType().getSimpleName())
				.containsExactly("Resource", "GenericOuter");
	}

	@Test
	void genericEnclosingResourceDropsTypeArgumentsBeforeResolvingItsNestedType() {
		// contract: enclosing-only type arguments are erased without discarding the nested resource
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, "genericEnclosingCall").getTarget();
		assertThat(variableRead.getVariable().getType())
				.as("resource in generic enclosing type [simple name, declaring type]")
				.extracting(
						CtTypeReference::getSimpleName,
						type -> type.getDeclaringType().getSimpleName())
				.containsExactly("PlainResource", "GenericOuter");
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"imported resource type, importedCall, ext.ImportedResource",
		"package-qualified nested resource type, qualifiedNestedCall, ext.QualifiedOuter$Resource"
	})
	void resourceInvocationRetainsItsQualifiedType(String scenario, String invocationName, String expectedType) {
		// contract: normalization retains imported packages and nested-type separators
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, invocationName).getTarget();
		assertThat(variableRead.getVariable().getType().getQualifiedName())
				.as(scenario)
				.isEqualTo(expectedType);
	}

	@Test
	void lowercaseDeclaringTypeIsNotTreatedAsAPackage() {
		// contract: source binding evidence takes precedence over capitalization conventions
		// (#6800)
		// given
		Launcher launcher = launcherForProblemReferenceBinding();

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, "lowercaseNestedCall").getTarget();
		assertThat(variableRead.getVariable().getType().getDeclaringType().getSimpleName()).isEqualTo("lower");
	}

	@Test
	void anonymousResourceCanBeUsedAsAConstructorArgument() {
		// contract: inferred anonymous resource bindings remain legal when reused as argument types
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		assertThat(model.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class)))
				.filteredOn(call -> call.getType().getSimpleName().equals("MissingWrapper"))
				.hasSize(2)
				.allSatisfy(call -> assertThat(call.getExecutable().getParameters())
						.singleElement()
						.extracting(CtTypeReference::getSimpleName)
						.isEqualTo("MissingResource"));
	}

	@Test
	void anonymousResourceCanDeclareReferencedFields() {
		// contract: inferred anonymous resource bindings remain legal as field declaring types
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		assertThat(model.getElements(new TypeFilter<CtFieldRead<?>>(CtFieldRead.class)))
				.filteredOn(read -> read.getVariable().getSimpleName().equals("missingField"))
				.isNotEmpty()
				.allSatisfy(read -> assertThat(read.getVariable().getDeclaringType().getSimpleName()).isEqualTo("MissingNode"));
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"nested generic resource, MissingNestedWrapper, Outer",
		"lowercase enclosing resource, MissingLowercaseWrapper, lower"
	})
	void nestedConstructorArgumentRetainsEveryTypeSegment(
			String scenario, String constructedType, String expectedDeclaringType) {
		// contract: argument recovery preserves nested names and source-declared enclosing types
		// (#6800)
		// given
		Launcher launcher = launcherForAnonymousResourceProblemBinding();

		// when
		var model = launcher.buildModel();

		// then
		var parameter = constructorParameter(model, constructedType);
		assertThat(parameter)
				.as("%s [simple name, declaring type]", scenario)
				.extracting(
						CtTypeReference::getSimpleName,
						type -> type.getDeclaringType().getSimpleName())
				.containsExactly("Resource", expectedDeclaringType);
	}

	@Test
	void preJavaTenTypeNamedVarRetainsItsDeclaredType() {
		// contract: `var` recovery is disabled while `var` remains an ordinary type name
		// (#6800)
		// given
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/PreJavaTenVarType.java");

		// when
		var model = launcher.buildModel();

		// then
		CtVariableRead<?> variableRead = (CtVariableRead<?>) invocationNamed(model, "varTypeCall").getTarget();
		assertThat(variableRead.getVariable().getType().getSimpleName()).isEqualTo("var");
	}

	private static Launcher launcherForProblemReferenceBinding() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/ProblemReferenceBinding.java");
		launcher.addInputResource("src/test/resources/spoon/test/reference/issue01");
		return launcher;
	}

	private static Launcher launcherForAnonymousResourceProblemBinding() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/spoon/test/reference/AnonymousResourceProblemBinding.java");
		launcher.addInputResource("src/test/resources/spoon/test/reference/issue01");
		return launcher;
	}

	private static spoon.reflect.reference.CtTypeReference<?> constructorParameter(
			spoon.reflect.CtModel model, String constructedType) {
		CtConstructorCall<?> constructorCall = model
				.filterChildren(new TypeFilter<>(CtConstructorCall.class))
				.select((CtConstructorCall<?> candidate) -> candidate.getType().getSimpleName().equals(constructedType))
				.first(CtConstructorCall.class);
		assertThat(constructorCall).as("constructor call creating <%s>", constructedType).isNotNull();
		return constructorCall
				.getExecutable()
				.getParameters()
				.get(0);
	}

	private static CtInvocation<?> invocationNamed(CtQueryable root, String name) {
		return findInvocation(
				root,
				candidate -> candidate.getExecutable().getSimpleName().equals(name),
				"invocation named <%s>".formatted(name));
	}

	private static CtInvocation<?> invocationNamed(CtQueryable root, String name, int argumentCount) {
		return findInvocation(
				root,
				candidate -> candidate.getExecutable().getSimpleName().equals(name)
						&& candidate.getArguments().size() == argumentCount,
				"invocation named <%s> with %d argument(s)".formatted(name, argumentCount));
	}

	private static CtInvocation<?> findInvocation(
			CtQueryable root, Filter<CtInvocation<?>> filter, String description) {
		CtInvocation<?> invocation = root
				.filterChildren(new TypeFilter<>(CtInvocation.class))
				.select(filter)
				.first(CtInvocation.class);
		assertThat(invocation).as(description).isNotNull();
		return invocation;
	}
}
