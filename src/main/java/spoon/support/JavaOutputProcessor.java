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
package spoon.support;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.processing.FileGenerator;
import spoon.processing.TraversalStrategy;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import spoon.reflect.cu.CompilationUnit;

/**
 * A processor that generates compilable Java source files from the meta-model.
 */
public class JavaOutputProcessor extends AbstractProcessor<CtNamedElement> implements FileGenerator<CtNamedElement> {
	PrettyPrinter printer;

	File directory;

	List<File> printedFiles = new ArrayList<>();

	/**
	 * Creates a new processor for generating Java source files.
	 *
	 * @param outputDirectory the root output directory
	 */
	public JavaOutputProcessor(File outputDirectory, PrettyPrinter printer) {
		this.directory = outputDirectory;
		this.printer = printer;
	}

	/**
	 * usedful for testing
	 */
	public JavaOutputProcessor() {
	}

	public PrettyPrinter getPrinter() {
		return printer;
	}

	public List<File> getCreatedFiles() {
		return printedFiles;
	}

	public File getOutputDirectory() {
		return directory;
	}

	@Override
	public void init() {
		// Skip loading properties
		// super.init();

		// Check output directory
		if (directory == null) {
			throw new RuntimeException("You should set output directory before printing");
		}
		// Create spooned dir
		if (directory.isFile()) {
			throw new RuntimeException("Output must be a directory");
		}
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new RuntimeException("Error creating output directory");
			}
		}
		try {
			directory = directory.getCanonicalFile();
		} catch (IOException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	Map<String, Map<Integer, Integer>> lineNumberMappings = new HashMap<>();

	/**
	 * Creates the Java file associated to the given element. Splits top-level
	 * classes in different files (even if they are in the same file in the
	 * original sources).
	 */
	public void createJavaFile(CtType<?> element) {

		getEnvironment().debugMessage("printing " + element.getQualifiedName() + " to " + directory);

		// we only create a file for top-level classes
		if (!element.isTopLevel()) {
			throw new IllegalArgumentException();
		}

		CompilationUnit cu = null;
		if (element.getPosition() != null) {
			cu = element.getPosition().getCompilationUnit();
			// this is a top level type (see check above)
			// if the compilation unit is not set, we use a default one
			if (cu == null) {
				cu = element.getFactory().CompilationUnit().create(element.getQualifiedName());
				cu.setDeclaredPackage(element.getPackage());
			}
		}
		List<CtType<?>> toBePrinted = new ArrayList<>();
		toBePrinted.add(element);

		printer.calculate(cu, toBePrinted);

		CtPackage pack = element.getPackage();

		PrintStream stream = null;

		// print type
		try {
			File file = new File(getPackageFile(pack).getAbsolutePath() + File.separatorChar + element.getSimpleName() + DefaultJavaPrettyPrinter.JAVA_FILE_EXTENSION);
			file.createNewFile();
			if (!printedFiles.contains(file)) {
				printedFiles.add(file);
			}
			stream = new PrintStream(file);
			stream.print(printer.getResult());
			for (CtType<?> t : toBePrinted) {
				lineNumberMappings.put(t.getQualifiedName(), printer.getLineNumberMapping());
			}
			stream.close();
		} catch (IOException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

	}

	@Override
	public boolean isToBeProcessed(CtNamedElement candidate) {
		return candidate instanceof CtType<?> || candidate instanceof CtPackage && (candidate.getComments().size() > 0 || candidate.getAnnotations().size() > 0);
	}

	/**
	 * Creates a source file for each processed top-level type and pretty prints
	 * its contents.
	 */
	public void process(CtNamedElement nameElement) {
		if (nameElement instanceof CtType && ((CtType) nameElement).isTopLevel()) {
			createJavaFile((CtType<?>) nameElement);
		} else if (nameElement instanceof CtPackage) {
			createPackageFile((CtPackage) nameElement);
		}
		printer.reset();
	}

	private void createPackageFile(CtPackage pack) {
		// Create package annotation file
		File packageAnnot = new File(getPackageFile(pack).getAbsolutePath() + File.separatorChar + DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION);
		if (!printedFiles.contains(packageAnnot)) {
			printedFiles.add(packageAnnot);
		}
		PrintStream stream = null;
		try {
			stream = new PrintStream(packageAnnot);
			stream.println(printer.printPackageInfo(pack));
			stream.close();
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private File getPackageFile(CtPackage pack) {
		File packageDir;
		if (pack.isUnnamedPackage()) {
			packageDir = new File(directory.getAbsolutePath());
		} else {
			// Create current package dir
			packageDir = new File(directory.getAbsolutePath() + File.separatorChar + pack.getQualifiedName().replace('.', File.separatorChar));
		}
		if (!packageDir.exists()) {
			if (!packageDir.mkdirs()) {
				throw new RuntimeException("Error creating output directory");
			}
		}
		return packageDir;
	}

	public void setOutputDirectory(File directory) {
		this.directory = directory;
	}

	public Map<String, Map<Integer, Integer>> getLineNumberMappings() {
		return lineNumberMappings;
	}

	@Override
	public TraversalStrategy getTraversalStrategy() {
		return TraversalStrategy.PRE_ORDER;
	}

}
