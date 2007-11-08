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
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;

/**
 * This class implements an Ant task for Spoon that encapsulates
 * {@link spoon.Launcher}.
 */
public class SpoonTask extends Java {

	/**
	 * Nested element to define a processor type.
	 */
	public static class ProcessorType {
		String type;

		/**
		 * Ant-required empty constructor.
		 */
		public ProcessorType() {
		}

		/**
		 * Constructs a new processor type.
		 *
		 * @param type
		 *            the type's fully qualified name
		 */
		public ProcessorType(String type) {
			setType(type);
		}

		/**
		 * Gets the processor type.
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the processor's type as a string representing the Java qualified
		 * name.
		 */
		public void setType(String type) {
			this.type = type;
		}
	}

	String classname;

	boolean compile = false;

	File input;

	int javaCompliance = 5;

	boolean nooutput = false;

	File output;

	File build;

	File spoonlet;

	Vector<FileSet> spoonletfileset = new Vector<FileSet>();

	List<ProcessorType> processorTypes = new ArrayList<ProcessorType>();

	File properties;

	Vector<FileSet> sourcefilesets = new Vector<FileSet>();

	boolean stats = false;

	File template;

	Vector<FileSet> templatefilesets = new Vector<FileSet>();

	boolean verbose = false;

	boolean debug = false;

	/**
	 * Constructor.
	 */
	public SpoonTask() {
		setClassname("spoon.Launcher");
		setFailonerror(true);
	}

	/**
	 * Adds a new processor type to be instantiated and used by Spoon when
	 * processing the code.
	 */
	public void addProcessor(ProcessorType processorType) {
		processorTypes.add(processorType);
	}

	/**
	 * Adds a source set.
	 */
	public void addSourceSet(FileSet set) {
		sourcefilesets.addElement(set);
	}

	/**
	 * Adds a template source set.
	 */
	public void addTemplateSet(FileSet set) {
		templatefilesets.addElement(set);
	}

	public void addSpoonletSet(FileSet set) {
		spoonletfileset.addElement(set);
	}

	/**
	 * Executes the task.
	 */
	@Override
	public void execute() throws BuildException {

		setFork(true);

		// Verbose
		if (verbose) {
			createArg().setValue("-v");
		}

		// debug
		if (debug) {
			createArg().setValue("--vvv");
		}

		if (fragments) {
			createArg().setValue("--fragments");
		}

		if (tabs) {
			createArg().setValue("--tabs");
		}

		createArg().setValue("--tabsize");
		createArg().setValue("" + tabSize);

		if (nooutput) {
			createArg().setValue("--no");
		} else {
			if (compile) {
				createArg().setValue("--compile");

				createArg().setValue("--build");

				createArg().setFile(build);

			}
		}

		createArg().setValue("--compliance");
		createArg().setValue("" + javaCompliance);

		// Input directories
		if ((spoonlet != null) || (spoonletfileset.size() > 0)) {
			createArg().setValue("-s");
			String f = "";
			if (spoonlet != null) {
				f += spoonlet.getAbsolutePath() + File.pathSeparator;
			}
			for (int i = 0; i < spoonletfileset.size(); i++) {
				FileSet fs = spoonletfileset.elementAt(i);
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				File dir = fs.getDir(getProject());
				String[] srcs = ds.getIncludedFiles();
				for (String element : srcs) {
					f += dir.getAbsolutePath() + File.separatorChar + element
							+ File.pathSeparator;
				}
			}
			createArg().setValue(f);
		}

		// output directory
		if (output != null) {
			if (output.exists() && !output.isDirectory()) {
				throw new BuildException("Output must be a directory");
			}
			createArg().setValue("-o");
			createArg().setValue(output.getAbsolutePath());
		}
		// Input directories
		if ((input != null) || (sourcefilesets.size() > 0)) {
			createArg().setValue("-i");
			String f = "";
			if (input != null) {
				f += input.getAbsolutePath() + File.pathSeparator;
			}
			for (int i = 0; i < sourcefilesets.size(); i++) {
				FileSet fs = sourcefilesets.elementAt(i);
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				File dir = fs.getDir(getProject());
				String[] srcs = ds.getIncludedFiles();
				for (String element : srcs) {
					f += dir.getAbsolutePath() + File.separatorChar + element
							+ File.pathSeparator;
				}
			}
			createArg().setValue(f);
		}

		// Template directories
		if ((template != null) || (templatefilesets.size() > 0)) {
			createArg().setValue("-t");
			String f = "";
			if (template != null) {
				if (!template.exists()) {
					throw new BuildException(
							"template file or directory does not exist ("
									+ template.getAbsolutePath() + ")");
				}
				f += template.getAbsolutePath() + File.pathSeparator;
			}
			for (int i = 0; i < templatefilesets.size(); i++) {
				FileSet fs = templatefilesets.elementAt(i);
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				File dir = fs.getDir(getProject());
				String[] srcs = ds.getIncludedFiles();
				for (String element : srcs) {
					f += dir.getAbsolutePath() + File.separatorChar + element
							+ File.pathSeparator;
				}
			}
			createArg().setValue(f);
		}

		// properties directory
		if (properties != null) {
			createArg().setValue("--properties");
			if (!properties.exists()) {
				throw new BuildException(
						"properties directory does not exist ("
								+ properties.getAbsolutePath() + ")");
			}
			createArg().setValue(properties.getAbsolutePath());
		}

		// processors
		if ((processorTypes != null) && (processorTypes.size() > 0)) {
			createArg().setValue("-p");
			String process = "";
			for (ProcessorType t : processorTypes) {
				process += t.type + File.pathSeparator;
			}
			createArg().setValue(process);
		}

		if (classname != null) {
			createArg().setValue(classname);
		}

		super.execute();

	}

	/**
	 * Sets the name of the laucher to be used.
	 */
	public void setClassName(String classname) {
		this.classname = classname;
	}

	/**
	 * Sets Spoon to be in verbose mode.
	 */
	public void setCompile(boolean compile) {
		this.compile = compile;
	}

	/**
	 * Sets a file or a directory to be processed (no templates, see
	 * {@link #setTemplate(File)}).
	 */
	@Override
	public void setInput(File input) {
		this.input = input;
	}

	/**
	 * Sets a Spoolet to be deployed.
	 *
	 * @param spoonlet
	 *            the deployment descriptor file (usually spoon.xml)
	 */
	public void setSpoonlet(File spoonlet) {
		this.spoonlet = spoonlet;
	}

	/**
	 * Sets the java14 property (to be able to parse java 1.4 source files).
	 */
	public void setJavaCompliance(int javaCompliance) {
		this.javaCompliance = javaCompliance;
	}

	/**
	 * Tells Spoon not to generate any files.
	 */
	public void setNoOutput(boolean nooutput) {
		this.nooutput = nooutput;
	}

	/**
	 * Sets the output directory for generated sources.
	 */
	@Override
	public void setOutput(File output) {
		this.output = output;
	}

	/**
	 * Sets the output directory for generated sources.
	 */
	public void setBuild(File build) {
		this.build = build;
	}

	/**
	 * Sets the root directory where the processors' properties XML
	 * configuration files are located.
	 */
	public void setProperties(File properties) {
		this.properties = properties;
	}

	/**
	 * Enables/disable printing out statistics on Spoon execution time.
	 */
	public void setStats(boolean stats) {
		this.stats = stats;
	}

	/**
	 * Sets a file or a directory to be processed (only templates, see
	 * {@link #setInput(File)}).
	 */
	public void setTemplate(File template) {
		this.template = template;
	}

	/**
	 * Sets Spoon to be in verbose mode.
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets Spoon to be in debug mode.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	boolean tabs = false;

	/**
	 * Sets Spoon to use tabulations instead of spaces when printing source.
	 */
	public void setTabs(boolean tabs) {
		this.tabs = tabs;
	}

	boolean fragments = false;

	/**
	 * Sets Spoon to use source code fragment driven generation technique
	 * (preserves original formatting).
	 */
	public void setFragments(boolean fragments) {
		this.fragments = fragments;
	}

	int tabSize = 4;

	/**
	 * Sets the tabulation size (default is 4 spaces).
	 */
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

}
