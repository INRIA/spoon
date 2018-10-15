package spoon.decompiler;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;

import java.io.File;

public class FernflowerDecompiler implements Decompiler {

	File outputDir;

	public FernflowerDecompiler(File outputDir) {
		this.outputDir = outputDir;
	}

	@Override
	public void decompile(String jarPath) {
		ConsoleDecompiler.main(new String[]{jarPath, outputDir.getPath()});
	}
}
