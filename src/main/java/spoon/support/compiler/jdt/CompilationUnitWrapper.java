package spoon.support.compiler.jdt;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.Launcher;

class CompilationUnitWrapper extends CompilationUnit {

	private final JDTBasedSpoonCompiler jdtCompiler;

	public CompilationUnitWrapper(JDTBasedSpoonCompiler jdtCompiler, CompilationUnit wrappedUnit) {
		super(null, wrappedUnit.fileName != null ? new String(
				wrappedUnit.fileName) : null, null,
				wrappedUnit.destinationPath != null ? new String(
						wrappedUnit.destinationPath) : null, false);
		this.jdtCompiler = jdtCompiler;
	}

	@Override
	public char[] getContents() {
		String s = new String(getFileName());
		if (this.jdtCompiler.factory != null
				&& this.jdtCompiler.factory.CompilationUnit().getMap().containsKey(s)) {
			try {
				if (this.jdtCompiler.loadedContent.containsKey(s)) {
					return this.jdtCompiler.loadedContent.get(s);
				} else {
					char[] content = IOUtils
							.toCharArray(this.jdtCompiler.getCompilationUnitInputStream(s));
					this.jdtCompiler.loadedContent.put(s, content);
					return content;
				}
			} catch (Exception e) {
				Launcher.logger.error(e.getMessage(), e);
			}
		}
		return super.getContents();
	}

}