package spoon.test.imports;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;
import spoon.reflect.visitor.MinimalImportScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.printer.ElementPrinterHelper;
import spoon.reflect.visitor.printer.PrinterHelper;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

/**
 * Created by gerard on 14/10/2014.
 */
public class ImportScannerTest {

	@Test
	public void testImportOnSpoon() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/main/java/spoon/");
		spoon.getEnvironment().setAutoImports(true);
		spoon.buildModel();

		PrinterHelper printer = new PrinterHelper(spoon.getEnvironment());
		ElementPrinterHelper printerHelper = new ElementPrinterHelper(printer, new DefaultJavaPrettyPrinter(spoon.getEnvironment()), spoon.getEnvironment());

		Map<CtType, List<String>> missingImports = new HashMap<>();
		Map<CtType, List<String>> unusedImports = new HashMap<>();

		for (CtType<?> ctType : spoon.getModel().getAllTypes()) {
			if (!ctType.isTopLevel()) {
				continue;
			}
			ImportScanner importContext = new ImportScannerImpl();
			importContext.computeImports(ctType);

			Collection<CtReference> computedRefImports = importContext.getAllImports();
			Set<String> computedTypeImports = new HashSet<>();
			Set<String> computedStaticImports = new HashSet<>();

			for (CtReference computedImport : computedRefImports) {
				String computedImportStr = printerHelper.printImport(computedImport).replace("import ", "").trim();

				if (computedImportStr.contains("static ")) {
					computedStaticImports.add(computedImportStr.replace("static ", "").trim());
				} else if (!"".equals(computedImportStr)) {
					computedTypeImports.add(computedImportStr);
				}
			}

			List<String> typeImports = getTypeImportsFromSourceCode(ctType.getPosition().getCompilationUnit().getOriginalSourceCode());
			List<String> staticImports = getStaticImportsFromSourceCode(ctType.getPosition().getCompilationUnit().getOriginalSourceCode());

			for (String computedImport : computedTypeImports) {
				if (!typeImports.contains(computedImport) && !isTypePresentInStaticImports(computedImport, staticImports)) {
					if (!unusedImports.containsKey(ctType)) {
						unusedImports.put(ctType, new ArrayList<>());
					}
					unusedImports.get(ctType).add(computedImport);
				}
			}

			for (String computedImport : computedStaticImports) {
				String typeOfStatic = computedImport.substring(0, computedImport.lastIndexOf("."));
				if (!staticImports.contains(computedImport) && !typeImports.contains(typeOfStatic)) {
					if (!unusedImports.containsKey(ctType)) {
						unusedImports.put(ctType, new ArrayList<>());
					}
					unusedImports.get(ctType).add(computedImport);
				}
			}

			for (String anImport : typeImports) {
				if (!computedTypeImports.contains(anImport)) {
					if (!missingImports.containsKey(ctType)) {
						missingImports.put(ctType, new ArrayList<>());
					}
					missingImports.get(ctType).add(anImport);
				}
			}

			for (String anImport : staticImports) {
				String typeOfStatic = anImport.substring(0, anImport.lastIndexOf("."));
				if (!computedStaticImports.contains(anImport) && !computedTypeImports.contains(typeOfStatic)) {
					if (!missingImports.containsKey(ctType)) {
						missingImports.put(ctType, new ArrayList<>());
					}
					missingImports.get(ctType).add(anImport);
				}
			}
		}

		if (!missingImports.isEmpty() || !unusedImports.isEmpty()) {
			int countUnusedImports = 0;
			for (List<String> imports : unusedImports.values()) {
				countUnusedImports += imports.size();
			}
			int countMissingImports = 0;
			for (List<String> imports : missingImports.values()) {
				countMissingImports += imports.size();
			}
			System.err.println("Import scanner imports " + countUnusedImports + " unused imports and misses " + countMissingImports + " imports");

			Set<CtType> keys = new HashSet<>(unusedImports.keySet());
			keys.addAll(missingImports.keySet());

			for (CtType type : keys) {
				System.err.println(type.getQualifiedName());
				if (missingImports.containsKey(type)) {
					List<String> imports = missingImports.get(type);
					for (String anImport : imports) {
						System.err.println("\t" + anImport + " missing");
					}
				}
				if (unusedImports.containsKey(type)) {
					List<String> imports = unusedImports.get(type);
					for (String anImport : imports) {
						System.err.println("\t" + anImport + " unused");
					}
				}
			}

			assertEquals("Import scanner imports " + countUnusedImports + " unused imports and misses " + countMissingImports + " imports",
					0, countMissingImports+countUnusedImports);
		}
	}

	private boolean isTypePresentInStaticImports(String type, Collection<String> staticImports) {
		for (String s : staticImports) {
			if (s.startsWith(type)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getStaticImportsFromSourceCode(String sourceCode) {
		List<String> imports = new ArrayList<>();
		String[] lines = sourceCode.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.startsWith("import static ")) {
				line = line.substring(13, line.length() - 1);
				imports.add(line.trim());
			}
		}
		return imports;
	}

	private List<String> getTypeImportsFromSourceCode(String sourceCode) {
		List<String> imports = new ArrayList<>();
		String[] lines = sourceCode.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.startsWith("import ") && !line.contains(" static ")) {
				line = line.substring(7, line.length() - 1);
				imports.add(line.trim());
			}
		}
		return imports;
	}

	@Test
	public void testComputeMinimalImportsInClass() throws Exception {
		String packageName = "spoon.test";
		String className = "SampleImportClass";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = build(packageName, className).getFactory();
		CtType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new MinimalImportScanner();
		importContext.computeImports(theClass);
		Collection<CtReference> imports = importContext.getAllImports();

		assertTrue(imports.isEmpty());
	}

	@Test
	public void testComputeImportsInClass() throws Exception {
		String packageName = "spoon.test";
		String className = "SampleImportClass";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = build(packageName, className).getFactory();
		CtType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new ImportScannerImpl();
		importContext.computeImports(theClass);
		Collection<CtReference> imports = importContext.getAllImports();

		// java.lang are also computed
		assertEquals(4, imports.size());
	}

	@Test
	public void testComputeImportsInClassWithSameName() throws Exception {
		String packageName = "spoon.test.imports.testclasses2";
		String className = "ImportSameName";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = build(packageName, className).getFactory();
		CtType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new ImportScannerImpl();
		importContext.computeImports(theClass);
		Collection<CtReference> imports = importContext.getAllImports();

		assertEquals(0, imports.size());
	}


	@Test
	public void testMultiCatchImport() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/imports/testclasses/MultiCatch.java"));

		compiler.build();

		final List<CtClass<?>> classes = Query.getElements(factory, new NameFilter<CtClass<?>>("MultiCatch"));

		ImportScanner importScanner = new ImportScannerImpl();
		importScanner.computeImports(classes.get(0));
		// as ArithmeticException come from java.lang it is not imported anymore
		//assertTrue( importScanner.isImported( factory.Type().createReference( ArithmeticException.class ) ));
		assertTrue( importScanner.isImported( factory.Type().createReference( AccessControlException.class ) ));
	}

	@Test
	public void testTargetTypeNull() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		CtFieldReference fieldRef = factory.createFieldReference();
		fieldRef.setStatic(true);

		ImportScanner importScanner = new MinimalImportScanner();
		importScanner.computeImports(fieldRef);

		Collection<CtReference> imports = importScanner.getAllImports();

		assertEquals(0, imports.size());
	}
}
