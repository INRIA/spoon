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
import java.util.Map;

import spoon.eclipse.jdt.core.compiler.CategorizedProblem;
import spoon.eclipse.jdt.internal.compiler.CompilationResult;
import spoon.eclipse.jdt.internal.compiler.ICompilerRequestor;
import spoon.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import spoon.eclipse.jdt.internal.compiler.IProblemFactory;
import spoon.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import spoon.eclipse.jdt.internal.compiler.batch.Main;
import spoon.eclipse.jdt.internal.compiler.env.INameEnvironment;
import spoon.eclipse.jdt.internal.compiler.util.Util;

import spoon.reflect.Factory;

public class TreeBuilderRequestor extends Main implements ICompilerRequestor {

	class Compiler extends spoon.eclipse.jdt.internal.compiler.Compiler {

		@SuppressWarnings("deprecation")
		public Compiler(INameEnvironment environment,
				IErrorHandlingPolicy policy, Map<?,?> settings,
				ICompilerRequestor requestor, IProblemFactory problemFactory,
				PrintWriter out, boolean statementsRecovery) {
			super(environment, policy, settings, requestor, problemFactory,
					statementsRecovery);
		}
		
		public CompilationUnitDeclaration[] compileUnits(
				CompilationUnit[] sourceUnits) {
			CompilationUnitDeclaration unit = null;
			int i = 0;
			// build and record parsed units
			beginToCompile(sourceUnits);
			// process all units (some more could be injected in the loop by
			// the lookup environment)
			for (; i < this.totalUnits; i++) {
				unit = unitsToProcess[i];
				this.parser.getMethodBodies(unit);

				// fault in fields & methods
				if (unit.scope != null)
					unit.scope.faultInTypes();
				// verify inherited methods
				if (unit.scope != null)
					unit.scope
							.verifyMethods(lookupEnvironment.methodVerifier());
				// type checking
				unit.resolve();
				// flow analysis
				unit.analyseCode();

				requestor.acceptResult(unit.compilationResult.tagAsAccepted());
			}
			return this.unitsToProcess;
		}
	}

	public static int JAVA_COMPLIANCE = 6;

	boolean success=true;
	
	public boolean compileSrc(Factory f, List<CtFile> files)
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
		String srcPath = f.getEnvironment().getSourcePath();
		args.add(srcPath);

//		JDTCompiler compiler = new JDTCompiler(new PrintWriter(System.out),
//				new PrintWriter(System.err));
		configure(args.toArray(new String[0]));
//		f.getEnvironment().debugMessage("compiling src: "+files);
		CompilationUnitDeclaration[] units = getUnits(files,f);
//		f.getEnvironment().debugMessage("got units in "+(System.currentTimeMillis()-t)+" ms");
		
		JDTTreeBuilder builder = new JDTTreeBuilder(f);

		for (CompilationUnitDeclaration unit : units) {
			try {
//				t=System.currentTimeMillis();
				unit.traverse(builder, unit.scope);
//				f.getEnvironment().debugMessage("built unit "+new String(unit.getMainTypeName())+" in "+(System.currentTimeMillis()-t)+" ms");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	public boolean compileTemplate(Factory f, List<CtFile> streams)
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
		return success;
	}

	Compiler batchCompiler;

	PrintWriter out;

	public TreeBuilderRequestor(PrintWriter outWriter, PrintWriter errWriter) {
		super(outWriter, errWriter, false);
	}

	public TreeBuilderRequestor() {
		super(new PrintWriter(System.out), new PrintWriter(System.err), false);
	}

	/*
	 * Build the set of compilation source units
	 */
	public CompilationUnit[] getCompilationUnits(List<CtFile> streams)
			throws Exception {
		CompilationUnit[] units = new CompilationUnit[streams.size()];
		int i = 0;
		for (CtFile stream : streams) {
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
	
	public CompilationUnitDeclaration[] getUnits(List<CtFile> streams,Factory f)
			throws Exception {
		this.startTime = System.currentTimeMillis();
		INameEnvironment environment = this.environment;
		if(environment == null)
			environment = getLibraryAccess();
		this.batchCompiler = new Compiler(environment, getHandlingPolicy(),
				this.options, this, getProblemFactory(), this.out, false);
		return batchCompiler.compileUnits(getCompilationUnits(streams));
	}
	
	List<CategorizedProblem[]> probs;
	
	public List<CategorizedProblem[]> getProbs() {
		if (probs == null) {
			probs = new ArrayList<CategorizedProblem[]>();
			
		}
		return probs;
	}
	
	public void acceptResult(CompilationResult result) {
		if (result.hasErrors()) {
			System.err.println(result);
			getProbs().add(result.problems);
			success=false;
		}
	}
}
