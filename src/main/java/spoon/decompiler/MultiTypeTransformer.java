package spoon.decompiler;

import spoon.reflect.declaration.CtType;

import java.util.Collection;
import java.util.LinkedHashSet;

public class MultiTypeTransformer implements TypeTransformer {

	protected LinkedHashSet<TypeTransformer> transformers;

	public MultiTypeTransformer() {
		transformers = new LinkedHashSet<>();
	}

	public void addTransformer(TypeTransformer transformer) {
		transformers.add(transformer);
	}

	public void addTransformers(Collection<TypeTransformer> transformers) {
		this.transformers.addAll(transformers);
	}

	@Override
	public void transform(CtType type) {
		for (TypeTransformer transformer: transformers) {
			if (transformer.accept(type)) {
				transformer.transform(type);
			}
		}
	}

	@Override
	public boolean accept(CtType type) {
		for (TypeTransformer transformer: transformers) {
			if (transformer.accept(type)) {
				return true;
			}
		}
		return false;
	}
}
