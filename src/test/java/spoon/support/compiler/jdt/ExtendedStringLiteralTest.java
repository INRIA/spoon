package spoon.support.compiler.jdt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;

public class ExtendedStringLiteralTest {

	@Test
	public void testExtendedStringLiteral() throws Exception {
		Launcher launcher = new Launcher() {
			@Override
			public SpoonCompiler createCompiler() {
				return new JDTBasedSpoonCompiler(getFactory()) {
					@Override
					protected JDTBatchCompiler createBatchCompiler(boolean useFactory) {
						return new JDTBatchCompiler(this, useFactory) {
							@Override
							public CompilationUnitDeclaration[] getUnits(List<SpoonFile> files) {
								startTime = System.currentTimeMillis();
								INameEnvironment environment = this.jdtCompiler.environment;
								if (environment == null) {
									environment = getLibraryAccess();
								}
								CompilerOptions compilerOptions = new CompilerOptions(this.options);

								// set to true to force executing visit(ExtendedStringLiteral,BlockScope)
								// which is not the case when set to false which is the default in JDTBatchCompiler
								// the test not succeeds since this was not the case before the pull request
								// and since visit(ExtendedStringLiteral,BlockScope) was throwing a RuntimeException
								compilerOptions.parseLiteralExpressionsAsConstants = true;
								
								TreeBuilderCompiler treeBuilderCompiler = new TreeBuilderCompiler(
										environment, getHandlingPolicy(), compilerOptions,
										this.jdtCompiler.requestor, getProblemFactory(), this.out,
										null);
								CompilationUnitDeclaration[] units = treeBuilderCompiler
										.buildUnits(getCompilationUnits(files));
								return units;
							}
						};
					}
				};
			}
		};
		SpoonCompiler comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources(
				"./src/test/java/spoon/support/compiler/jdt/ExtendedStringLiteralClass.java"));
		comp.build();
		
		CtClass<?> cl =
			comp.getFactory().Package().get("spoon.support.compiler.jdt").
			getType("ExtendedStringLiteralClass");
		CtField<?> f = cl.getField("extendedStringLiteral");
		CtExpression<?> de = f.getDefaultExpression();
		assertEquals("\"hello world!\"", de.toString());
	}
}
