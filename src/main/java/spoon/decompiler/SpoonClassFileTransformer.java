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
	public Decompiler decompiler;
	SpoonModelBuilder compiler;
	IncrementalLauncher launcher;
	Set<File> inputSources;

	//Exclude jvm classes
	public static final Predicate<String> defaultFilter = s -> !(s.startsWith("java") || s.startsWith("sun"));

	/**
	 * Default Constructor for SpooClassFileTransformer
	 *
	 * @param classNameFilter Filter for classname. If classeNameFilter.test(className) returns false,
	 *                        the class will be loaded without decompilation nor transformation.
	 *                        If null, a default filter will filter out typical jvm classes (starting with java* or sun*)
	 *                        Note @{SpoonClassFileTransformer.defaultFilter} may be used in conjunction of custom filter
	 *                        with `defaultFilter.and(classNameFilter)`.
	 */
	public SpoonClassFileTransformer(Predicate<String> classNameFilter) {
		if (classNameFilter == null) {
			this.classNameFilter = defaultFilter;
		} else {
			this.classNameFilter = classNameFilter;
		}
		String classPathAr[] = System.getProperty("java.class.path").split(":");
		Set<String> classPath = new HashSet<>(Arrays.asList(classPathAr));
		pathToDecompiled = "spoon-decompiled";
		pathToRecompile = "spoon-recompiled";
		pathToCache = "spoon-cache";


		//inputSources = Collections.singleton(new File(pathToDecompiled));
		inputSources = new HashSet<>();
		inputSources.add(new File(pathToDecompiled));
		launcher = new IncrementalLauncher(inputSources, classPath, new File(pathToCache));
		launcher.addInputResource(pathToDecompiled);

		decompiler = s -> Main.main(new String[]{s, "--outputdir", pathToDecompiled});


		//Should we wait for directory creation?

		compiler = launcher.createCompiler();
		launcher.buildModel();
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
			launcher.getEnvironment().debugMessage("[Agent] loading " + className);

			//If the class is not matched by user's filter, resume unmodified loading
			if (!classNameFilter.test(className)) {
				return classfileBuffer;
			}

			//Decompile classfile
			launcher.getEnvironment().debugMessage("[Agent] decompiling " + className);
			String pathToClassFile = loader.getResource(className + ".class").getPath();
			decompiler.decompile(pathToClassFile);

			//Get updated model
			CtModel model = launcher.getModel();

			//Get class model
			CtType toBeTransformed =  model.getAllTypes().stream().filter(t -> t.getQualifiedName().equals(className.replace("/", "."))).findAny().get();

			//If the class model is not modified by user, resume unmodified loading
			if (!accept(toBeTransformed)) {
				return classfileBuffer;
			}
			launcher.getEnvironment().debugMessage("[Agent] transforming " + className);
			transform(toBeTransformed);

			//Compile new class model
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
			launcher.getEnvironment().debugMessage("[ERROR][Agent] while processing " + className);
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
