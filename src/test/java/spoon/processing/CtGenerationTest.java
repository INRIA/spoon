/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.generating.CloneVisitorGenerator;
import spoon.generating.CtBiScannerGenerator;
import spoon.generating.ReplacementVisitorGenerator;
import spoon.generating.RoleHandlersGenerator;
import spoon.metamodel.MetamodelProperty;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.DefaultTokenWriter;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;

public class CtGenerationTest {
	private String oldLineSeparator;

	@BeforeEach
	public void setup() {
		this.oldLineSeparator = System.getProperty("line.separator", "\n");
		//use always LINUX line separator, because generated files are committed to Spoon repository which expects that.
		System.setProperty("line.separator", "\n");
	}

	@AfterEach
	public void teardown() {

		System.setProperty("line.separator", this.oldLineSeparator);
	}

	@Test
	public void testGenerateReplacementVisitor() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().useTabulations(true);
		configurePrinter(launcher);
		launcher.setSourceOutputDirectory("./target/generated/");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");

		try (Stream<Path> files = Files.list(new File("./src/main/java/spoon/support/visitor/replace/").toPath())) {
			files.forEach(path -> {
				if (!path.getFileName().toString().endsWith("ReplacementVisitor.java")) {
					launcher.addInputResource(path.toString());
				}
			});
		}

		launcher.addInputResource("./src/test/java/spoon/generating/replace/ReplacementVisitor.java");
		launcher.addProcessor(new ReplacementVisitorGenerator());
		launcher.setOutputFilter(new RegexFilter("spoon.support.visitor.replace.*"));
		launcher.run();

		// cp ./target/generated/spoon/support/visitor/replace/ReplacementVisitor.java ./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java
		CtClass<Object> actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/support/visitor/replace/ReplacementVisitor.java")).Class().get("spoon.support.visitor.replace.ReplacementVisitor");
		CtClass<Object> expected = build(new File("./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java")).Class().get("spoon.support.visitor.replace.ReplacementVisitor");
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testGenerateCtBiScanner() {
		// contract: generates the biscanner that is used for equality checking
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().useTabulations(true);
		configurePrinter(launcher);
		launcher.setSourceOutputDirectory("./target/generated/");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.addInputResource("./src/test/java/spoon/generating/scanner/");
		launcher.addProcessor(new CtBiScannerGenerator());
		launcher.setOutputFilter(new RegexFilter("spoon.reflect.visitor.CtBiScannerDefault"));
		launcher.run();

		// we don't necessarily want to hard-wired the relation between CtScanner and CtBiScannerDefault.java
		// this can be done on an informed basis when important changes are made in the metamodel/scanner
		// and then we can have smaller clean tested pull requests to see the impact of the change
		// cp ./target/generated/spoon/reflect/visitor/CtBiScannerDefault.java ./src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java
		assertThat(build(new File("./src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java")).Class().get(CtBiScannerDefault.class))
				.isEqualTo(build(new File("./target/generated/spoon/reflect/visitor/CtBiScannerDefault.java")).Class().get(CtBiScannerDefault.class));
	}

	@Test
	public void testGenerateCloneVisitor() {
		// contract: generates CloneBuilder.java and CloneBuilder.java
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().useTabulations(true);
		configurePrinter(launcher);
		launcher.setSourceOutputDirectory("./target/generated/");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		// Implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtInheritanceScanner.java");
		launcher.addInputResource("./src/test/java/spoon/generating/clone/");
		launcher.addProcessor(new CloneVisitorGenerator());
		launcher.setOutputFilter(new RegexFilter("spoon.support.visitor.clone.*"));
		launcher.run();

		// cp ./target/generated/spoon/support/visitor/clone/CloneBuilder.java  ./src/main/java/spoon/support/visitor/clone/CloneBuilder.java
		CtClass<Object> actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/support/visitor/clone/CloneBuilder.java")).Class().get("spoon.support.visitor.clone.CloneBuilder");
		CtClass<Object> expected = build(new File("./src/main/java/spoon/support/visitor/clone/CloneBuilder.java")).Class().get("spoon.support.visitor.clone.CloneBuilder");
		assertThat(actual).isEqualTo(expected);

		// cp ./target/generated/spoon/support/visitor/clone/CloneVisitor.java  ./src/main/java/spoon/support/visitor/clone/CloneVisitor.java
		actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/support/visitor/clone/CloneVisitor.java")).Class().get("spoon.support.visitor.clone.CloneVisitor");
		expected = build(new File("./src/main/java/spoon/support/visitor/clone/CloneVisitor.java")).Class().get("spoon.support.visitor.clone.CloneVisitor");
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testGenerateRoleHandler() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setCopyResources(false);
		launcher.getEnvironment().useTabulations(true);
		configurePrinter(launcher);
		launcher.setSourceOutputDirectory("./target/generated/");
		// Spoon model interfaces
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		// Templates
		launcher.addInputResource("./src/test/java/spoon/generating/meta");
		// Linked classes
		launcher.addInputResource("./src/main/java/spoon/reflect/meta/impl/AbstractRoleHandler.java");
		launcher.addProcessor(new RoleHandlersGenerator());
		launcher.setOutputFilter(new RegexFilter("\\Q" + RoleHandlersGenerator.TARGET_PACKAGE + ".ModelRoleHandlers\\E.*"));
		try {
			System.setProperty(MetamodelProperty.class.getName() + "-noRoleHandler", "true");
			launcher.run();
		} finally {
			System.setProperty(MetamodelProperty.class.getName() + "-noRoleHandler", "false");
		}

		// cp ./target/generated/spoon/reflect/meta/impl/ModelRoleHandlers.java ./src/main/java/spoon/reflect/meta/impl/ModelRoleHandlers.java
		CtClass<Object> actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/reflect/meta/impl/ModelRoleHandlers.java")).Class().get("spoon.reflect.meta.impl.ModelRoleHandlers");
		CtClass<Object> expected = build(new File("./src/main/java/spoon/reflect/meta/impl/ModelRoleHandlers.java")).Class().get("spoon.reflect.meta.impl.ModelRoleHandlers");
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	@Disabled("only meant to be run manually to make copying easier")
	void copyGeneratedFiles() throws IOException {
		copy(
			"./target/generated/spoon/support/visitor/replace/ReplacementVisitor.java",
			"./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java"
		);
		copy(
			"./target/generated/spoon/reflect/visitor/CtBiScannerDefault.java",
			"./src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java"
		);
		copy(
			"./target/generated/spoon/support/visitor/clone/CloneBuilder.java",
			"./src/main/java/spoon/support/visitor/clone/CloneBuilder.java"
		);
		copy(
			"./target/generated/spoon/support/visitor/clone/CloneVisitor.java",
			"./src/main/java/spoon/support/visitor/clone/CloneVisitor.java"
		);
		copy(
			"./target/generated/spoon/reflect/meta/impl/ModelRoleHandlers.java",
			"./src/main/java/spoon/reflect/meta/impl/ModelRoleHandlers.java"
		);
	}

	private static void copy(String from, String to) throws IOException {
		Files.copy(Path.of(from), Path.of(to), StandardCopyOption.REPLACE_EXISTING);
	}

	private void configurePrinter(Launcher launcher) {
		Environment environment = launcher.getEnvironment();
		environment.setPrettyPrinterCreator(new Supplier<>() {
			@Override
			public PrettyPrinter get() {
				// we want to create a printer configured for the given environment,
				// but we are who creates this printer - juggle around this StackOverflowError
				environment.setPrettyPrinterCreator(null);
				DefaultJavaPrettyPrinter printer = (DefaultJavaPrettyPrinter) environment.createPrettyPrinter();
				environment.setPrettyPrinterCreator(this);
				PrinterHelper printerHelper = new PrinterHelper(environment);
				printerHelper.setPrefixBlockComments(true);
				printer.setPrinterTokenWriter(new DefaultTokenWriter(printerHelper));
				return printer;
			}
		});
	}

	private class RegexFilter implements Filter<CtType<?>> {
		private final Pattern regex;

		private RegexFilter(String regex) {
			if (regex == null) {
				throw new IllegalArgumentException();
			}
			this.regex = Pattern.compile(regex);
		}

		public boolean matches(CtType<?> element) {
			Matcher m = regex.matcher(element.getQualifiedName());
			return m.matches();
		}
	}
}
