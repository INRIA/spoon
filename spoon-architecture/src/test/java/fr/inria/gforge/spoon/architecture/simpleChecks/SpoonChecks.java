package fr.inria.gforge.spoon.architecture.simpleChecks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.ElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.constraints.FieldReferenceMatcher;
import fr.inria.gforge.spoon.architecture.constraints.InvocationMatcher;
import fr.inria.gforge.spoon.architecture.errorhandling.NopError;
import fr.inria.gforge.spoon.architecture.preconditions.AnnotationHelper;
import fr.inria.gforge.spoon.architecture.preconditions.ModifierFilter;
import fr.inria.gforge.spoon.architecture.preconditions.Naming;
import fr.inria.gforge.spoon.architecture.preconditions.Visibility;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonChecks {

	@Architecture
	public void statelessFactory(CtModel srcModel, CtModel testModel) {
		Precondition<CtClass<?>> pre =
				Precondition.of(DefaultElementFilter.CLASSES.getFilter(), Naming.contains("Factory"));
		Constraint<CtClass<?>> con = Constraint.of(new NopError<CtClass<?>>(),
				(clazz) -> clazz.getFields().stream().allMatch(field -> stateless(field)));
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	private boolean stateless(CtField<?> field) {
		return Naming.equals("factory").test(field)
				|| (ModifierFilter.isFinal().and(ModifierFilter.isTransient()).test(field));
	}
	// commented out because the lookup for the factory fails, because test resources are missing
	// @Architecture
	public void testFactorySubFactory(CtModel srcModel, CtModel testModel) {
		Precondition<CtClass<?>> pre =
				Precondition.of(DefaultElementFilter.CLASSES.getFilter(),
				Naming.contains("Factory"),
				(clazz) -> clazz.getSuperclass().getSimpleName().equals("SubFactory"));
		CtClass<?> factory = srcModel.getElements(ElementFilter.ofClassObject(CtClass.class, Naming.equals("Factory"))).get(0);
		Constraint<CtClass<?>> con = Constraint.of(new NopError<CtClass<?>>(),
				(clazz) -> clazz.getMethods()
				.stream()
				.filter(Naming.startsWith("create"))
				.allMatch(v -> factory.getMethods().contains(v)));
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "srcModel")
	public void testDocumentation(CtModel srcModel) {
		// Contract:
		// Precondition: Get all methods except setters etc.
		Precondition<CtMethod<?>> pre = 
		Precondition.of(DefaultElementFilter.METHODS.getFilter(),
		Naming.startsWith("get").negate(),
		Naming.startsWith("set").negate(),
		Naming.startsWith("is").negate(),
		Naming.startsWith("add").negate(),
		Naming.startsWith("remove").negate(),
		// only the top declarations should be documented (not the overriding methods which are lower in the hierarchy)
		(method) -> method.getTopDefinitions().isEmpty(),
		 // means that only large methods must be documented),
		ModifierFilter.isAbstract().or(method -> method.filterChildren(new TypeFilter<>(CtCodeElement.class)).list().size() > 33), 
		Visibility.isPublic());
		Constraint<CtMethod<?>> con = Constraint.of((method) -> System.out.println(method.getDeclaringType().getQualifiedName()+ "#"+ method.getSignature()),
		(method) -> method.getDocComment().length() > 15);
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "srcModel")
	public void metamodelPackageRule(CtModel srcModel) {
		List<String> exceptions = Arrays.asList("CtTypeMemberWildcardImportReferenceImpl", "InvisibleArrayConstructorImpl");

		Precondition<CtType<?>> pre = Precondition.of(
		DefaultElementFilter.TYPES.getFilter(),
		(type) -> !exceptions.contains(type.getSimpleName()),
		(type) -> Naming.equals("spoon.reflect.declaration")
		.or(Naming.equals("spoon.reflect.code"))
		.or(Naming.equals("spoon.reflect.reference")).test(type.getTopLevelType().getPackage()));
		List<CtType<?>> interfaces = srcModel.getElements(ElementFilter.ofTypeFilter(DefaultElementFilter.TYPES.getFilter(),
		(type) -> type.isTopLevel() && 
		Naming.equals("spoon.reflect.declaration")
		.or(Naming.equals("spoon.reflect.code"))
		.or(Naming.equals("spoon.reflect.reference"))
		.test(type.getTopLevelType().getPackage())));
		List<CtType<?>> defaultCoreFactory = srcModel.getElements(ElementFilter.ofTypeFilter(DefaultElementFilter.TYPES.getFilter(),
		Naming.equals("DefaultCoreFactory")));
		interfaces.addAll(defaultCoreFactory);

		Constraint<CtType<?>> con = Constraint.of((v) -> System.out.println(v), 
		(type) -> {
			String implName = type.getQualifiedName().replace(".support", "").replace("Impl", "");
			CtType<?> impl = interfaces.stream().filter(v -> v.getQualifiedName().equals(implName)).findFirst().get();
			return type.getReference().isSubtypeOf(impl.getReference());
		});
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "testModel")
	public void methodNameStartsWithTest(CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(
		DefaultElementFilter.METHODS.getFilter(), 
		Visibility.isPublic(), 
		AnnotationHelper.hasAnnotationMatcher(Test.class).or(AnnotationHelper.hasAnnotationMatcher(org.junit.jupiter.api.Test.class)));
		Constraint<CtNamedElement> con = Constraint.of((element) -> System.out.println(element), Naming.startsWith("test"));
		ArchitectureTest.of(pre, con).runCheck(testModel);
	}

	@Architecture(modelNames = "testModel")
	public void testNoJunit3(CtModel testModel) {
		Precondition<CtTypeReference<?>> pre = Precondition.of(
			DefaultElementFilter.TYPE_REFERENCE.getFilter());
		Constraint<CtTypeReference<?>> con = Constraint.of(
		(element) -> System.out.println(element),
		(element) -> element.getQualifiedName().equals("junit.framework.TestCase"));
		ArchitectureTest.of(pre,con).runCheck(testModel);
	}

	@Architecture()
	public void noTreeSetWithoutComparators(CtModel srcModel, CtModel testModel) {
		Precondition<CtConstructorCall<?>> pre = Precondition.of(new TypeFilter<>(CtConstructorCall.class));
		Constraint<CtConstructorCall<?>> con = Constraint.of(
		(element) -> System.out.println(element),
		(element) -> element.getType().getQualifiedName().equals("java.util.TreeSet"),
		(element) -> element.getArguments().isEmpty());
		ArchitectureTest<CtConstructorCall<?>> test = ArchitectureTest.of(pre, con);
		test.runCheck(srcModel);
		test.runCheck(testModel);
	}

	@Architecture(modelNames = "srcModel")
	public void testStaticClasses(CtModel srcModel) {
		// contract: helper classes only have static methods and a private constructor

		//		spoon.compiler.SpoonResourceHelper
		//		spoon.reflect.visitor.Query
		//		spoon.support.compiler.jdt.JDTTreeBuilderQuery
		//		spoon.support.compiler.SnippetCompilationHelper
		//		spoon.support.util.ByteSerialization
		//		spoon.support.util.RtHelper
		//		spoon.support.visitor.equals.CloneHelper
		//		spoon.template.Substitution
		//		spoon.testing.utils.Check
		//		spoon.testing.utils.ProcessorUtils
		//		spoon.testing.Assert
		Precondition<CtClass<?>> pre = Precondition.of(
			DefaultElementFilter.CLASSES.getFilter());
		Constraint<CtClass<?>> con = Constraint.of(
		(element) -> System.out.println(element),
		(clazz) -> clazz.getSuperclass() == null,
		(clazz) -> clazz.getMethods().stream().allMatch(ModifierFilter.isStatic()),
		(clazz) -> clazz.getConstructors().stream().allMatch(ModifierFilter.isStatic()));
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "srcModel")
	public void testInterfacesAreCtScannable(CtModel srcModel) {
		// looks like an expert level test we support that later at most
	}

	@Architecture(modelNames = "srcModel")
	public void testSpecPackage(CtModel srcModel) {
		Set<String> officialPackages = new HashSet<>();
		// .... add packages here
		Precondition<CtPackage> pre = Precondition.of(DefaultElementFilter.PACKAGES.getFilter());
		Constraint<CtPackage> con = Constraint.of(new NopError<CtPackage>(),
		(packageElement) -> officialPackages.contains(packageElement.getQualifiedName())); 
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "srcModel")
	public void checkPrivateMethodInvocations(CtModel srcModel) {
		InvocationMatcher matcher = new InvocationMatcher(srcModel);
		Precondition<CtMethod<?>> pre = Precondition.of(DefaultElementFilter.METHODS.getFilter(),
		Visibility.isPrivate(),
		Naming.equals("readObject").negate(),
		Naming.equals("readResolve").negate());
		Constraint<CtMethod<?>> con = Constraint.of(new NopError<CtMethod<?>>(), matcher);
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "srcModel")
	public void checkFields(CtModel srcModel) {
		FieldReferenceMatcher matcher = new FieldReferenceMatcher(srcModel);
		Precondition<CtField<?>> pre = Precondition.of(DefaultElementFilter.FIELDS.getFilter(),
		Visibility.isPrivate(),
		Naming.equals("readObject").negate(),
		Naming.equals("readResolve").negate());
		Constraint<CtField<?>> con = Constraint.of(new NopError<CtField<?>>(), matcher);
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}
}
