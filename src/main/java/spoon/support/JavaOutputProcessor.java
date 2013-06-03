/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.processing.FileGenerator;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.FragmentDrivenJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;

/**
 * A processor that generates compilable Java source files from the meta-model.
 */
public class JavaOutputProcessor extends AbstractProcessor<CtSimpleType<?>>
		implements FileGenerator<CtSimpleType<?>> {
	File directory;

	List<File> printedFiles = new ArrayList<File>();

	boolean writePackageAnnotationFile = true;

	/**
	 * Creates a new processor for generating Java source files.
	 * 
	 * @param outputDirectory
	 *            the root output directory
	 */
	public JavaOutputProcessor(File outputDirectory) {
		this.directory = outputDirectory;
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
	}

	Map<String, Map<Integer, Integer>> lineNumberMappings = new HashMap<String, Map<Integer, Integer>>();

	/**
	 * Creates the Java file associated to the given element.
	 */
	public void createJavaFile(CtSimpleType<?> element) {

		CompilationUnit cu = null;
		if (element.getPosition() != null)
			cu = element.getPosition().getCompilationUnit();

		// skip non-main types
		if (cu != null && cu.getMainType() != element)
			return;

		List<CtSimpleType<?>> toBePrinted = new ArrayList<CtSimpleType<?>>();

		if (cu == null) {
			toBePrinted.add(element);
		} else {
			toBePrinted.addAll(cu.getDeclaredTypes());
		}

		PrettyPrinter printer = null;

		if (getEnvironment().isUsingSourceCodeFragments()) {
			try {
				printer = new FragmentDrivenJavaPrettyPrinter(getEnvironment(),
						element.getPosition().getCompilationUnit());
			} catch (Exception e) {
				e.printStackTrace();
				printer = null;
			}
		}
		if (printer == null) {
			printer = new DefaultJavaPrettyPrinter(getEnvironment());
			printer.calculate(cu,toBePrinted);
		}

		CtPackage pack = element.getPackage();
		PrintStream stream = null;

		// Check output directory
		if (directory == null)
			throw new RuntimeException(
					"You should set output directory before printing");
		// Create spooned dir
		if (directory.isFile())
			throw new RuntimeException("Output must be a directory");
		if (!directory.exists()) {
			if (!directory.mkdirs())
				throw new RuntimeException("Error creating output directory");
		}

		// create package directory
		File packageDir;
		if (pack.getQualifiedName().equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			packageDir = new File(directory.getAbsolutePath());
		} else {
			// Create current package dir
			packageDir = new File(directory.getAbsolutePath()
					+ File.separatorChar
					+ pack.getQualifiedName().replace('.', File.separatorChar));
		}
		if (!packageDir.exists()) {
			if (!packageDir.mkdirs())
				throw new RuntimeException("Error creating output directory");
		}

		// Create package annotation file
		if (writePackageAnnotationFile
				&& element.getPackage().getAnnotations().size() > 0) {
			File packageAnnot = new File(packageDir.getAbsolutePath()
					+ File.separatorChar
					+ DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION);
			if (!printedFiles.contains(packageAnnot))
				printedFiles.add(packageAnnot);
			try {
				stream = new PrintStream(packageAnnot);
				stream.println(printer.getPackageDeclaration());
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (stream != null)
					stream.close();
			}
		}

		// print type
		try {
			File file = new File(packageDir.getAbsolutePath()
					+ File.separatorChar + element.getSimpleName()
					+ DefaultJavaPrettyPrinter.JAVA_FILE_EXTENSION);
			file.createNewFile();
			if (!printedFiles.contains(file)) {
				printedFiles.add(file);
			}
			stream = new PrintStream(file);
			stream.print(printer.getResult());
			for (CtSimpleType<?> t : toBePrinted) {
				lineNumberMappings.put(t.getQualifiedName(), printer
						.getLineNumberMapping());
			}
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null)
				stream.close();
		}

	}

	/**
	 * Creates a source file for each processed top-level type and pretty prints
	 * its contents.
	 */
	public void process(CtSimpleType<?> type) {
		if (type.isTopLevel())
			createJavaFile(type);
	}

	public void setOutputDirectory(File directory) {
		this.directory = directory;
	}

	public Map<String, Map<Integer, Integer>> getLineNumberMappings() {
		return lineNumberMappings;
	}

}
