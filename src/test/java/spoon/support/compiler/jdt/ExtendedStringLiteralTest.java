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
package spoon.support.compiler.jdt;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.support.compiler.jdt.testclasses.ExtendedStringLiteralTestClass;

public class ExtendedStringLiteralTest {

	@Test
	public void testExtendedStringLiteral() throws Exception {
		Launcher launcher = new Launcher() {
			@Override
			public SpoonModelBuilder createCompiler() {
				return new JDTBasedSpoonCompiler(getFactory()) {
					@Override
					protected JDTBatchCompiler createBatchCompiler() {
						return new JDTBatchCompiler(this) {
							@Override
							public CompilationUnitDeclaration[] getUnits() {
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
								return treeBuilderCompiler.buildUnits(getCompilationUnits());
							}
						};
					}
				};
			}
		};
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources(
				"./src/test/java/" + ExtendedStringLiteralTestClass.class.getCanonicalName().replace('.', '/') + ".java"));
		comp.build();

		CtClass<?> cl =
			comp.getFactory().Package().get("spoon.support.compiler.jdt.testclasses").
			getType("ExtendedStringLiteralTestClass");
		CtField<?> f = cl.getField("extendedStringLiteral");
		CtExpression<?> de = f.getDefaultExpression();
		assertEquals("\"hello world!\"", de.toString());
	}
}
