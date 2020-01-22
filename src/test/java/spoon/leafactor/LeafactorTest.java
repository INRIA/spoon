package spoon.leafactor;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.leafactor.engine.CompilationUnitGroup;
import spoon.leafactor.engine.RefactoringRule;
import spoon.leafactor.engine.logging.IterationLogger;
import spoon.leafactor.rules.RecycleRefactoringRule;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class LeafactorTest {

    private List<RemoteRepository> getRemoteRepos() {
        RemoteRepository mavenCentral = new RemoteRepository.Builder("maven-central", "default", "http://repo1.maven.org/maven2/").build();
        RemoteRepository mavenGoogle = new RemoteRepository.Builder("maven-google", "default", "http://maven.google.com/").build();
        List<RemoteRepository> remoteRepositories = new ArrayList<>();
//        remoteRepositories.add(mavenCentral);
        remoteRepositories.add(mavenGoogle);
        return remoteRepositories;
    }

    private RepositorySystem newSystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
        locator.addService( TransporterFactory.class, FileTransporterFactory.class );
        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
        locator.getService( RepositorySystem.class );
        return locator.getService(RepositorySystem.class);
    }

    private RepositorySystemSession newSession(RepositorySystem repositorySystem, LocalRepository localRepository) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager( session, localRepository ) );
        return session;
    }

    private ArtifactResult resolveArtifact(RepositorySystem repoSystem,
                                           RepositorySystemSession repoSession,
                                           List<RemoteRepository> remoteRepos,
                                           Artifact artifact)
            throws IllegalArgumentException, ArtifactResolutionException {
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(artifact);
        request.setRepositories(remoteRepos);
        return repoSystem.resolveArtifact(repoSession, request);
    }

    private String downloadAndroidPlatform() throws IOException {
        String platform = "https://dl.google.com/android/repository/platform-28_r06.zip";
        File tempDirectory = Files.createTempDirectory("sdk").toFile();
        File destination = new File(tempDirectory.getAbsolutePath() + "/platform-28_r06.zip");
        // 80mb download
        System.out.println("Downloading platform");
        FileUtils.copyURLToFile(new URL(platform), destination);
        try {
            ZipFile zipFile = new ZipFile(destination);
            zipFile.extractAll(tempDirectory.getAbsolutePath());
        } catch (ZipException e) {
            e.printStackTrace();
        }
        File androidJar = new File(tempDirectory.getAbsolutePath() + "/android-9/android.jar");
        return androidJar.getAbsolutePath();
    }

    @Test
    public void runTest() throws IOException, IllegalArgumentException, ArtifactResolutionException {
        // Creating the spoon launcher
        Launcher launcher = new Launcher();
        Environment environment = launcher.getEnvironment();
        List<String> dependencies = new ArrayList<>();
        // this is not required to reproduce the bug
        //dependencies.add(downloadAndroidPlatform());

        // Preparing to Repository system for maven dependency resolution.
//        RepositorySystem repositorySystem = newSystem();
//        File temp = Files.createTempDirectory("maven-repo").toFile();
//        LocalRepository localRepository = new LocalRepository(temp, "simple");
//        RepositorySystemSession repositorySystemSession = newSession(repositorySystem, localRepository);
        // Download an artifact from maven remote repo
//        Artifact artifact = new DefaultArtifact("com.android.support:appcompat-v7:aar:28.0.0");
//        dependencies.add(resolveArtifact(repositorySystem, repositorySystemSession, getRemoteRepos(), artifact).getArtifact().getFile().getAbsolutePath());

        environment.setSourceClasspath(dependenciesToClassPath(dependencies));
        environment.setAutoImports(true);
        environment.setPrettyPrinterCreator(() -> {
            SniperJavaPrettyPrinter sniperJavaPrettyPrinter = new SniperJavaPrettyPrinter(environment);
            sniperJavaPrettyPrinter.setIgnoreImplicit(false);
            return sniperJavaPrettyPrinter;
        });

        CompilationUnitGroup compilationUnitGroup = new CompilationUnitGroup(launcher);

        File outputDirectory = Files.createTempDirectory("output").toFile();
        compilationUnitGroup.setSourceOutputDirectory(outputDirectory);

        // always prefer one single test when you propose a failing test case
        compilationUnitGroup.add(new File("src/test/resources/leafactor/testing/sample1/src/EasyPaint.java"));

        List<RefactoringRule> refactoringRules = new ArrayList<>();
        IterationLogger logger = new IterationLogger();
        // Adding all the refactoring rules
        refactoringRules.add(new RecycleRefactoringRule(logger));
//                refactoringRules.add(new ViewHolderRefactoringRule(logger));
//                refactoringRules.add(new DrawAllocationRefactoringRule(logger));
//                refactoringRules.add(new WakeLockRefactoringRule(logger));

        compilationUnitGroup.run(refactoringRules);
        System.out.println("OutputDirectory: " + outputDirectory);
        String producedEasyPaintJavaFileContent = new String(Files.readAllBytes(Paths.get(outputDirectory.getAbsolutePath() + "/anupam/acrylic/EasyPaint.java")), StandardCharsets.UTF_8);
        // the expected value encoded in was incorrect EasyPaintExpected.java
        String expectedEasyPaintJavaFileContent = new String(Files.readAllBytes(Paths.get("src/test/resources/leafactor/testing/sample1/expected/EasyPaintExpected.java")), StandardCharsets.UTF_8);
        assertEquals(expectedEasyPaintJavaFileContent, producedEasyPaintJavaFileContent);
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
