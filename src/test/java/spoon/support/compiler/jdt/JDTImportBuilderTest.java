package spoon.support.compiler.jdt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import spoon.compiler.Environment;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.InterfaceFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtAbstractImportVisitor;
import spoon.reflect.visitor.CtImportVisitor;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.cu.CompilationUnitImpl;

public class JDTImportBuilderTest {

	@Mock
	private Factory factory;

	@Mock
	private ICompilationUnit compilationUnit;

	@Mock
	private InterfaceFactory interfaceFactory;

	private Environment environment = new StandardEnvironment();

	private CompilationUnit spoonUnit = new CompilationUnitImpl();

	private List<ImportReference> imports = new ArrayList<>();

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);

		setupMockFactories();

		when(compilationUnit.getContents()).thenReturn(new char[0]);
	}

	@Test
	public void findunresolvedImport() {
		// setup import
		CompilationUnitDeclaration declarationUnit = setupImportStatement("some.unknown.package.RandomType");

		// collect imports
		new JDTImportBuilder(declarationUnit, factory).build();

		// verify import not resolved
		assertUnresolvedImport(spoonUnit.getImports().get(0), "some.unknown.package.RandomType");
	}

	@Test
	public void classNotFoundIfNotInSameClassloader() throws Exception {
		// setup JAR with imported types in separate classloader
		setupEnvironmentWithCustomClassLoader("./src/test/resources/folderWithJar/jarFile.jar");
		CompilationUnitDeclaration declarationUnit = setupImportStatement("spoon.test.reference.ReferencedClass");

		// collect imports
		new JDTImportBuilder(declarationUnit, factory).build();

		// verify import resolved in classpath
		assertResolvedImport(spoonUnit.getImports().get(0), "spoon.test.reference.ReferencedClass");
	}

	private CompilationUnitDeclaration setupImportStatement(String importName) {
		imports.add(createImport(importName, false));
		CompilationUnitDeclaration declarationUnit = setupImports(imports);
		return declarationUnit;
	}

	private void setupEnvironmentWithCustomClassLoader(String jarFile) throws MalformedURLException {
		File jarWithImportedType = new File(jarFile);
		ClassLoader customLoader = new URLClassLoader(new URL[] { jarWithImportedType.toURI().toURL() },
				JDTImportBuilder.class.getClassLoader());
		environment.setInputClassLoader(customLoader);
	}

	private void assertResolvedImport(CtImport ctImport, String theImport) {
		CtImportVisitor visitor = new CtAbstractImportVisitor() {
			@Override
			public <T> void visitTypeImport(CtTypeReference<T> typeReference) {
				assertEquals(theImport, typeReference.getQualifiedName());
			}

			@Override
			public <T> void visitUnresolvedImport(CtUnresolvedImport unresolvedImport) {
				fail("expecting resolved import, found unresolved import instead "
						+ unresolvedImport.getUnresolvedReference());
			}
		};
		ctImport.accept(visitor);
	}

	private void assertUnresolvedImport(CtImport ctImport, String theImport) {
		CtImportVisitor visitor = new CtAbstractImportVisitor() {
			@Override
			public <T> void visitTypeImport(CtTypeReference<T> typeReference) {
				fail("expecting unresolved import, found resolved import instead " + typeReference.getQualifiedName());
			}

			@Override
			public <T> void visitUnresolvedImport(CtUnresolvedImport unresolvedImport) {
				assertEquals(theImport, unresolvedImport.getUnresolvedReference());
			}
		};
		ctImport.accept(visitor);
	}

	private ImportReference createImport(String name, Boolean isStatic) {
		ImportReference ref = mock(ImportReference.class);
		when(ref.isStatic()).thenReturn(isStatic);
		when(ref.toString()).thenReturn(name);
		return ref;
	}

	private CompilationUnitDeclaration setupImports(List<ImportReference> imports) {

		CompilationResult compilationResult = new CompilationResult("Foo.java".toCharArray(), 0, 10, 10);
		compilationResult.compilationUnit = compilationUnit;
		CompilationUnitDeclaration declarationUnit = new CompilationUnitDeclaration(mock(ProblemReporter.class),
				compilationResult, 10);
		declarationUnit.imports = imports.toArray(new ImportReference[0]);
		return declarationUnit;
	}

	private void setupMockFactories() {
		CompilationUnitFactory compilationUnitFactory = Mockito.mock(CompilationUnitFactory.class);
		when(compilationUnitFactory.getOrCreate(Mockito.anyString())).thenReturn(spoonUnit);
		when(factory.CompilationUnit()).thenReturn(compilationUnitFactory);
		when(factory.Type()).thenReturn(new TypeFactory());
		when(factory.Interface()).thenReturn(interfaceFactory);
		when(factory.getEnvironment()).thenReturn(environment);
		when(factory.Core()).thenReturn(mock(CoreFactory.class));
	}

}
