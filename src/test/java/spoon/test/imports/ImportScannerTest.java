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
			Set<String> computedImports = new HashSet<>();

			for (CtReference computedImport : computedRefImports) {
				String computedImportStr = printerHelper.printImport(computedImport).replace("import ", "").replace("static ", "").trim();
				if (!"".equals(computedImportStr)) {
					computedImports.add(computedImportStr);
				}
			}

			List<String> imports = getImportsFromSourceCode(ctType.getPosition().getCompilationUnit().getOriginalSourceCode());

			for (String computedImport : computedImports) {
				if (!imports.contains(computedImport)) {
					if (!unusedImports.containsKey(ctType)) {
						unusedImports.put(ctType, new ArrayList<>());
					}
					unusedImports.get(ctType).add(computedImport);
				}
			}

			for (String anImport : imports) {
				if (!computedImports.contains(anImport)) {
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

	private List<String> getImportsFromSourceCode(String sourceCode) {
		List<String> imports = new ArrayList<>();
		String[] lines = sourceCode.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.startsWith("import ")) {
				line = line.substring(7, line.length() - 1);
				if (line.startsWith("static")) {
					line = line.substring(6);
				}
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
