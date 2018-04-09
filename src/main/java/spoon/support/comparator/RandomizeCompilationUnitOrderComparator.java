/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.support.comparator;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import java.util.Comparator;
import java.util.Random;

public class RandomizeCompilationUnitOrderComparator implements Comparator<CompilationUnitDeclaration> {
	private Random random;

	public RandomizeCompilationUnitOrderComparator(int seed) {
		this.random = new Random(seed);
	}

	@Override
	public int compare(CompilationUnitDeclaration o1, CompilationUnitDeclaration o2) {
		int r = this.random.nextInt(3); // can be 0, 1 or 2
		return r - 1; // can be -1, 0 or 1
	}
}
