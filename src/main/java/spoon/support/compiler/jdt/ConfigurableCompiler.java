package spoon.support.compiler.jdt;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.SpoonException;
import spoon.compiler.SpoonFile;

public class ConfigurableCompiler extends JDTBatchCompiler {
	protected CompilationUnit[] compilationUnits;

	public ConfigurableCompiler(JDTBasedSpoonCompiler p_jdtCompiler) {
		super(p_jdtCompiler);
	}

	public ConfigurableCompiler(JDTBasedSpoonCompiler p_jdtCompiler, OutputStream p_outWriter, OutputStream p_errWriter) {
		super(p_jdtCompiler, p_outWriter, p_errWriter);
	}

	@Override
	public CompilationUnit[] getCompilationUnits() {
		return compilationUnits;
	}

	public void setCompilationUnits(CompilationUnit[] compilationUnits) {
		this.compilationUnits = compilationUnits;
	}

	public void setInputFiles(List<SpoonFile> files) {
		List<CompilationUnit> culist = new ArrayList<>(files.size());
		for (SpoonFile f : files) {
			if (filesToBeIgnored.contains(f.getPath())) {
				continue;
			}
			try {
				String fName = "";
				if (f.isActualFile()) {
					fName = f.getPath();
				} else {
					fName = f.getName();
				}
				culist.add(new CompilationUnit(IOUtils.toCharArray(f
						.getContent(), jdtCompiler.encoding), fName, null));
			} catch (Exception e) {
				throw new SpoonException(e);
			}
		}
		this.compilationUnits = culist.toArray(new CompilationUnit[0]);
	}
}
