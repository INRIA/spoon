/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect;

import spoon.processing.Processor;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.CtModuleImpl;
import spoon.support.util.internal.ElementNameMap;

import java.util.*;
import java.util.stream.Collectors;

public class CtModelImpl implements CtModel {

	private static final long serialVersionUID = 1L;

	private final CtModule unnamedModule;
	private final Modules modules;
	private boolean buildModelFinished;

	public CtModelImpl(Factory factory) {
		this.unnamedModule = new CtModuleImpl.UnnamedModule(factory);
		this.modules = new Modules();
		addModule(unnamedModule);
	}

	@Override
	public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
		return getUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).filterChildren(filter);
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return getUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).map(function);
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return getUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).map(queryStep);
	}

	@Override
	public Collection<CtType<?>> getAllTypes() {
		return getAllPackages().stream().map(CtPackage::getTypes).flatMap(Collection::stream).collect(Collectors.toList());
	}


	@Override
	public Collection<CtPackage> getAllPackages() {
		return Collections.unmodifiableCollection(getElements(new TypeFilter<>(CtPackage.class)));
	}

	@Override
	public CtPackage getRootPackage() {
		return getUnnamedModule().getRootPackage();
	}

	@Override
	public CtModule getUnnamedModule() {
		return this.unnamedModule;
	}

	@Override
	public CtModule getModule(String name) {
		return modules.get(name);
	}

	@Override
	public Collection<CtModule> getAllModules() {
		return Collections.unmodifiableCollection(modules.values());
	}

	@Override
	public void processWith(Processor<?> processor) {
		QueueProcessingManager processingManager = new QueueProcessingManager(getUnnamedModule().getFactory());
		processingManager.addProcessor(processor);
		processingManager.process(getAllModules());
	}

	@Override
	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return filterChildren(filter).list();
	}


	@Override
	public boolean isBuildModelFinished() {
		return this.buildModelFinished;
	}

	@Override
	public <T extends CtModel> T addModule(CtModule module) {
		modules.put(module.getSimpleName(), module);
		return (T) this;
	}

	@Override
	public <T extends CtModel> T removeModule(CtModule module) {
		modules.remove(module.getSimpleName());
		return (T) this;
	}

	@Override
	public <T extends CtModel> T setBuildModelIsFinished(boolean buildModelFinished) {
		this.buildModelFinished = buildModelFinished;
		return (T) this;
	}

	public void updateModuleName(CtModule newModule, String oldName) {
		modules.updateKey(oldName, newModule.getSimpleName());
	}

	private static class Modules extends ElementNameMap<CtModule>{
		@Override
		protected CtElement getOwner() {
			return null;
		}

		@Override
		protected CtRole getRole() {
			return CtRole.DECLARED_MODULE;
		}
	}
}
