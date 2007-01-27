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

package spoon.support.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

public class JDTCompiler implements ICompilerRequestor {

	public static ICompilationUnit getUnit(String name, File file)
			throws Exception {

		String[] tmp = name.split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[512];
		int read;
		while ((read = in.read(buffer, 0, 512)) >= 0)
			out.write(buffer, 0, read);

		ICompilationUnit unit = null;

		unit = new BasicCompilationUnit(out.toString().toCharArray(), pack,
				file.getName());

		out.close();
		in.close();
		return unit;
	}

	CompilerOptions compilerOption;

	List<ClassFile> classFiles = new ArrayList<ClassFile>();

	public void acceptResult(CompilationResult result) {
		if (result.hasErrors()) {
			System.err.println(result);
		}

		for (ClassFile f : result.getClassFiles()) {
			classFiles.add(f);
		}
	}

	public List<ClassFile> compile(ICompilationUnit[] units) {
		org.eclipse.jdt.internal.compiler.Compiler compiler = new org.eclipse.jdt.internal.compiler.Compiler(
				getLibraryAccess(), getHandlingPolicy(), getCompilerOption(),
				this, new DefaultProblemFactory());
		compiler.compile(units);
		return classFiles;
	}

	public CompilerOptions getCompilerOption() {
		if (compilerOption == null) {
			compilerOption = new CompilerOptions();
			compilerOption.sourceLevel = ClassFileConstants.JDK1_5;
			compilerOption.suppressWarnings = true;
		}
		return compilerOption;
	}

	private IErrorHandlingPolicy getHandlingPolicy() {
		return new IErrorHandlingPolicy() {
			public boolean proceedOnErrors() {
				return true; // stop if there are some errors
			}

			public boolean stopOnFirstError() {
				return false;
			}
		};
	}

	FileSystem getLibraryAccess() {
		String bootpath = System.getProperty("sun.boot.class.path");
		String classpath = System.getProperty("java.class.path");
		List<String> lst = new ArrayList<String>();
		for (String s : bootpath.split(File.pathSeparator)) {
			File f = new File(s);
			if (f.exists()) {
				lst.add(f.getAbsolutePath());
			}
		}
		for (String s : classpath.split(File.pathSeparator)) {
			File f = new File(s);
			if (f.exists()) {
				lst.add(f.getAbsolutePath());
			}
		}
		return new FileSystem(lst.toArray(new String[0]), new String[0], System
				.getProperty("file.encoding"));
	}

	public List<ClassFile> getClassFiles() {
		return classFiles;
	}

}
