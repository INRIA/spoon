package spoon.processing;

import org.junit.ComparisonFailure;
import org.junit.Test;
import spoon.Launcher;
import spoon.generating.CloneVisitorGenerator;
import spoon.generating.CtBiScannerGenerator;
import spoon.generating.ReplacementVisitorGenerator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.visitor.replace.ReplacementVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;

public class CtGenerationTest {
	@Test
	public void testGenerateReplacementVisitor() throws Exception {
		//use always LINUX line separator, because generated files are committed to Spoon repository which expects that. 
		System.setProperty("line.separator", "\n");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().useTabulations(true);
		//launcher.getEnvironment().setAutoImports(true);
		launcher.setSourceOutputDirectory("./target/generated/");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.addInputResource("./src/main/java/spoon/generating/replace/");
		launcher.addProcessor(new ReplacementVisitorGenerator());
		launcher.setOutputFilter(new RegexFilter("spoon.support.visitor.replace.*"));
		launcher.run();

		// cp ./target/generated/spoon/support/visitor/replace/ReplacementVisitor.java ./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java
		CtClass<Object> actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory()+"/spoon/support/visitor/replace/ReplacementVisitor.java")).Class().get("spoon.support.visitor.replace.ReplacementVisitor");
		CtClass<Object> expected = build(new File("./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java")).Class().get("spoon.support.visitor.replace.ReplacementVisitor");
		try {
			assertThat(actual).isEqualTo(expected);
		} catch (AssertionError e) {
			throw new ComparisonFailure("ReplacementVisitor different", expected.toString(), actual.toString());
		}
	}

	@Test
	public void testGenerateCtBiScanner() throws Exception {
		// contract: generates the biscanner that is used for equality checking
		//use always LINUX line separator, because generated files are committed to Spoon repository which expects that. 
		System.setProperty("line.separator", "\n");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().useTabulations(true);
		launcher.setSourceOutputDirectory("./target/generated/");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.addInputResource("./src/main/java/spoon/generating/scanner/");
		launcher.addProcessor(new CtBiScannerGenerator());
		launcher.setOutputFilter(new RegexFilter("spoon.reflect.visitor.CtBiScannerDefault"));
		launcher.run();

		// cp ./target/generated/spoon/reflect/visitor/CtBiScannerDefault.java ./src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java
		// we don't necessarily want to hard-wired the relation bewteen CtScanner and CtBiScannerDefault.java
		// this can be done on an informed basis when important changes are made in the metamodel/scanner
		// and then we can have smaller clean tested pull requests to see the impact of the change
		// cp ./target/generated/spoon/reflect/visitor/CtBiScannerDefault.java ./src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java
		assertThat(build(new File("./src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java")).Class().get(CtBiScannerDefault.class))
				.isEqualTo(build(new File("./target/generated/spoon/reflect/visitor/CtBiScannerDefault.java")).Class().get(CtBiScannerDefault.class));
	}

	@Test
	public void testGenerateCloneVisitor() throws Exception {
		//use always LINUX line separator, because generated files are committed to Spoon repository which expects that. 
		System.setProperty("line.separator", "\n");
		// contract: generates CloneBuilder.java and CloneBuilder.java
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().useTabulations(true);
		//launcher.getEnvironment().setAutoImports(true);
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
		launcher.addInputResource("./src/main/java/spoon/generating/clone/");
		launcher.addProcessor(new CloneVisitorGenerator());
		launcher.setOutputFilter(new RegexFilter("spoon.support.visitor.clone.*"));
		launcher.run();

		// cp ./target/generated/spoon/support/visitor/clone/CloneBuilder.java  ./src/main/java/spoon/support/visitor/clone/CloneBuilder.java
		CtClass<Object> actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory()+"/spoon/support/visitor/clone/CloneBuilder.java")).Class().get("spoon.support.visitor.clone.CloneBuilder");
		CtClass<Object> expected = build(new File("./src/main/java/spoon/support/visitor/clone/CloneBuilder.java")).Class().get("spoon.support.visitor.clone.CloneBuilder");
		try {
			assertThat(actual)
					.isEqualTo(expected);
		} catch (AssertionError e) {
			throw new ComparisonFailure("CloneBuilder different", expected.toString(), actual.toString());
		}

		// cp ./target/generated/spoon/support/visitor/clone/CloneVisitor.java  ./src/main/java/spoon/support/visitor/clone/CloneVisitor.java
		actual = build(new File(launcher.getModelBuilder().getSourceOutputDirectory()+"/spoon/support/visitor/clone/CloneVisitor.java")).Class().get("spoon.support.visitor.clone.CloneVisitor");
		expected = build(new File("./src/main/java/spoon/support/visitor/clone/CloneVisitor.java")).Class().get("spoon.support.visitor.clone.CloneVisitor");
		try {
			assertThat(actual)
					.isEqualTo(expected);
		} catch (AssertionError e) {
			throw new ComparisonFailure("CloneVisitor different", expected.toString(), actual.toString());
		}
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
