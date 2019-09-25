package spoon;

import org.apache.commons.io.FileUtils;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResource;
import spoon.decompiler.CFRDecompiler;
import spoon.decompiler.Decompiler;
import spoon.support.compiler.FileSystemFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecompiledResource extends FileSystemFolder {

	/**
	 * Creates a FileSystemFolder containing the decompiled .class and .jar from path.
	 * The actual folder is located at pathToDecompiledSources.
	 * @param path path to the folder containing the bytecode to decompile
	 * @param classpath additional classpath that could help decompilation.
	 * @param decompiler decompiler to use. If null the default will be used.
	 * @param pathToDecompiledSources output directory for decompiled sources.
	 */
	public DecompiledResource(String path, String[] classpath, Decompiler decompiler, String pathToDecompiledSources) {
		super(pathToDecompiledSources);

		final Decompiler decompilerToUse;
		if (decompiler == null) {
			decompilerToUse = new CFRDecompiler();
		} else {
			decompilerToUse = decompiler;
		}

		File output = new File(pathToDecompiledSources);
		output.mkdirs();

		try (Stream<Path> walk = Files.walk(Paths.get(path))) {

			walk.filter(Files::isRegularFile)
					.filter(x -> x.toString().endsWith(".class") || x.toString().endsWith(".jar"))
					.map(x -> x.toString())
					.forEach(
						f -> decompilerToUse.decompile(f, pathToDecompiledSources, classpath)
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
