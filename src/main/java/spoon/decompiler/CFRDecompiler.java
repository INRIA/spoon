package spoon.decompiler;

import java.io.File;
import org.benf.cfr.reader.Main;

public class CFRDecompiler implements Decompiler {
	@Override
	public void decompile(File jar, File outputDir) {
		Main.main(new String[]{jar.getAbsolutePath(), "--outputdir", outputDir.getAbsolutePath()});
	}
}
