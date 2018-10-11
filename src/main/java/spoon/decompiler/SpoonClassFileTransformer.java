/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.decompiler;

import org.benf.cfr.reader.Main;
import spoon.IncrementalLauncher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public abstract class SpoonClassFileTransformer implements ClassFileTransformer {

	Predicate<String> classNameFilter;
	String pathToDecompiled;
	String pathToRecompile;
	String pathToCache;
	File cache;
	Set<String> classPath;
	public Decompiler decompiler;
	Set<File> inputSources;

	//Exclude jvm classes
	public static final Predicate<String> defaultFilter = s -> !(s.startsWith("java") || s.startsWith("sun"));


	/**
	 * Default Constructor for SpoonClassFileTransformer
	 *
	 * @param classNameFilter Filter for classname. If classeNameFilter.test(className) returns false,
	 *                        the class will be loaded without decompilation nor transformation.
	 *                        If null, a default filter will filter out typical jvm classes (starting with java* or sun*)
	 *                        Note @{SpoonClassFileTransformer.defaultFilter} may be used in conjunction of custom filter
	 *                        with `defaultFilter.and(classNameFilter)`.
	 */
	public SpoonClassFileTransformer(Predicate<String> classNameFilter) {
		this(classNameFilter, "spoon-decompiled", "spoon-cache", "spoon-recompiled");
	}

	/**
	 * Default Constructor for SpoonClassFileTransformer
	 *
	 * @param classNameFilter Filter for classname. If classeNameFilter.test(className) returns false,
	 *                        the class will be loaded without decompilation nor transformation.
	 *                        If null, a default filter will filter out typical jvm classes (starting with java* or sun*)
	 *                        Note @{SpoonClassFileTransformer.defaultFilter} may be used in conjunction of custom filter
	 *                        with `defaultFilter.and(classNameFilter)`.
	 * @param pathToDecompiled path to directory in which to put decompiled sources.
	 * @param pathToCache path to cache directory for IncrementalLauncher
	 * @param pathToRecompile path to recompiled classes
	 */
	public SpoonClassFileTransformer(Predicate<String> classNameFilter, String pathToDecompiled, String pathToCache, String pathToRecompile) {
		if (classNameFilter == null) {
			this.classNameFilter = defaultFilter;
		} else {
			this.classNameFilter = classNameFilter;
		}
		String classPathAr[] = System.getProperty("java.class.path").split(":");
		classPath = new HashSet<>(Arrays.asList(classPathAr));
		this.pathToDecompiled = pathToDecompiled;
		this.pathToRecompile = pathToRecompile;
		this.pathToCache = pathToCache;

		cache = new File(pathToCache);


		//inputSources = Collections.singleton(new File(pathToDecompiled));
		inputSources = new HashSet<>();
		inputSources.add(new File(pathToDecompiled));

		decompiler = s -> Main.main(new String[]{s, "--outputdir", pathToDecompiled});
	}

	@Override
	public byte[] transform(
			ClassLoader loader,
			String className,
			Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain,
			byte[] classfileBuffer
	) throws IllegalClassFormatException {
		try {

			//If the class is not matched by user's filter, resume unmodified loading
			if (!classNameFilter.test(className)) {
				return classfileBuffer;
			}

			//Decompile classfile
			String pathToClassFile = loader.getResource(className + ".class").getPath();
			decompiler.decompile(pathToClassFile);

			IncrementalLauncher launcher = new IncrementalLauncher(inputSources, classPath, cache);
			launcher.addInputResource(pathToDecompiled);

			//Get updated model
			CtModel model = launcher.buildModel();
			launcher.saveCache();

			//Get class model
			CtType toBeTransformed =  model.getAllTypes().stream().filter(t -> t.getQualifiedName().equals(className.replace("/", "."))).findAny().get();

			//If the class model is not modified by user, resume unmodified loading
			if (!accept(toBeTransformed)) {
				return classfileBuffer;
			}
			launcher.getEnvironment().debugMessage("[Agent] transforming " + className);
			transform(toBeTransformed);

			//Compile new class model
			SpoonModelBuilder compiler = launcher.createCompiler();
			compiler.compile(SpoonModelBuilder.InputType.CTTYPES);


			File transformedClass = new File(compiler.getBinaryOutputDirectory(), className + ".class");
			try {
				//Load Modified classFIle
				byte[] fileContent = Files.readAllBytes(transformedClass.toPath());
				launcher.getEnvironment().debugMessage("[Agent] loading transformed " + className);
				return fileContent;
			} catch (IOException e) {
				launcher.getEnvironment().debugMessage("[ERROR][Agent] while loading transformed " + className);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classfileBuffer;
	}

	/**
	 * User defined filter to discard type that will not be transformed by the SpoonClassFileTransformer.
	 * @param type type considered for transformation
	 */
	public abstract boolean accept(CtType type);


	/**
	 * User's implementation of transformation to apply on type.
	 * @param type type to be transformed
	 */
	public abstract void transform(CtType type);
}
