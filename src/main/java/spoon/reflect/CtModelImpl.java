/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
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

	@Override
	public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
		return getRootPackage().getFactory().Query().createQuery(this.getRootPackage()).filterChildren(filter);
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return getRootPackage().getFactory().Query().createQuery(this.getRootPackage()).map(function);
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return getRootPackage().getFactory().Query().createQuery(this.getRootPackage()).map(queryStep);
	}

	public static class CtRootPackage extends CtPackageImpl {
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

				@Override
				public Factory getFactory() {
					return CtRootPackage.this.getFactory();
				}
			});
		}

		@Override
		public String getSimpleName() {
			return super.getSimpleName();
		}

		@Override
		public <T extends CtNamedElement> T setSimpleName(String name) {
			if (name == null) {
				return (T) this;
			}

			if (name.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
				return super.setSimpleName(name);
			}

			return (T) this;
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

	private final CtModule unnamedModule;

	public CtModelImpl(Factory f) {
		this.unnamedModule = f.Module().getOrCreate(CtModule.TOP_LEVEL_MODULE_NAME);
		getRootPackage().setFactory(f);
	}

	@Override
	public CtPackage getRootPackage() {
		return getUnnamedModule().getRootPackage();
	}


	@Override
	public Collection<CtType<?>> getAllTypes() {
		final List<CtType<?>> result = new ArrayList<>();
		getAllPackages().forEach(ctPackage -> {
			result.addAll(ctPackage.getTypes());
		});
		return result;
	}


	@Override
	public Collection<CtPackage> getAllPackages() {
<<<<<<< HEAD
		return Collections.unmodifiableCollection(getElements(new TypeFilter<>(CtPackage.class)));
=======
		return Collections.unmodifiableCollection(getRootPackage().getElements(new TypeFilter<>(CtPackage.class)));
>>>>>>> Start to implement classes
	}

	@Override
	public CtModule getUnnamedModule() {
		return this.unnamedModule;
	}

	@Override
	public Collection<CtModule> getAllModules() {
		return this.unnamedModule.getFactory().Module().getAllModules();
	}


	@Override
	public void processWith(Processor<?> processor) {
		QueueProcessingManager processingManager = new QueueProcessingManager(getRootPackage().getFactory());
		processingManager.addProcessor(processor);
		processingManager.process(getRootPackage());
	}

	@Override
	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return filterChildren(filter).list();
	}

}
