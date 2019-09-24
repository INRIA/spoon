package spoon.decompiler;

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.Buffer;
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
import com.strobel.decompiler.AnsiTextOutput;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.BytecodeLanguage;
import com.strobel.decompiler.languages.BytecodeOutputOptions;
import com.strobel.decompiler.languages.TypeDecompilationResults;
import com.strobel.io.PathHelper;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProcyonDecompiler implements Decompiler {
	@Override
	public void decompile(String inputPath, String outputPath, String[] classpath) {
		try {
			if (inputPath.endsWith(".jar")) {
				decompileJar(inputPath,outputPath);
			} else if (inputPath.endsWith(".class")) {
				File in = new File(inputPath);
				String className = getClassName(new FileInputStream(in));

				DecompilationOptions decompilationOptions = new DecompilationOptions();
				decompilationOptions.setSettings(DecompilerSettings.javaDefaults());
				decompilationOptions.setFullDecompilation(true);
				DecompilerSettings settings = decompilationOptions.getSettings();
				ITypeLoader oldTypeLoader = settings.getTypeLoader();

				settings.setShowSyntheticMembers(false);
				settings.setTypeLoader(oldTypeLoader);
				MetadataSystem metadataSystem = new NoRetryMetadataSystem(settings.getTypeLoader());
				decompileType(metadataSystem,className,decompilationOptions,outputPath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getClassName(InputStream is) throws Exception {
		DataInputStream dis = new DataInputStream(is);
		dis.readLong(); // skip header and class version
		int cpcnt = (dis.readShort() & 0xffff) - 1;
		int[] classes = new int[cpcnt];
		String[] strings = new String[cpcnt];
		for (int i = 0; i < cpcnt; i++) {
			int t = dis.read();
			if (t == 7) classes[i] = dis.readShort() & 0xffff;
			else if (t == 1) strings[i] = dis.readUTF();
			else if (t == 5 || t == 6) {
				dis.readLong();
				i++;
			} else if (t == 8) dis.readShort();
			else dis.readInt();
		}
		dis.readShort(); // skip access flags
		return strings[classes[(dis.readShort() & 0xffff) - 1] - 1].replace('/', '.');
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
			throw new IllegalStateException(String.format( "Could not create output file \"%s\".", outputPath));
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
