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

package spoon.support.reflect.cu;

import java.io.File;
import java.io.Serializable;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;

/**
 * This class represents the position of a Java program element in a source
 * file.
 */

public class SourcePositionImpl implements SourcePosition, Cloneable,
		Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Search the line number corresponding to a specific position
	 */
	private int searchLineNumber(int[] startLineIndexes, int position) {
		if (startLineIndexes == null)
			return 1;
		int length = startLineIndexes.length;
		if (length == 0)
			return 1;
		int g = 0, d = length - 1;
		int m = 0, start;
		while (g <= d) {
			m = (g + d) / 2;
			if (position < (start = startLineIndexes[m])) {
				d = m - 1;
			} else if (position > start) {
				g = m + 1;
			} else {
				return m + 1;
			}
		}
		if (position < startLineIndexes[m]) {
			return m + 1;
		}
		return m + 2;
	}

	/**
	 * Search the column number
	 */
	private int searchColumnNumber(int[] startLineIndexes, int position) {
		if (startLineIndexes == null)
			return 1;
		int length = startLineIndexes.length;
		if (length == 0)
			return 1;
		int i = 0;
		for (i = 0; i < startLineIndexes.length - 1; i++) {
			if (startLineIndexes[i] < position
					&& (startLineIndexes[i + 1] > position))
				return position - startLineIndexes[i];
		}
		int tabCount = 0;
		int tabSize = 0;
		if (getCompilationUnit() != null) {
			tabSize = getCompilationUnit().getFactory().getEnvironment()
					.getTabulationSize();
			String source = getCompilationUnit().getOriginalSourceCode();
			for (int j = startLineIndexes[i]; j < position; j++) {
				if (source.charAt(j) == '\t') {
					tabCount++;
				}
			}
		}
		return (position - startLineIndexes[i]) - tabCount
				+ (tabCount * tabSize);
	}

	int[] lineSeparatorPositions;

	private int sourceStart, sourceEnd;

	public SourcePositionImpl(CompilationUnit compilationUnit, int sourceStart,
			int sourceEnd, int[] lineSeparatorPositions) {
		super();
		this.compilationUnit = compilationUnit;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.lineSeparatorPositions = lineSeparatorPositions;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public int getColumn() {
		return searchColumnNumber(lineSeparatorPositions, sourceStart);
	}

	public int getEndColumn() {
		return searchColumnNumber(lineSeparatorPositions, sourceEnd);
	}

	public File getFile() {
		if (compilationUnit == null)
			return null;
		return compilationUnit.getFile();
	}

	public int getLine() {
		return searchLineNumber(lineSeparatorPositions, sourceStart);
	}

	public int getEndLine() {
		return searchLineNumber(lineSeparatorPositions, sourceEnd);
	}

	public int getSourceEnd() {
		return sourceEnd;
	}

	public int getSourceStart() {
		return sourceStart;
	}

	/**
	 * Returns a string representation of this position in the form
	 * "sourcefile:line", or "sourcefile" if no line number is available.
	 */
	public String toString() {
		int ln = getLine();
		return (ln >= 1) ? getFile().getAbsolutePath() + ":" + ln : getFile()
				.getAbsolutePath();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SourcePosition))
			return false;
		SourcePosition s = (SourcePosition) obj;
		return (getFile() == null ? s.getFile() == null : getFile().equals(
				s.getFile()))
				&& getLine() == s.getLine() && getColumn() == s.getColumn();
	}

	CompilationUnit compilationUnit;

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

}
