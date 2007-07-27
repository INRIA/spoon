package spoon.reflect.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.processing.Environment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourceCodeFragment;
import spoon.reflect.declaration.CtSimpleType;

/**
 * This pretty printer is used when Spoon is use with the "fragments" mode. This
 * pretty printer simply prints out the original source code of the compilation
 * unit and replaces some fragments of code as defined by the
 * {@link CompilationUnit#getSourceCodeFraments()} method.
 */

public class FragmentDrivenJavaPrettyPrinter implements PrettyPrinter {

	private void addjustFragments(List<SourceCodeFragment> fragments) {
		int i = 0;
		for (SourceCodeFragment f : fragments) {
			for (int j = i + 1; j < fragments.size(); j++) {
				fragments.get(j).position += f.code.length()
						- f.replacementLength;
			}
			i++;
		}
	}

	Map<Integer, Integer> lineNumberMapping = new HashMap<Integer, Integer>();

	public StringBuffer getResult() {
		StringBuffer sb = new StringBuffer();
		sb.append(compilationUnit.getOriginalSourceCode());
		List<SourceCodeFragment> fragments = new ArrayList<SourceCodeFragment>();
		if (compilationUnit.getSourceCodeFraments() != null) {
			for (SourceCodeFragment f : compilationUnit.getSourceCodeFraments()) {
				fragments.add(new SourceCodeFragment(f.position, f.code,
						f.replacementLength));
			}
		}
		addjustFragments(fragments);
		for (SourceCodeFragment f : fragments) {
			sb.replace(f.position, f.position + f.replacementLength, f.code);
			// sb.insert(f.position, f.code);
		}
		return sb;
	}

	public String getPackageDeclaration() {
		return "";
	}

	Environment env;

	CompilationUnit compilationUnit;

	/**
	 * Creates a new fragment-driven pretty printer for the given compilation
	 * unit.
	 */
	public FragmentDrivenJavaPrettyPrinter(Environment env,
			CompilationUnit compilationUnit) throws Exception {
		this.env = env;
		this.compilationUnit = compilationUnit;
	}

	public void calculate(CompilationUnit originalCompilationUnit,
			List<CtSimpleType<?>> types) {
		// do nothing
	}

	public Map<Integer, Integer> getLineNumberMapping() {
		return lineNumberMapping;
	}

}
