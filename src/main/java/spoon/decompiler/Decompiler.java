package spoon.decompiler;

import java.io.File;

public interface Decompiler {

	/**
	 * Sets the output directory for source generated.
	 *
	 * @param jar
	 * 		Path to jar to be analyzed.
	 * @param outputDir
	 * 		Path for the output directory for decompilation.
	 */
	void decompile(File jar, File outputDir);
}
