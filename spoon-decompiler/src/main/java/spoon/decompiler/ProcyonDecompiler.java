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

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.CompositeTypeLoader;
import com.strobel.assembler.metadata.DeobfuscationUtilities;
import com.strobel.assembler.metadata.IMetadataResolver;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.assembler.metadata.MetadataParser;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.BytecodeLanguage;
import com.strobel.io.PathHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import spoon.support.Experimental;

@Experimental
public class ProcyonDecompiler implements Decompiler {

	@Override
	public void decompile(String inputPath, String outputPath, String[] classpath) {
		try {
			if (inputPath.endsWith(".jar")) {
				decompileJar(inputPath, outputPath);
			} else if (inputPath.endsWith(".class")) {
				decompileClass(inputPath, outputPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void decompileClass(String path, String outputDir) throws Exception {
		final File tempClass = new File(path);

		DecompilerSettings settings = DecompilerSettings.javaDefaults();

		MetadataSystem metadataSystem = new MetadataSystem(new InputTypeLoader());
		TypeReference type = metadataSystem.lookupType(tempClass
				.getCanonicalPath());

		DecompilationOptions decompilationOptions = new DecompilationOptions();
		decompilationOptions.setSettings(DecompilerSettings.javaDefaults());
		decompilationOptions.setFullDecompilation(true);

		TypeDefinition resolvedType = null;
		if (type == null || ((resolvedType = type.resolve()) == null)) {
			throw new Exception("Unable to resolve type.");
		}

		final Writer writer = createWriter(resolvedType, settings, outputDir);
		final boolean writeToFile = writer instanceof FileOutputWriter;
		final PlainTextOutput output;

		output = new PlainTextOutput(writer);

		output.setUnicodeOutputEnabled(settings.isUnicodeOutputEnabled());

		if (settings.getLanguage() instanceof BytecodeLanguage) {
			output.setIndentToken("  ");
		}

		if (writeToFile) {
			System.out.printf("Decompiling %s...\n", resolvedType.getFullName());
		}

		settings.getLanguage().decompileType(resolvedType, output, decompilationOptions);

		writer.flush();

		if (writeToFile) {
			writer.close();
		}
	}

	private void decompileJar(String jarFilePath, String outputDir) throws IOException {
		DecompilationOptions decompilationOptions = new DecompilationOptions();
		decompilationOptions.setSettings(DecompilerSettings.javaDefaults());
		decompilationOptions.setFullDecompilation(true);
		final File jarFile = new File(jarFilePath);

		if (!jarFile.exists()) {
			throw new FileNotFoundException("File not found: " + jarFilePath);
		}

		final DecompilerSettings settings = decompilationOptions.getSettings();
		settings.setTypeLoader(new InputTypeLoader());
		settings.setExcludeNestedTypes(false);


		final JarFile jar = new JarFile(jarFile);
		final Enumeration<JarEntry> entries = jar.entries();

		final boolean oldShowSyntheticMembers = settings.getShowSyntheticMembers();
		final ITypeLoader oldTypeLoader = settings.getTypeLoader();

		settings.setShowSyntheticMembers(false);
		settings.setTypeLoader(new CompositeTypeLoader(new JarTypeLoader(jar), oldTypeLoader));

		try {
			MetadataSystem metadataSystem = new NoRetryMetadataSystem(settings.getTypeLoader());

			int classesDecompiled = 0;

			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();
				final String name = entry.getName();

				if (!name.endsWith(".class")) {
					continue;
				}

				final String internalName = StringUtilities.removeRight(name, ".class");

				try {
					decompileType(metadataSystem, internalName, decompilationOptions, outputDir);

					if (++classesDecompiled % 100 == 0) {
						metadataSystem = new NoRetryMetadataSystem(settings.getTypeLoader());
					}
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}
		} finally {
			settings.setShowSyntheticMembers(oldShowSyntheticMembers);
			settings.setTypeLoader(oldTypeLoader);
		}
	}

	private void decompileType(MetadataSystem metadataSystem, String typeName, DecompilationOptions options, String outputDir) throws IOException {

		final TypeReference type;
		final DecompilerSettings settings = options.getSettings();

		if (typeName.length() == 1) {
			//
			// Hack to get around classes whose descriptors clash with primitive types.
			//

			final MetadataParser parser = new MetadataParser(IMetadataResolver.EMPTY);
			final TypeReference reference = parser.parseTypeDescriptor(typeName);

			type = metadataSystem.resolve(reference);
		} else {
			type = metadataSystem.lookupType(typeName);
		}

		final TypeDefinition resolvedType;

		if (type == null || (resolvedType = type.resolve()) == null) {
			System.err.printf("!!! ERROR: Failed to load class %s.\n", typeName);
			return;
		}

		DeobfuscationUtilities.processType(resolvedType);

		if (resolvedType.isNested() || resolvedType.isAnonymous() || resolvedType.isSynthetic()) {
			return;
		}

		final Writer writer = createWriter(resolvedType, settings, outputDir);
		final boolean writeToFile = writer instanceof FileOutputWriter;
		final PlainTextOutput output;

		output = new PlainTextOutput(writer);

		output.setUnicodeOutputEnabled(settings.isUnicodeOutputEnabled());

		if (settings.getLanguage() instanceof BytecodeLanguage) {
			output.setIndentToken("  ");
		}

		if (writeToFile) {
			System.out.printf("Decompiling %s...\n", typeName);
		}

		settings.getLanguage().decompileType(resolvedType, output, options);

		writer.flush();

		if (writeToFile) {
			writer.close();
		}
	}

	private Writer createWriter(TypeDefinition type, DecompilerSettings settings, String outputDirectory) throws IOException {

		if (StringUtilities.isNullOrWhitespace(outputDirectory)) {
			return new OutputStreamWriter(
					System.out,
					settings.isUnicodeOutputEnabled() ? Charset.forName("UTF-8")
							: Charset.defaultCharset()
			);
		}

		String outputPath;
		String fileName = type.getName() + settings.getLanguage().getFileExtension();
		String packageName = type.getPackageName();

		if (StringUtilities.isNullOrWhitespace(packageName)) {
			outputPath = PathHelper.combine(outputDirectory, fileName);
		} else {
			outputPath = PathHelper.combine(
					outputDirectory,
					packageName.replace('.', PathHelper.DirectorySeparator),
					fileName
			);
		}

		File outputFile = new File(outputPath);
		File parentFile = outputFile.getParentFile();

		if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
			throw new IllegalStateException(String.format("Could not create output directory for file \"%s\".",	outputPath));
		}

		if (!outputFile.exists() && !outputFile.createNewFile()) {
			throw new IllegalStateException(String.format("Could not create output file \"%s\".", outputPath));
		}

		return new FileOutputWriter(outputFile, settings);
	}

	final class FileOutputWriter extends OutputStreamWriter {
		private final File file;

		FileOutputWriter(final File file, final DecompilerSettings settings) throws IOException {
			super(
					new FileOutputStream(file),
					settings.isUnicodeOutputEnabled() ? Charset.forName("UTF-8")
							: Charset.defaultCharset()
			);
			this.file = file;
		}
	}

	final class NoRetryMetadataSystem extends MetadataSystem {
		private final Set<String> _failedTypes = new HashSet<>();

		NoRetryMetadataSystem(final ITypeLoader typeLoader) {
			super(typeLoader);
		}

		@Override
		protected TypeDefinition resolveType(final String descriptor, final boolean mightBePrimitive) {
			if (_failedTypes.contains(descriptor)) {
				return null;
			}

			final TypeDefinition result = super.resolveType(descriptor, mightBePrimitive);

			if (result == null) {
				_failedTypes.add(descriptor);
			}

			return result;
		}
	}
}
