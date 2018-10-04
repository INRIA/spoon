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
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtUsedServiceImpl extends CtElementImpl implements CtUsedService {
	@MetamodelPropertyField(role = CtRole.SERVICE_TYPE)
	private CtTypeReference serviceType;

	@Override
	public CtTypeReference getServiceType() {
		return this.serviceType;
	}

	@Override
	public <T extends CtUsedService> T setServiceType(CtTypeReference usedService) {
		if (usedService != null) {
			usedService.setParent(this);
		}

		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.SERVICE_TYPE, usedService, this.serviceType);
		this.serviceType = usedService;
		return (T) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtUsedService(this);
	}

	@Override
	public CtUsedService clone() {
		return (CtUsedService) super.clone();
	}
}
