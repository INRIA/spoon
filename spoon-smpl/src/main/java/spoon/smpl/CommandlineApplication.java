package spoon.smpl;

import fr.inria.controlflow.ControlFlowBuilder;
import fr.inria.controlflow.ControlFlowGraph;
import spoon.Launcher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandlineApplication {
    // https://stackoverflow.com/a/326440
    static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void usage() {
        System.out.println("usage:");
    }

    enum ArgumentState { BASE, FILENAME_SMPL, FILENAME_JAVA };

    public static void main(String[] args) {
        String smplFilename = null;
        String javaFilename = null;

        ArgumentState argumentState = ArgumentState.BASE;

        for (String arg : args) {
            switch (argumentState) {
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

        if (argumentState != ArgumentState.BASE) {
            usage();

            System.exit(args.length > 0 ? 1 : 0);
        }

        if (smplFilename != null && javaFilename != null) {
            try {
                SmPLRule smplRule = SmPLParser.parse(readFile(smplFilename, StandardCharsets.UTF_8));

                Launcher.parseClass(readFile(javaFilename, StandardCharsets.UTF_8))
                        .getMethods()
                        .forEach((mth) -> {
                            System.out.println(mth.getSimpleName());

                            ControlFlowBuilder cfgBuilder = new ControlFlowBuilder();
                            ControlFlowGraph cfg = cfgBuilder.build(mth);
                            cfg.simplify();

                            System.out.println(cfg.toGraphVisText());

                            Model model = new CFGModel(cfg);
                            ModelChecker modelChecker = new ModelChecker(model);

                            smplRule.getFormula().accept(modelChecker);
                            System.out.println(modelChecker.getResult());
                        });

                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
