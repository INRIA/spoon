/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.processing.FileGenerator;
import spoon.processing.TraversalStrategy;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.compiler.SpoonProgress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A processor that generates compilable Java source files from the meta-model.
 */
public class JavaOutputProcessor extends AbstractProcessor<CtNamedElement> implements FileGenerator<CtNamedElement> {
	PrettyPrinter printer;

	List<File> printedFiles = new ArrayList<>();

	/**
	 * @param printer  the PrettyPrinter to use for written the files
	 */
	public JavaOutputProcessor(PrettyPrinter printer) {
		this.printer = printer;
	}

	/**
	 * Such processor will use printer created by {@link Environment#createPrettyPrinter()}
	 */
	public JavaOutputProcessor() {
	}

	@Override
	public Environment getEnvironment() {
		return this.getFactory().getEnvironment();
	}

	public PrettyPrinter getPrinter() {
		if (printer == null) {
			//create printer using CURRENT Environment settings
			return getFactory().getEnvironment().createPrettyPrinter();
		}
		return printer;
	}

	@Override
	public List<File> getCreatedFiles() {
		return printedFiles;
	}

	@Override
	public File getOutputDirectory() {
		return this.getEnvironment().getSourceOutputDirectory();
	}

	@Override
	public void init() {
		// Skip loading properties
		// super.init();
		File directory = getOutputDirectory();

		// Check output directory
		if (directory == null) {
			throw new SpoonException("You should set output directory before printing");
		}

		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new SpoonException("Error creating output directory");
			}
		}
	}

	Map<String, Map<Integer, Integer>> lineNumberMappings = new HashMap<>();

	/**
	 * Creates the Java file associated to the given element. Splits top-level
	 * classes in different files (even if they are in the same file in the
	 * original sources).
	 */
	public void createJavaFile(CtType<?> element) {
		Path typePath = getElementPath(element);

		// we only create a file for top-level classes
		if (!element.isTopLevel()) {
			throw new IllegalArgumentException();
		}

		CompilationUnit cu = this.getFactory().CompilationUnit().getOrCreate(element);
		List<CtType<?>> toBePrinted = new ArrayList<>();
		toBePrinted.add(element);

		PrettyPrinter printer = getPrinter();
		printer.calculate(cu, toBePrinted);


		try {
			File file = typePath.toFile();
			file.createNewFile();
			if (!printedFiles.contains(file)) {
				printedFiles.add(file);
			}
			// print type
			try (PrintStream stream = new PrintStream(file)) {
				stream.print(printer.getResult());
				for (CtType<?> t : toBePrinted) {
					lineNumberMappings.put(t.getQualifiedName(), printer.getLineNumberMapping());
				}
			}
		} catch (IOException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		if (getEnvironment().getSpoonProgress() != null) {
			getEnvironment().getSpoonProgress().step(SpoonProgress.Process.PRINT, element.getQualifiedName());
		}
	}

	@Override
	public boolean isToBeProcessed(CtNamedElement candidate) {
		return candidate instanceof CtType<?> || candidate instanceof CtModule || candidate instanceof CtPackage && (!candidate.getComments().isEmpty() || !candidate.getAnnotations().isEmpty());
	}

	/**
	 * Creates a source file for each processed top-level type and pretty prints
	 * its contents.
	 */
	@Override
	public void process(CtNamedElement nameElement) {
		if (nameElement instanceof CtType && ((CtType) nameElement).isTopLevel()) {
			createJavaFile((CtType<?>) nameElement);
		} else if (nameElement instanceof CtPackage) {
			createPackageFile((CtPackage) nameElement);
		} else if (nameElement instanceof CtModule) {
			createModuleFile((CtModule) nameElement);
		}
	}

	private void createPackageFile(CtPackage pack) {
		// Create package annotation file
		File packageAnnot = getElementPath(pack).toFile();
		if (!printedFiles.contains(packageAnnot)) {
			printedFiles.add(packageAnnot);
		}
		try (PrintStream stream = new PrintStream(packageAnnot)) {
			stream.println(getPrinter().printPackageInfo(pack));
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
	}

	private void createModuleFile(CtModule module) {
		if (getEnvironment().getComplianceLevel() > 8 && module != getFactory().getModel().getUnnamedModule()) {
			File moduleFile = getElementPath(module).toFile();
			if (!printedFiles.contains(moduleFile)) {
				printedFiles.add(moduleFile);
			}
			try (PrintStream stream = new PrintStream(moduleFile)) {
				stream.println(getPrinter().printModuleInfo(module));
			} catch (FileNotFoundException e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private Path getElementPath(CtModule type) {
		return createFolders(getEnvironment().getOutputDestinationHandler()
				.getOutputPath(type, null, null));
	}

	private Path getElementPath(CtPackage type) {
		return createFolders(getEnvironment().getOutputDestinationHandler()
				.getOutputPath(type.getDeclaringModule(), type, null));
	}

	private Path getElementPath(CtType type) {
		return createFolders(getEnvironment().getOutputDestinationHandler()
				.getOutputPath(type.getPackage().getDeclaringModule(),
						type.getPackage(), type));
	}

	private Path createFolders(Path outputPath) {
		if (!outputPath.getParent().toFile().exists()) {
			if (!outputPath.getParent().toFile().mkdirs()) {
				throw new RuntimeException("Error creating output directory");
			}
		}
		return outputPath;
	}

	public Map<String, Map<Integer, Integer>> getLineNumberMappings() {
		return lineNumberMappings;
	}

	@Override
	public TraversalStrategy getTraversalStrategy() {
		return TraversalStrategy.PRE_ORDER;
	}

}
