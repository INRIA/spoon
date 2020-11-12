/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import spoon.architecture.report.IReportPrinter;
import spoon.reflect.CtModel;
/**
 * This defines the main controller for architecture checking.
 * The method {@code #runChecks()} and {@code #runChecks(String)} define the start for architecture checking.
 * For creation use either {@code #createChecker()} or {@code #createCheckerWithoutDefault()} for simple use cases or {@code SpoonArchitecturalChecker.Builder} for more settings.
 * <p>
 * Architecture checking consists of 3 phases
 * <ul>
 * <li> Building meta model
 * <li> Gathering checks
 * <li> Invoking checks and generating report
 * </ul>
 * For the first phase the {@link IModelBuilder} is used.
 * The {@code IRunner} and {@code IReportPrinter} handle the second and third phase.
 * <p>
 * This class is has the capability for multiple printer. This guarantees even in case of error the method {@code IReportPrinter#finishPrinting()} is called.
 */
public class SpoonArchitecturalChecker implements ISpoonArchitecturalChecker {



	private IModelBuilder<CtModel> builder;
	private List<IReportPrinter> printers;
	private IRunner<CtModel> runner;
	private SpoonArchitecturalChecker() {
		builder = new ModelBuilder();
		//use as default a NOP printer
		printers = new ArrayList<>();
		printers.add(new IReportPrinter() { });
		runner = new SpoonRunner(builder);
	}

	/**
	 * Creates an architectural checker with default settings. Default settings are:
	 * <ul>
	 * <li> A NOP-Printer
	 * <li> A default src and test path
	 * <li> A default runner, which uses the test path as lookup path.
	 * </ul>
	 * @return  an properly initialized architectural checker
	 */
	public static SpoonArchitecturalChecker createChecker() {
		SpoonArchitecturalChecker checker = new SpoonArchitecturalChecker();
		checker.builder.insertInputPath(DefaultPath.SOURCE.getIdentifier(), DefaultPath.SOURCE.getPath());
		checker.builder.insertInputPath(DefaultPath.TEST.getIdentifier(), DefaultPath.TEST.getPath());
		return checker;
	}

	public static SpoonArchitecturalChecker createCheckerWithoutDefault() {
		return new SpoonArchitecturalChecker();
	}

	/**
	 * This defines an builder for {@code SpoonArchitecturalChecker}. It has multiple methods for settings.
	 * You can call an setter multiple times.
	 */
	public static class Builder {

		private SpoonArchitecturalChecker checker;
		public Builder() {
			checker = new SpoonArchitecturalChecker();
		}

		public Builder addReportPrinter(IReportPrinter printer) {
			checker.printers.add(printer);
			return this;
		}
		public Builder addModelBuilder(IModelBuilder<CtModel> builder) {
			checker.builder = builder;
			return this;
		}
		public Builder addRunner(IRunner<CtModel> runner) {
			checker.runner = runner;
			return this;
		}
		public Builder useDefaultPath() {
			checker.builder.insertInputPath(DefaultPath.SOURCE.getIdentifier(), DefaultPath.SOURCE.getPath());
			checker.builder.insertInputPath(DefaultPath.TEST.getIdentifier(), DefaultPath.TEST.getPath());
			return this;
		}
		public SpoonArchitecturalChecker build() {
			return checker;
		}
	}


	@Override
	public void runChecks() {
		runChecks(DefaultPath.TEST.getIdentifier());
	}


	@Override
	public void runChecks(String testPath) {
		printers.forEach(printer -> printer.startPrinting());
		Iterable<Method> architectureChecks = runner.selectMethods(builder.getModelWithIdentifier(testPath.toLowerCase()));
		try {
			for (Method method : architectureChecks) {
				printers.forEach(printer -> printer.beforeMethod(method));
				runner.invokeMethod(method);
				printers.forEach(printer -> printer.afterMethod(method));
			}
		} catch (Exception e) {
			// guarantees the finish printing method gets called in case of error
			printers.forEach(printer -> printer.finishPrinting());
			throw e;
		}
		printers.forEach(printer -> printer.finishPrinting());
	}

	/**
	 * This enum defines default paths for model building. Each path consists of an identifier and a path.
	 * The identifier is lowercase only.
	 */
	private enum DefaultPath {
		SOURCE("srcmodel", "src/main/java"),
		TEST("testmodel", "src/test/java");

		private String path;
		private String identifier;
		DefaultPath(String identifier, String path) {
			this.path = path;
			this.identifier = identifier;
		}
		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @return the identifier for this. The identifier consists of lower case characters only.
		 */
		public String getIdentifier() {
			return identifier;
		}
	}
}
