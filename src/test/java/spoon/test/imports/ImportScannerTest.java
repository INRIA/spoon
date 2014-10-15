package spoon.test.imports;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;
import spoon.test.TestUtils;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gerard on 14/10/2014.
 */
public class ImportScannerTest {

	@Test
	public void testComputeImportsInClass() throws Exception {
		String packageName = "spoon.test";
		String className = "SampleImportClass";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = TestUtils.build(packageName, className).getFactory();
		CtSimpleType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importContext
				.computeImports(theClass);

		assertEquals(2, imports.size());
	}
}
