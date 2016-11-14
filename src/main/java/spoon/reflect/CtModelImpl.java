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
package spoon.reflect;

import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtPackageImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CtModelImpl implements CtModel {

	private static final long serialVersionUID = 1L;

	private static class CtRootPackage extends CtPackageImpl {
		{
			this.setSimpleName(CtPackage.TOP_LEVEL_PACKAGE_NAME);
			this.setParent(new CtElementImpl() {
				@Override
				public void accept(CtVisitor visitor) {

				}

				@Override
				public CtElement getParent() throws ParentNotInitializedException {
					return null;
				}
							});
		}

		@Override
		public String getSimpleName() {
			return super.getSimpleName();
		}

		@Override
		public String getQualifiedName() {
			return "";
		}

		@Override
		public String toString() {
			return TOP_LEVEL_PACKAGE_NAME;
		}

	}

	private final CtPackage rootPackage = new CtRootPackage();

	public CtModelImpl(Factory f) {
		rootPackage.setFactory(f);
	}

	@Override
	public CtPackage getRootPackage() {
		return rootPackage;
	}


	@Override
	public Collection<CtType<?>> getAllTypes() {
		List<CtType<?>> types = new ArrayList<>();
		for (CtPackage pack : getAllPackages()) {
			types.addAll(pack.getTypes());
		}
		return types;
	}


	@Override
	public Collection<CtPackage> getAllPackages() {
		return Collections.unmodifiableCollection(rootPackage.getElements(new TypeFilter<>(CtPackage.class)));
	}


	@Override
	public void processWith(Processor<?> processor) {
		QueueProcessingManager processingManager = new QueueProcessingManager(rootPackage.getFactory());
		processingManager.addProcessor(processor);
		processingManager.process(getRootPackage());
	}

	@Override
	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return getRootPackage().getElements(filter);
	}

}
