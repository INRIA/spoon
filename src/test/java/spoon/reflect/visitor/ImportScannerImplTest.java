/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ImportScannerImpl}, specifically the private method
 * {@code matchesTypeName} which is exercised through the public API
 * via {@code computeImports}/{@code getAllImports}.
 */
public class ImportScannerImplTest {

	/**
	 * contract: when a class has a Javadoc tag {@code @see SomeType} referencing a
	 * type by simple name, {@code ImportScannerImpl} detects that the pre-existing
	 * import of that type is used and includes it in {@code getAllImports()}.
	 *
	 * This exercises {@code matchesTypeName} through the path:
	 * computeImports -> scan -> visitCtJavaDoc -> matchesTypeName
	 */
	@Test
	void testMatchesTypeNameViaAtSeeTag() {
		// arrange: build a model of a class with @see List in its Javadoc
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/reflect/visitor/testdata/JavadocSeeList.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CtClass<?> clazz = factory.Class().get("spoon.reflect.visitor.testdata.JavadocSeeList");

		// Create the import for java.util.List that we want to check is detected
		CtTypeReference<?> listRef = factory.Type().createReference("java.util.List");
		CtImport listImport = factory.Type().createImport(listRef);

		// act: initialize scanner with the List import, then scan the class
		ImportScannerImpl scanner = new ImportScannerImpl();
		scanner.initWithImports(List.of(listImport));
		scanner.computeImports(clazz);

		// assert: the List import should appear in getAllImports() because it was referenced in @see
		Set<String> importNames = scanner.getAllImports().stream()
				.filter(imp -> imp.getReference() instanceof CtTypeReference)
				.map(imp -> ((CtTypeReference<?>) imp.getReference()).getQualifiedName())
				.collect(Collectors.toSet());

		assertTrue(importNames.contains("java.util.List"),
				"Expected java.util.List to be detected as used via @see Javadoc tag, but got: " + importNames);
	}

	/**
	 * contract: when a class has an inline Javadoc tag {@code {@link SomeType}}
	 * referencing a type by simple name, {@code ImportScannerImpl} detects that
	 * the pre-existing import of that type is used.
	 */
	@Test
	void testMatchesTypeNameViaInlineLinkTag() {
		// arrange: build a model of a class with {@link Map} in its Javadoc
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/reflect/visitor/testdata/JavadocLinkMap.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CtClass<?> clazz = factory.Class().get("spoon.reflect.visitor.testdata.JavadocLinkMap");

		CtTypeReference<?> mapRef = factory.Type().createReference("java.util.Map");
		CtImport mapImport = factory.Type().createImport(mapRef);

		ImportScannerImpl scanner = new ImportScannerImpl();
		scanner.initWithImports(List.of(mapImport));
		scanner.computeImports(clazz);

		Set<String> importNames = scanner.getAllImports().stream()
				.filter(imp -> imp.getReference() instanceof CtTypeReference)
				.map(imp -> ((CtTypeReference<?>) imp.getReference()).getQualifiedName())
				.collect(Collectors.toSet());

		assertTrue(importNames.contains("java.util.Map"),
				"Expected java.util.Map to be detected as used via {@link} Javadoc tag, but got: " + importNames);
	}

	/**
	 * contract: when a Javadoc contains {@code @throws SomeException}, the import
	 * for that exception type is detected as used by {@code ImportScannerImpl}.
	 */
	@Test
	void testMatchesTypeNameViaAtThrowsTag() {
		// arrange: build a model of a class with @throws IOException in its Javadoc
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/reflect/visitor/testdata/JavadocThrowsIOException.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CtClass<?> clazz = factory.Class().get("spoon.reflect.visitor.testdata.JavadocThrowsIOException");

		CtTypeReference<?> exRef = factory.Type().createReference("java.io.IOException");
		CtImport exImport = factory.Type().createImport(exRef);

		ImportScannerImpl scanner = new ImportScannerImpl();
		scanner.initWithImports(List.of(exImport));
		scanner.computeImports(clazz);

		Set<String> importNames = scanner.getAllImports().stream()
				.filter(imp -> imp.getReference() instanceof CtTypeReference)
				.map(imp -> ((CtTypeReference<?>) imp.getReference()).getQualifiedName())
				.collect(Collectors.toSet());

		assertTrue(importNames.contains("java.io.IOException"),
				"Expected java.io.IOException to be detected as used via @throws Javadoc tag, but got: " + importNames);
	}

	/**
	 * contract: when a Javadoc tag references a type by its fully qualified name
	 * (e.g. {@code @see java.util.List}), the import for that type is still
	 * detected as used.
	 */
	@Test
	void testMatchesTypeNameWithFullyQualifiedNameInTag() {
		// arrange: build a model of a class with @see java.util.List (FQN) in its Javadoc
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/reflect/visitor/testdata/JavadocSeeFQNList.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CtClass<?> clazz = factory.Class().get("spoon.reflect.visitor.testdata.JavadocSeeFQNList");

		CtTypeReference<?> listRef = factory.Type().createReference("java.util.List");
		CtImport listImport = factory.Type().createImport(listRef);

		ImportScannerImpl scanner = new ImportScannerImpl();
		scanner.initWithImports(List.of(listImport));
		scanner.computeImports(clazz);

		Set<String> importNames = scanner.getAllImports().stream()
				.filter(imp -> imp.getReference() instanceof CtTypeReference)
				.map(imp -> ((CtTypeReference<?>) imp.getReference()).getQualifiedName())
				.collect(Collectors.toSet());

		assertTrue(importNames.contains("java.util.List"),
				"Expected java.util.List to be detected as used via @see FQN Javadoc tag, but got: " + importNames);
	}

	/**
	 * contract: when a Javadoc tag references a type in a method parameter position
	 * (e.g. {@code @see Object#equals(String)}), the import for the param type
	 * is detected as used.
	 */
	@Test
	void testMatchesTypeNameViaMethodParamInTag() {
		// arrange: build a model of a class with @see #doSomething(ArrayList) in its Javadoc
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/reflect/visitor/testdata/JavadocSeeMethodParam.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CtClass<?> clazz = factory.Class().get("spoon.reflect.visitor.testdata.JavadocSeeMethodParam");

		CtTypeReference<?> alRef = factory.Type().createReference("java.util.ArrayList");
		CtImport alImport = factory.Type().createImport(alRef);

		ImportScannerImpl scanner = new ImportScannerImpl();
		scanner.initWithImports(List.of(alImport));
		scanner.computeImports(clazz);

		Set<String> importNames = scanner.getAllImports().stream()
				.filter(imp -> imp.getReference() instanceof CtTypeReference)
				.map(imp -> ((CtTypeReference<?>) imp.getReference()).getQualifiedName())
				.collect(Collectors.toSet());

		assertTrue(importNames.contains("java.util.ArrayList"),
				"Expected java.util.ArrayList to be detected as used via @see method param Javadoc tag, but got: " + importNames);
	}
}
