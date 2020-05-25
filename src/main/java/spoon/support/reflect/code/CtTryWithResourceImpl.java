/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.RESOURCES_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.TRY_RESOURCE;

public class CtTryWithResourceImpl extends CtTryImpl implements CtTryWithResource {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = TRY_RESOURCE)
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
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, TRY_RESOURCE, this.resources, new ArrayList<>(this.resources));
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
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, TRY_RESOURCE, this.resources, resource);
		resources.add(resource);
		return (T) this;
	}

	@Override
	public boolean removeResource(CtLocalVariable<?> resource) {
		if (resources == CtElementImpl.<CtLocalVariable<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, TRY_RESOURCE, resources, resources.indexOf(resource), resource);
		return resources.remove(resource);
	}

	@Override
	public CtTryWithResource clone() {
		return (CtTryWithResource) super.clone();
	}
}
