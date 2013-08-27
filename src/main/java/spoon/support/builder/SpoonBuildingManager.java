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

package spoon.support.builder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import spoon.eclipse.jdt.core.compiler.CategorizedProblem;
import spoon.processing.Builder;
import spoon.processing.Severity;
import spoon.reflect.Factory;
import spoon.support.builder.support.FileSystemFile;
import spoon.support.builder.support.FileSystemFolder;
import spoon.support.builder.support.VirtualFile;
import spoon.support.builder.support.VirtualFolder;

public class SpoonBuildingManager implements Builder {

	Factory factory;

	private boolean build = false;

	VirtualFolder sources = new VirtualFolder();

	VirtualFolder templates = new VirtualFolder();

	public SpoonBuildingManager(Factory factory) {
		this.factory = factory;
	}

	public void addInputSource(SpoonRessource source) throws IOException {
		if (source.isFile())
			this.sources.addFile((SpoonFile) source);
		else
			this.sources.addFolder((SpoonFolder) source);
	}

	public void addInputSource(File source) throws IOException {
		if (FileFactory.isFile(source))
			this.sources.addFile(FileFactory.createFile(source));
		else
			this.sources.addFolder(FileFactory.createFolder(source));
	}

	public void addTemplateSource(SpoonRessource source) throws IOException {
		if (source.isFile())
			this.templates.addFile((SpoonFile) source);
		else
			this.templates.addFolder((SpoonFolder) source);
	}

	public void addTemplateSource(File source) throws IOException {
		if (FileFactory.isFile(source))
			this.templates.addFile(FileFactory.createFile(source));
		else
			this.templates.addFolder(FileFactory.createFolder(source));
	}

	SpoonCompiler compiler = null;

	public boolean build() throws Exception {
		if (factory == null) {
			throw new Exception("Factory not initialized");
		}
		if (build) {
			throw new Exception("Model already built");
		}
		build = true;

		boolean srcSuccess, templateSuccess;
		factory.getEnvironment().debugMessage(
				"compiling sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		compiler = new SpoonCompiler();
		compiler.JAVA_COMPLIANCE = factory.getEnvironment()
				.getComplianceLevel();
		initCompiler();
		srcSuccess = compiler.compileSrc(factory, sources.getAllJavaFiles());
		if (compiler.probs.size() > 0) {
			for (CategorizedProblem[] cps : compiler.probs) {
				for (int i = 0; i < cps.length; i++) {
					CategorizedProblem problem = cps[i];
					if (problem != null) {
						File file = new File(new String(
								problem.getOriginatingFileName()));
						String filename = file.getAbsolutePath();
						factory.getEnvironment().report(
								null,
								problem.isError() ? Severity.ERROR : problem
										.isWarning() ? Severity.WARNING
										: Severity.MESSAGE,

								problem.getMessage() + " at " + filename + ":"
										+ problem.getSourceLineNumber());
					}
				}
			}
		}
		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage(
				"compiling templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		templateSuccess = compiler.compileTemplate(factory,
				templates.getAllJavaFiles());
		factory.Template().parseTypes();
		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		return srcSuccess && templateSuccess;
	}

	public void initCompiler() {
		// compiler.setEnvironment(compiler.batchCompiler.);
		// does nothing by default
	}

	public Set<File> getInputSources() {
		Set<File> files = new HashSet<File>();
		for (SpoonFolder file : getSource().getSubFolder()) {
			files.add(new File(file.getPath()));
		}
		return files;
	}

	public VirtualFolder getSource() {
		return sources;
	}

	public VirtualFolder getTemplates() {
		return templates;
	}

	public Set<File> getTemplateSources() {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	public SpoonCompiler getCompiler() {
		return compiler;
	}

}
