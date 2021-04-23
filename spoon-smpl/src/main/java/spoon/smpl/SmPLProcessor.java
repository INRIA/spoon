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

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtTypeMember;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: does it work on lambdas?
// TODO: is it really not possible to pass command line arguments to processors?
// TODO: use sniper for diffs

/**
 * SmPLProcessor is a Spoon Processor capable of batch application of a single SmPL patch to every executable block
 * (method, constructor, lambda) found in the input as scanned by spoon.Launcher.
 */
public class SmPLProcessor extends AbstractProcessor<CtExecutable<?>> {
	/**
	 * Workaround for not being able to pass arguments to Spoon processors when running spoon.Launcher -p processor.
	 * <p>
	 * Supported commandline arguments are those of spoon.Launcher plus the following, which must be specified
	 * preceding any arguments intended to reach spoon.Launcher:
	 * <p>
	 * --with-diff-command COMMAND,  enables diffing of patched executable blocks
	 * <p>
	 * where COMMAND is a shell command that will be executed via Runtime.exec
	 * and should be structured such as "bash -c \"diff -u {a} {b}\""
	 * where {a} and {b} will be substituted for the full paths of files
	 * containing the source code of the unpatched executable block ({a})
	 * and the patched executable block ({b}).
	 * <p>
	 * --with-smpl-file FILENAME     where FILENAME specifies full path to filename of SmPL patch
	 * <p>
	 * <p>
	 * Example commandline:
	 * <p>
	 * java -classpath [...] spoon.smpl.SmPLProcessor --with-diff-command "bash -c \"diff -u {a} {b}\""  \
	 * --with-smpl-file /path/to/patch.smpl               \
	 * -i /path/to/sources                                \
	 * -o /path/to/spooned/output                         \
	 * --with-imports                                     \
	 * -p spoon.smpl.SmPLProcessor
	 *
	 * @param args Commandline arguments
	 */
	public static void main(String[] args) {
		totalTimer.start();

		// Pure function to reverse a list of strings
		Function<List<String>, List<String>> reverse = xs -> {
			List<String> ys = new ArrayList<>(xs);
			Collections.reverse(ys);
			return ys;
		};

		Stack<String> argStack = new Stack<>();
		argStack.addAll(reverse.apply(Arrays.asList(args)));

		boolean doneProcessingArgs = false;

		while (!doneProcessingArgs) {
			String arg = argStack.pop();

			switch (arg) {
				case "--with-diff-command":
					diffCommand = shellSplit(argStack.pop());
					break;

				case "--with-smpl-file":
					setSmPLRule(loadPatchFile(argStack.pop()));
					break;

				default:
					argStack.push(arg);
					doneProcessingArgs = true;
					break;
			}
		}

		// Call spoon.Launcher with remaining arguments
		Launcher.main(reverse.apply(new ArrayList<>(argStack)).toArray(new String[0]));

		if (diffCommand != null) {
			procTimer.start();
			printDiffs();
			procTimer.stop();
		}

		totalTimer.stop();

		double totaltime = totalTimer.getAccumulatedTime() / 1E9;
		double proctime = procTimer.getAccumulatedTime() / 1E9;
		double greptime = grepTimer.getAccumulatedTime() / 1E9;
		double patchtime = patchTimer.getAccumulatedTime() / 1E9;

		System.out.println("grepCounter: " + grepCounter);
		System.out.println("grepTimer: " + greptime + " s.");
		System.out.println("tryPatchCounter: " + tryPatchCounter);
		System.out.println("transformationCounter: " + transformationCounter);
		System.out.println("patchTimer: " + patchtime + " s.");
		System.out.println("totalTimer: " + totaltime + " s.");
		System.out.println("procTimer: " + proctime + " s.");
	}

	/**
	 * Load an SmPL patch from a given plain-text SmPL patch.
	 *
	 * @param smpl Plain-text SmPL patch
	 * @return SmPLRule instance
	 */
	public static SmPLRule loadPatchString(String smpl) {
		return SmPLParser.parse(smpl);
	}

	/**
	 * Load an SmPL patch from a given filename.
	 *
	 * @param filename Full path to filename containing SmPL patch
	 * @return SmPLRule instance
	 */
	public static SmPLRule loadPatchFile(String filename) {
		try {
			return loadPatchString(readFile(filename, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new IllegalStateException("Unable to read patch from file: " + e.getMessage());
		}
	}

	/**
	 * Set the SmPL rule that the processor should apply.
	 *
	 * @param rule SmPL rule to apply
	 */
	public static void setSmPLRule(SmPLRule rule) {
		smplRule = rule;
	}

	/**
	 * If a diff command has been set, use it to print the diffs of all transformed files.
	 */
	public static void printDiffs() {
		if (diffCommand == null) {
			return;
		}

		List<File> files = transformedCUs.keySet().stream().sorted().collect(Collectors.toList());

		for (File file : files) {

			String _package = transformedCUs.get(file).getDeclaredPackage().toString().replace(".", "/");

			String dirA = "/tmp/a/" + _package;
			String dirB = "/tmp/b/" + _package;

			String fileA = dirA + "/" + file.getName();
			String fileB = dirB + "/" + file.getName();

			new File(dirA).mkdirs();
			new File(dirB).mkdirs();

			tryWriteFile(fileA, originalSource.get(file));
			tryWriteFile(fileB, printCompilationUnit(transformedCUs.get(file)));

			String[] reifiedDiffCommand = Arrays.stream(diffCommand)
												.map(s -> s.replace("{a}", fileA)
															.replace("{b}", fileB))
												.toArray(String[]::new);

			try {
				Process process = Runtime.getRuntime().exec(reifiedDiffCommand);
				System.out.println(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main Processor implementation, applies the patch to any CtExecutables.
	 *
	 * @param ctExecutable Executable block to patch
	 */
	@Override
	public void process(CtExecutable<?> ctExecutable) {
		procTimer.start();

		try {
			if (smplRule == null) {
				throw new IllegalStateException("SmPL rule must be loaded before calling process()");
			}

			File sourceFile = ctExecutable.getPosition().getFile();

			if (sourceFile != null) {
				if (skippedFiles.contains(sourceFile)) {
					return;
				}

				if (!candidateFiles.contains(sourceFile)) {
					grepTimer.start();

					boolean isCandidateFile = smplRule.isPotentialMatch(sourceFile);

					grepTimer.stop();
					grepCounter += 1;

					if (isCandidateFile) {
						candidateFiles.add(sourceFile);
						System.out.println("HANDLING: " + sourceFile.getPath());
					} else {
						skippedFiles.add(sourceFile);
						System.out.println("Skipped: " + sourceFile.getPath());
						return;
					}
				}
			}

			grepTimer.start();

			boolean potentialMatch = smplRule.isPotentialMatch(ctExecutable);

			grepTimer.stop();
			grepCounter += 1;

			if (!potentialMatch) {
				return;
			}

			System.out.print("Potential: " + getFullyQualifiedName(ctExecutable));

			storeOriginalSourceIfNotAlreadyStored(ctExecutable.getPosition().getCompilationUnit());

			patchTimer.start();

			new TypeAccessReplacer(EnumSet.of(TypeAccessReplacer.Options.NoCheckParents)).scan(ctExecutable);
			boolean patchApplied = tryApplyPatch(ctExecutable, smplRule);

			patchTimer.stop();

			if (patchApplied) {
				System.out.println("... Patched!");
				transformedCUs.put(ctExecutable.getPosition().getFile(), ctExecutable.getPosition().getCompilationUnit());
				transformationCounter += 1;
			} else {
				System.out.print("\n");
			}

			tryPatchCounter += 1;
		} finally {
			procTimer.stop();
		}
	}

	/**
	 * If not already stored, store the untransformed source code of a given compilation unit.
	 *
	 * @param compilationUnit Compilation unit to store the source code of
	 */
	private static void storeOriginalSourceIfNotAlreadyStored(CtCompilationUnit compilationUnit) {
		if (!originalSource.containsKey(compilationUnit.getFile())) {
			originalSource.put(compilationUnit.getFile(), printCompilationUnit(compilationUnit));
		}
	}

	/**
	 * Pretty-print the source code representation of a given compilation unit.
	 *
	 * @param compilationUnit Compilation unit to pretty-print
	 * @return Pretty-printed String
	 */
	private static String printCompilationUnit(CtCompilationUnit compilationUnit) {
		return compilationUnit.getFactory().getEnvironment().createPrettyPrinter().printCompilationUnit(compilationUnit);
	}

	/**
	 * Apply a patch to an executable block, returning a boolean indication of whether any transformations were made.
	 *
	 * @param ctExecutable Executable block to patch
	 * @param rule         SmPL rule to apply
	 * @return True if the executable block was transformed by the rule, false otherwise
	 */
	private static boolean tryApplyPatch(CtExecutable<?> ctExecutable, SmPLRule rule) {
		CFGModel model = new CFGModel(new SmPLMethodCFG(ctExecutable));
		ModelChecker checker = new ModelChecker(model);
		rule.getFormula().accept(checker);

		ModelChecker.ResultSet results = checker.getResult();

		if (results.isEmpty() || results.getAllWitnesses().isEmpty()) {
			model.getCfg().restoreUnsupportedElements();
			return false;
		}

		for (ModelChecker.Result result : results) {
			if (!result.getEnvironment().isEmpty()) {
				throw new IllegalStateException("nonempty environment");
			}
		}

		Transformer.transform(model, results.getAllWitnesses());

		if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {
			Transformer.copyAddedMethods(model, rule);
		}

		model.getCfg().restoreUnsupportedElements();

		return true;
	}

	/**
	 * Get the fully-qualified name of a given CtExecutable.
	 *
	 * @param ctExecutable CtExecutable to compute name of
	 * @return Fully-qualified name
	 */
	private static String getFullyQualifiedName(CtExecutable<?> ctExecutable) {
		return ((CtTypeMember) ctExecutable).getDeclaringType().getQualifiedName() + "." + ctExecutable.getSimpleName();
	}

	/**
	 * Simple model of a timer useful in benchmarking.
	 */
	private static class Timer {
		/**
		 * Start the timer.
		 */
		public void start() {
			started = System.nanoTime();
		}

		/**
		 * Stop the timer, accumulating any elapsed time.
		 */
		public void stop() {
			if (started == 0L) {
				throw new IllegalStateException("must start timer before stopping it");
			}

			accumulated += System.nanoTime() - started;
			started = 0L;
		}

		/**
		 * Get the accumulated time in nanoseconds.
		 *
		 * @return accumulated time in nanoseconds
		 */
		public long getAccumulatedTime() {
			return accumulated;
		}

		private long started = 0L;
		private long accumulated = 0L;
	}

	/**
	 * Read all contents of a plain text file.
	 *
	 * @param path     Path to file
	 * @param encoding Character encoding of file
	 * @return Contents of file
	 * @throws IOException on IO errors
	 * @author https://stackoverflow.com/a/326440
	 */
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * Create a new empty file and write a String to it.
	 *
	 * @param path    Full path to filename
	 * @param content String content to write to file
	 * @throws IOException on IO errors
	 */
	private static void writeFile(String path, String content) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8, false));
		writer.write(content);
		writer.close();
	}

	/**
	 * Create a new empty file and write a String to it, silently handling IO errors.
	 *
	 * @param path    Full path to filename
	 * @param content String content to write to file
	 */
	private static void tryWriteFile(String path, String content) {
		try {
			writeFile(path, content);
		} catch (IOException ignored) {
		}
	}

	/**
	 * Split a String using common shell semantics.
	 * <p>
	 * The input String is split into 1) individual words and 2) double-quote-delimited strings while honoring
	 * backslash as escape character.
	 * <p>
	 * Example: shellSplit(hello "cruel world" and "inner \"quotes\" are fun")
	 * => [hello, cruel world, and, inner "quotes" are fun]
	 *
	 * @param str String to split
	 * @return Array of split components
	 */
	private static String[] shellSplit(String str) {
		List<StringBuilder> result = new ArrayList<>();
		result.add(new StringBuilder());
		StringBuilder current = result.get(0);

		boolean inWhitespace = false;
		boolean inString = false;
		boolean inEscape = false;

		for (char c : str.toCharArray()) {
			if (c == '\\' && !inEscape) {
				inEscape = true;
				continue;
			}

			if (!inEscape) {
				if (c == ' ' && !inString && !inWhitespace) {
					inWhitespace = true;
					current = new StringBuilder();
					result.add(current);
				}

				if (c == '"') {
					inString = !inString;
				}

				if (c != ' ') {
					inWhitespace = false;
				}

				if (inWhitespace || c == '"') {
					continue;
				}
			} else {
				inEscape = false;
			}

			current.append(c);
		}

		return result.stream().map(StringBuilder::toString).toArray(String[]::new);
	}

	/**
	 * SmPL rule to apply.
	 */
	private static SmPLRule smplRule = null;

	/**
	 * Diff command to use (if non-null, otherwise diffing is disabled).
	 */
	private static String[] diffCommand = null;

	/**
	 * Timer for full execution of main() method.
	 */
	private static Timer totalTimer = new Timer();

	/**
	 * Timer for calls to the process() method.
	 */
	private static Timer procTimer = new Timer();

	/**
	 * Timer for application of SmPLGrep optimization through SmPLRule::isPotentialMatch().
	 */
	private static Timer grepTimer = new Timer();

	/**
	 * Timer for application of patch (creating CFG from executable, creating Model from CFG, model checking and
	 * transformation).
	 */
	private static Timer patchTimer = new Timer();

	/**
	 * Counter for number of times SmPLGrep pattern matching was performed.
	 */
	private static int grepCounter = 0;

	/**
	 * Counter for number of times patch application was performed.
	 */
	private static int tryPatchCounter = 0;

	/**
	 * Counter for number of times a patch application resulted in code transformation.
	 */
	private static int transformationCounter = 0;

	/**
	 * Transformed compilation units, kept track of in order to print diffs.
	 */
	private static Map<File, CtCompilationUnit> transformedCUs = new HashMap<>();

	/**
	 * Original compilation unit sources, kept track of in order to print diffs.
	 */
	private static Map<File, String> originalSource = new HashMap<>();

	/**
	 * Set of Files matching the SmPLGrep pattern, being candidates for containing patchable executables.
	 */
	private static Set<File> candidateFiles = new HashSet<>();

	/**
	 * Set of Files not matching the SmPLGrep pattern, having no possibility of matching the patch.
	 */
	private static Set<File> skippedFiles = new HashSet<>();
}
