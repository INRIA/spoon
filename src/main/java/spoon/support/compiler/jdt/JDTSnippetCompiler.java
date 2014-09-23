package spoon.support.compiler.jdt;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.SnippetCompilationError;
import spoon.support.compiler.VirtualFile;

public class JDTSnippetCompiler extends JDTBasedSpoonCompiler {

	public JDTSnippetCompiler(Factory factory, String contents) {
		super(factory);
		addInputSource(new VirtualFile(contents, ""));
	}

	@Override
	public boolean build() throws Exception {
		if (factory == null) {
			throw new Exception("Factory not initialized");
		}

		boolean srcSuccess;
		factory.getEnvironment().debugMessage(
				"compiling sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();
		srcSuccess = buildSources();
		reportProblems(factory.getEnvironment());
		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		t = System.currentTimeMillis();
		return srcSuccess;
	}

	@Override
	protected boolean buildSources() throws Exception {
		if (sources.getAllJavaFiles().isEmpty())
			return true;
		// long t=System.currentTimeMillis();
		// Build input
		JDTBatchCompiler batchCompiler = createBatchCompiler();
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		if (encoding != null) {
			args.add("-encoding");
			args.add(encoding);
		}
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		// args.add("-d");
		// args.add("none");

		if (getSourceClasspath() != null) {
			args.add("-cp");
			args.add(computeJdtClassPath());
		} else {
			ClassLoader currentClassLoader = Thread.currentThread()
					.getContextClassLoader();// ClassLoader.getSystemClassLoader();
			if (currentClassLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
				if (urls != null && urls.length > 0) {
					String classpath = ".";
					for (URL url : urls) {
						classpath += File.pathSeparator + url.getFile();
					}
					if (classpath != null) {
						args.add("-cp");
						args.add(classpath);
					}
				}
			}
		}
		// args.add("-nowarn");
		// Set<String> paths = new HashSet<String>();
		// for (SpoonFile file : sources.getAllJavaFiles()) {
		// // We can not use file.getPath() because of in-memory code or files
		// // within archives
		// paths.add(file.getParent().getPath());
		// }
		// args.addAll(paths);
		// args.addAll(sources.getRootJavaPaths());

		File f = createTmpJavaFile(new File("."));
		args.add(f.getPath());
		getFactory().getEnvironment().debugMessage("build args: " + args);

		try {
			batchCompiler.configure(args.toArray(new String[0]));
		} catch (Exception e) {
			Launcher.logger.error("build args: " + args);
			Launcher.logger.error("sources: " + sources.getAllJavaFiles());
			Launcher.logger.error(e.getMessage(), e);
			throw e;
		}
		CompilationUnitDeclaration[] units = batchCompiler.getUnits(sources
				.getAllJavaFiles());

		if(f!=null && f.exists()) {
			f.delete();
		}
		
		// here we build the model
		JDTTreeBuilder builder = new JDTTreeBuilder(factory);
		for (CompilationUnitDeclaration unit : units) {
			unit.traverse(builder, unit.scope);
		}

		return probs.size() == 0;
	}

	@Override
	protected void report(Environment environment, CategorizedProblem problem) {
		throw new SnippetCompilationError(problem.getMessage() + "at line "
				+ problem.getSourceLineNumber());

	}

}
