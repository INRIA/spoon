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

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.util.Util;

/**
 * A basic implementation of <code>ICompilationUnit</code> for use in the
 * <code>SourceMapper</code>.
 * 
 * @see ICompilationUnit
 */
public class BasicCompilationUnit implements ICompilationUnit {
	protected char[] contents;

	// Note that if this compiler ICompilationUnit's content is known in
	// advance, the fileName is not used to retrieve this content.
	// Instead it is used to keep enough information to recreate the
	// IJavaElement corresponding to this compiler ICompilationUnit.
	// Thus the fileName can be a path to a .class file, or even a path in a
	// .jar to a .class file.
	// (e.g. /P/lib/mylib.jar|org/eclipse/test/X.class)
	protected char[] fileName;

	protected char[][] packageName;

	protected char[] mainTypeName;

	protected String encoding;

	public BasicCompilationUnit(char[] contents, char[][] packageName,
			String fileName) {
		this.contents = contents;
		this.fileName = fileName.toCharArray();
		this.packageName = packageName;
	}

	public BasicCompilationUnit(char[] contents, char[][] packageName,
			String fileName, String encoding) {
		this(contents, packageName, fileName);
		this.encoding = encoding;
	}

	public BasicCompilationUnit(char[] contents, char[][] packageName,
			String fileName, IJavaElement javaElement) {
		this(contents, packageName, fileName);
	}

	public char[] getContents() {
		if (this.contents != null)
			return this.contents; // answer the cached source

		// otherwise retrieve it
		try {
			return Util.getFileCharContent(new File(new String(this.fileName)),
					this.encoding);
		} catch (IOException e) {
			// could not read file: returns an empty array
		}
		return CharOperation.NO_CHAR;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.env.IDependent#getFileName()
	 */
	public char[] getFileName() {
		return this.fileName;
	}

	public char[] getMainTypeName() {
		if (this.mainTypeName == null) {
			int start = CharOperation.lastIndexOf('/', this.fileName) + 1;
			if (start == 0
					|| start < CharOperation.lastIndexOf('\\', this.fileName))
				start = CharOperation.lastIndexOf('\\', this.fileName) + 1;
			int separator = CharOperation.indexOf('|', this.fileName) + 1;
			if (separator > start) // case of a .class file in a default
				// package in a jar
				start = separator;

			int end = CharOperation.lastIndexOf('$', this.fileName);
			if (end == -1 || !Util.isClassFileName(this.fileName)) {
				end = CharOperation.lastIndexOf('.', this.fileName);
				if (end == -1)
					end = this.fileName.length;
			}

			this.mainTypeName = CharOperation.subarray(this.fileName, start,
					end);
		}
		return this.mainTypeName;
	}

	public char[][] getPackageName() {
		return this.packageName;
	}

	public String toString() {
		return "CompilationUnit: " + new String(this.fileName); //$NON-NLS-1$
	}
}
