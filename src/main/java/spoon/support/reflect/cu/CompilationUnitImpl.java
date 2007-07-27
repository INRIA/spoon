package spoon.support.reflect.cu;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.processing.FactoryAccessor;
import spoon.reflect.Factory;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.Import;
import spoon.reflect.cu.SourceCodeFragment;
import spoon.reflect.declaration.CtSimpleType;

public class CompilationUnitImpl implements CompilationUnit, FactoryAccessor {

	Factory factory;

	List<CtSimpleType<?>> declaredTypes = new ArrayList<CtSimpleType<?>>();

	public List<CtSimpleType<?>> getDeclaredTypes() {
		return declaredTypes;
	}

	File file;

	public File getFile() {
		return file;
	}

	public CtSimpleType<?> getMainType() {
		if (getFile() == null)
			return getDeclaredTypes().get(0);
		for (CtSimpleType<?> t : getDeclaredTypes()) {
			String name = getFile().getName();
			name = name.substring(0, name.lastIndexOf("."));
			if (t.getSimpleName().equals(name))
				return t;
		}
		throw new RuntimeException("inconsistent compilation unit: '" + file
				+ "': declared types are " + getDeclaredTypes());
	}

	public void setDeclaredTypes(List<CtSimpleType<?>> types) {
		this.declaredTypes = types;
	}

	public void setFile(File file) {
		this.file = file;
	}

	List<SourceCodeFragment> fragments;

	public void addSourceCodeFragment(SourceCodeFragment fragment) {
		if (fragments == null) {
			fragments = new ArrayList<SourceCodeFragment>();
		}
		int i = 0;
		for (SourceCodeFragment f : fragments) {
			if (fragment.position <= f.position) {
				break;
			}
			i++;
		}
		fragments.add(i, fragment);
	}

	public List<SourceCodeFragment> getSourceCodeFraments() {
		return fragments;
	}

	String originalSourceCode;

	public String getOriginalSourceCode() {
		try {
			if (originalSourceCode == null) {
				FileInputStream s = new FileInputStream(getFile());
				byte[] elementBytes = new byte[s.available()];
				s.read(elementBytes);
				s.close();
				originalSourceCode = new String(elementBytes);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return originalSourceCode;
	}

	public int beginOfLineIndex(int index) {
		int cur = index;
		while (cur >= 0 && getOriginalSourceCode().charAt(cur) != '\n') {
			cur--;
		}
		return cur + 1;
	}

	public int nextLineIndex(int index) {
		int cur = index;
		while (cur < getOriginalSourceCode().length()
				&& getOriginalSourceCode().charAt(cur) != '\n') {
			cur++;
		}
		return cur + 1;
	}

	public int getTabCount(int index) {
		int cur = index;
		int tabCount = 0;
		int whiteSpaceCount = 0;
		while (cur < getOriginalSourceCode().length()
				&& (getOriginalSourceCode().charAt(cur) == ' ' || getOriginalSourceCode()
						.charAt(cur) == '\t')) {
			if (getOriginalSourceCode().charAt(cur) == '\t')
				tabCount++;
			if (getOriginalSourceCode().charAt(cur) == ' ')
				whiteSpaceCount++;
			cur++;
		}
		tabCount += whiteSpaceCount
				/ getFactory().getEnvironment().getTabulationSize();
		return tabCount;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	boolean autoImport=true;
	
	Set<Import> manualImports=new HashSet<Import>();

	public boolean isAutoImport() {
		return autoImport;
	}

	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}

	public Set<Import> getManualImports() {
		return manualImports;
	}

	public void setManualImports(Set<Import> manualImports) {
		this.manualImports = manualImports;
	}
	
	
	
}
