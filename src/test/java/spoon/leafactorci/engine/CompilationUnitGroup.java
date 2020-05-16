package spoon.leafactorci.engine;


import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a group of compilation units defined by a list of files
 */
public class CompilationUnitGroup {

    private final Launcher launcher;
    private List<File> files;
    private File sourceOutputDirectory;

    /**
     * Creates a compilation unit group
     */
    public CompilationUnitGroup() {
        launcher = new Launcher();
        Environment e = launcher.getEnvironment();
        e.setNoClasspath(true);
        e.setAutoImports(true);
        launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment()));
        files = new ArrayList<>();
    }

    public CompilationUnitGroup(Launcher launcher) {
        this.launcher = launcher;
        files = new ArrayList<>();
    }

    public File getSourceOutputDirectory() {
        return sourceOutputDirectory;
    }

    public void setSourceOutputDirectory(File sourceOutputDirectory) {
        this.sourceOutputDirectory = sourceOutputDirectory;
    }

    /**
     * Adds a new file to the list of files that make up the compilation unit group
     *
     * @param file The file to add
     * @throws IOException Thrown when there is IO exception
     */
    public void add(File file) throws IOException {
        if (file.isDirectory()) {
            files.addAll(getDirectoryFiles(file));
        } else {
            files.add(file);
        }
    }

    /**
     * Returns the Java files in the directory as alist
     *
     * @return A list of Java files
     * @throws IOException Thrown when there is IO exception
     */
    private static List<File> getDirectoryFiles(File directory) throws IOException {
        List<File> files = new ArrayList<>();
        Files.find(Paths.get(directory.getPath()),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .forEach(path -> files.add(path.toFile()));
        return files.stream().filter(file -> file.getName().endsWith(".java")).collect(Collectors.toList());
    }

    private Map<File, String> generatePackageNameMap() {
        Map<File, String> packageNameMap = new HashMap<>();
        for (File file : this.files) {
            BufferedReader reader;
            String loadedLines = "";
            String packageName = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                Pattern pattern = Pattern.compile("package\\s+([a-zA_Z_][\\.\\w]*);");
                loadedLines = line;
                while (line != null) {
                    Matcher matcher = pattern.matcher(loadedLines);
                    if (matcher.find()) {
                        packageName = matcher.group(1);
                        break;
                    }
                    line = reader.readLine();
                    loadedLines += line + "\n";
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (packageName == null) {
                throw new RuntimeException("Could not find package name for source file " + file);
            }
            packageNameMap.put(file, packageName);
        }
        return packageNameMap;
    }

    /**
     * Executes a refactoring job with a list of refactoring rules over the directory
     *
     * @param refactoringRules A list of refactoring rules
     * @throws IOException Thrown when there is IO exception
     */
    public void run(List<RefactoringRule> refactoringRules) throws IOException {
        if (refactoringRules == null) {
            throw new RuntimeException("The refactoring rules list cannot be null");
        }

        if (refactoringRules.size() == 0) {
            throw new RuntimeException("The refactoring rules list cannot be empty");
        }

        for (RefactoringRule rule : refactoringRules) {
            launcher.addProcessor(rule);
        }

        //FOR TESTING ONLY
        launcher.addProcessor(new AbstractProcessor<CtBlock>() {
            @Override
            public void process(CtBlock ctBlock) {
                System.out.println(">> " + ctBlock.toStringDebug() + " <<");
            }
        });
        ///////////////////////

        for (File file : this.files) {
            launcher.addInputResource(file.getAbsolutePath());
        }

        boolean replaceOriginal = sourceOutputDirectory == null;
        if (!replaceOriginal) {
            System.out.println("Outputting files to " + sourceOutputDirectory);
            launcher.setSourceOutputDirectory(sourceOutputDirectory);
        } else {
            Path tempDir = Files.createTempDirectory("temporary-output");
            System.out.println("Outputting files to " + tempDir);
            launcher.setSourceOutputDirectory(tempDir.toFile());
            sourceOutputDirectory = tempDir.toFile();
        }

        try {
            launcher.run();
            if (replaceOriginal) {
                Map<File, String> packageNameMap = this.generatePackageNameMap();
                System.out.println("Replacing original files.");
                for (File file : files) {
                    String packageName = packageNameMap.get(file);
                    String fileName = file.getName();
                    System.out.println("Package: " + packageName);
                    System.out.println("File Name: " + fileName);
                    String tempFilePath = sourceOutputDirectory + "/" +
                            packageName
                                    .replaceAll("\\.", "/")
                                    .replaceAll("\\s", "") +
                            "/" + fileName;
                    System.out.println("Absolute path: " + tempFilePath);
                    File tempFile = new File(tempFilePath);
                    if (!tempFile.exists() || tempFile.length() == 0) {
                        System.out.println("Warning, skipping file " + tempFilePath);
                    } else {
                        Files.copy(Paths.get(tempFilePath), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Run each file in isolation, each file uses a new launcher instance
     *
     * @param refactoringRules A list of refactoring rules
     * @throws IOException Thrown when there is IO exception
     */
    public void runInIsolation(List<RefactoringRule> refactoringRules) throws IOException {
        for (File currentFile : this.files) {
            System.out.println("\n------------------------");
            System.out.println("--- Now processing file " + currentFile.getCanonicalFile().getName() + ". ---\n");
            Launcher launcher = new Launcher();
            Environment e = launcher.getEnvironment();
            e.setNoClasspath(true);
            e.setAutoImports(true);
            launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment()));
            if (refactoringRules == null) {
                throw new RuntimeException("The refactoring rules list cannot be null.");
            }

            if (refactoringRules.size() == 0) {
                throw new RuntimeException("The refactoring rules list cannot be empty.");
            }

            for (RefactoringRule rule : refactoringRules) {
                launcher.addProcessor(rule);
            }

            //FOR TESTING ONLY
//            launcher.addProcessor(new AbstractProcessor<CtBlock>() {
//                @Override
//                public void process(CtBlock ctBlock) {
//                    System.out.println(">> " + ctBlock.toStringDebug() + " <<");
//                }
//            });
            ///////////////////////

            launcher.addInputResource(currentFile.getAbsolutePath());

            boolean replaceOriginal = sourceOutputDirectory == null;
            File inferedOutputDirectory = sourceOutputDirectory;
            if (!replaceOriginal) {
                System.out.println("Outputting result to " + sourceOutputDirectory);
                launcher.setSourceOutputDirectory(sourceOutputDirectory);
            } else {
                Path tempDir = Files.createTempDirectory("temporary-output");
                System.out.println("Outputting result to " + tempDir);
                launcher.setSourceOutputDirectory(tempDir.toFile());
                inferedOutputDirectory = tempDir.toFile();
            }

            try {
                launcher.run();
                if (replaceOriginal) {
                    Map<File, String> packageNameMap = this.generatePackageNameMap();
                    System.out.println("\nReplacing original file.");
                    String packageName = packageNameMap.get(currentFile);
                    String fileName = currentFile.getName();
                    System.out.println("Package: " + packageName);
                    System.out.println("File Name: " + fileName);
                    String tempFilePath = inferedOutputDirectory + "/" +
                            packageName
                                    .replaceAll("\\.", "/")
                                    .replaceAll("\\s", "") +
                            "/" + fileName;
                    System.out.println("Absolute path: " + tempFilePath);
                    File tempFile = new File(tempFilePath);
                    System.out.println("Before size: " + currentFile.length() + " bytes");
                    System.out.println("After size: " + tempFile.length() + " bytes");
                    if (!tempFile.exists() || tempFile.length() == 0) {
                        System.out.println("Warning, skipping file " + tempFilePath);
                    } else {
                        Files.copy(Paths.get(tempFilePath), currentFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (Exception exception) {
                System.out.println("Spoon was unable to read file due to " + exception.getCause());
            } finally {
                System.out.println("------------------------");
            }
        }

    }

}
