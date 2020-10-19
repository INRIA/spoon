package fr.inria.gforge.spoon.architecture.simpleChecks;

import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.ElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.errorhandling.NopError;
import fr.inria.gforge.spoon.architecture.preconditions.Modifier;
import fr.inria.gforge.spoon.architecture.preconditions.Naming;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;

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

	@Architecture
	public void testDocumentation(CtModel srcModel, CtModel testModel) {
		//TODO:_
	}
}
