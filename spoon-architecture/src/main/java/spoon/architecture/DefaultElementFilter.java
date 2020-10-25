package spoon.architecture;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;

@SuppressWarnings("unchecked")
public enum DefaultElementFilter {
	METHODS() {
		@Override
		public Filter<CtMethod<?>> getFilter() {
			return new TypeFilter<CtMethod<?>>(CtMethod.class);
		}
	},
	FIELDS() {
		@Override
		public Filter<CtField<?>> getFilter() {
			return new TypeFilter<CtField<?>>(CtField.class);
		}
	},
	CLASSES() {
		@Override
		public Filter<CtClass<?>> getFilter() {
			return new TypeFilter<CtClass<?>>(CtClass.class);

		}
	},
	INTERFACES() {
		@Override
		public Filter<CtInterface<?>> getFilter() {
			return new TypeFilter<CtInterface<?>>(CtInterface.class);

		}
	},
	TYPES() {
		@Override
		public AbstractFilter<CtElement> getFilter() {
			return new AbstractFilter<CtElement>() {
				private AbstractFilter<CtType<?>> filter = new TypeFilter<CtType<?>>(CtType.class);
				@Override
				public boolean matches(CtElement element) {
					if (element instanceof CtType) {
						return filter.matches((CtType<?>) element) && !(element instanceof CtTypeParameter);
					}
					return false;
				}

			};
		}
	},
	PACKAGES() {
		@Override
		public Filter<CtPackage> getFilter() {
			return new TypeFilter<CtPackage>(CtPackage.class);

		}
	},
	TYPE_REFERENCE() {
		@Override
		public Filter<CtTypeReference<?>> getFilter() {
			return new TypeFilter<CtTypeReference<?>>(CtTypeReference.class);

		}
};

	public abstract <T extends CtElement> Filter<T> getFilter();
}
