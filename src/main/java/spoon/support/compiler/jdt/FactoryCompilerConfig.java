/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.SpoonModelBuilder;
import spoon.reflect.declaration.CtType;

public class FactoryCompilerConfig implements SpoonModelBuilder.InputType {

	public static final SpoonModelBuilder.InputType INSTANCE = new FactoryCompilerConfig();

	//avoid direct instantiation. But somebody can inherit
	protected FactoryCompilerConfig() {
	};

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
		compiler.setCompilationUnits(unitList.toArray(new CompilationUnit[unitList.size()]));
	}
}
