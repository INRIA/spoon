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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

import spoon.processing.Builder;
import spoon.reflect.Factory;
import spoon.support.builder.support.CtFolderVirtual;

public class SpoonBuildingManager implements Builder {

	private boolean build = false;

	CtFolderVirtual source = new CtFolderVirtual();

	CtFolderVirtual templates = new CtFolderVirtual();

	public SpoonBuildingManager() {
		super();
	}

	public void addInputSource(CtResource source) throws IOException {
		if (source.isFile())
			this.source.addFile((CtFile) source);
		else
			this.source.addFolder((CtFolder) source);
	}

	public void addInputSource(File source) throws IOException {
		if (FileFactory.isFile(source))
			this.source.addFile(FileFactory.createFile(source));
		else
			this.source.addFolder(FileFactory.createFolder(source));
	}

	public void addTemplateSource(CtResource source) throws IOException {
		if (source.isFile())
			this.templates.addFile((CtFile) source);
		else
			this.templates.addFolder((CtFolder) source);
	}

	public void addTemplateSource(File source) throws IOException {
		if (FileFactory.isFile(source))
			this.templates.addFile(FileFactory.createFile(source));
		else
			this.templates.addFolder(FileFactory.createFolder(source));
	}

	public boolean build(Factory factory) throws Exception {
		if (build)
			throw new Exception("Model already built");
		build = true;
		factory.setBuilder(this);
		JDTCompiler.JAVA_COMPLIANCE = factory.getEnvironment()
				.getComplianceLevel();
		boolean srcSuccess,templateSuccess;
		factory.getEnvironment().debugMessage("compiling sources: "+source.getAllJavaFiles());
		long t=System.currentTimeMillis();
		JDTCompiler compiler = new JDTCompiler();
		srcSuccess=compiler.compileSrc(factory, source.getAllJavaFiles());
		if(!srcSuccess){
			for(CategorizedProblem[] cps :compiler.probs){
				for (int i = 0; i < cps.length; i++) {
					CategorizedProblem problem = cps[i];
					if(problem!=null)
						getProbs().add(problem.getMessage());
				}
			}
		}
		factory.getEnvironment().debugMessage("compiled in "+(System.currentTimeMillis()-t)+" ms");
		factory.getEnvironment().debugMessage("compiling templates: "+templates.getAllJavaFiles());
		t=System.currentTimeMillis();
		templateSuccess=compiler.compileTemplate(factory, templates.getAllJavaFiles());
		factory.Template().parseTypes();
		factory.getEnvironment().debugMessage("compiled in "+(System.currentTimeMillis()-t)+" ms");
		return srcSuccess&&templateSuccess;
	}
	
	List<String> probs;
	
	public List<String> getProbs() {
		if (probs == null) {
			probs = new ArrayList<String>();
			
		}
		return probs;
	}

	public Set<File> getInputSources() {
		// TODO Auto-generated method stub
		return null;
	}

	public CtFolderVirtual getSource() {
		return source;
	}

	public CtFolderVirtual getTemplates() {
		return templates;
	}

	public Set<File> getTemplateSources() {
		// TODO Auto-generated method stub
		return null;
	}

}
