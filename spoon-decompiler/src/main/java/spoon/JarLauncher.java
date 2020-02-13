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
package spoon;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import spoon.decompiler.CFRDecompiler;
import spoon.decompiler.Decompiler;
import spoon.support.Experimental;
import spoon.support.compiler.SpoonPom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Experimental
public class JarLauncher extends Launcher {
	File pom;
	File jar;
	File decompiledRoot;
	File decompiledSrc;
	Decompiler decompiler;
	boolean decompile = false;

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 */
	public JarLauncher(String jarPath) {
		this(jarPath, null, (String) null);
	}

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath) {
		this(jarPath, decompiledSrcPath, (String) null);
	}

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 * @param pom path to pom associated with the jar to be analyzed
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath, String pom) {
		this(jarPath, decompiledSrcPath, pom, null);
	}

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 * @param decompiler Instance implementing {@link spoon.decompiler.Decompiler} to be used
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath, Decompiler decompiler) {
		this(jarPath, decompiledSrcPath, null, decompiler);
	}

	/**
	 * JarLauncher constructor. Uses the default Decompiler (CFR)
	 * If decompiledSrcPath is null a temporary directory will be created and filled with decompiled sources.
	 * This directory is deleted every time this constructor is called.
	 * If a decompiledSrcPath is provided, deletion of the content of the directory is up to the user.
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 * @param pom path to pom associated with the jar to be analyzed
	 * @param decompiler Instance implementing {@link spoon.decompiler.Decompiler} to be used
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath, String pom, Decompiler decompiler) {
		this.decompiler = decompiler;
		if (decompiledSrcPath == null) {
			decompiledSrcPath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "spoon-tmp";
			decompile = true;
		}
		this.decompiledRoot = new File(decompiledSrcPath);
		if (decompiledRoot.exists() && !decompiledRoot.canWrite()) {
			throw new SpoonException("Dir " + decompiledRoot.getPath() + " already exists and is not deletable.");
		} else if (decompiledRoot.exists() && decompile) {
			try {
				FileUtils.deleteDirectory(decompiledRoot);
			} catch (IOException e) {
				throw new SpoonException("Dir " + decompiledRoot.getPath() + " already exists and is not deletable.");
			}
		}
		if (!decompiledRoot.exists()) {
			decompiledRoot.mkdirs();
			decompile = true;
		}
		decompiledSrc = new File(decompiledRoot, "src/main/java");
		if (!decompiledSrc.exists()) {
			decompiledSrc.mkdirs();
			decompile = true;
		}

		if (decompiler == null) {
			this.decompiler = getDefaultDecompiler();
		}

		jar = new File(jarPath);
		if (!jar.exists() || !jar.isFile()) {
			throw new SpoonException("Jar " + jar.getPath() + " not found.");
		}


		decompile = true;
		init(pom);
	}

	private void init(String pomPath) {
		String[] classpath = null;

		if (pomPath != null) {
			File srcPom = new File(pomPath);
			if (!srcPom.exists() || !srcPom.isFile()) {
				throw new SpoonException("Pom " + srcPom.getPath() + " not found.");
			}
			try {
				pom = new File(decompiledRoot, "pom.xml");
				Files.copy(srcPom.toPath(), pom.toPath(), REPLACE_EXISTING);
			} catch (IOException e) {
				throw new SpoonException("Unable to write " + pom.getPath());
			}
			try {
				SpoonPom pomModel = new SpoonPom(pom.getPath(), null, MavenLauncher.SOURCE_TYPE.APP_SOURCE, getEnvironment());
				getEnvironment().setComplianceLevel(pomModel.getSourceVersion());
				classpath = pomModel.buildClassPath(null, MavenLauncher.SOURCE_TYPE.APP_SOURCE, LOGGER, false);
				// dependencies
				this.getModelBuilder().setSourceClasspath(classpath);
			} catch (IOException | XmlPullParserException e) {
				throw new SpoonException("Failed to read classpath file.");
			}
		}

		//We call the decompiler only if jar has changed since last decompilation.
		if (decompile) {
			decompiler.decompile(jar.getAbsolutePath(), decompiledSrc.getAbsolutePath(), classpath);
		}

		addInputResource(decompiledSrc.getAbsolutePath());
	}

	protected Decompiler getDefaultDecompiler() {
		return new CFRDecompiler();
	}
}
