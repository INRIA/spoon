/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.SAXException;

import spoon.processing.Environment;
import spoon.processing.FileGenerator;
import spoon.processing.ProblemFixer;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.processing.Severity;
import spoon.reflect.Factory;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtSimpleType;
import spoon.support.processing.XmlProcessorProperties;

/**
 * This class implements a simple Spoon environment that reports messages in the
 * standard output stream (Java-compliant).
 */
public class StandardEnvironment implements Serializable, Environment {

    /**
     * The processors' properties files extension (.xml)
     */
    public static final String PROPERTIES_EXT = ".xml";

    private static final long serialVersionUID = 1L;

    private boolean debug = false;

    private FileGenerator<? extends CtElement> defaultFileGenerator;

    private int errorCount = 0;

    private transient Factory factory;

    ProcessingManager manager;

    private boolean processingStopped = false;

    private boolean verbose = false;

    private int warningCount = 0;

    private File xmlRootFolder;

    /**
     * Creates a new environment with a <code>null</code> default file
     * generator.
     */
    public StandardEnvironment() {
    }

    /**
     * Creates a new environment.
     */
    public StandardEnvironment(FileGenerator<? extends CtElement> defaultFileGenerator) {
        this.defaultFileGenerator = defaultFileGenerator;
    }

    public void debugMessage(String message) {
        if (isDebug()) {
            System.out.println(message);
        }
    }

    public FileGenerator<? extends CtElement> getDefaultFileGenerator() {
        return defaultFileGenerator;
    }

    public Factory getFactory() {
        return factory;
    }

    public ProcessingManager getManager() {
        return manager;
    }

    Map<String, ProcessorProperties> processorProperties = new TreeMap<String, ProcessorProperties>();

    public ProcessorProperties getProcessorProperties(String processorName)
            throws FileNotFoundException, IOException, SAXException {
        if (processorProperties.containsKey(processorName)) {
            return processorProperties.get(processorName);
        }

        InputStream in = getPropertyStream(processorName);
        XmlProcessorProperties prop =null;
        try {
            prop = new XmlProcessorProperties(getFactory(),
                    processorName, in);
        } catch(SAXException e) {
            throw new RuntimeException(e);
        }
        processorProperties.put(processorName, prop);
        return prop;
    }

    private InputStream getPropertyStream(String processorName)
            throws FileNotFoundException {
        for (File child : getXmlRootFolder().listFiles()) {
            if (child.getName().equals(processorName + PROPERTIES_EXT)) {
                return new FileInputStream(child);
            }
        }
        throw new FileNotFoundException();
    }

    /**
     * Gets the root folder where the processors' XML configuration files are
     * located.
     */
    public File getXmlRootFolder() {
        if (xmlRootFolder == null) {
            xmlRootFolder = new File(".");
        }
        return xmlRootFolder;
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * Tells if the processing is stopped, generally because one of the
     * processors called {@link #setProcessingStopped(boolean)} after reporting
     * an error.
     */
    public boolean isProcessingStopped() {
        return processingStopped;
    }

    /**
     * Returns true if Spoon is in verbose mode.
     */
    public boolean isVerbose() {
        return verbose;
    }

    private void prefix(StringBuffer buffer, Severity severity) {
        // Prefix message
        switch (severity) {
        case ERROR:
            buffer.append("error: ");
            errorCount++;
            break;
        case WARNING:
            buffer.append("warning: ");
            warningCount++;
            break;
        case MESSAGE:
            break;
        }
    }

    private void print(StringBuffer buffer, Severity severity) {
        switch (severity) {
        case ERROR:
        case WARNING:
            System.out.println(buffer.toString());
            break;
        default:
            if (isVerbose()) {
                System.out.println(buffer.toString());
            }
        }
    }

    public void report(Processor<?> processor, Severity severity,
            CtElement element, String message) {
        StringBuffer buffer = new StringBuffer();

        prefix(buffer, severity);

        // Adding message
        buffer.append(message);

        // Add sourceposition (javac format)
        CtSimpleType<?> type = (element instanceof CtSimpleType) ? (CtSimpleType<?>) element
                : element.getParent(CtSimpleType.class);
        SourcePosition sp = element.getPosition();

        if (sp == null) {
            buffer.append(" (Unknown Source)");
        } else {
            buffer.append(" at " + type.getQualifiedName() + ".");
            CtExecutable<?> exe = (element instanceof CtExecutable) ? (CtExecutable<?>) element
                    : element.getParent(CtExecutable.class);
            if (exe != null) {
                buffer.append(exe.getSimpleName());
            }
            buffer.append("(" + sp.getFile().getName() + ":" + sp.getLine()
                    + ")");
        }

        print(buffer, severity);
    }

    public void report(Processor<?> processor, Severity severity, String message) {
        StringBuffer buffer = new StringBuffer();

        prefix(buffer, severity);
        // Adding message
        buffer.append(message);
        print(buffer, severity);
    }

    /**
     * This method should be called to report the end of the processing.
     */
    public void reportEnd() {
        if(!isVerbose()) {
            return;
        }
        System.out.print("end of processing: ");
        if (warningCount > 0) {
            System.out.print(warningCount + " warning");
            if (warningCount > 1) {
                System.out.print("s");
            }
            if (errorCount > 0) {
                System.out.print(", ");
            }
        }
        if (errorCount > 0) {
            System.out.print(errorCount + " error");
            if (errorCount > 1) {
                System.out.print("s");
            }
        }
        if ((errorCount + warningCount) > 0) {
            System.out.print("\n");
        } else {
            System.out.println("no errors, no warnings");
        }
    }

    public void reportProgressMessage(String message) {
        if(!isVerbose()) {
            return;
        }
        System.out.println(message);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setDefaultFileGenerator(
            FileGenerator<? extends CtElement> defaultFileGenerator) {
        this.defaultFileGenerator = defaultFileGenerator;
        defaultFileGenerator.setFactory(getFactory());
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public void setManager(ProcessingManager manager) {
        this.manager = manager;
    }

    public void setProcessingStopped(boolean processingStopped) {
        this.processingStopped = processingStopped;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Sets the root folder where the processors' XML configuration files are
     * located.
     */
    public void setXmlRootFolder(File xmlRootFolder) {
        this.xmlRootFolder = xmlRootFolder;
    }

    int complianceLevel = 5;

    public int getComplianceLevel() {
        return complianceLevel;
    }

    public void setComplianceLevel(int level) {
        complianceLevel = level;
    }

    public void setProcessorProperties(String processorName,
            ProcessorProperties prop) {
        processorProperties.put(processorName, prop);
    }

    public void report(Processor<?> processor, Severity severity,
            CtElement element, String message, ProblemFixer<?>... fix) {
        // Fix not (yet) used in command-line mode
        report(processor, severity, element, message);
    }

    public boolean isUsingSourceCodeFragments() {
        return useSourceCodeFragments;
    }

    boolean useSourceCodeFragments=false;

    public void useSourceCodeFragments(boolean b) {
        useSourceCodeFragments=b;
    }

    boolean useTabulations=false;

    public boolean isUsingTabulations() {
        return useTabulations;
    }

    public void useTabulations(boolean tabulation) {
        useTabulations = tabulation;
    }

    int tabulationSize=4;

    public int getTabulationSize() {
        return tabulationSize;
    }

    public void setTabulationSize(int tabulationSize) {
        this.tabulationSize = tabulationSize;
    }

    public String getSourcePath() {
        return ".";
    }

}
