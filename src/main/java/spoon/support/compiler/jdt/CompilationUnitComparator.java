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
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.Launcher;

import java.util.Comparator;
import java.util.Random;

public class CompilationUnitComparator implements Comparator<CompilationUnitDeclaration> {

	@Override
	public int compare(CompilationUnitDeclaration o1, CompilationUnitDeclaration o2) {
		int seed = 0;
		try {
			if (System.getenv("SPOON_SEED_CU_COMPARATOR") != null) {
				seed = Integer.parseInt(System.getenv("SPOON_SEED_CU_COMPARATOR"));
				Launcher.LOGGER.warn("Seed for CU sorting set with: " + seed);
			}
		} catch (NumberFormatException | SecurityException e) {
			Launcher.LOGGER.error("Error while parsing Spoon seed for CU sorting", e);
		}

		if (seed == 0) {
			String s1 = new String(o1.getFileName());
			String s2 = new String(o2.getFileName());
			return s1.compareTo(s2);
		} else {
			Random random = new Random(seed);
			int r = random.nextInt(3); // can be 0, 1 or 2
			return r - 1; // can be -1, 0 or 1
		}
	}
}
