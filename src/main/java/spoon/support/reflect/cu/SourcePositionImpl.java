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
package spoon.support.reflect.cu;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;

import java.io.File;
import java.io.Serializable;

/**
 * This immutable class represents the position of a Java program element in a source
 * file.
 */
public class SourcePositionImpl implements SourcePosition, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Search the line number corresponding to a specific position
	 */
	private int searchLineNumber(int position) {
		if (lineSeparatorPositions == null) {
			return -1;
		}
		int length = lineSeparatorPositions.length;
		if (length == 0) {
			return -1;
		}
		int g = 0, d = length - 1;
		int m = 0, start;
		while (g <= d) {
			m = (g + d) / 2;
			if (position < (start = lineSeparatorPositions[m])) {
				d = m - 1;
			} else if (position > start) {
				g = m + 1;
			} else {
				return m + 1;
			}
		}
		if (position < lineSeparatorPositions[m]) {
			return m + 1;
		}
		return m + 2;
	}

	/**
	 * Search the column number
	 */
	private int searchColumnNumber(int position) {
		if (lineSeparatorPositions == null) {
			return -1;
		}
		int length = lineSeparatorPositions.length;
		if (length == 0) {
			return -1;
		}
		int i = 0;
		for (i = 0; i < lineSeparatorPositions.length - 1; i++) {
			if (lineSeparatorPositions[i] < position && (lineSeparatorPositions[i + 1] > position)) {
				return position - lineSeparatorPositions[i];
			}
		}
		int tabCount = 0;
		int tabSize = 0;
		if (getCompilationUnit() != null) {
			tabSize = getCompilationUnit().getFactory().getEnvironment().getTabulationSize();
			String source = getCompilationUnit().getOriginalSourceCode();
			for (int j = lineSeparatorPositions[i]; j < position; j++) {
				if (source.charAt(j) == '\t') {
					tabCount++;
				}
			}
		}
		return (position - lineSeparatorPositions[i]) - tabCount + (tabCount * tabSize);
	}

	/** The position of the first byte of this element (incl. documentation and modifiers) */
	private int sourceStart = -1;

	/** The position of the last byte of this element */
	private int sourceEnd = -1;

	/** The position of the first byte of the name of the element, if appropriate (eg the method name)  */
	private int nameSourceStart = 1;

	/** The line number of the start of the element, if appropriate (eg the method name).
	 * Computed lazily by {@link #getLine()}
	 */
	private int sourceStartline = -1;

	/**
	 * The index of line breaks, as computed by JDT.
	 * Used to compute line numbers afterwards.
	 */
	int[] lineSeparatorPositions;

	public SourcePositionImpl(CompilationUnit compilationUnit, int sourceStart, int nameSourceStart, int sourceEnd, int[] lineSeparatorPositions) {
		super();
		this.compilationUnit = compilationUnit;
		this.nameSourceStart = nameSourceStart;
		this.sourceEnd = sourceEnd;
		this.sourceStart = sourceStart;
		this.lineSeparatorPositions = lineSeparatorPositions;
	}

	public int getColumn() {
		return searchColumnNumber(nameSourceStart);
	}

	public int getEndColumn() {
		return searchColumnNumber(sourceEnd);
	}

	public File getFile() {
		if (compilationUnit == null) {
			return null;
		}
		return compilationUnit.getFile();
	}

	public int getLine() {
		if (sourceStartline == -1) {
			this.sourceStartline = searchLineNumber(this.nameSourceStart);
		}
		return sourceStartline;
	}

	public int getEndLine() {
		return searchLineNumber(sourceEnd);
	}

	public int getSourceEnd() {
		return this.sourceEnd;
	}

	public int getSourceStart() {
		return this.sourceStart;
	}

	/**
	 * Returns a string representation of this position in the form
	 * "sourcefile:line", or "sourcefile" if no line number is available.
	 */
	@Override
	public String toString() {
		if (getFile() == null) {
			return "(unknown file)";
		}
		int ln = getLine();
		return (ln >= 1) ? "(" + getFile().getAbsolutePath().replace('\\', '/').replace("C:/", "/") + ":" + ln + ")" : getFile().getAbsolutePath().replace('\\', '/').replace("C:/", "/");
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SourcePosition)) {
			return false;
		}
		SourcePosition s = (SourcePosition) obj;
		return (getFile() == null ? s.getFile() == null : getFile().equals(s.getFile())) && getLine() == s.getLine() && getColumn() == s.getColumn();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getLine();
		result = prime * result + getColumn();
		result = prime * result + (getFile() != null ? getFile().hashCode() : 1);
		return result;
	}

	transient CompilationUnit compilationUnit;

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	@Override
	public int getNameSourceStart() {
		return nameSourceStart;
	}

}
