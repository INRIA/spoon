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
        System.out.println("smplcli ACTION [ARG [ARG ..]]");
        System.out.println();
        System.out.println("    ACTIONs:");
        System.out.println("        check        run model checker");
        System.out.println("                     requires --smpl-file and --java-file");
        System.out.println();
        System.out.println("        rewrite      rewrite SmPL input");
        System.out.println("                     requires --smpl-file");
        System.out.println();
        System.out.println("    ARGs:");
        System.out.println("        --smpl-file FILENAME");
        System.out.println("        --java-file FILENAME");
        System.out.println();
    }

    enum Action { MODELCHECK, REWRITE };
    enum ArgumentState { BASE, FAIL, ACTION, FILENAME_SMPL, FILENAME_JAVA };

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
                        action = Action.MODELCHECK;
                        argumentState = ArgumentState.BASE;
                    } else if (arg.equals("rewrite")) {
                        action = Action.REWRITE;
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

        if (argumentState != ArgumentState.BASE) {
            usage();

            System.exit(args.length > 0 ? 1 : 0);
        }

        if (action == Action.MODELCHECK) {
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
        }
    }
}
