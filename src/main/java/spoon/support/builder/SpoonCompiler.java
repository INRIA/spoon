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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.util.Util;

import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.support.FileSystemFile;

/**
 * Builds the Spoon model from a set of Java Files. The model is accessible
 * with:
 * 
 * <pre>
 * factory.Package().get("spoon.support.builder")
 * factory.Package().getAllRoots()
 * </pre>
 * 
 * See method main.
 */
public class SpoonCompiler extends Main {

	public int javaCompliance = 7;

	String classpath = null;

	public SpoonCompiler(PrintWriter outWriter, PrintWriter errWriter) {
		super(outWriter, errWriter, false, null, null);
	}

	public SpoonCompiler() {
		super(new PrintWriter(System.out), new PrintWriter(System.err), false,
				null, null);
	}

	// example usage
	public static void main(String[] args) {
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList<SpoonFile>();
		SpoonFile file = new FileSystemFile(new File(
				"./src/main/java/spoon/support/builder/SpoonCompiler.java"));
		files.add(file);
		System.out.println(file.getPath());
		try {
			Factory factory = new Factory(new DefaultCoreFactory(),
					new StandardEnvironment());
			comp.compileSrc(factory, files);
			System.out.println(factory.Package().get("spoon.support.builder")
					.getTypes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean compileSrc(Factory f, List<SpoonFile> files)
			throws Exception {
		if (files.isEmpty())
			return true;
		// long t=System.currentTimeMillis();
		// Build input
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		ClassLoader currentClassLoader = Thread.currentThread()
				.getContextClassLoader();// ClassLoader.getSystemClassLoader();

		if (classpath != null) {
			args.add("-cp");
			args.add(classpath);
		} else {
			if (currentClassLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
				if (urls != null && urls.length > 0) {
					String classpath = ".";
					for (URL url : urls) {
						classpath += File.pathSeparator + url.getFile();
					}
					if (classpath != null) {
						args.add("-cp");
						args.add(classpath);
					}
				}
			}
		}
		// args.add("-nowarn");
		// method configure JDT of JDT requires at least one file or one
		// directory
		Set<String> paths = new HashSet<String>();
		for (SpoonFile file : files) {
			// We can not use file.getPath() because when using in-memory code
			// (e.g. snippets)
			// there is no real file on the disk
			// In this case, the virtual parent of the virtual file is "." (by
			// convention)
			// and we are sure it exists
			// However, if . contains a lot of subfolders and Java files, it
			// will take a lot of time
			paths.add(file.getParent().getPath());
		}
		args.addAll(paths);

		// JDTCompiler compiler = new JDTCompiler(new PrintWriter(System.out),
		// new PrintWriter(System.err));
		
		// Thanks Renaud for this wonderful System.out
		//System.out.println(args);
		
		configure(args.toArray(new String[0]));
		// configure(new String[0]);
		// f.getEnvironment().debugMessage("compiling src: "+files);
		CompilationUnitDeclaration[] units = getUnits(files, f);
		// f.getEnvironment().debugMessage("got units in "+(System.currentTimeMillis()-t)+" ms");

		JDTTreeBuilder builder = new JDTTreeBuilder(f);

		// here we build the model
		for (CompilationUnitDeclaration unit : units) {
			// try {
			unit.traverse(builder, unit.scope);
			// // for debug
			// } catch (Exception e) {
			// // bad things sometimes happen, for instance when
			// methodDeclaration.binding in JDTTreeBuilder
			// System.err.println(new
			// String(unit.getFileName())+" "+e.getMessage()+e.getStackTrace()[0]);
			// }
		}

		return probs.size() == 0;
	}

	public boolean compileTemplate(Factory f, List<SpoonFile> streams)
			throws Exception {
		if (streams.isEmpty())
			return true;
		// Build input
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		args.add("-nowarn");
		args.add(".");

		// JDTCompiler compiler = new JDTCompiler(new PrintWriter(System.out),
		// new PrintWriter(System.err));
		configure(args.toArray(new String[0]));

		CompilationUnitDeclaration[] units = getUnits(streams, f);

		JDTTreeBuilder builder = new JDTTreeBuilder(f);
		builder.template = true;
		for (CompilationUnitDeclaration unit : units) {
			unit.traverse(builder, unit.scope);
		}
		return probs.size() == 0;
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

	INameEnvironment environment = null;

	public void setEnvironment(INameEnvironment environment) {
		this.environment = environment;
	}

	public CompilationUnitDeclaration[] getUnits(List<SpoonFile> streams,
			Factory f) throws Exception {
		this.startTime = System.currentTimeMillis();
		INameEnvironment environment = this.environment;
		if (environment == null)
			environment = getLibraryAccess();
		TreeBuilderCompiler batchCompiler = new TreeBuilderCompiler(
				environment, getHandlingPolicy(), this.options, this.requestor,
				getProblemFactory(), this.out, false);
		CompilationUnitDeclaration[] units = batchCompiler
				.compileUnits(getCompilationUnits(streams));
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

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
}
