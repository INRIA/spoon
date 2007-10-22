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
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import spoon.processing.Builder;
import spoon.processing.Environment;
import spoon.processing.ProcessingManager;
import spoon.reflect.CoreFactory;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.util.JDTCompiler;

/**
 * A classloader that gets classes from Java source files and process them
 * before actually loading them.
 */
public class SpoonClassLoader extends ClassLoader {

	private CoreFactory coreFactory;

	private Environment environment;

	private Factory factory;

	private ProcessingManager processing;

	private File sourcePath;

	/**
	 * Constructs a Spoon classloader.
	 */
	public SpoonClassLoader() {
		super();
	}

	/**
	 * Constructs a Spoon classloader within the context of a given parent
	 * classloader.
	 */
	public SpoonClassLoader(ClassLoader parent) {
		super(parent);
	}

	private Class<?> createClass(String qualifiedName) {
		try {
			// Process file
			processJavaFile(qualifiedName);
			return classcache.get(qualifiedName);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Gets the associated (default) core factory.
	 */
	public CoreFactory getCoreFactory() {
		if (coreFactory == null) {
			coreFactory = new DefaultCoreFactory();
		}
		return coreFactory;
	}

	/**
	 * Gets the associated (standard) environment.
	 */
	public Environment getEnvironment() {
		if (environment == null) {
			environment = new StandardEnvironment();
		}
		return environment;
	}

	/**
	 * Gets the associated factory.
	 */
	public Factory getFactory() {
		if (factory == null) {
			factory = new Factory(getCoreFactory(), getEnvironment());
		}
		return factory;
	}

	/**
	 * Gets the processing manager.
	 */
	public ProcessingManager getProcessingManager() {
		if (processing == null) {
			processing = new RuntimeProcessingManager(getFactory());
		}
		return processing;
	}

	/**
	 * Gets the source path.
	 */
	public File getSourcePath() {
		if (sourcePath == null) {
			sourcePath = new File("");
		}
		return sourcePath;
	}

	private Map<String, Class<?>> classcache = new TreeMap<String, Class<?>>();

	/**
	 * Loads a given class from its name.
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {

		// Look in cache
		if (classcache.containsKey(name)) {
			return classcache.get(name);
		}

		// Try to gets from spoon factory
		Class<?> clas = null;
		clas = createClass(name);

		// Try to get in system class
		if (clas == null) {
			clas = findSystemClass(name);
		}

		if (clas == null)
			throw new ClassNotFoundException(name);
		return clas;
	}

	private void processJavaFile(String qualifiedName) throws Exception {
		// Try to resolve in model
		CtSimpleType<?> c = getFactory().Type().get(qualifiedName);

		// Try to resolve in source path
		if (c == null) {
			File f = resolve(qualifiedName);
			if (f == null || !f.exists())
				throw new ClassNotFoundException(qualifiedName);
			Builder builder = getFactory().getBuilder();
			builder.addInputSource(f);
			builder.build();
			c = getFactory().Type().get(qualifiedName);
		}

		// not resolved
		if (c == null)
			throw new ClassNotFoundException(qualifiedName);
		// Processing it
		getProcessingManager().process(c);

		// Printing it
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(getEnvironment());
		printer.scan(c);

		String[] tmp = c.getQualifiedName().split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		spoon.support.util.BasicCompilationUnit unit = new spoon.support.util.BasicCompilationUnit(
				printer.toString().toCharArray(), pack, c.getSimpleName()
						+ ".java");

		JDTCompiler comp = new JDTCompiler();
		comp.compile(new ICompilationUnit[] { unit });

		for (ClassFile f : comp.getClassFiles()) {
			String name = new String(f.fileName()).replace('/', '.');
			Class<?> cl = defineClass(name, f.getBytes(), 0, f.getBytes().length);
			classcache.put(name, cl);
		}

	}

	private File resolve(String qualifiedName) {
		File current = sourcePath;
		String[] path = qualifiedName.split("[.]");
		for (String p : path) {
			for (File f : current.listFiles()) {
				if (f.getName().equals(p) || f.getName().equals(p + ".java")) {
					current = f;
					continue;
				}
			}
		}
		if (!current.isDirectory())
			return current;
		return null;
	}

	/**
	 * Sets the core factory.
	 */
	public void setCoreFactory(CoreFactory coreFactory) {
		this.coreFactory = coreFactory;
	}

	/**
	 * Sets the environment.
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Sets the factory.
	 */
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	/**
	 * Sets the used processing manager.
	 */
	public void setProcessingManager(ProcessingManager processing) {
		this.processing = processing;
	}

	/**
	 * Sets the source path.
	 */
	public void setSourcePath(File sourcePath) {
		this.sourcePath = sourcePath;
	}

}
