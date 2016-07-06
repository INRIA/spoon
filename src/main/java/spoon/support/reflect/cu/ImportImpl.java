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
package spoon.support.reflect.cu;

import spoon.reflect.cu.Import;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class ImportImpl implements Import {

	public ImportImpl(CtTypeReference<?> type) {
		reference = type;
	}

	public ImportImpl(CtPackageReference pack) {
		reference = pack;
	}

	public ImportImpl(CtFieldReference<?> field) {
		reference = field;
	}

	CtReference reference;

	@Override
	public String toString() {
		String str = "import " + reference.toString();
		if (reference instanceof CtPackageReference) {
			str += ".*";
		}
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Import) {
			return reference.equals(((Import) obj).getReference());
		}
		return false;
	}

	public CtReference getReference() {
		return reference;
	}

}
