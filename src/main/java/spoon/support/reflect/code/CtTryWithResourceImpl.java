package spoon.support.reflect.code;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.RESOURCES_CONTAINER_DEFAULT_CAPACITY;

public class CtTryWithResourceImpl extends CtTryImpl
		implements CtTryWithResource {
	private static final long serialVersionUID = 1L;

	List<CtLocalVariable<?>> resources = EMPTY_LIST();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTryWithResource(this);
	}

	@Override
	public List<CtLocalVariable<?>> getResources() {
		return resources;
	}

	@Override
	public void setResources(List<CtLocalVariable<?>> resources) {		
		this.resources.clear();
		for(CtLocalVariable l:resources) {
			addResource(l);
		}
	}

	@Override
	public boolean addResource(CtLocalVariable<?> resource) {
		if (resources == CtElementImpl.<CtLocalVariable<?>>EMPTY_LIST()) {
			resources = new ArrayList<CtLocalVariable<?>>(
					RESOURCES_CONTAINER_DEFAULT_CAPACITY);
		}
		resource.setParent(this);
		return resources.add(resource);
	}

	@Override
	public boolean removeResource(CtLocalVariable<?> resource) {
		return resources != CtElementImpl.<CtLocalVariable<?>>EMPTY_LIST() &&
				resources.remove(resource);
	}
}
