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

import spoon.support.gui.SpoonModelTree;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;

/**
 * This class implements an integrated command-line launcher for processing
 * programs at compile-time using the JDT-based builder (Eclipse). It takes
 * arguments that allow building, processing, printing, and compiling Java
 * programs. Launch with no arguments (see {@link #main(String[])}) for detailed
 * usage.
 * 
 * 
 * @see spoon.compiler.Environment
 * @see spoon.reflect.Factory
 * @see spoon.compiler.SpoonCompiler
 * @see spoon.processing.ProcessingManager
 * @see spoon.processing.Processor
 */
public class Launcher extends AbstractLauncher {

	private static Launcher instance;

	/**
	 * Gets the launcher instance.
	 */
	public static Launcher getInstance() {
		return instance;
	}

	/**
	 * A default program entry point (instantiates a launcher with the given
	 * arguments and calls {@link #run()}).
	 */
	public static void main(String[] args) throws Exception {
		// Main.compile(new String[] { "-help" }, new PrintWriter(System.out),
		// new PrintWriter(System.err), null);
		if (args.length != 0) {
			instance = new Launcher(args);
		} else {
			instance = new Launcher(new String[] { "--help" });
		}
		instance.run();
	}

	/**
	 * Constructor.
	 */
	public Launcher(String[] args) throws JSAPException {
		super(args);
	}

	/**
	 * Adds some specific arguments to the common ones.
	 */
	@Override
	protected JSAP defineArgs() throws JSAPException {
		JSAP jsap = super.defineArgs();

		// Compile Output files
		// sw1 = new Switch("compile");
		// sw1.setShortFlag('c');
		// sw1.setLongFlag("compile");
		// sw1.setHelp("compile generated sources");
		// jsap.registerParameter(sw1);

		// build output directory
		// FlaggedOption opt2 = new FlaggedOption("build");
		// opt2.setShortFlag('b');
		// opt2.setLongFlag("build");
		// opt2.setDefault("spoonBuild");
		// opt2.setHelp("specify where to place generated class files");
		// opt2.setStringParser(FileStringParser.getParser());
		// opt2.setRequired(false);
		// jsap.registerParameter(opt2);

		// show GUI
		Switch sw1 = new Switch("gui");
		sw1.setShortFlag('g');
		sw1.setLongFlag("gui");
		sw1.setHelp("Show spoon model after processing");
		jsap.registerParameter(sw1);

		return jsap;
	}

	/**
	 * Starts the Spoon processing.
	 */
	@Override
	public void run() throws Exception {
		try {
			super.run();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		// display GUI
		if (getArguments().getBoolean("gui")) {
			new SpoonModelTree(getFactory());
		}

	}

}
