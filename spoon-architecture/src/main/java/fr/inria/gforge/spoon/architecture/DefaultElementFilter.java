package fr.inria.gforge.spoon.architecture;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public enum DefaultElementFilter {
	METHODS() {
		@Override
		public TypeFilter<CtMethod<?>> getFilter() {
			return new TypeFilter<CtMethod<?>>(CtMethod.class);
		}
	},
	FIELDS() {
		@Override
		public TypeFilter<CtField<?>> getFilter() {
			return new TypeFilter<CtField<?>>(CtField.class);
		}
	},
	CLASSES() {
		@Override
		public TypeFilter<CtClass<?>> getFilter() {
			return new TypeFilter<CtClass<?>>(CtClass.class);

		}
	},
	INTERFACES() {
		@Override
		public TypeFilter<CtInterface<?>> getFilter() {
			return new TypeFilter<CtInterface<?>>(CtInterface.class);

		}
	};

	public abstract <T extends CtElement> TypeFilter<T> getFilter();
}
