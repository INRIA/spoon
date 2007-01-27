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

package spoon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * This class implements an Ant task for Spoon that encapsulates
 * {@link spoon.LtLauncher}.
 */
public class LtSpoonTask extends SpoonTask {

	String sourcepath;

	List<ProcessorType> ltprocessorTypes = new ArrayList<ProcessorType>();

	/**
	 * Default constructor.
	 */
	public LtSpoonTask() {
		setClassname(LtLauncher.class.getName());
        setFailonerror(true);
	}

	/**
	 * Adds a new processor type to be instantiated and used by Spoon when
	 * processing the code.
	 */
	public void addLtProcessor(ProcessorType processorType) {
		this.ltprocessorTypes.add(processorType);
	}

	/**
	 * Executes the task.
	 */
	@Override
	public void execute() throws BuildException {
		if (classname == null) {
			throw new BuildException("classname is mandatory");
		}

		if (sourcepath != null) {
			createArg().setValue("-i");
			createArg().setValue(sourcepath);
		}

		// lt processors
		if (ltprocessorTypes != null && ltprocessorTypes.size() > 0) {
			createArg().setValue("-l");
			String processor = "";
			for (ProcessorType t : ltprocessorTypes)
				processor += t.getType() + File.pathSeparator;
			createArg().setValue(processor);
		}

		super.execute();
	}

	/**
	 * Sets the source path.
	 */
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

}
