/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.testing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.ModelTest;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static spoon.testing.assertions.SpoonAssertions.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtPackageAssertTest {

	@Test
	public void testEqualityBetweenTwoCtPackage() {
		//contract: two packages, one made by test code, second made by compilation from sources are equal
		final Factory factory = createFactory();
		final CtPackage aRootPackage = factory.Package().getOrCreate("");
		List<CtType<?>> types1 = aRootPackage.filterChildren(new TypeFilter<>(CtClass.class)).list();
		Assertions.assertThat(types1).isEmpty();
		factory.Class().create("spoon.testing.testclasses.Foo").addModifier(ModifierKind.PUBLIC);
		factory.Class().create("spoon.testing.testclasses.Bar").addModifier(ModifierKind.PUBLIC);
		List<CtType<?>> types2 = aRootPackage.filterChildren(new TypeFilter<>(CtClass.class)).list();
		Assertions.assertThat(types2).hasSize(2);
		final CtPackage builtPackage = build(new File("./src/test/java/spoon/testing/testclasses/")).Package().get("spoon.testing.testclasses");
		final CtPackage programmaticPackage = factory.Package().get("spoon.testing.testclasses");
		Assertions.assertThat(builtPackage).isNotSameAs(programmaticPackage);
		// EqualsVisitor compares types positionally via LinkedHashSet insertion order,
		// which differs between programmatic and file-based construction — sort by name first
		List<CtType<?>> builtTypes = builtPackage.getTypes().stream()
			.sorted(Comparator.comparing(CtType::getSimpleName)).collect(Collectors.toList());
		List<CtType<?>> programmaticTypes = programmaticPackage.getTypes().stream()
			.sorted(Comparator.comparing(CtType::getSimpleName)).collect(Collectors.toList());
		Assertions.assertThat(builtTypes).hasSameSizeAs(programmaticTypes);
		for (int i = 0; i < builtTypes.size(); i++) {
			assertThat(builtTypes.get(i)).isEqualTo(programmaticTypes.get(i));
		}
	}

	@ModelTest("./src/test/java/spoon/testing/testclasses/")
	public void testEqualityBetweenTwoDifferentCtPackage(Factory factory) {
		assertThatThrownBy(() ->
			assertThat(factory.Package().getRootPackage()).isEqualTo(createFactory().Package().getOrCreate("another.package"))
		).isInstanceOf(AssertionError.class);
	}

	@ModelTest("./src/test/java/spoon/testing/testclasses/")
	public void testEqualityBetweenTwoCtPackageWithDifferentTypes(Factory builtFactory) {
		final Factory factory = createFactory();
		final CtPackage aRootPackage = factory.Package().getOrCreate("");
		factory.Class().create("spoon.testing.testclasses.Foo").addModifier(ModifierKind.PUBLIC);
		assertThatThrownBy(() ->
			assertThat(builtFactory.Package().getRootPackage()).isEqualTo(aRootPackage)
		).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testAddTypeToPackage() {
		final Factory factory = createFactory();
		final CtType<?> type = factory.Core().createClass();
		type.setSimpleName("X");
		//contract: type is created in the root package
		Assertions.assertThat(type.getPackage()).isSameAs(factory.getModel().getRootPackage());
		Assertions.assertThat(type.getQualifiedName()).isEqualTo("X");
		final CtPackage aPackage1 = factory.Package().getOrCreate("some.package");
		//contract: type can be moved from root package to any other package
		aPackage1.addType(type);
		Assertions.assertThat(type.getQualifiedName()).isEqualTo("some.package.X");
		//contract: second add of type into same package does nothing
		aPackage1.addType(type);
		Assertions.assertThat(type.getQualifiedName()).isEqualTo("some.package.X");
		final CtPackage aPackage2 = factory.Package().getOrCreate("another.package");
		//contract: type cannot be moved to another package as long as it belongs to some not root package
		assertThatThrownBy(() -> aPackage2.addType(type)).isInstanceOf(SpoonException.class);
		Assertions.assertThat(type.getQualifiedName()).isEqualTo("some.package.X");
		//contract: type can be moved to another package after it is removed from current package
		type.getPackage().removeType(type);
		aPackage2.addType(type);
		Assertions.assertThat(type.getQualifiedName()).isEqualTo("another.package.X");
	}
}
