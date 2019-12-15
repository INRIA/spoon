package com.leafactor.gradle.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.builder.model.SourceProvider;
import com.leafactor.cli.engine.*;
import com.leafactor.cli.engine.logging.IterationLogEntry;
import com.leafactor.cli.engine.logging.IterationLogger;
import com.leafactor.cli.engine.logging.IterationPhaseLogEntry;
import com.leafactor.cli.rules.DrawAllocationRefactoringRule;
import com.leafactor.cli.rules.RecycleRefactoringRule;
import com.leafactor.cli.rules.ViewHolderRefactoringRule;
import com.leafactor.cli.rules.WakeLockRefactoringRule;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.UnionFileCollection;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.processing.TraversalStrategy;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Refactor extends DefaultTask {
    private Project project;
    private LauncherExtension launcherExtension;

    void init(Project project, LauncherExtension launcherExtension) {
        this.project = project;
        this.launcherExtension = launcherExtension;
    }

    @TaskAction
    public void task() throws IOException {
        // Check if the Android AppPlugin is present
        if (!project.getPlugins().hasPlugin(AppPlugin.class)) {
            throw new RuntimeException("should be declared after 'com.android.application'");
        }
        // Get the AppExtension from the gradle runtime
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        assert appExtension != null;
        // We will hold all the resolved dependency paths in a List of Strings
        final List<String> dependencyPaths = new ArrayList<>();
        // Getting the resolved configurations and gathering the file dependencies.
        if (project.getConfigurations().getByName("implementation").getState() == Configuration.State.UNRESOLVED) {
            project.getConfigurations().getByName("implementation").setCanBeResolved(true);
        }
        project.getConfigurations().getByName("implementation").getResolvedConfiguration().getResolvedArtifacts()
                .forEach(resolvedArtifact -> {
                    dependencyPaths.add(resolvedArtifact.getFile().getAbsolutePath());
                });

        // Get the SDK directory and the current API LEVEL.
        File sdkDirectory = appExtension.getSdkDirectory();
        String APILevel = appExtension.getCompileSdkVersion();
        System.out.println("SDK: " + sdkDirectory);
        System.out.println("API LEVEL: " + APILevel);
        String androidJarPath = sdkDirectory.getAbsolutePath() + "/platforms/" + APILevel + "/android.jar";
        dependencyPaths.add(androidJarPath);

        String projectPath = project.getProjectDir().toPath().toString();
        System.out.println("PROJECT PATH: " + projectPath);

        appExtension.getApplicationVariants().forEach((applicationVariant -> {

            try {
                System.out.println("ApplicationVariant" + applicationVariant.getName());
                String flavorName = applicationVariant.getFlavorName();
                String buildTypeName = applicationVariant.getBuildType().getName();
                String variantName = applicationVariant.getName();
                String variantNameCapitalized = variantName.substring(0, 1).toUpperCase() + variantName.substring(1);
                String flavorImplementation = flavorName.isEmpty() ? "implementation" : flavorName + "Implementation";
                System.out.println("Flavor: " + flavorName);
                System.out.println("Build type: " + buildTypeName);
                System.out.println("Variant: " + variantNameCapitalized);

                if ((launcherExtension.isWhiteListVariants() && !launcherExtension.getVariants().contains(variantName))
                        || (!launcherExtension.isWhiteListVariants() && launcherExtension.getVariants().contains(variantName))) {
                    return;
                }

                //    generated/aidl_source_output_dir/{variant}/compile{variant.capitalize}Aidl/out
                //    generated/not_namespaced_r_class_sources/{variant}/process{variant.capitalize}Resources/r/
                //    generated/source/buildConfig/{flavour}/{buildType}
                String aidlFilesDir = String.format("%s/build/generated/aidl_source_output_dir/%s/compile%sAidl/out", projectPath, variantName, variantNameCapitalized);
                String rFilesDir = String.format("%s/build/generated/not_namespaced_r_class_sources/%s/process%sResources/r", projectPath, variantName, variantNameCapitalized);
                String buildConfigFileDir = String.format("%s/build/generated/source/buildConfig/%s/%s", projectPath, flavorName, buildTypeName);
                String mainFilesDir = Paths.get(projectPath, "src", "main", "java").toString();
                String flavorFilesDir = Paths.get(projectPath, "src", flavorName, "java").toString();

                if (flavorName.isEmpty()) {
                    if (!new File(aidlFilesDir).exists()) {
                        aidlFilesDir = String.format("%s/build/generated/aidl_source_output_dir/%s/out", projectPath, variantName);
                    }
                    if (!new File(rFilesDir).exists()) {
                        rFilesDir = String.format("%s/build/generated/not_namespaced_r_class_sources/%s/r", projectPath, variantName);
                    }
                    if (!new File(buildConfigFileDir).exists()) {
                        buildConfigFileDir = String.format("%s/build/generated/source/buildConfig/%s", projectPath, buildTypeName);
                    }
                }

                System.out.println("AIDL Files Dir: " + aidlFilesDir);
                System.out.println("R Files Dir: " + rFilesDir);
                System.out.println("BuildConfig File Dir: " + buildConfigFileDir);
                System.out.println("Main Files Dir: " + mainFilesDir);
                System.out.println("Flavor Files Dir: " + flavorFilesDir);

                // Creating the spoon launcher
                Launcher launcher = new Launcher();
                Environment environment = launcher.getEnvironment();

                // Configure the environment to use a custom classPath
                List<String> flavorDependencies = new ArrayList<>(dependencyPaths);
                if (project.getConfigurations().getByName(flavorImplementation).getState() == Configuration.State.UNRESOLVED) {
                    project.getConfigurations().getByName(flavorImplementation).setCanBeResolved(true);
                }
                project.getConfigurations().getByName(flavorImplementation).getResolvedConfiguration().getResolvedArtifacts()
                        .forEach(resolvedArtifact -> {
                            flavorDependencies.add(resolvedArtifact.getFile().getAbsolutePath());
                        });
//                environment.setNoClasspath(false);
                environment.setSourceClasspath(dependenciesToClassPath(flavorDependencies));
                environment.setAutoImports(true);
                environment.setPrettyPrinterCreator(() -> {
                    SniperJavaPrettyPrinter sniperJavaPrettyPrinter = new SniperJavaPrettyPrinter(environment);
                    sniperJavaPrettyPrinter.setIgnoreImplicit(false);
                    return sniperJavaPrettyPrinter;});
//                environment.setPrettyPrinterCreator(
//                        () -> new SniperJavaPrettyPrinter(environment));
//                environment.setPrettyPrinterCreator(() -> {
//                    // Todo - Temporary fix, awaiting final fix from Spoon collaborators
//                    // https://github.com/INRIA/spoon/pull/3136
//                    DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(environment);
//                    List<Processor<CtElement>> preprocessors = Collections.unmodifiableList(new ArrayList());
//                    printer.setIgnoreImplicit(true);
//                    printer.setPreprocessors(preprocessors);
//                    return printer;
//                });

                CompilationUnitGroup compilationUnitGroup = new CompilationUnitGroup(launcher);

                // Optional output directory
                File outputDirectory = null;
                if (launcherExtension.getSourceOutputDirectory() != null) {
                    outputDirectory = new File(launcherExtension.getSourceOutputDirectory());
                }

                if (outputDirectory != null && !outputDirectory.isDirectory()) {
                    throw new RuntimeException("No such directory " + launcherExtension.getSourceOutputDirectory());
                }

                if (outputDirectory != null) {
                    File leafactorGenDir = new File(outputDirectory.getAbsolutePath() + "/leafactor-ci/" + variantName);
                    if (!leafactorGenDir.exists() && !leafactorGenDir.mkdirs()) {
                        throw new RuntimeException("Could not create directory " + leafactorGenDir.getAbsolutePath());
                    }
                    compilationUnitGroup.setSourceOutputDirectory(leafactorGenDir);
                }

                if (new File(aidlFilesDir).exists()) {
                    compilationUnitGroup.add(new File(aidlFilesDir));
                }
                if (new File(rFilesDir).exists()) {
                    compilationUnitGroup.add(new File(rFilesDir));
                }
                if (new File(buildConfigFileDir).exists()) {
                    compilationUnitGroup.add(new File(buildConfigFileDir));
                }
                if (new File(mainFilesDir).exists()) {
                    compilationUnitGroup.add(new File(mainFilesDir));
                }
                if (new File(flavorFilesDir).exists()) {
                    compilationUnitGroup.add(new File(flavorFilesDir));
                }

                // Run the group of compilation units with the set of refactoring rules
                IterationLogger logger = new IterationLogger();
                List<RefactoringRule> refactoringRules = new ArrayList<>();
                // Adding all the refactoring rules
                refactoringRules.add(new RecycleRefactoringRule(logger));
//                refactoringRules.add(new ViewHolderRefactoringRule(logger));
//                refactoringRules.add(new DrawAllocationRefactoringRule(logger));
//                refactoringRules.add(new WakeLockRefactoringRule(logger));

                System.out.println("Running");
                compilationUnitGroup.run(refactoringRules);
//                System.out.println("Logging results:");
                for (IterationLogEntry entry : logger.getLogs()) {
//                    System.out.println("Log entry:");
//                    System.out.println("Timestamp: " + entry.getTimeStamp());
//                    System.out.println("Name: " + entry.getName());
//                    System.out.println("Description: " + entry.getDescription());
//                    System.out.println("Refactoring rule: " + entry.getRule().getClass().getName());
                    if (entry instanceof IterationPhaseLogEntry) {
                        IterationPhaseLogEntry iterationPhaseLogEntry = (IterationPhaseLogEntry) entry;
                        Duration duration = iterationPhaseLogEntry.getPhaseDuration();
//                        System.out.println(iterationPhaseLogEntry.getStartPhaseTimestamp());
//                        System.out.println(iterationPhaseLogEntry.getEndPhaseTimestamp());
//                        System.out.println("Duration: " + duration.toNanos());
                    }
                }
            } catch (Exception e) {
                // Todo - Be more specific.
                e.printStackTrace();
            }
        }));
    }

    private String[] dependenciesToClassPath(List<String> dependencyPaths) throws IOException {
        // Migrate list to array of string
        String[] classPath = new String[dependencyPaths.size()];
        for (int i = 0; i < dependencyPaths.size(); i++) {
            String originalFile = dependencyPaths.get(i);
            if (dependencyPaths.get(i).endsWith(".aar")) {
                // AAR Files need to be converted to JAR in order to be included
                Path tempDirectory = Files.createTempDirectory("aarFileExtraction");
                String destination = tempDirectory.toAbsolutePath().toString();
                try {
                    ZipFile zipFile = new ZipFile(originalFile);
                    zipFile.extractAll(destination);
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                classPath[i] = destination + "/classes.jar";
            } else {
                classPath[i] = originalFile;
            }
        }
        return classPath;
    }
}