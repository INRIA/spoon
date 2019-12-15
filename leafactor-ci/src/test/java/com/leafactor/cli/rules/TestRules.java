package com.leafactor.cli.rules;

import com.leafactor.cli.engine.logging.IterationLogger;
import com.leafactor.cli.engine.RefactoringRule;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRules {
    // https://github.com/gradle/gradle/issues/5975
    @TestFactory
    Collection<DynamicTest> dynamicTestsWithCollection() throws IOException, URISyntaxException {
        File resourcesDirectory = new File("src/test/resources/com/leafactor/cli/rules");
        try (Stream<Path> paths = Files.walk(resourcesDirectory.toPath())) {
            return paths
                    .filter((path) -> !path.equals(resourcesDirectory.toPath()))
                    .filter(Files::isDirectory)
                    .map((file) -> {
                        try (Stream<Path> subPaths = Files.walk(file)) {
                            return subPaths.filter((subFile) -> !subFile.equals(file))
                                    .filter(Files::isDirectory)
                                    .map((subFile) -> {
                                        return DynamicTest.dynamicTest(
                                                file.getFileName() + "-" + subFile.getFileName(),
                                                () -> {
    //                                                togglePrints(true);
                                                    String beforePath = subFile.toAbsolutePath() + "/Input.java";
                                                    String afterPath = subFile.toAbsolutePath() + "/Output.java";

                                                    // Load input files
                                                    System.out.println("SUBFILE: " + subFile.toAbsolutePath());
                                                    System.out.println("[" + subFile.getFileName().toString() + "] Loading Files");
                                                    String outputSample = new String(Files.readAllBytes(Paths.get(afterPath)));

                                                    System.out.println("[" + subFile.getFileName().toString() + "] Finding and refactoring opportunities");
    //                                                togglePrints(false);

                                                    IterationLogger logger = new IterationLogger();
                                                    final Launcher launcher = new Launcher();
                                                    final Environment e = launcher.getEnvironment();
                                                    e.setLevel("INFO");
                                                    e.setNoClasspath(true);
                                                    e.setAutoImports(true);
                                                    launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment())
                                                    );
                                                    launcher.addInputResource(beforePath);

                                                    Class<?> clazz = Class.forName("com.leafactor.cli.rules." + file.getFileName().toString());
                                                    Constructor<?> ctor = clazz.getConstructor(IterationLogger.class);
                                                    RefactoringRule rule = (RefactoringRule) ctor.newInstance(logger);
                                                    launcher.addProcessor(rule);
                                                    Path tempDir = Files.createTempDirectory("temporary-output");
                                                    System.out.println("TempDir: " + tempDir);
                                                    launcher.setSourceOutputDirectory(tempDir.toFile());
                                                    launcher.run();
                                                    CtModel model = launcher.getModel();
                                                    String packageName = model.getAllPackages().toArray()[model.getAllPackages().size() - 1].toString();
                                                    packageName = packageName.replaceAll("\\.", "/");
                                                    String producedFile = new String(Files.readAllBytes(Paths.get(tempDir + "/" + packageName + "/" + "Input.java")));
    //                                                togglePrints(true);
                                                    System.out.println("[" + subFile.getFileName().toString() + "] Comparing result");
                                                    // Compare result with the sample
                                                    producedFile = producedFile.replaceAll("\t", "    ");
                                                    assertEquals(outputSample, producedFile);
                                                });
                                    }).collect(Collectors.toList());
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }

}