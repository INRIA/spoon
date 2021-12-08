package spoon.test.sealedclasses;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSealable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.CtExtendedModifier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static spoon.test.SpoonTestHelpers.contentEquals;

public class SealedClassesTest {

	@Test
	void testSealedClassWithInnerSubclassModelImplicit() {
		// contract: inner subtypes are implicit and in the permitted types set
		Launcher launcher = createLauncher();

		launcher.addInputResource("src/test/resources/sealedclasses/SealedClassWithNestedSubclasses.java");
		CtModel ctModel = launcher.buildModel();
		CtSealable outer = (CtSealable) ctModel.getAllTypes().iterator().next();

		for (CtTypeReference<?> permitted : outer.getPermittedTypes()) {
			assertThat(permitted.isImplicit(), is(true));
		}
	}

	@Test
	void testSealedClassWithInnerSubclassModelModifiers() {
		// contract: sealed and non-sealed modifiers are present in the model
		Launcher launcher = createLauncher();

		launcher.addInputResource("src/test/resources/sealedclasses/SealedClassWithNestedSubclasses.java");
		CtModel ctModel = launcher.buildModel();
		CtClass<?> outer = (CtClass<?>) ctModel.getAllTypes().iterator().next();
		assertThat(outer.getExtendedModifiers(), hasItems(new CtExtendedModifier(ModifierKind.SEALED, false)));

		CtClass<?> nestedFinal = outer.getNestedType("NestedFinal");
		assertThat(nestedFinal.getExtendedModifiers(), hasItem(new CtExtendedModifier(ModifierKind.FINAL, false)));
		assertThat(outer.getPermittedTypes(), hasItem(nestedFinal.getReference()));

		CtType<?> nestedNonSealed = outer.getNestedType("NestedNonSealed");
		assertThat(nestedNonSealed.getExtendedModifiers(), hasItem(new CtExtendedModifier(ModifierKind.NON_SEALED, false)));
		assertThat(outer.getPermittedTypes(), hasItem(nestedNonSealed.getReference()));
	}

	@Test
	void testEnumSealed() {
		// contract: enums with anonymous enum values are sealed and the anonymous types are final
		Launcher launcher = createLauncher();

		launcher.addInputResource("src/test/resources/sealedclasses/EnumWithAnonymousValue.java");
		CtModel ctModel = launcher.buildModel();
		CtEnum<?> ctEnum = ctModel.getElements(new TypeFilter<CtEnum<?>>(CtEnum.class)).get(0);
		// not final
		assertThat(ctEnum.isFinal(), is(false));
		// but (implicitly) sealed
		assertThat(ctEnum.getExtendedModifiers(), contentEquals(
				new CtExtendedModifier(ModifierKind.PUBLIC, false),
				new CtExtendedModifier(ModifierKind.SEALED, true)
		));

		// TODO the RHS type is wrong currently, see #4291
/*		assertThat(ctEnum.getPermittedTypes(),
				contentEquals(ctEnum.getEnumValue("VALUE").getDefaultExpression().getType()));*/
	}

	@Test
	void testMultiCompilationUnitSealed() {
		// contract: extending types in other compilation units are present in the permitted types set
		Launcher launcher = createLauncher();

		launcher.addInputResource("src/test/resources/sealedclasses/SealedClassWithPermits.java");
		launcher.addInputResource("src/test/resources/sealedclasses/ExtendingClass.java");
		launcher.addInputResource("src/test/resources/sealedclasses/OtherExtendingClass.java");
		CtModel ctModel = launcher.buildModel();
		CtPackage ctPackage = ctModel.getAllPackages().iterator().next();
		CtClass<?> sealedClassWithPermits = ctPackage.getType("SealedClassWithPermits");
		CtType<?> extendingClass = ctPackage.getType("ExtendingClass");
		CtType<?> otherExtendingClass = ctPackage.getType("OtherExtendingClass");

		assertThat(sealedClassWithPermits.getPermittedTypes().size(), is(2));
		for (CtTypeReference<?> permittedType : sealedClassWithPermits.getPermittedTypes()) {
			// outer types are always explicit
			assertThat(permittedType.isImplicit(), is(false));
		}
		assertThat(sealedClassWithPermits.getPermittedTypes(), hasItem(extendingClass.getReference()));
		assertThat(sealedClassWithPermits.getPermittedTypes(), hasItem(otherExtendingClass.getReference()));
	}

	private static Launcher createLauncher() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(17);
		return launcher;
	}
}
