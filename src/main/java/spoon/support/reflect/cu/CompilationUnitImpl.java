/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

import spoon.SpoonException;
import spoon.processing.FactoryAccessor;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.COMPILATION_UNIT_DECLARED_TYPES_CONTAINER_DEFAULT_CAPACITY;

/**
 * Implements a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public class CompilationUnitImpl implements CompilationUnit, FactoryAccessor {
	private static final long serialVersionUID = 1L;

	Factory factory;

	List<CtType<?>> declaredTypes = new ArrayList<>(COMPILATION_UNIT_DECLARED_TYPES_CONTAINER_DEFAULT_CAPACITY);

	CtPackage ctPackage;

	Set<CtImport> imports = new HashSet<>();

	CtModule ctModule;

	File file;

	private SourcePosition sourcePosition;
	/**
	 * The index of line breaks, as computed by JDT.
	 * Used to compute line numbers afterwards.
	 */
	private int[] lineSeparatorPositions;

	private ElementSourceFragment rootFragment;

	@Override
	public UNIT_TYPE getUnitType() {
		// we try to guess based on the file name
		if (file != null) {
			if (file.getName().equals(DefaultJavaPrettyPrinter.JAVA_MODULE_DECLARATION)) {
				return UNIT_TYPE.MODULE_DECLARATION;
			} else if (file.getName().equals(DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION)) {
				return UNIT_TYPE.PACKAGE_DECLARATION;
			} else {
				return UNIT_TYPE.TYPE_DECLARATION;
			}
		// else we just check if there is a declared type
		} else {
			if (getDeclaredTypes().isEmpty()) {
				if (getDeclaredModule() != null) {
					return UNIT_TYPE.MODULE_DECLARATION;
				} else if (getDeclaredPackage() != null) {
					return UNIT_TYPE.PACKAGE_DECLARATION;
				} else {
					return UNIT_TYPE.UNKNOWN;
				}
			} else {
				return UNIT_TYPE.TYPE_DECLARATION;
			}
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public CtType<?> getMainType() {
		if (getFile() == null) {
			return getDeclaredTypes().get(0);
		}
		for (CtType<?> t : getDeclaredTypes()) {
			String name = getFile().getName();
			name = name.substring(0, name.lastIndexOf('.'));
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

	@Override
	public List<CtType<?>> getDeclaredTypes() {
		return Collections.unmodifiableList(declaredTypes);
	}

	@Override
	public void setDeclaredTypes(List<CtType<?>> types) {
		this.declaredTypes.clear();
		this.declaredTypes.addAll(types);
	}

	@Override
	public void addDeclaredType(CtType type) {
		this.declaredTypes.add(type);
	}

	@Override
	public CtModule getDeclaredModule() {
		return this.ctModule;
	}

	@Override
	public void setDeclaredModule(CtModule module) {
		this.ctModule = module;
	}

	@Override
	public CtPackage getDeclaredPackage() {
		return ctPackage;
	}

	@Override
	public void setDeclaredPackage(CtPackage ctPackage) {
		this.ctPackage = ctPackage;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public List<File> getBinaryFiles() {
		final List<File> binaries = new ArrayList<>();
		final String output = getFactory()
				.getEnvironment()
				.getBinaryOutputDirectory();
		if (output != null) {
			final File base = Paths
					.get(output, getDeclaredPackage()
					.getQualifiedName()
					.replace(".", File.separator))
					.toFile();
			if (base.isDirectory()) {
				for (final CtType type : getDeclaredTypes()) {
					// Add main type, for instance, 'Foo.class'.
					final String nameOfType = type.getSimpleName();
					final File fileOfType = new File(
							base, nameOfType + ".class");
					if (fileOfType.isFile()) {
						binaries.add(fileOfType);
					}
					// Add inner/anonymous types, for instance,
					// 'Foo$Bar.class'. Use 'getElements()' rather than
					// 'getNestedTypes()' to also fetch inner types of inner
					// types of inner types ... and so on.
					for (final CtType inner : type.getElements(
							new TypeFilter<>(CtType.class))) {
						// 'getElements' does not only return inner types but
						// also returns 'type' itself. Thus, we need to ensure
						// to not add 'type' twice.
						if (!inner.equals(type)) {
							final String nameOfInner =
									nameOfType + "$" + inner.getSimpleName();
							final File fileOfInnerType = new File(
									base, nameOfInner + ".class");
							if (fileOfInnerType.isFile()) {
								binaries.add(fileOfInnerType);
							}
						}
					}
				}
			}
		}
		return binaries;
	}

	String originalSourceCode;

	@Override
	public String getOriginalSourceCode() {

		if (originalSourceCode == null && getFile() != null) {
			try (FileInputStream s = new FileInputStream(getFile())) {
				byte[] elementBytes = new byte[s.available()];
				s.read(elementBytes);
				originalSourceCode = new String(elementBytes, this.getFactory().getEnvironment().getEncoding());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return originalSourceCode;
	}

	@Override
	public int beginOfLineIndex(int index) {
		int cur = index;
		while (cur >= 0 && getOriginalSourceCode().charAt(cur) != '\n') {
			cur--;
		}
		return cur + 1;
	}

	@Override
	public int nextLineIndex(int index) {
		int cur = index;
		while (cur < getOriginalSourceCode().length()
				&& getOriginalSourceCode().charAt(cur) != '\n') {
			cur++;
		}
		return cur + 1;
	}

	@Override
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

	@Override
	public Set<CtImport> getImports() {
		return this.imports;
	}

	@Override
	public void setImports(Set<CtImport> imports) {
		this.imports = imports;
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	boolean autoImport = true;

	public boolean isAutoImport() {
		return autoImport;
	}

	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}

	private PartialSourcePositionImpl myPartialSourcePosition;
	/**
	 * @return a {@link SourcePosition} which points to this {@link CompilationUnit}. It always returns same value to safe memory.
	 */
	public SourcePosition getOrCreatePartialSourcePosition() {
		if (myPartialSourcePosition == null) {
			myPartialSourcePosition = new PartialSourcePositionImpl(this);
		}
		return myPartialSourcePosition;
	}

	@Override
	public ElementSourceFragment getOriginalSourceFragment() {
		if (rootFragment == null) {
			if (ctModule != null) {
				throw new SpoonException("Root source fragment of compilation unit of module is not supported");
			}
			if (ctPackage != null && declaredTypes.isEmpty()) {
				throw new SpoonException("Root source fragment of compilation unit of package is not supported");
			}
			rootFragment = new ElementSourceFragment(this, null);
			for (CtImport imprt : getImports()) {
				rootFragment.addChild(new ElementSourceFragment(imprt, null /*TODO role for import of CU*/));
			}
			for (CtType<?> ctType : declaredTypes) {
				rootFragment.addTreeOfSourceFragmentsOfElement(ctType);
			}
		}
		return rootFragment;
	}

	@Override
	public int[] getLineSeparatorPositions() {
		return lineSeparatorPositions;
	}

	@Override
	public void setLineSeparatorPositions(int[] lineSeparatorPositions) {
		this.lineSeparatorPositions = lineSeparatorPositions;
	}

	@Override
	public SourcePosition getPosition() {
		if (sourcePosition == null) {
			String sourceCode = getOriginalSourceCode();
			if (sourceCode != null) {
				sourcePosition = getFactory().Core().createSourcePosition(this, 0, sourceCode.length() - 1, getLineSeparatorPositions());
			} else {
				sourcePosition = SourcePosition.NOPOSITION;
			}
		}
		return sourcePosition;
	}
}
