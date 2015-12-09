/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

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
		 * 		the type's fully qualified name
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

	File input;

	int javaCompliance = 7;

	boolean nooutput = false;

	boolean compile = false;

	File output;

	File destination;

	List<ProcessorType> processorTypes = new ArrayList<ProcessorType>();

	File properties;

	Vector<FileSet> sourcefilesets = new Vector<FileSet>();

	boolean stats = false;

	File template;

	Vector<FileSet> templatefilesets = new Vector<FileSet>();

	boolean verbose = false;

	boolean debug = false;

	boolean imports = false;

	boolean noclasspath = false;

	boolean precompile = false;

	boolean buildOnlyOutdatedFiles = false;

	String outputType;

	String encoding;

	boolean lines;

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

	/**
	 * Executes the task.
	 */
	@Override
	public void execute() throws BuildException {

		setFork(false);

		// Verbose
		if (verbose) {
			createArg().setValue("-v");
		}

		// debug
		if (debug) {
			createArg().setValue("--vvv");
		}

		if (precompile) {
			createArg().setValue("--precompile");
		}

		if (tabs) {
			createArg().setValue("--tabs");
		}

		if (imports) {
			createArg().setValue("--with-imports");
		}

		if (noclasspath) {
			createArg().setValue("--noclasspath");
		}

		createArg().setValue("--tabsize");
		createArg().setValue("" + tabSize);

		if (outputType != null) {
			createArg().setValue("--output-type");
			createArg().setValue(outputType);
		}

		if (encoding != null) {
			createArg().setValue("--encoding");
			createArg().setValue(encoding);
		}

		if (compile) {
			createArg().setValue("--compile");
		}

		if (buildOnlyOutdatedFiles) {
			createArg().setValue("--buildOnlyOutdatedFiles");
		}

		if (lines) {
			createArg().setValue("--lines");
		}

		createArg().setValue("--compliance");
		createArg().setValue("" + javaCompliance);

		// output directory
		if (output != null) {
			if (output.exists() && !output.isDirectory()) {
				throw new BuildException("Output must be a directory");
			}
			createArg().setValue("-o");
			createArg().setValue(output.getAbsolutePath());
		}
		// destination directory
		if (destination != null) {
			if (destination.exists() && !destination.isDirectory()) {
				throw new BuildException("Destination must be a directory");
			}
			createArg().setValue("-d");
			createArg().setValue(destination.getAbsolutePath());
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
				File dir = fs.getDir(getProject());
				f += dir.getAbsolutePath() + File.pathSeparator;
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
				File dir = fs.getDir(getProject());
				f += dir.getAbsolutePath() + File.pathSeparator;
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

		if (sourceClasspath != null) {
			createArg().setValue("--source-classpath");
			createArg().setValue(sourceClasspath.toString());
		} else if (getCommandLine().getClasspath() != null) {
			createArg().setValue("--source-classpath");
			createArg().setValue(getCommandLine().getClasspath().toString());
		}

		if (templateClasspath != null) {
			createArg().setValue("--template-classpath");
			createArg().setValue(templateClasspath.toString());
		} else if (getCommandLine().getClasspath() != null) {
			createArg().setValue("--template-classpath");
			createArg().setValue(getCommandLine().getClasspath().toString());
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
	 * Sets a file or a directory to be processed (no templates, see
	 * {@link #setTemplate(File)}).
	 */
	@Override
	public void setInput(File input) {
		this.input = input;
	}

	/**
	 * Sets the java14 property (to be able to parse java 1.4 source files).
	 */
	public void setJavaCompliance(int javaCompliance) {
		this.javaCompliance = javaCompliance;
	}

	/**
	 * Tells Spoon not to generate any source files.
	 */
	public void setNoOutput(boolean nooutput) {
		this.nooutput = nooutput;
	}

	/**
	 * Tells Spoon to generate class files (bytecode).
	 */
	public void setCompile(boolean compile) {
		this.compile = compile;
	}

	/**
	 * Sets the output directory for generated sources.
	 */
	@Override
	public void setOutput(File output) {
		this.output = output;
	}

	/**
	 * Sets the destination directory for compiled classes (bytecode).
	 */
	public void setDestination(File destination) {
		this.destination = destination;
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

	int tabSize = 4;

	/**
	 * Sets the tabulation size (default is 4 spaces).
	 */
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

	/**
	 * Tells if Spoon should precompile the input files before processing.
	 */
	public void setPrecompile(boolean precompile) {
		this.precompile = precompile;
	}

	Path sourceClasspath;

	/**
	 * Set the classpath to be used when building, processing and compiling the
	 * sources.
	 *
	 * @param s
	 * 		an Ant Path object containing the classpath.
	 */
	public void setSourceClasspath(Path s) {
		createSourceClasspath().append(s);
	}

	/**
	 * Source classpath to use, by reference.
	 *
	 * @param r
	 * 		a reference to an existing classpath
	 */
	public void setSourceClasspathRef(Reference r) {
		createSourceClasspath().setRefid(r);
	}

	private Path createSourceClasspath() {
		if (sourceClasspath == null) {
			sourceClasspath = new Path(getProject());
		}
		return sourceClasspath;
	}

	Path templateClasspath;

	/**
	 * Set the classpath to be used when building the template sources.
	 *
	 * @param s
	 * 		an Ant Path object containing the classpath.
	 */
	public void setTemplateClasspath(Path s) {
		createTemplateClasspath().append(s);
	}

	/**
	 * Template classpath to use, by reference.
	 *
	 * @param r
	 * 		a reference to an existing classpath
	 */
	public void setTemplateClasspathRef(Reference r) {
		createTemplateClasspath().setRefid(r);
	}

	private Path createTemplateClasspath() {
		if (templateClasspath == null) {
			templateClasspath = new Path(getProject());
		}
		return templateClasspath;
	}

	/**
	 * Sets Spoon to build only the outdated source files (gives better
	 * performances). This option will be ignored if the noouput option is on.
	 */
	public void setBuildOnlyOutdatedFiles(boolean buildOnlyOutdatedFiles) {
		this.buildOnlyOutdatedFiles = buildOnlyOutdatedFiles;
	}

	/**
	 * Sets the output type (none, classes, or compilationunits).
	 */
	public void setOutputType(String ouputType) {
		this.outputType = ouputType;
	}

	/**
	 * Sets the encoding to be used by the compiler.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Sets automatic imports in generated files.
	 */
	public void setImports(boolean imports) {
		this.imports = imports;
	}

	/**
	 * Does not assume a full classpath
	 */
	public void setNoClasspath(boolean noclasspath) {
		this.noclasspath = noclasspath;
	}

	/**
	 * Tells if Spoon should try to preserve the original line numbers when
	 * generating the source code (may lead to human-unfriendly formatting).
	 */
	public void setLines(boolean lines) {
		this.lines = lines;
	}

}
