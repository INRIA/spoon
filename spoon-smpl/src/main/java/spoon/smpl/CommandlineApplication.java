/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.SubformulaCollector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CommandLineApplication offers a command line interface to some of the features of spoon-smpl.
 */
public class CommandlineApplication {
	/**
	 * Hide utility class constructor.
	 */
	private CommandlineApplication() { }

	/**
	 * Read all contents of a plain text file.
	 *
	 * @param path     Path to file
	 * @param encoding Character encoding of file
	 * @return Contents of file
	 * @throws IOException on IO errors
	 * @author https://stackoverflow.com/a/326440
	 */
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * Print usage.
	 */
	private static void usage() {
		System.out.println("usage:");
		System.out.println("smplcli ACTION [ARG [ARG ..]]");
		System.out.println();
		System.out.println("    ACTIONs:");
		System.out.println("        patch        apply SmPL patch");
		System.out.println("                     requires --smpl-file and --java-file");
		System.out.println();
		System.out.println("        check        run model checker");
		System.out.println("                     requires --smpl-file and --java-file");
		System.out.println();
		System.out.println("        checksub     run model checker on every subformula");
		System.out.println("                     requires --smpl-file and --java-file");
		System.out.println();
		System.out.println("        rewrite      rewrite SmPL input");
		System.out.println("                     requires --smpl-file");
		System.out.println();
		System.out.println("        compile      compile SmPL input");
		System.out.println("                     requires --smpl-file");
		System.out.println();
		System.out.println("        ctl          compile and print CTL formula");
		System.out.println("                     requires --smpl-file");
		System.out.println();
		System.out.println("    ARGs:");
		System.out.println("        --smpl-file FILENAME");
		System.out.println("        --java-file FILENAME");
		System.out.println();
	}

	/**
	 * Enumeration of possible actions.
	 */
	enum Action {
		/**
		 * Run the model checker.
		 */
		CHECK,

		/**
		 * Run the model checker on every sub-formula.
		 */
		CHECKSUB,

		/**
		 * Have the SmPL parser rewrite an SmPL patch into the spoon-smpl Java SmPL DSL.
		 */
		REWRITE,

		/**
		 * Compile an SmPL patch and print the produced SmPLRule.
		 */
		COMPILE,

		/**
		 * Apply a given SmPL patch to each method in the single class defined in a given Java source file.
		 */
		PATCH,

		/**
		 * Compile an SmPL patch and pretty-print the resulting Formula.
		 */
		CTL
	}

	/**
	 * Enumeration of possible states for the argument parser.
	 */
	enum ArgumentState {
		/**
		 * Initial state.
		 */
		BASE,

		/**
		 * Failure state.
		 */
		FAIL,

		/**
		 * Expecting action argument.
		 */
		ACTION,

		/**
		 * Expecting SmPL patch filename.
		 */
		FILENAME_SMPL,

		/**
		 * Expecting Java source filename.
		 */
		FILENAME_JAVA
	}

	/**
	 * Main entry point.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		Action action = null;
		String smplFilename = null;
		String javaFilename = null;

		ArgumentState argumentState = ArgumentState.ACTION;

		for (String arg : args) {
			if (argumentState == ArgumentState.FAIL) {
				break;
			}

			switch (argumentState) {
				case ACTION:
					if (arg.equals("check")) {
						action = Action.CHECK;
						argumentState = ArgumentState.BASE;
					} else if (arg.equals("checksub")) {
						action = Action.CHECKSUB;
						argumentState = ArgumentState.BASE;
					} else if (arg.equals("rewrite")) {
						action = Action.REWRITE;
						argumentState = ArgumentState.BASE;
					} else if (arg.equals("compile")) {
						action = Action.COMPILE;
						argumentState = ArgumentState.BASE;
					} else if (arg.equals("patch")) {
						action = Action.PATCH;
						argumentState = ArgumentState.BASE;
					} else if (arg.equals("ctl")) {
						action = Action.CTL;
						argumentState = ArgumentState.BASE;
					} else {
						argumentState = ArgumentState.FAIL;
					}
				case BASE:
					if (arg.equals("--smpl-file")) {
						argumentState = ArgumentState.FILENAME_SMPL;
					} else if (arg.equals("--java-file")) {
						argumentState = ArgumentState.FILENAME_JAVA;
					}
					break;

				case FILENAME_SMPL:
					smplFilename = arg;
					argumentState = ArgumentState.BASE;
					break;

				case FILENAME_JAVA:
					javaFilename = arg;
					argumentState = ArgumentState.BASE;
					break;

				default:
					break;
			}
		}

		if (action == null || argumentState != ArgumentState.BASE) {
			usage();

			System.exit(args.length > 0 ? 1 : 0);
		}

		if (action == Action.CHECK || action == Action.CHECKSUB || action == Action.PATCH) {
			if (smplFilename != null && javaFilename != null) {
				try {
					SmPLRule smplRule = SmPLParser.parse(readFile(smplFilename, StandardCharsets.UTF_8));

					CtClass<?> inputClass = SpoonJavaParser.parseClass(readFile(javaFilename, StandardCharsets.UTF_8));

					for (CtMethod<?> method : inputClass.getMethods()) {
						SmPLMethodCFG cfg = new SmPLMethodCFG(method);

						CFGModel model = new CFGModel(cfg);
						ModelChecker modelChecker = new ModelChecker(model);

						if (action == Action.CHECKSUB) {
							System.out.println(method.getSimpleName());
							System.out.println(model);

							SubformulaCollector subformulas = new SubformulaCollector();
							smplRule.getFormula().accept(subformulas);

							for (Formula phi : subformulas.getResult()) {
								phi.accept(modelChecker);

								System.out.println(DebugUtils.prettifyFormula(phi));
								System.out.print("  ");
								System.out.println(modelChecker.getResult());
								System.out.println();
							}
						} else {
							smplRule.getFormula().accept(modelChecker);

							if (action == Action.CHECK) {
								System.out.println(method.getSimpleName());
								System.out.println(model);
								System.out.println(modelChecker.getResult());
							} else if (action == Action.PATCH) {
								ModelChecker.ResultSet results = modelChecker.getResult();
								Transformer.transform(model, results.getAllWitnesses());

								if (results.size() > 0 && smplRule.getMethodsAdded().size() > 0) {
									Transformer.copyAddedMethods(model, smplRule);
								}

								model.getCfg().restoreUnsupportedElements();
							}
						}
					}

					if (action == Action.PATCH) {
						System.out.println(inputClass);
					}

					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				System.out.println("check: Missing file name");
				System.out.println();
				usage();
				System.exit(1);
			}
		} else if (action == Action.REWRITE) {
			if (smplFilename != null) {
				try {
					System.out.println(SmPLParser.rewrite(readFile(smplFilename, StandardCharsets.UTF_8)));
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				System.out.println("rewrite: Missing file name");
				System.out.println();
				usage();
				System.exit(1);
			}
		} else if (action == Action.CTL || action == Action.COMPILE) {
			if (smplFilename != null) {
				try {
					SmPLRule rule = SmPLParser.parse(readFile(smplFilename, StandardCharsets.UTF_8));

					if (action == Action.CTL) {
						System.out.println(DebugUtils.prettifyFormula(rule.getFormula()));
					} else if (action == Action.COMPILE) {
						System.out.println(rule);
					}

					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				System.out.println("ctl: Missing file name");
				System.out.println();
				usage();
				System.exit(1);
			}
		}
	}
}
