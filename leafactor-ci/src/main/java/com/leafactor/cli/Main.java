package com.leafactor.cli;

import com.leafactor.cli.engine.logging.IterationLogger;
import com.leafactor.cli.rules.DrawAllocationRefactoringRule;
import com.leafactor.cli.rules.RecycleRefactoringRule;

import com.leafactor.cli.rules.ViewHolderRefactoringRule;
import com.leafactor.cli.rules.WakeLockRefactoringRule;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        IterationLogger logger = new IterationLogger();
        final Launcher launcher = new Launcher();
        final Environment e = launcher.getEnvironment();
        e.setLevel("INFO");
        e.setNoClasspath(true);
        e.setAutoImports(true);
        launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment())
        );
        launcher.addInputResource("C:\\repositories\\leafactor-ci\\src\\main\\resources\\WakeLockSample.java");
        launcher.addProcessor(new RecycleRefactoringRule(logger));
        launcher.addProcessor(new DrawAllocationRefactoringRule(logger));
        launcher.addProcessor(new ViewHolderRefactoringRule(logger));
        launcher.addProcessor(new WakeLockRefactoringRule(logger));
        Path tempDir = Files.createTempDirectory("temporary-output");
        System.out.println("TempDir: " + tempDir);
        launcher.setSourceOutputDirectory(tempDir.toFile());
        launcher.run();
        CtModel model = launcher.getModel();
        String packageName = model.getAllPackages().toArray()[model.getAllPackages().size() - 1].toString();
        packageName = packageName.replaceAll("\\.", "\\\\");
        File file = new File(tempDir + "\\" + packageName + "\\" + "WakeLockSample.java");
        System.out.println(file);
        System.out.println(file.exists());
        System.out.println("-----------------------------------------------------");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
        System.out.println("-----------------------------------------------------");
    }
}