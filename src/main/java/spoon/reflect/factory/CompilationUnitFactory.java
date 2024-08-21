/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.SpoonException;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTSnippetCompiler;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A factory to create some evaluation utilities on the Spoon metamodel.
 */
public class CompilationUnitFactory extends SubFactory {

	/**
	 * Creates the evaluation factory.
	 */
	public CompilationUnitFactory(Factory factory) {
		super(factory);
	}

	/**
	 *
	 * A map file path -&gt unit.
	 *
	 * Core contract: key == unit.getFile().getPath()
	 *
	 * contract maintained by method addType.
	 */
	private transient Map<String, CompilationUnit> cachedCompilationUnits = new TreeMap<>();

	/**
	 * Gets an immutable compilation unit map.
	 *
	 * If you want to add a type to be pretty-printed, use {@link #addType(CtType)}.
	 *
	 * @return a map (path -&gt; {@link CompilationUnit})
	 */
	public Map<String, CompilationUnit> getMap() {
		// strong encapsulation
		// the map is an internal data structure not meant
		// to be directly manipulated
		return Collections.unmodifiableMap(cachedCompilationUnits);
	}

	/**
	 * Creates a compilation unit with no associated files.
	 */
	public CompilationUnit create() {
		return factory.Core().createCompilationUnit();
	}

	public CompilationUnit getOrCreate(CtPackage ctPackage) {
		if (!(ctPackage.getPosition().getCompilationUnit() instanceof NoSourcePosition.NullCompilationUnit)) {
			return ctPackage.getPosition().getCompilationUnit();
		} else {

			CtModule module;
			if (factory.getEnvironment().getComplianceLevel() > 8) {
				module = ctPackage.getParent(CtModule.class);
			} else {
				module = null;
			}
			File file = this.factory.getEnvironment().getOutputDestinationHandler().getOutputPath(module, ctPackage, null).toFile();
			try {
				String path = file.getCanonicalPath();
				CompilationUnit result = this.getOrCreate(path);
				result.setDeclaredPackage(ctPackage);
				ctPackage.setPosition(this.factory.createPartialSourcePosition(result));
				return result;
			} catch (IOException e) {
				throw new SpoonException("Cannot get path for file: " + file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * remove a type from the list of types to be pretty-printed
	 */
	public void removeType(CtType<?> type) {
		File file = type.getPosition().getCompilationUnit().getFile();
		if (file != null) {
			cachedCompilationUnits.remove(type.getPosition().getCompilationUnit().getFile().getAbsolutePath());
		} else {
			// Virtual files do not have a file, so fall back to a slow scan, trying to find them by reference equality
			cachedCompilationUnits.values().removeIf(it -> it == type.getPosition().getCompilationUnit());
		}
	}

	/**
	 * add a new type to be pretty-printed
	 */
	public CompilationUnit addType(CtType type) {
		if (type.isTopLevel()) {
			CtModule module;
			if (type.getPackage() != null && factory.getEnvironment().getComplianceLevel() > 8) {
				module = type.getPackage().getParent(CtModule.class);
			} else {
				module = null;
			}
			File file = this.factory.getEnvironment().getOutputDestinationHandler().getOutputPath(module, type.getPackage(), type).toFile();
			try {
				String path = file.getCanonicalPath();
				CompilationUnit result = this._create(path);
				result.addDeclaredType(type);
				// for sniper, we need to keep the link to original source code
				// type.setPosition(this.factory.createPartialSourcePosition(result));
				return result;
			} catch (IOException e) {
				throw new SpoonException("Cannot get path for file: " + file.getAbsolutePath(), e);
			}
		} else {
			return getOrCreate(type.getTopLevelType());
		}

	}

	/**
	 * Returns the compilation unit corresponding to this type. Creates one on-the-fly if needed.
	 */
	public CompilationUnit getOrCreate(CtType type) {
		if (type == null) {
			return null;
		}
		if (!(type.getPosition().getCompilationUnit() instanceof NoSourcePosition.NullCompilationUnit)) {
			return type.getPosition().getCompilationUnit();
		}
		CompilationUnit compilationUnit = addType(type);
		type.setPosition(this.factory.createPartialSourcePosition(compilationUnit));
		return compilationUnit;
	}

	public CompilationUnit getOrCreate(CtModule module) {
		if (!(module.getPosition().getCompilationUnit() instanceof NoSourcePosition.NullCompilationUnit)) {
			return module.getPosition().getCompilationUnit();
		} else {
			File file = this.factory.getEnvironment().getOutputDestinationHandler().getOutputPath(module, null, null).toFile();
			try {
				String path = file.getCanonicalPath();
				CompilationUnit result = this.getOrCreate(path);
				result.setDeclaredModule(module);
				module.setPosition(this.factory.createPartialSourcePosition(result));
				return result;
			} catch (IOException e) {
				throw new SpoonException("Cannot get path for file: " + file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * Creates or gets a compilation unit for a given file path.
	 */
	public CompilationUnit getOrCreate(String filePath) {
		CompilationUnit cu = cachedCompilationUnits.get(filePath);
		if (cu != null) {
			return cu;
		}
		return _create(filePath);
	}

	private CompilationUnit _create(String filePath) {
		CompilationUnit cu;
		if (filePath.startsWith(JDTSnippetCompiler.SNIPPET_FILENAME_PREFIX)) {
			cu = factory.Core().createCompilationUnit();
			//put the virtual compilation unit of code snippet into cache too, so the JDTCommentBuilder can found it
			cachedCompilationUnits.put(filePath, cu);
			return cu;
		}
		cu = factory.Core().createCompilationUnit();

		if (!filePath.equals(VirtualFile.VIRTUAL_FILE_NAME)) {
			cu.setFile(new File(filePath));
		}

		cachedCompilationUnits.put(filePath, cu);
		return cu;
	}

	/**
	 * Removes compilation unit from the cache and returns it
	 * Used by JDTSnippetCompiler to remove processed snippet from the cache
	 * @param filePath
	 * @return a cached compilation unit or null
	 */
	public CompilationUnit removeFromCache(String filePath) {
		return cachedCompilationUnits.remove(filePath);
	}

}
