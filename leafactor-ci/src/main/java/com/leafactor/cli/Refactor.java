package com.leafactor.cli;

import com.leafactor.cli.engine.CompilationUnitGroup;
import com.leafactor.cli.engine.logging.IterationLogger;
import com.leafactor.cli.engine.RefactoringRule;
import com.leafactor.cli.rules.DrawAllocationRefactoringRule;
import com.leafactor.cli.rules.ViewHolderRefactoringRule;
import com.leafactor.cli.rules.WakeLockRefactoringRule;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.leafactor.cli.rules.RecycleRefactoringRule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandLine.Command(name = "Refactor", mixinStandardHelpOptions = true, version = "1.0")
public class Refactor implements Runnable {
    @Option(names = {"-v", "--verbose"}, description = "Verbose mode. Helpful for troubleshooting. " +
            "Multiple -v options increase the verbosity.")
    private boolean[] verbose = new boolean[0];

    @Option(arity = "1..*", names = {"-r", "--rules"}, description = "Specify the rules you want to apply in the refactoring process.\n" +
            " - RecycleRefactoring\n" +
            " - ViewHolderRefactoring\n" +
            " - DrawAllocationRefactoring\n" +
            " - WakeLockRefactoring\n")
    private String[] rules = new String[0];

    @Parameters(arity = "1..*", paramLabel = "File or Directory", description = "File(s) or directories to process.")
    private File[] inputFiles;

    static private Map<String, RefactoringRule> rulesMap = new HashMap<>();

    private IterationLogger logger;

    private Refactor(IterationLogger logger) {
        this.logger = logger;
    }

    public void run() {
        boolean verbose = this.verbose.length > 0;

        List<RefactoringRule> refactoringRules = new ArrayList<>();
        if (rules.length == 0) {
            refactoringRules.addAll(rulesMap.values());
        } else {
            for (String rule : rules) {
                refactoringRules.add(rulesMap.get(rule));
            }
        }
        CompilationUnitGroup group = new CompilationUnitGroup();
        for (File file : inputFiles) {
            if (!file.exists()) {
                System.out.println("File does not exist: " + file.getAbsolutePath());
                continue;
            }

            if ((file.isDirectory() || file.getAbsolutePath().endsWith(".java"))) {
                try {
                    group.add(file);
                } catch (IOException e) {
                    System.out.println("Error creating compilation group with file: " + file.getAbsolutePath());
                    return;
                }
            } else {
                System.out.println("Invalid file: " + file.getAbsolutePath());
            }
        }
        try {
            group.run(refactoringRules);
        } catch (IOException e) {
            if (verbose) {
                e.printStackTrace();
            } else {
                System.out.println("Something went wrong.");
            }
        }
    }

    public static void main(String[] args) {
        IterationLogger logger = new IterationLogger();
        rulesMap.put("RecycleRefactoring", new RecycleRefactoringRule(logger));
        rulesMap.put("ViewHolderRefactoring", new ViewHolderRefactoringRule(logger));
        rulesMap.put("DrawAllocationRefactoring", new DrawAllocationRefactoringRule(logger));
        rulesMap.put("WakeLockRefactoring", new WakeLockRefactoringRule(logger));
        CommandLine.run(new Refactor(logger), args);
    }
}