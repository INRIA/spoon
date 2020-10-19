package fr.inria.gforge.spoon.architecture.simpleChecks;

import java.util.Arrays;
import java.util.List;
import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.ElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.errorhandling.NopError;
import fr.inria.gforge.spoon.architecture.preconditions.Modifier;
import fr.inria.gforge.spoon.architecture.preconditions.Naming;
import fr.inria.gforge.spoon.architecture.preconditions.Visibility;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
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
		return Naming.equal("factory").test(field)
				|| (Modifier.FINAL.test(field) && Modifier.TRANSIENT.test(field));
	}
	// commented out because the lookup for the factory fails, because test resources are missing
	// @Architecture
	public void testFactorySubFactory(CtModel srcModel, CtModel testModel) {
		Precondition<CtClass<?>> pre =
				Precondition.of(DefaultElementFilter.CLASSES.getFilter(),
				Naming.contains("Factory"),
				(clazz) -> clazz.getSuperclass().getSimpleName().equals("SubFactory"));
		CtClass<?> factory = srcModel.getElements(ElementFilter.ofClassObject(CtClass.class, Naming.equal("Factory"))).get(0);
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
		Modifier.ABSTRACT.or(method -> method.filterChildren(new TypeFilter<>(CtCodeElement.class)).list().size() > 33), 
		Visibility.PUBLIC);
		Constraint<CtMethod<?>> con = Constraint.of((method) -> System.out.println(method.getDeclaringType().getQualifiedName()+ "#"+ method.getSignature()),
		(method) -> method.getDocComment().length() > 15);
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}

	@Architecture(modelNames = "srcModel")
	public void metamodelPackageRule(CtModel srcModel) {
		List<String> exceptions = Arrays.asList("CtTypeMemberWildcardImportReferenceImpl", "InvisibleArrayConstructorImpl");

		Precondition<CtType<?>> pre = 
		Precondition.of(DefaultElementFilter.TYPES.getFilter(),
		(type) -> !exceptions.contains(type.getSimpleName()),
		(type) -> Naming.equal("spoon.reflect.declaration")
		.or(Naming.equal("spoon.reflect.code"))
		.or(Naming.equal("spoon.reflect.reference")).test(type.getPackage()));

		List<CtType<?>> interfaces = srcModel.getElements(ElementFilter.ofTypeFilter(DefaultElementFilter.TYPES.getFilter(),
		(type) -> Naming.equal("spoon.reflect.declaration")
		.or(Naming.equal("spoon.reflect.code"))
		.or(Naming.equal("spoon.reflect.reference"))
		.test(type.getPackage())));
		List<CtType<?>> defaultCoreFactory = srcModel.getElements(ElementFilter.ofTypeFilter(DefaultElementFilter.TYPES.getFilter(),
		Naming.equal("DefaultCoreFactory")));
		interfaces.addAll(defaultCoreFactory);

		Constraint<CtType<?>> con = Constraint.of((v) -> System.out.println(v), 
		(type) -> {
			String implName = type.getQualifiedName().replace(".support", "").replace("Impl", "");
			CtType<?> impl = interfaces.stream().filter(v -> v.getQualifiedName().equals(implName)).findFirst().get();
			return type.getReference().isSubtypeOf(impl.getReference());
		});
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}
}
