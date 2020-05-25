/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
