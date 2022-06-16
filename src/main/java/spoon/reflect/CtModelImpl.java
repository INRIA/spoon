/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect;

import spoon.SpoonException;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.CtUnnamedModuleImpl;
import spoon.support.util.internal.ElementNameMap;

import java.util.*;
import java.util.stream.Collectors;

public class CtModelImpl implements CtModel {

	private static final long serialVersionUID = 1L;

	private final CtModule unnamedModule;
	private final Modules modules;
	private boolean buildModelFinished;

	public CtModelImpl(Factory factory) {
		this.unnamedModule = new CtUnnamedModuleImpl(factory);
		this.modules = new Modules();
		addModule(unnamedModule);
	}

	@Override
	public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
		return getUnnamedModule()
				.getFactory()
				.Query()
				.createQuery(this.getAllModules().toArray())
				.filterChildren(filter);
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return getUnnamedModule()
				.getFactory()
				.Query()
				.createQuery(this.getAllModules().toArray())
				.map(function);
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return getUnnamedModule()
				.getFactory()
				.Query()
				.createQuery(this.getAllModules().toArray())
				.map(queryStep);
	}

	@Override
	public Collection<CtType<?>> getAllTypes() {
		return getAllPackages()
				.stream()
				.flatMap(ctPackage -> ctPackage.getTypes().stream())
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Collection<CtPackage> getAllPackages() {
		return getAllModules()
				.stream()
				.map(CtModule::getAllPackages)
				.flatMap(Collection::stream)
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	@Deprecated
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
	public CtPackage getPackage(String qualifiedName) {
		if(qualifiedName == null || qualifiedName.isEmpty()){
			return unnamedModule.getRootPackage();
		}

		return getAllModules()
				.stream()
				.map(ctModule -> ctModule.getPackage(qualifiedName))
				.filter(Objects::nonNull)
				.reduce((u, v) -> throwDuplicateException(qualifiedName))
				.orElse(null);
	}

	private <T> T throwDuplicateException(String qualifiedName) {
		throw new SpoonException(
				"Ambiguous package name detected. If you believe the code you analyzed is correct, please"
						+ " file an issue and reference https://github.com/INRIA/spoon/issues/4051. "
						+ "Error details: " + qualifiedName
		);
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
