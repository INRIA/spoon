package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtModuleProvidedService;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CtModuleProvidedServiceImpl extends CtElementImpl implements CtModuleProvidedService {
	private CtTypeReference providingType;
	private List<CtTypeReference> usedTypes = CtElementImpl.emptyList();

	public CtModuleProvidedServiceImpl() {
		super();
	}

	@Override
	public CtTypeReference getProvidingType() {
		return this.providingType;
	}

	@Override
	public <T extends CtModuleProvidedService> T setProvidingType(CtTypeReference providingType) {
		if (providingType != null) {
			providingType.setParent(this);
		}

		this.providingType = providingType;
		return (T) this;
	}

	@Override
	public List<CtTypeReference> getUsedTypes() {
		return Collections.unmodifiableList(this.usedTypes);
	}

	@Override
	public <T extends CtModuleProvidedService> T setUsedTypes(List<CtTypeReference> usedTypes) {
		if (usedTypes == null || usedTypes.size() == 0) {
			this.usedTypes = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.usedTypes == CtElementImpl.<CtTypeReference>emptyList()) {
			this.usedTypes = new ArrayList<>();
		}

		this.usedTypes.addAll(usedTypes);
		return (T) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtModuleProvidedService(this);
	}
}
