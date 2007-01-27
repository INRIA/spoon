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

package spoon.support.builder.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;

public class CtFileZip implements CtFile {

	byte[] buffer;

	String name;

	CtFolderZip parent;

	public CtFileZip(CtFolderZip parent, String name, byte[] buffer) {
		super();
		this.buffer = buffer;
		this.name = name;
		this.parent = parent;
	}

	public InputStream getContent() {
		return new ByteArrayInputStream(buffer);
	}

	public String getName() {
		return name;
	}

	public CtFolder getParent() {
		return parent;
	}

	public boolean isFile() {
		return true;
	}

	public boolean isJava() {
		return getName().endsWith(".java");
	}

	public String getPath() {
		return toString();
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
