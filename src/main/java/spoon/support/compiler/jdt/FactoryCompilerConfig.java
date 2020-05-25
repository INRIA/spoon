/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.SpoonModelBuilder;
import spoon.reflect.declaration.CtType;

public class FactoryCompilerConfig implements SpoonModelBuilder.InputType {

	public static final SpoonModelBuilder.InputType INSTANCE = new FactoryCompilerConfig();

	//avoid direct instantiation. But somebody can inherit
	protected FactoryCompilerConfig() {
	}

	/**
	 * returns the compilation units corresponding to the types in the factory.
	 */
	@Override
	public void initializeCompiler(JDTBatchCompiler compiler) {
		JDTBasedSpoonCompiler jdtCompiler = compiler.getJdtCompiler();
		List<CompilationUnit> unitList = new ArrayList<>();
		for (CtType<?> ctType : jdtCompiler.getFactory().Type().getAll()) {
			if (ctType.isTopLevel()) {
				unitList.add(new CompilationUnitWrapper(ctType));
			}
		}
		compiler.setCompilationUnits(unitList.toArray(new CompilationUnit[0]));
	}
}
