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
package spoon.support.reflect.code;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.RESOURCES_CONTAINER_DEFAULT_CAPACITY;

public class CtTryWithResourceImpl extends CtTryImpl implements CtTryWithResource {
	private static final long serialVersionUID = 1L;

	List<CtLocalVariable<?>> resources = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTryWithResource(this);
	}

	@Override
	public List<CtLocalVariable<?>> getResources() {
		return resources;
	}

	@Override
	public <T extends CtTryWithResource> T setResources(List<CtLocalVariable<?>> resources) {
		if (resources == null || resources.isEmpty()) {
			this.resources = CtElementImpl.emptyList();
			return (T) this;
		}
		this.resources.clear();
		for (CtLocalVariable<?> l : resources) {
			addResource(l);
		}
		return (T) this;
	}

	@Override
	public <T extends CtTryWithResource> T addResource(CtLocalVariable<?> resource) {
		if (resource == null) {
			return (T) this;
		}
		if (resources == CtElementImpl.<CtLocalVariable<?>>emptyList()) {
			resources = new ArrayList<>(RESOURCES_CONTAINER_DEFAULT_CAPACITY);
		}
		resource.setParent(this);
		resources.add(resource);
		return (T) this;
	}

	@Override
	public boolean removeResource(CtLocalVariable<?> resource) {
		return resources != CtElementImpl.<CtLocalVariable<?>>emptyList() && resources.remove(resource);
	}

	@Override
	public CtTryWithResource clone() {
		return (CtTryWithResource) super.clone();
	}
}
