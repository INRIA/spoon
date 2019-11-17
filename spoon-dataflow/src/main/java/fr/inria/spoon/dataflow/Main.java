package fr.inria.spoon.dataflow;

import fr.inria.spoon.dataflow.scanners.CheckersScanner;
import org.apache.commons.cli.*;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.util.Arrays;

public class Main
{
    private static Options createCommandLineOptions()
    {
        Options options = new Options();

        Option sourcesOption = new Option("sources", "sources to be analyzed");
        sourcesOption.setRequired(true);
        sourcesOption.setArgName("arg...");
        sourcesOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(sourcesOption);

        Option classpathOption = new Option("classpath", "sources classpath");
        classpathOption.setRequired(false);
        classpathOption.setArgName("arg...");
        classpathOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(classpathOption);

        return options;
    }

    public static void main(String[] args)
    {
        Options options = createCommandLineOptions();

        CommandLine cmd;
        try
        {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            System.out.println("ERROR: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.setOptionComparator(null);
            formatter.printHelp( " java -jar spoon-dataflow.jar", null, options, null, true);
            System.exit(1);
            return;
        }

        String[] sources = cmd.getOptionValues("sources");
        String[] classpath = cmd.getOptionValues("classpath");

        Launcher launcher = new Launcher();
        //launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setComplianceLevel(10);
        Arrays.stream(sources).forEach(launcher::addInputResource);
        if (classpath != null)
        {
            launcher.getEnvironment().setSourceClasspath(classpath);
        }
        CtModel ctModel = launcher.buildModel();

        CheckersScanner scanner = new CheckersScanner(launcher.getFactory());
        ctModel.getAllTypes().forEach(scanner::scan);
        scanner.getWarnings().forEach(w -> System.out.println("WARNING: " + w));
    }
}
