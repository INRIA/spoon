/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.cu;

import spoon.processing.FactoryAccessor;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.Import;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.COMPILATION_UNIT_DECLARED_TYPES_CONTAINER_DEFAULT_CAPACITY;

public class CompilationUnitImpl implements CompilationUnit, FactoryAccessor {

	Factory factory;

	List<CtType<?>> declaredTypes = new ArrayList<CtType<?>>(
			COMPILATION_UNIT_DECLARED_TYPES_CONTAINER_DEFAULT_CAPACITY);

	public List<CtType<?>> getDeclaredTypes() {
		return declaredTypes;
	}

	File file;

	public File getFile() {
		return file;
	}

	public CtType<?> getMainType() {
		if (getFile() == null) {
			return getDeclaredTypes().get(0);
		}
		for (CtType<?> t : getDeclaredTypes()) {
			String name = getFile().getName();
			name = name.substring(0, name.lastIndexOf("."));
			if (t.getSimpleName().equals(name)) {
				return t;
			}
		}
		throw new RuntimeException(
				"inconsistent compilation unit: '"
						+ file
						+ "': declared types are "
						+ getDeclaredTypes());
	}

	public void setDeclaredTypes(List<CtType<?>> types) {
		this.declaredTypes = types;
	}

	public void setFile(File file) {
		this.file = file;
	}

	String originalSourceCode;

	public String getOriginalSourceCode() {
		try {
			if (originalSourceCode == null) {
				FileInputStream s = new FileInputStream(getFile());
				byte[] elementBytes = new byte[s.available()];
				s.read(elementBytes);
				s.close();
				originalSourceCode = new String(elementBytes);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return originalSourceCode;
	}

	public int beginOfLineIndex(int index) {
		int cur = index;
		while (cur >= 0 && getOriginalSourceCode().charAt(cur) != '\n') {
			cur--;
		}
		return cur + 1;
	}

	public int nextLineIndex(int index) {
		int cur = index;
		while (cur < getOriginalSourceCode().length()
				&& getOriginalSourceCode().charAt(cur) != '\n') {
			cur++;
		}
		return cur + 1;
	}

	public int getTabCount(int index) {
		int cur = index;
		int tabCount = 0;
		int whiteSpaceCount = 0;
		while (cur < getOriginalSourceCode().length()
				&& (getOriginalSourceCode().charAt(cur) == ' ' || getOriginalSourceCode()
				.charAt(cur) == '\t')) {
			if (getOriginalSourceCode().charAt(cur) == '\t') {
				tabCount++;
			}
			if (getOriginalSourceCode().charAt(cur) == ' ') {
				whiteSpaceCount++;
			}
			cur++;
		}
		tabCount += whiteSpaceCount
				/ getFactory().getEnvironment().getTabulationSize();
		return tabCount;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	boolean autoImport = true;

	Set<Import> manualImports = new HashSet<Import>();

	public boolean isAutoImport() {
		return autoImport;
	}

	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}

	public Set<Import> getManualImports() {
		return manualImports;
	}

	public void setManualImports(Set<Import> manualImports) {
		this.manualImports = manualImports;
	}

}
