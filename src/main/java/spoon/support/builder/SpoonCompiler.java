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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import spoon.eclipse.jdt.core.compiler.CategorizedProblem;
import spoon.eclipse.jdt.internal.compiler.CompilationResult;
import spoon.eclipse.jdt.internal.compiler.ICompilerRequestor;
import spoon.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import spoon.eclipse.jdt.internal.compiler.batch.Main;
import spoon.eclipse.jdt.internal.compiler.env.INameEnvironment;
import spoon.eclipse.jdt.internal.compiler.util.Util;
import spoon.reflect.Factory;

public class SpoonCompiler  extends Main {
	
	public int JAVA_COMPLIANCE = 6;

	public SpoonCompiler(PrintWriter outWriter, PrintWriter errWriter) {
		super(outWriter, errWriter, false);
	}

	public SpoonCompiler() {
		super(new PrintWriter(System.out), new PrintWriter(System.err), false);
	}
	
	public boolean compileSrc(Factory f, List<SpoonFile> files)
			throws Exception {
		if(files.isEmpty()) return true;
//		long t=System.currentTimeMillis();
		// Build input
		List<String> args = new ArrayList<String>();
		args.add("-1." + JAVA_COMPLIANCE);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();//ClassLoader.getSystemClassLoader();
		
		
		if(currentClassLoader instanceof URLClassLoader){
			URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
			if(urls!=null && urls.length>0){
				String classpath = ".";
				for (URL url : urls) {
					classpath+=File.pathSeparator+url.getFile();
				}
				if(classpath!=null){
					args.add("-cp");
					args.add(classpath);
				}
			}
		}
		
		// args.add("-nowarn");
		for (SpoonFile file : files) {
			args.add(file.getPath());
		}

//		JDTCompiler compiler = new JDTCompiler(new PrintWriter(System.out),
//				new PrintWriter(System.err));
		configure(args.toArray(new String[0]));
//		f.getEnvironment().debugMessage("compiling src: "+files);
		CompilationUnitDeclaration[] units = getUnits(files,f);
//		f.getEnvironment().debugMessage("got units in "+(System.currentTimeMillis()-t)+" ms");
		
		JDTTreeBuilder builder = new JDTTreeBuilder(f);

		// here we build the model
		for (CompilationUnitDeclaration unit : units) {
//		  try {
			unit.traverse(builder, unit.scope);
//		 // for debug
//		  } catch (Exception e) {
//			// bad things sometimes happen, for instance when methodDeclaration.binding in JDTTreeBuilder
//			System.err.println(new String(unit.getFileName())+" "+e.getMessage()+e.getStackTrace()[0]);
//		  }
		}
		
		return probs.size()==0;
	}

	public boolean compileTemplate(Factory f, List<SpoonFile> streams)
			throws Exception {
		if(streams.isEmpty()) return true;
		// Build input
		List<String> args = new ArrayList<String>();
		args.add("-1." + JAVA_COMPLIANCE);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		args.add("-nowarn");
		args.add(".");

//		JDTCompiler compiler = new JDTCompiler(new PrintWriter(System.out),
//				new PrintWriter(System.err));
		configure(args.toArray(new String[0]));

		CompilationUnitDeclaration[] units = getUnits(streams,f);

		JDTTreeBuilder builder = new JDTTreeBuilder(f);
		builder.template = true;
		for (CompilationUnitDeclaration unit : units) {
			unit.traverse(builder, unit.scope);
		}
		return probs.size()==0;
	}

	PrintWriter out;



	/*
	 * Build the set of compilation source units
	 */
	public CompilationUnit[] getCompilationUnits(List<SpoonFile> streams)
			throws Exception {
		CompilationUnit[] units = new CompilationUnit[streams.size()];
		int i = 0;
		for (SpoonFile stream : streams) {
			InputStream in = stream.getContent();
			units[i] = new CompilationUnit(Util.getInputStreamAsCharArray(in,
					-1, null), stream.getPath(), null);
			in.close();
			i++;
		}
		return units;
	}

	INameEnvironment environment=null;
	
	public void setEnvironment(INameEnvironment environment) {
		this.environment=environment;
	}
	
	public CompilationUnitDeclaration[] getUnits(List<SpoonFile> streams,Factory f)
			throws Exception {
		this.startTime = System.currentTimeMillis();
		INameEnvironment environment = this.environment;
		if(environment == null)
			environment = getLibraryAccess();
		TreeBuilderCompiler  batchCompiler = new TreeBuilderCompiler(environment, getHandlingPolicy(),
				this.options, this.requestor, getProblemFactory(), this.out, false);
		CompilationUnitDeclaration[] units = batchCompiler.compileUnits(getCompilationUnits(streams));
		return units;
	}
	
	final List<CategorizedProblem[]> probs = new ArrayList<CategorizedProblem[]>();
	
	public final TreeBuilderRequestor requestor = new TreeBuilderRequestor();
	
	// this class can not be static because it uses the fiel probs
	public class TreeBuilderRequestor implements ICompilerRequestor {

		public void acceptResult(CompilationResult result) {
			if (result.hasErrors()) {
				probs.add(result.problems);
			}
		}
	
	}
}
