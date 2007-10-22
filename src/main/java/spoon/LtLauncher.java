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
import java.lang.reflect.Method;
import java.util.ArrayList;

import spoon.processing.ProcessingManager;
import spoon.support.RuntimeProcessingManager;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;

/**
 * This launcher can run a program within a special {@link SpoonClassLoader}
 * that will process the used classes at load time. Actually this launcher
 * accepts a list of processors that will be applied before the program is run,
 * and another list of processors that will be applied at load-time (only once
 * the classes are used and not before). You can then process bytecode at
 * load-time by using this launcher with the decompile option. Launch with no
 * arguments (see {@link #main(String[])}) for detailed usage.
 */
public class LtLauncher extends Launcher {

	/**
	 * Starts a program with this launcher.
	 * 
	 * @param args
	 *            run with no args to print usage
	 * @throws JSAPException
	 * @throws Exception
	 *             any untrapped exception
	 */
	static public void main(String args[]) throws Exception {
		new LtLauncher(args).run();
	}

	/**
	 * Constructor with arguments.
	 */
	public LtLauncher(String[] args) throws JSAPException {
		super(args);
	}

	/**
	 * Defines the arguments accepted by this launcher.
	 */
	protected JSAP defineArgs() throws JSAPException {
		JSAP jsap = super.defineArgs();

		// Processor qualified name
		FlaggedOption opt2 = new FlaggedOption("ltprocessors");
		opt2.setShortFlag('l');
		opt2.setLongFlag("ltprocessors");
		opt2.setHelp("List of load-time processors to use");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		opt2 = new FlaggedOption("sourcepath");
		opt2.setLongFlag("sourcepath");
		opt2.setHelp("Specify where to find input source files");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		return jsap;
	}

	java.util.List<String> ltprocessors = new ArrayList<String>();

	/**
	 * Adds a processor.
	 */
	@Override
	public void addProcessor(String name) {
		super.addProcessor(name);
		ltprocessors.add(name);
	}

	/**
	 * Gets the loadtime processor types.
	 */
	protected java.util.List<String> getLtProcessorTypes() {
		if (getArguments().getString("ltprocessors") != null) {
			for (String processorName : getArguments()
					.getString("ltprocessors").split(File.pathSeparator)) {
				ltprocessors.add(processorName);
			}
		}
		return ltprocessors;
	}

	/**
	 * Processes the program within a {@link SpoonClassLoader}.
	 */
    @Override
	public void run() throws Exception {
		getFactory().getEnvironment().debugMessage(
				"loading command-line arguments...");
		processArguments();

		getFactory().getEnvironment().debugMessage("start Processing...");

		long t = System.currentTimeMillis();
		build();
		getFactory().getEnvironment().debugMessage(
				"model built in " + (System.currentTimeMillis() - t)
						+ " ms");
		t = System.currentTimeMillis();
		process();
		getFactory().getEnvironment().debugMessage(
				"model processed in " + (System.currentTimeMillis() - t)
						+ " ms");
		t = System.currentTimeMillis();

		// Create a CompilingClassLoader
		SpoonClassLoader ccl = new SpoonClassLoader();
		ccl.setFactory(getFactory());
		if (getArguments().getString("sourcepath") != null)
			ccl.setSourcePath(new File(getArguments().getString(
					"sourcepath")));

		// Create runtime processing manager
		ProcessingManager pm = new RuntimeProcessingManager(getFactory());
		for (String s : getLtProcessorTypes())
			pm.addProcessor(s);
		ccl.setProcessingManager(pm);

		getFactory().getEnvironment().debugMessage("running...");
		// Gets main class
		String progClass = getArguments().getString("class");
		String progArgs[] = getArguments().getStringArray("arguments");

		// Launch main class using reflection
		Class<?> clas = ccl.loadClass(progClass);
		Class<?> mainArgType[] = { (new String[0]).getClass() };
		Method main = clas.getMethod("main", mainArgType);
		Object argsArray[] = { progArgs };
		main.invoke(null, argsArray);
	}
}
