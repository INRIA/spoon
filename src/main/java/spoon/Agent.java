package spoon;

/*
 * Commented imports
 * import com.martiansoftware.jsap.JSAP;
 * import com.martiansoftware.jsap.JSAPResult;
 * import spoon.decompiler.MultiTypeTransformer;
 * import spoon.decompiler.SpoonClassFileTransformer;
 * import spoon.decompiler.TypeTransformer;
 * import spoon.processing.Processor;
 * import spoon.reflect.declaration.CtElement;
 * import spoon.reflect.declaration.CtType;
 *
 * import java.lang.instrument.Instrumentation;
 * import java.util.ArrayList;
 * import java.util.List;
 * import java.util.function.Predicate;
 * end
*/

public class Agent {

/*
	public static void premain(String agentArgs, Instrumentation inst) {


		//parse arguments

		Args args = new Args(agentArgs);

		TypeTransformer transformer = new MultiTypeTransformer();
		((MultiTypeTransformer) transformer).addTransformers(args.processors);
		//register
		inst.addTransformer(new SpoonClassFileTransformer(args.classFilter, args.pathToDecompile, args.pathToCache, args.pathToRecompile) {
			@Override
			public boolean accept(CtType type) {
				for(Processor p : args.processors) {
					return p.isToBeProcessed(type);
				}
				return false;
			}

			@Override
			public void transform(CtType type) {
				for(Processor p :  ) {
					if (p.isToBeProcessed(type)) {
						p.process(type);
					}
				}
			}
		});
	}

	private static Predicate<String> defaultClassFilter = className -> !className.startsWith("java")
			&& !className.startsWith("sun")
			&& !className.startsWith("jdk")
			&& !className.startsWith(("fr/inria/spoon/"))
			&& !className.startsWith(("fr/inria/spoon/"));
	private static String argSeparator = " ";
	private static String listSeparator = ",";
	private static String argNameSeparator = "=";
	private static class Args {
		public Predicate<String> classFilter = defaultClassFilter;
		public List<Processor> processors = new ArrayList<>();
		public String pathToDecompile;
		public String pathToRecompile;
		public String pathToCache;

		public Args(String in) {
			if(in != null) {
				String[] args = in.split(argSeparator);
				for(String arg: args) {
					String argName = arg.split(argNameSeparator)[0];
					switch (argName) {
						case "includes":

							break;

						case "excludes":

							break;

						case "processors":

							break;

						default:

					}
				}
			} else {
				printUsage();
			}
		}

		public static void printUsage() {

		}
	}
	*/
}
