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

import spoon.processing.FileGenerator;
import spoon.reflect.Factory;
import spoon.support.ByteCodeOutputProcessor;
import spoon.support.JavaOutputProcessor;
import spoon.support.gui.SpoonModelTree;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * This class implements an integrated command-line launcher for processing
 * programs at compile-time using the JDT-based builder (Eclipse). It takes
 * arguments that allow building, processing, printing, and compiling Java
 * programs. Launch with no arguments (see {@link #main(String[])}) for
 * detailed usage.
 * 
 * 
 * @see spoon.processing.Environment
 * @see spoon.reflect.Factory
 * @see spoon.processing.Builder
 * @see spoon.processing.ProcessingManager
 * @see spoon.processing.Processor
 */
public class Launcher extends AbstractLauncher {
	/**
	 * A default program entry point (instantiates a launcher with the given
	 * arguments and calls {@link #run()}).
	 */
	public static void main(String[] args) throws Exception {
        try {
            new Launcher(args).run();
        } catch(Exception exc) {
            exc.printStackTrace();
            throw exc;
        }
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

		// Disable output generation
		Switch sw1 = new Switch("nooutput");
		sw1.setLongFlag("no");
		sw1.setHelp("disable output printing");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		// Compile Output files
		sw1 = new Switch("compile");
		sw1.setShortFlag('c');
		sw1.setLongFlag("compile");
		sw1.setHelp("compile generated sources");
		jsap.registerParameter(sw1);

		// build output directory
		FlaggedOption opt2 = new FlaggedOption("build");
		opt2.setShortFlag('b');
		opt2.setLongFlag("build");
		opt2.setDefault("spoonBuild");
		opt2.setHelp("specify where to place generated class files");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// show GUI
		sw1 = new Switch("gui");
		sw1.setShortFlag('g');
		sw1.setLongFlag("gui");
		sw1.setHelp("show spoon model after processing");
		jsap.registerParameter(sw1);

		return jsap;
	}

	/**
	 * Creates the factory and associated environment for constructing the
	 * model, initialized with the launcher's arguments.
	 */
	@Override
	protected Factory createFactory() {
		Factory f = super.createFactory();

		if (getArguments().getBoolean("compile")) {
			FileGenerator<?> printer = f.getEnvironment()
					.getDefaultFileGenerator();
			ByteCodeOutputProcessor p = new ByteCodeOutputProcessor(
					(JavaOutputProcessor) printer, getArguments().getFile(
							"build"));
			f.getEnvironment().setDefaultFileGenerator(p);
		}

		return f;
	}

	/**
	 * Prints out the built model into files.
	 */
	@Override
	protected void print() {
		if (!getArguments().getBoolean("nooutput"))
			super.print();
	}

	/**
	 * Starts the Spoon processing.
	 */
	@Override
	public void run() throws Exception {
		super.run();

		// display GUI
		if (getArguments().getBoolean("gui"))
			new SpoonModelTree(getFactory());

	}

}
