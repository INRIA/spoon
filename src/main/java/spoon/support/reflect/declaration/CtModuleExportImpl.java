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
package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtModuleExport;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.Collections;
import java.util.Set;

public class CtModuleExportImpl extends CtElementImpl implements CtModuleExport {
	CtPackageReference packageReference;

	Set<CtModuleReference> targets = CtElementImpl.emptySet();

	@Override
	public CtPackageReference getPackageReference() {
		return this.packageReference;
	}

	@Override
	public <T extends CtModuleExport> T setPackageReference(CtPackageReference packageReference) {
		if (packageReference != null) {
			packageReference.setParent(this);
		}
		this.packageReference = packageReference;
		return (T) this;
	}

	@Override
	public Set<CtModuleReference> getTargetExport() {
		return Collections.unmodifiableSet(targets);
	}

	@Override
	public <T extends CtModuleExport> T setTargetExport(Set<CtModuleReference> targetExport) {
		if (targetExport == null || targetExport.isEmpty()) {
			this.targets = CtElementImpl.emptySet();
			return (T) this;
		}

		this.targets = Collections.unmodifiableSet(targetExport);
		return (T) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtModuleExport(this);
	}
}
