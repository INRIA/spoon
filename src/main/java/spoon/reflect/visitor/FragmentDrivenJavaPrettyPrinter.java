package spoon.reflect.visitor;

import java.util.ArrayList;
import java.util.List;

import spoon.processing.Environment;
import spoon.reflect.declaration.CompilationUnit;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.SourceCodeFragment;

public class FragmentDrivenJavaPrettyPrinter implements JavaPrettyPrinter {

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

	public FragmentDrivenJavaPrettyPrinter(Environment env,
			CompilationUnit compilationUnit) throws Exception {
		this.env = env;
		this.compilationUnit = compilationUnit;
	}

	public void calculate(List<CtSimpleType<?>> types) {
		// do nothing
	}

}
